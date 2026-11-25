package com.pfc.thindesk.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ContaInicialInitializer implements CommandLineRunner {

    private final UsuarioContaService contaService;
    private final String usuario;
    private final String senhaUsuario;
    private final String admin;
    private final String senhaAdmin;

    public ContaInicialInitializer(
            UsuarioContaService contaService,
            @Value("${spring.security.user.name:usuario}") String usuario,
            @Value("${spring.security.user.password:usuario123}") String senhaUsuario,
            @Value("${thindesk.admin.username:admin}") String admin,
            @Value("${thindesk.admin.password:admin123}") String senhaAdmin) {
        this.contaService = contaService;
        this.usuario = usuario;
        this.senhaUsuario = senhaUsuario;
        this.admin = admin;
        this.senhaAdmin = senhaAdmin;
    }

    @Override
    public void run(String... args) {
        contaService.criarContaInicial(usuario, senhaUsuario, "USER");
        contaService.criarContaInicial(admin, senhaAdmin, "ADMIN");
    }
}
