package com.univesp.pi.pji310.s3.t4.pi_2025_s1.integracao;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Produto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProdutoIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String url() {
        return "http://localhost:" + port + "/produtos";
    }

    @Test
    void deveExecutarCrudCompletoDeProduto() {
        Produto produto = new Produto(null, "Fertilizante", "Para plantio", new BigDecimal("19.90"));

        // Criar
        ResponseEntity<Produto> post = restTemplate.postForEntity(url(), produto, Produto.class);
        assertThat(post.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        URI location = post.getHeaders().getLocation();
        assertThat(location).isNotNull();

        // Consultar
        Produto consultado = restTemplate.getForObject(location, Produto.class);
        assertThat(consultado.getNome()).isEqualTo("Fertilizante");

        // Atualizar
        consultado.setNome("Fertilizante Premium");
        restTemplate.put(location, consultado);
        Produto atualizado = restTemplate.getForObject(location, Produto.class);
        assertThat(atualizado.getNome()).isEqualTo("Fertilizante Premium");

        // Deletar
        restTemplate.delete(location);

        ResponseEntity<String> getAfterDelete = restTemplate.getForEntity(location, String.class);
        assertThat(getAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(getAfterDelete.getBody()).contains("Produto não encontrado");
    }

    @Test
    void deveRetornarBadRequestQuandoProdutoInvalido() {
        Produto invalido = new Produto(); // todos campos nulos
        ResponseEntity<String> response = restTemplate.postForEntity(url(), invalido, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Nome é obrigatório", "Descrição é obrigatória", "Preço é obrigatório");
    }

    @Test
    void deveRetornarBadRequestQuandoIdCorpoDiferenteDoPath() {
        Produto produto = new Produto(null, "Teste", "Inconsistente", new BigDecimal("5.50"));
        Produto salvo = restTemplate.postForEntity(url(), produto, Produto.class).getBody();

        assertThat(salvo).isNotNull();
        salvo.setId(salvo.getId() + 1);

        HttpEntity<Produto> entity = new HttpEntity<>(salvo);
        ResponseEntity<String> response = restTemplate.exchange(url() + "/" + (salvo.getId() - 1), HttpMethod.PUT, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("ID do caminho e do corpo não coincidem");
    }
}
