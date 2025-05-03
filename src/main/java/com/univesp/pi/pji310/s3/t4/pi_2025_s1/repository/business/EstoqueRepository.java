package com.univesp.pi.pji310.s3.t4.pi_2025_s1.repository.business;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.business.Estoque;

import java.util.Optional;

public interface EstoqueRepository
        extends BaseRepository<Estoque> {

    Optional<Estoque> findByProdutoId(Long id);
}
