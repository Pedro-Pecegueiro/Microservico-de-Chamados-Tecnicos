package com.pfc.thindesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CriarContaRequest {

    @NotBlank(message = "Informe o nome de usuário.")
    @Size(min = 3, max = 60, message = "O usuário deve ter entre 3 e 60 caracteres.")
    private String usuario;

    @NotBlank(message = "Informe uma senha.")
    @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres.")
    private String senha;

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
