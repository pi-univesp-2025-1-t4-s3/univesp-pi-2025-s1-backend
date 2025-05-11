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

        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            throw new IllegalArgumentException("Venda deve conter pelo menos um item.");
        }

        venda.getItens().forEach(item -> item.setVenda(venda));

        // Valida e debita o estoque ANTES de salvar
        venda.getItens().forEach(item ->
                estoqueService.decrementarQuantidadePorProduto(item.getProduto().getId(), item.getQuantidade())
        );

        return vendaRepository.save(venda);
    }

    @Transactional
    public Venda atualizar(Long id, Venda novaVenda) {
        Venda existente = vendaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada: " + id));

        // Repor estoque dos itens antigos
        existente.getItens().forEach(itemAntigo ->
                estoqueService.incrementarQuantidadePorProduto(itemAntigo.getProduto().getId(), itemAntigo.getQuantidade())
        );

        // Preparar nova lista de itens e validar débito de estoque
        List<ItemVenda> novosItens = novaVenda.getItens();
        for (ItemVenda item : novosItens) {
            estoqueService.decrementarQuantidadePorProduto(item.getProduto().getId(), item.getQuantidade());
        }

        // Atualizar dados após validações
        existente.setData(novaVenda.getData());
        existente.getItens().clear();

        for (ItemVenda novoItem : novosItens) {
            novoItem.setVenda(existente);
            existente.getItens().add(novoItem);
        }

        return vendaRepository.save(existente);
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
