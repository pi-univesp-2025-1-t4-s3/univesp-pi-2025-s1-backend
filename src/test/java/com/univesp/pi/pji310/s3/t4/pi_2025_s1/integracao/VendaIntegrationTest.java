package com.univesp.pi.pji310.s3.t4.pi_2025_s1.integracao;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VendaIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void deveExecutarCrudCompletoDeVenda() {
        // Criar produto
        Produto produto = new Produto();
        produto.setNome("Adubo Natural");
        produto.setDescricao("Produto orgânico");
        produto.setPreco(BigDecimal.valueOf(19.99));

        ResponseEntity<Produto> respostaProduto = restTemplate.postForEntity(
                baseUrl("/produtos"), produto, Produto.class
        );

        assertEquals(HttpStatus.CREATED, respostaProduto.getStatusCode());
        Produto produtoCriado = respostaProduto.getBody();
        assertNotNull(produtoCriado);
        assertNotNull(produtoCriado.getId());

        // Incrementar estoque via endpoint correto
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Integer> estoqueRequest = new HttpEntity<>(10, headers);
        restTemplate.postForEntity(
                baseUrl("/estoques/produto/" + produtoCriado.getId() + "/incrementar"),
                estoqueRequest,
                Void.class
        );

        // Criar venda com 2 unidades
        Venda venda = new Venda();
        venda.setData(LocalDate.now());

        ItemVenda item = new ItemVenda();
        item.setProduto(produtoCriado);
        item.setQuantidade(2);

        venda.setItens(List.of(item));

        ResponseEntity<Venda> respostaVenda = restTemplate.postForEntity(
                baseUrl("/vendas"), venda, Venda.class
        );

        assertEquals(HttpStatus.CREATED, respostaVenda.getStatusCode());
        Venda vendaCriada = respostaVenda.getBody();
        assertNotNull(vendaCriada);
        assertEquals(2, vendaCriada.getItens().get(0).getQuantidade());

        // Verificar estoque após venda: 10 - 2 = 8
        ResponseEntity<Estoque> estoqueAposVenda = restTemplate.getForEntity(
                baseUrl("/estoques/produto/" + produtoCriado.getId()), Estoque.class
        );

        assertEquals(HttpStatus.OK, estoqueAposVenda.getStatusCode());
        assertEquals(8, estoqueAposVenda.getBody().getQuantidade());

        // Atualizar venda para 3 unidades
        vendaCriada.getItens().get(0).setQuantidade(3);
        HttpEntity<Venda> requestEntity = new HttpEntity<>(vendaCriada, headers);

        ResponseEntity<Venda> respostaAtualizada = restTemplate.exchange(
                baseUrl("/vendas/" + vendaCriada.getId()),
                HttpMethod.PUT,
                requestEntity,
                Venda.class
        );

        assertEquals(HttpStatus.OK, respostaAtualizada.getStatusCode());
        assertEquals(3, respostaAtualizada.getBody().getItens().get(0).getQuantidade());

        // Verificar estoque após atualização: 8 - 1 = 7
        ResponseEntity<Estoque> estoqueAposAtualizacao = restTemplate.getForEntity(
                baseUrl("/estoques/produto/" + produtoCriado.getId()), Estoque.class
        );

        assertEquals(HttpStatus.OK, estoqueAposAtualizacao.getStatusCode());
        assertEquals(7, estoqueAposAtualizacao.getBody().getQuantidade());
    }

    @Test
    void deveRejeitarVendaComEstoqueInsuficiente() {
        // Criar produto
        Produto produto = new Produto();
        produto.setNome("Inseticida Natural");
        produto.setDescricao("Produto biodegradável");
        produto.setPreco(BigDecimal.valueOf(29.90));

        ResponseEntity<Produto> respostaProduto = restTemplate.postForEntity(
                baseUrl("/produtos"), produto, Produto.class
        );

        assertEquals(HttpStatus.CREATED, respostaProduto.getStatusCode());
        Produto produtoCriado = respostaProduto.getBody();
        assertNotNull(produtoCriado);
        assertNotNull(produtoCriado.getId());

        // Tentar criar venda com 10 unidades (sem adicionar estoque)
        Venda venda = new Venda();
        venda.setData(LocalDate.now());

        ItemVenda item = new ItemVenda();
        item.setProduto(produtoCriado);
        item.setQuantidade(10);

        venda.setItens(List.of(item));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Venda> request = new HttpEntity<>(venda, headers);

        ResponseEntity<String> respostaErro = restTemplate.postForEntity(
                baseUrl("/vendas"), request, String.class
        );

        // Deve retornar 400 com mensagem de erro
        assertEquals(HttpStatus.BAD_REQUEST, respostaErro.getStatusCode());
        assertTrue(respostaErro.getBody().contains("Estoque insuficiente"));

        // Verificar que nenhuma venda foi registrada
        ResponseEntity<Venda[]> vendasResponse = restTemplate.getForEntity(
                baseUrl("/vendas"), Venda[].class
        );

        Venda[] vendas = vendasResponse.getBody();
        assertNotNull(vendas);
        boolean contemVenda = List.of(vendas).stream()
                .anyMatch(v -> v.getItens().stream()
                        .anyMatch(i -> i.getProduto().getId().equals(produtoCriado.getId())));

        assertFalse(contemVenda, "Venda não deveria ter sido registrada no banco");
    }

    @Test
    void deveFazerRollbackSeEstoqueInsuficienteParaUmDosItens() {
        // Criar dois produtos
        Produto produto1 = new Produto();
        produto1.setNome("Produto A");
        produto1.setDescricao("Estoque suficiente");
        produto1.setPreco(BigDecimal.valueOf(10.00));

        Produto produto2 = new Produto();
        produto2.setNome("Produto B");
        produto2.setDescricao("Estoque insuficiente");
        produto2.setPreco(BigDecimal.valueOf(20.00));

        Produto criado1 = restTemplate.postForEntity(baseUrl("/produtos"), produto1, Produto.class).getBody();
        Produto criado2 = restTemplate.postForEntity(baseUrl("/produtos"), produto2, Produto.class).getBody();

        assertNotNull(criado1);
        assertNotNull(criado2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Integer> estoqueRequest = new HttpEntity<>(5, headers);

        restTemplate.postForEntity(
                baseUrl("/estoques/produto/" + criado1.getId() + "/incrementar"),
                estoqueRequest,
                Void.class
        );

        // Montar venda com dois itens: um com estoque suficiente, outro não
        ItemVenda item1 = new ItemVenda();
        item1.setProduto(criado1);
        item1.setQuantidade(3); // válido

        ItemVenda item2 = new ItemVenda();
        item2.setProduto(criado2);
        item2.setQuantidade(1); // sem estoque

        Venda venda = new Venda();
        venda.setData(LocalDate.now());
        venda.setItens(List.of(item1, item2));

        HttpEntity<Venda> request = new HttpEntity<>(venda, headers);

        ResponseEntity<String> respostaErro = restTemplate.postForEntity(
                baseUrl("/vendas"), request, String.class
        );

        // Deve retornar erro por estoque insuficiente
        assertEquals(HttpStatus.BAD_REQUEST, respostaErro.getStatusCode());
        assertTrue(respostaErro.getBody().contains("Estoque insuficiente"));

        // Verificar que a venda NÃO foi salva
        ResponseEntity<Venda[]> vendasResponse = restTemplate.getForEntity(baseUrl("/vendas"), Venda[].class);
        assertNotNull(vendasResponse.getBody());

        boolean contemVenda = List.of(vendasResponse.getBody()).stream()
                .anyMatch(v -> v.getItens().stream()
                        .anyMatch(i -> i.getProduto().getId().equals(criado1.getId()) ||
                                i.getProduto().getId().equals(criado2.getId())));
        assertFalse(contemVenda, "A venda não deveria ter sido salva");

        // Verificar que o estoque do produto1 continua intacto (5 unidades)
        ResponseEntity<Estoque> estoqueProduto1 = restTemplate.getForEntity(
                baseUrl("/estoques/produto/" + criado1.getId()), Estoque.class
        );

        assertEquals(HttpStatus.OK, estoqueProduto1.getStatusCode());
        assertEquals(5, estoqueProduto1.getBody().getQuantidade());
    }

    @Test
    void deveFazerRollbackNaAtualizacaoSeEstoqueInsuficienteParaNovoItem() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Criar dois produtos
        Produto produto1 = new Produto();
        produto1.setNome("Produto Atualizável");
        produto1.setDescricao("Com estoque");
        produto1.setPreco(BigDecimal.valueOf(15.00));

        Produto produto2 = new Produto();
        produto2.setNome("Produto Sem Estoque");
        produto2.setDescricao("Sem estoque");
        produto2.setPreco(BigDecimal.valueOf(25.00));

        Produto criado1 = restTemplate.postForEntity(baseUrl("/produtos"), produto1, Produto.class).getBody();
        Produto criado2 = restTemplate.postForEntity(baseUrl("/produtos"), produto2, Produto.class).getBody();

        assertNotNull(criado1);
        assertNotNull(criado2);

        // Incrementar estoque apenas para produto1
        HttpEntity<Integer> estoqueRequest = new HttpEntity<>(5, headers);
        restTemplate.postForEntity(
                baseUrl("/estoques/produto/" + criado1.getId() + "/incrementar"),
                estoqueRequest,
                Void.class
        );

        // Criar venda inicial com produto1
        ItemVenda itemInicial = new ItemVenda();
        itemInicial.setProduto(criado1);
        itemInicial.setQuantidade(2);

        Venda venda = new Venda();
        venda.setData(LocalDate.now());
        venda.setItens(List.of(itemInicial));

        Venda vendaCriada = restTemplate.postForEntity(
                baseUrl("/vendas"), new HttpEntity<>(venda, headers), Venda.class
        ).getBody();

        assertNotNull(vendaCriada);
        Long vendaId = vendaCriada.getId();

        // Verificar estoque atual: 5 - 2 = 3
        Estoque estoqueAntes = restTemplate.getForEntity(
                baseUrl("/estoques/produto/" + criado1.getId()), Estoque.class
        ).getBody();
        assertEquals(3, estoqueAntes.getQuantidade());

        // Tentar atualizar venda: manter produto1, adicionar produto2 (sem estoque)
        ItemVenda novoItem1 = new ItemVenda();
        novoItem1.setProduto(criado1);
        novoItem1.setQuantidade(1);

        ItemVenda novoItem2 = new ItemVenda();
        novoItem2.setProduto(criado2);
        novoItem2.setQuantidade(1); // sem estoque

        vendaCriada.setItens(List.of(novoItem1, novoItem2));

        HttpEntity<Venda> requestAtualizacao = new HttpEntity<>(vendaCriada, headers);

        ResponseEntity<String> respostaErro = restTemplate.exchange(
                baseUrl("/vendas/" + vendaId),
                HttpMethod.PUT,
                requestAtualizacao,
                String.class
        );

        // Verificar que falhou com BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, respostaErro.getStatusCode());
        assertTrue(respostaErro.getBody().contains("Estoque insuficiente"));

        // Verificar que venda original não foi alterada
        Venda vendaAposErro = restTemplate.getForEntity(
                baseUrl("/vendas/" + vendaId), Venda.class
        ).getBody();

        assertNotNull(vendaAposErro);
        assertEquals(1, vendaAposErro.getItens().size());
        assertEquals(criado1.getId(), vendaAposErro.getItens().get(0).getProduto().getId());
        assertEquals(2, vendaAposErro.getItens().get(0).getQuantidade());

        // Verificar que estoque de produto1 continua como estava (3)
        Estoque estoqueFinal = restTemplate.getForEntity(
                baseUrl("/estoques/produto/" + criado1.getId()), Estoque.class
        ).getBody();
        assertEquals(3, estoqueFinal.getQuantidade());
    }


}
