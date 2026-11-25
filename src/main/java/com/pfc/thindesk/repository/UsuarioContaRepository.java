package com.pfc.thindesk.repository;

import com.pfc.thindesk.entity.UsuarioConta;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsuarioContaRepository extends MongoRepository<UsuarioConta, String> {
    Optional<UsuarioConta> findByUsuario(String usuario);
    Optional<UsuarioConta> findByUsuarioAndAtivoTrue(String usuario);
    boolean existsByUsuario(String usuario);
}
