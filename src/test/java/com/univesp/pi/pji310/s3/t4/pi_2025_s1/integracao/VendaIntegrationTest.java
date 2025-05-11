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
}
