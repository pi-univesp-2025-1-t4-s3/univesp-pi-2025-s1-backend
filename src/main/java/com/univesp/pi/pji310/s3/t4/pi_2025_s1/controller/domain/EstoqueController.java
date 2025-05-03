package com.univesp.pi.pji310.s3.t4.pi_2025_s1.controller.domain;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Estoque;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.service.domain.EstoqueService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/estoques")
@RequiredArgsConstructor
@Validated
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping
    public ResponseEntity<List<Estoque>> listarTodos() {
        return ResponseEntity.ok(estoqueService.listarTodos());
    }

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<Estoque> consultarPorProduto(@PathVariable Long produtoId) {
        return ResponseEntity.ok(estoqueService.buscarPorProdutoId(produtoId));
    }

    @PostMapping("/produto/{produtoId}/incrementar")
    public ResponseEntity<Void> incrementarPorProduto(
            @PathVariable Long produtoId,
            @RequestBody @NotNull @Positive Integer quantidade) {

        estoqueService.incrementarQuantidadePorProduto(produtoId, quantidade);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/produto/{produtoId}/decrementar")
    public ResponseEntity<Void> decrementarPorProduto(
            @PathVariable Long produtoId,
            @RequestBody @NotNull @Positive Integer quantidade) {

        estoqueService.decrementarQuantidadePorProduto(produtoId, quantidade);
        return ResponseEntity.noContent().build();
    }
}
