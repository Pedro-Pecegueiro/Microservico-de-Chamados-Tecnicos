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
        return chamadoRepository.findAll();
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

    /** Mantém a edição legada protegida contra mudanças após a resolução. */
    public Chamado atualizarChamado(String id, Chamado chamadoAtualizado) {
        Chamado chamado = buscarPorId(id);
        if ("RESOLVIDO".equalsIgnoreCase(chamado.getStatus())) {
            throw new RegraChamadoException("Chamados resolvidos não podem ser alterados novamente.");
        }
        if ("RESOLVIDO".equalsIgnoreCase(chamadoAtualizado.getStatus())) {
            throw new RegraChamadoException("Use o endpoint de resolução para concluir um chamado.");
        }

        chamado.setDescricao(chamadoAtualizado.getDescricao());
        chamado.setStatus(chamadoAtualizado.getStatus());
        chamado.setTipo(chamadoAtualizado.getTipo());
        chamado.setTecnico(chamadoAtualizado.getTecnico());
        return chamadoRepository.save(chamado);
    }
}
