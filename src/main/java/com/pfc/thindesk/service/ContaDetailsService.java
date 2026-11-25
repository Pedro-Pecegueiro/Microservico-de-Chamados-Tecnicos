package com.pfc.thindesk.service;

import com.pfc.thindesk.repository.UsuarioContaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ContaDetailsService implements UserDetailsService {

    private final UsuarioContaRepository repository;

    public ContaDetailsService(UsuarioContaRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsuarioAndAtivoTrue(username)
                .map(conta -> User.withUsername(conta.getUsuario())
                        .password(conta.getSenhaHash())
                        .roles(conta.getPerfil())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Conta não encontrada."));
    }
}
