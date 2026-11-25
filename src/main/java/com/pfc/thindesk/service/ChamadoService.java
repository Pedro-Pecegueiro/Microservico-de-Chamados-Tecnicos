package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.repository.ChamadoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;

    public ChamadoService(ChamadoRepository chamadoRepository) {
        this.chamadoRepository = chamadoRepository;
    }

    public Chamado abrirChamado(String titulo, String descricao, String solicitante) {
        Chamado chamado = new Chamado();
        chamado.setTitulo(titulo.trim());
        chamado.setDescricao(descricao.trim());
        chamado.setSolicitante(solicitante);
        chamado.setStatus("ABERTO");
        chamado.setDataAbertura(Instant.now());
        return chamadoRepository.save(chamado);
    }

    public List<Chamado> listarChamadosDoUsuario(String solicitante) {
        return chamadoRepository.findBySolicitanteOrderByDataAberturaDesc(solicitante);
    }

    public List<Chamado> listarChamados() {
        return chamadoRepository.findAllByOrderByDataAberturaDesc();
    }

    public Chamado buscarPorId(String id) {
        return chamadoRepository.findById(id)
                .orElseThrow(() -> new ChamadoNaoEncontradoException(id));
    }

    public Chamado resolverChamado(String id, String solucao, String tecnico) {
        Chamado chamado = buscarPorId(id);
        if ("RESOLVIDO".equalsIgnoreCase(chamado.getStatus())) {
            throw new RegraChamadoException("Chamados resolvidos não podem ser alterados novamente.");
        }
        if (!"ABERTO".equalsIgnoreCase(chamado.getStatus())) {
            throw new RegraChamadoException("Somente chamados com status ABERTO podem ser resolvidos.");
        }

        chamado.setSolucao(solucao.trim());
        chamado.setTecnico(tecnico);
        chamado.setStatus("RESOLVIDO");
        chamado.setDataResolucao(Instant.now());
        return chamadoRepository.save(chamado);
    }

    public Chamado atualizarChamado(String id, String titulo, String descricao) {
        Chamado chamado = buscarPorId(id);
        validarChamadoAberto(chamado);

        chamado.setTitulo(titulo.trim());
        chamado.setDescricao(descricao.trim());
        return chamadoRepository.save(chamado);
    }

    public void cancelarChamado(String id) {
        Chamado chamado = buscarPorId(id);
        validarChamadoAberto(chamado);
        chamadoRepository.delete(chamado);
    }

    private void validarChamadoAberto(Chamado chamado) {
        if (!"ABERTO".equalsIgnoreCase(chamado.getStatus())) {
            throw new RegraChamadoException("Somente chamados ABERTOS podem ser alterados ou cancelados.");
        }
    }
}