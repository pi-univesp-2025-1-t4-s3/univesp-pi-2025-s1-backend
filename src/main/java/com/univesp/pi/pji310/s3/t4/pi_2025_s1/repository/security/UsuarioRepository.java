package com.univesp.pi.pji310.s3.t4.pi_2025_s1.repository.security;

import com.univesp.pi.pji310.s3.t4.pi_2025_s1.domain.security.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
}
