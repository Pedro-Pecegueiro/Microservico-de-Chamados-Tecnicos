package com.pfc.thindesk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;


@Document(collection = "chamados")
public class Chamado {
    
    @Id
    private String id;
    private String titulo;
    private String descricao;
    private String status;
    private String tipo;
    private String tecnico;
    private String solicitante;
    private Instant dataAbertura;
    private String solucao;
    private Instant dataResolucao;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTecnico() {
        return tecnico;
    }

    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public Instant getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(Instant dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public String getSolucao() {
        return solucao;
    }

    public void setSolucao(String solucao) {
        this.solucao = solucao;
    }

    public Instant getDataResolucao() {
        return dataResolucao;
    }

    public void setDataResolucao(Instant dataResolucao) {
        this.dataResolucao = dataResolucao;
    }

    /** Compatibilidade com a tela legada, que chama o solicitante de usuário. */
    @Transient
    @JsonIgnore
    public String getUsuario() {
        return solicitante;
    }

    @Transient
    @JsonIgnore
    public void setUsuario(String usuario) {
        this.solicitante = usuario;
    }
}
