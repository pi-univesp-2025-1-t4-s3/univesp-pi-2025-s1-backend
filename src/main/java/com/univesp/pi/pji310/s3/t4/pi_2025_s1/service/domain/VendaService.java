package com.univesp.pi.pji310.s3.t4.pi_2025_s1.service.domain;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.ItemVenda;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Venda;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.repository.business.VendaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final EstoqueService estoqueService;

    public Venda salvar(Venda venda) {

        if (venda.getItens() != null) {
            venda.getItens().forEach(item -> item.setVenda(venda));
        }

        Venda salva = vendaRepository.save(venda);

        // Debita o estoque após a venda
        salva.getItens().forEach(item ->
                estoqueService.decrementarQuantidadePorProduto(item.getProduto().getId(), item.getQuantidade())
        );

        return salva;
    }

    @Transactional
    public Venda atualizar(Long id, Venda novaVenda) {
        Venda existente = vendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada: " + id));

        // Repor estoque dos itens antigos
        existente.getItens().forEach(itemAntigo ->
                estoqueService.incrementarQuantidadePorProduto(itemAntigo.getProduto().getId(), itemAntigo.getQuantidade())
        );

        existente.setData(novaVenda.getData());
        existente.getItens().clear();

        for (ItemVenda novoItem : novaVenda.getItens()) {
            novoItem.setVenda(existente);
            existente.getItens().add(novoItem);
        }

        Venda atualizada = vendaRepository.save(existente);

        // Debita estoque dos novos itens
        atualizada.getItens().forEach(itemNovo ->
                estoqueService.decrementarQuantidadePorProduto(itemNovo.getProduto().getId(), itemNovo.getQuantidade())
        );

        return atualizada;
    }

    public Venda buscarPorId(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada: " + id));
    }

    public List<Venda> listarTodas() {
        return vendaRepository.findAll();
    }

    public void excluirPorId(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada para exclusão: " + id));

        venda.getItens().forEach(item ->
                estoqueService.incrementarQuantidadePorProduto(item.getProduto().getId(), item.getQuantidade())
        );

        vendaRepository.deleteById(id);
    }
}
