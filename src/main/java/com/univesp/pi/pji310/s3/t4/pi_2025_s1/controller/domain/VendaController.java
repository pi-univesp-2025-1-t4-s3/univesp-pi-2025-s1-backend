package com.univesp.pi.pji310.s3.t4.pi_2025_s1.controller.domain;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Venda;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.service.domain.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;

    @GetMapping
    public ResponseEntity<List<Venda>> listarTodas() {
        return ResponseEntity.ok(vendaService.listarTodas());
    }

    @PostMapping
    public ResponseEntity<Venda> criar(@RequestBody @Valid Venda venda) {
        Venda criada = vendaService.salvar(venda);
        return ResponseEntity.created(URI.create("/vendas/" + criada.getId())).body(criada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venda> atualizar(@PathVariable Long id, @Valid @RequestBody Venda vendaAtualizada) {
        Venda atualizada = vendaService.atualizar(id, vendaAtualizada);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        vendaService.excluirPorId(id);
        return ResponseEntity.noContent().build();
    }
}
