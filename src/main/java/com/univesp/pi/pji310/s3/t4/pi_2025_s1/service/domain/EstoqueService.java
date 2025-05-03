package com.univesp.pi.pji310.s3.t4.pi_2025_s1.service.domain;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Estoque;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Produto;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.repository.business.EstoqueRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoService produtoService;

    public List<Estoque> listarTodos() {
        return estoqueRepository.findAll();
    }

    public Estoque buscarPorProdutoId(Long produtoId) {
        return estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado para o produto " + produtoId));
    }

    public void decrementarQuantidadePorProduto(Long produtoId, int quantidade) {

        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseGet(() -> {
                    Produto produto = produtoService.buscarPorId(produtoId);

                    return inicializarEstoque(produto);
                });

        int novaQuantidade = estoque.getQuantidade() - quantidade;
        if (novaQuantidade < 0) {
            throw new IllegalArgumentException("Estoque insuficiente para o produto " + produtoId);
        }

        estoque.setQuantidade(novaQuantidade);
        estoqueRepository.save(estoque);
    }

    public void incrementarQuantidadePorProduto(Long produtoId, int quantidade) {
        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseGet(() -> {
                    Produto produto = produtoService.buscarPorId(produtoId);

                    return inicializarEstoque(produto);
                });

        estoque.setQuantidade(estoque.getQuantidade() + quantidade);
        estoqueRepository.save(estoque);
    }

    private static Estoque inicializarEstoque(Produto produto) {
        Estoque novoEstoque = new Estoque();
        novoEstoque.setProduto(produto);
        novoEstoque.setQuantidade(0);
        return novoEstoque;
    }
}

