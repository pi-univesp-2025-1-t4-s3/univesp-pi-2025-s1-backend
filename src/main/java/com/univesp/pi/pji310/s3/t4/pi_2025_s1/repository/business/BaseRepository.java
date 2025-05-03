package com.univesp.pi.pji310.s3.t4.pi_2025_s1.repository.business;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface BaseRepository<T>
        extends JpaRepository<T, Long>, PagingAndSortingRepository<T, Long> {
}
