package com.univesp.pi.pji310.s3.t4.pi_2025_s1.controller.domain;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Produto;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.service.domain.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody @Valid Produto produto) {
        Produto salvo = produtoService.salvar(produto);
        URI location = URI.create("/produtos/" + salvo.getId());
        return ResponseEntity.created(location).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody @Valid Produto produto) {
        if (!id.equals(produto.getId())) {
            throw new IllegalArgumentException("ID do caminho e do corpo não coincidem.");
        }
        return ResponseEntity.ok(produtoService.salvar(produto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        produtoService.excluirPorId(id);
        return ResponseEntity.noContent().build();
    }
}
