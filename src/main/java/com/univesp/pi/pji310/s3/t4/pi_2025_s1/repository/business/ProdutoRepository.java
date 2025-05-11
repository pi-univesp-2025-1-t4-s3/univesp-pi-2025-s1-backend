package com.univesp.pi.pji310.s3.t4.pi_2025_s1.repository.business;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Produto;

import java.util.List;

public interface ProdutoRepository
        extends BaseRepository<Produto> {

    List<Produto> findByNomeContainingIgnoreCase(String nome);
}
