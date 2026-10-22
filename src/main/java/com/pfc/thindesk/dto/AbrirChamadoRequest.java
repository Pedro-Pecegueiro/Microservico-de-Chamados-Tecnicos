package com.pfc.thindesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AbrirChamadoRequest {

    @NotBlank(message = "O título é obrigatório.")
    @Size(max = 120, message = "O título deve ter no máximo 120 caracteres.")
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória.")
    @Size(max = 5000, message = "A descrição deve ter no máximo 5000 caracteres.")
    private String descricao;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
