package com.univesp.pi.pji310.s3.t4.pi_2025_s1.service.domain;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Produto;
import com.univesp.pi.pji310.s3.t4.pi_2025_s1.repository.business.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + id));
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public void excluirPorId(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new EntityNotFoundException("Produto não encontrado para exclusão: " + id);
        }
        produtoRepository.deleteById(id);
    }
}

