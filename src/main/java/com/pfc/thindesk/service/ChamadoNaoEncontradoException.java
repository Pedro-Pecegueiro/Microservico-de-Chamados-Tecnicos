package com.pfc.thindesk.service;

public class ChamadoNaoEncontradoException extends RuntimeException {

    public ChamadoNaoEncontradoException(String id) {
        super("Chamado não encontrado: " + id);
    }
}
