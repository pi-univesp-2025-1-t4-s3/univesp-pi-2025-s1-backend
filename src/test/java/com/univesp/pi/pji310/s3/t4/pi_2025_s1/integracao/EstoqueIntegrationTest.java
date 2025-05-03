package com.univesp.pi.pji310.s3.t4.pi_2025_s1.integracao;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Estoque;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Produto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EstoqueIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String produtoUrl() {
        return "http://localhost:" + port + "/produtos";
    }

    private String estoqueUrl(Long produtoId, String operacao) {
        return "http://localhost:" + port + "/estoques/produto/" + produtoId + "/" + operacao;
    }

    private String estoqueConsultaUrl(Long produtoId) {
        return "http://localhost:" + port + "/estoques/produto/" + produtoId;
    }

    @Test
    void deveExecutarFluxoCompletoDeEstoquePorProduto() {
        Produto p = new Produto(null, "Produto Estoque", "controle", new BigDecimal("50.00"));
        Produto salvo = restTemplate.postForEntity(produtoUrl(), p, Produto.class).getBody();
        assertThat(salvo).isNotNull();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Incrementar
        restTemplate.postForEntity(estoqueUrl(salvo.getId(), "incrementar"), new HttpEntity<>(10, headers), Void.class);

        // Consultar
        ResponseEntity<Estoque> estoque = restTemplate.getForEntity(estoqueConsultaUrl(salvo.getId()), Estoque.class);
        assertThat(estoque.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(estoque.getBody().getQuantidade()).isEqualTo(10);

        // Decrementar
        restTemplate.postForEntity(estoqueUrl(salvo.getId(), "decrementar"), new HttpEntity<>(4, headers), Void.class);

        // Validar final
        ResponseEntity<Estoque> finalEstoque = restTemplate.getForEntity(estoqueConsultaUrl(salvo.getId()), Estoque.class);
        assertThat(finalEstoque.getBody().getQuantidade()).isEqualTo(6);
    }

    @Test
    void deveRetornarErroQuandoDecrementaSemEstoqueCriado() {
        Produto p = new Produto(null, "Produto Sem Estoque", "vazio", new BigDecimal("9.99"));
        Produto salvo = restTemplate.postForEntity(produtoUrl(), p, Produto.class).getBody();

        // Tenta decrementar sem criar estoque
        HttpEntity<Integer> request = new HttpEntity<>(1);
        ResponseEntity<String> resposta = restTemplate.postForEntity(
                estoqueUrl(salvo.getId(), "decrementar"),
                request,
                String.class
        );

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).contains("Estoque insuficiente");
    }

    @Test
    void deveRetornarErroQuandoDecrementaMaisQueDisponivel() {
        Produto produto = new Produto(null, "Produto com Estoque Limitado", "para teste de erro", new BigDecimal("29.90"));
        Produto salvo = restTemplate.postForEntity(produtoUrl(), produto, Produto.class).getBody();
        assertThat(salvo).isNotNull();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Criar estoque com 3 unidades
        restTemplate.postForEntity(
                estoqueUrl(salvo.getId(), "incrementar"),
                new HttpEntity<>(3, headers),
                Void.class
        );

        // Tentar decrementar 10 (mais do que o disponível)
        ResponseEntity<String> resposta = restTemplate.postForEntity(
                estoqueUrl(salvo.getId(), "decrementar"),
                new HttpEntity<>(10, headers),
                String.class
        );

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).contains("Estoque insuficiente");
    }

    @Test
    void deveRetornarBadRequestAoIncrementarComValorInvalido() {
        Produto produto = new Produto(null, "Produto Incremento Inválido", "teste", new BigDecimal("10.00"));
        Produto salvo = restTemplate.postForEntity(produtoUrl(), produto, Produto.class).getBody();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Quantidade null
        ResponseEntity<String> respostaNull = restTemplate.postForEntity(
                estoqueUrl(salvo.getId(), "incrementar"),
                new HttpEntity<>(null, headers),
                String.class
        );
        assertThat(respostaNull.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Quantidade zero
        ResponseEntity<String> respostaZero = restTemplate.postForEntity(
                estoqueUrl(salvo.getId(), "incrementar"),
                new HttpEntity<>(0, headers),
                String.class
        );
        assertThat(respostaZero.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Quantidade negativa
        ResponseEntity<String> respostaNegativo = restTemplate.postForEntity(
                estoqueUrl(salvo.getId(), "incrementar"),
                new HttpEntity<>(-5, headers),
                String.class
        );
        assertThat(respostaNegativo.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void deveRetornarBadRequestAoDecrementarComValorInvalido() {
        Produto produto = new Produto(null, "Produto Decremento Inválido", "teste", new BigDecimal("15.00"));
        Produto salvo = restTemplate.postForEntity(produtoUrl(), produto, Produto.class).getBody();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Quantidade null
        ResponseEntity<String> respostaNull = restTemplate.postForEntity(
                estoqueUrl(salvo.getId(), "decrementar"),
                new HttpEntity<>(null, headers),
                String.class
        );
        assertThat(respostaNull.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Quantidade zero
        ResponseEntity<String> respostaZero = restTemplate.postForEntity(
                estoqueUrl(salvo.getId(), "decrementar"),
                new HttpEntity<>(0, headers),
                String.class
        );
        assertThat(respostaZero.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Quantidade negativa
        ResponseEntity<String> respostaNegativo = restTemplate.postForEntity(
                estoqueUrl(salvo.getId(), "decrementar"),
                new HttpEntity<>(-2, headers),
                String.class
        );
        assertThat(respostaNegativo.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
