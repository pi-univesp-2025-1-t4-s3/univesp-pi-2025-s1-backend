package com.univesp.pi.pji310.s3.t4.pi_2025_s1.controller.domain;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Estoque;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.service.domain.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoques")
@RequiredArgsConstructor
@Validated
@Tag(name = "Estoque", description = "Operações relacionadas ao estoque de produtos")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @Operation(summary = "Listar todos os estoques")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Estoque>> listarTodos() {
        return ResponseEntity.ok(estoqueService.listarTodos());
    }

    @Operation(summary = "Consultar estoque de um produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Estoque não encontrado")
    })
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<Estoque> consultarPorProduto(@PathVariable Long produtoId) {
        return ResponseEntity.ok(estoqueService.buscarPorProdutoId(produtoId));
    }

    @Operation(summary = "Incrementar estoque de um produto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Estoque incrementado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PostMapping("/produto/{produtoId}/incrementar")
    public ResponseEntity<Void> incrementarPorProduto(
            @Parameter(description = "ID do produto") @PathVariable Long produtoId,
            @RequestBody @NotNull @Positive Integer quantidade) {

        estoqueService.incrementarQuantidadePorProduto(produtoId, quantidade);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Decrementar estoque de um produto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Estoque decrementado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quantidade excede o disponível"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PostMapping("/produto/{produtoId}/decrementar")
    public ResponseEntity<Void> decrementarPorProduto(
            @Parameter(description = "ID do produto") @PathVariable Long produtoId,
            @RequestBody @NotNull @Positive Integer quantidade) {

        estoqueService.decrementarQuantidadePorProduto(produtoId, quantidade);
        return ResponseEntity.noContent().build();
    }
}
