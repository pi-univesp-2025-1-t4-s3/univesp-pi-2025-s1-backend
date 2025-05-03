package com.univesp.pi.pji310.s3.t4.pi_2025_s1.controller.domain;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Venda;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.service.domain.VendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
@Tag(name = "Venda", description = "Operações relacionadas às vendas de produtos")
public class VendaController {

    private final VendaService vendaService;

    @Operation(summary = "Listar todas as vendas")
    @ApiResponse(responseCode = "200", description = "Lista de vendas retornada")
    @GetMapping
    public ResponseEntity<List<Venda>> listarTodas() {
        return ResponseEntity.ok(vendaService.listarTodas());
    }

    @Operation(summary = "Criar nova venda")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venda criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado ao debitar estoque")
    })
    @PostMapping
    public ResponseEntity<Venda> criar(@RequestBody @Valid Venda venda) {
        Venda criada = vendaService.salvar(venda);
        return ResponseEntity.created(URI.create("/vendas/" + criada.getId())).body(criada);
    }

    @Operation(summary = "Buscar venda por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venda encontrada"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));
    }

    @Operation(summary = "Atualizar venda")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venda atualizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Venda ou Produto não encontrados")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Venda> atualizar(@PathVariable Long id, @Valid @RequestBody Venda vendaAtualizada) {
        Venda atualizada = vendaService.atualizar(id, vendaAtualizada);
        return ResponseEntity.ok(atualizada);
    }

    @Operation(summary = "Remover venda")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Venda removida"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        vendaService.excluirPorId(id);
        return ResponseEntity.noContent().build();
    }
}
