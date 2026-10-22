package com.pfc.thindesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResolverChamadoRequest {

    @NotBlank(message = "A solução aplicada é obrigatória.")
    @Size(max = 5000, message = "A solução deve ter no máximo 5000 caracteres.")
    private String solucao;

    public String getSolucao() {
        return solucao;
    }

    public void setSolucao(String solucao) {
        this.solucao = solucao;
    }
}
