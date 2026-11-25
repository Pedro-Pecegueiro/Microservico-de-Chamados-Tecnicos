package com.pfc.thindesk.service;

import com.pfc.thindesk.dto.CriarContaRequest;
import com.pfc.thindesk.entity.UsuarioConta;
import com.pfc.thindesk.repository.UsuarioContaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioContaService {

    private final UsuarioContaRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioContaService(UsuarioContaRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioConta> listar() {
        return repository.findAll().stream()
                .filter(UsuarioConta::isAtivo)
                .toList();
    }

    public UsuarioConta criarUsuario(CriarContaRequest request) {
        String usuario = request.getUsuario().trim();
        if (repository.existsByUsuario(usuario)) {
            throw new RegraChamadoException("Já existe uma conta com esse nome de usuário.");
        }
        UsuarioConta conta = new UsuarioConta();
        conta.setUsuario(usuario);
        conta.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        conta.setPerfil("USER");
        conta.setAtivo(true);
        return repository.save(conta);
    }

    public void criarContaInicial(String usuario, String senha, String perfil) {
        repository.findByUsuario(usuario).ifPresentOrElse(
                existente -> {
                    if (!existente.isAtivo()) {
                        existente.setAtivo(true);
                        repository.save(existente);
                    }
                },
                () -> {
                    UsuarioConta conta = new UsuarioConta();
                    conta.setUsuario(usuario);
                    conta.setSenhaHash(passwordEncoder.encode(senha));
                    conta.setPerfil(perfil);
                    conta.setAtivo(true);
                    repository.save(conta);
                });
    }

    public void desativar(String id) {
        UsuarioConta conta = repository.findById(id)
                .orElseThrow(() -> new RegraChamadoException("Conta não encontrada."));
        if ("ADMIN".equals(conta.getPerfil())) {
            throw new RegraChamadoException("A conta de administrador não pode ser desativada por esta tela.");
        }
        conta.setAtivo(false);
        repository.save(conta);
    }
}
