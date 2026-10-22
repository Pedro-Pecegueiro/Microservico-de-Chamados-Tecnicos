package com.pfc.thindesk.controller;

import com.pfc.thindesk.dto.AbrirChamadoRequest;
import com.pfc.thindesk.dto.ResolverChamadoRequest;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.service.ChamadoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/chamados")
public class ChamadoController {

    private final ChamadoService chamadoService;

    public ChamadoController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    @PostMapping
    public ResponseEntity<Chamado> abrirChamado(
            @Valid @RequestBody AbrirChamadoRequest request, Authentication authentication) {
        Chamado chamado = chamadoService.abrirChamado(
                request.getTitulo(), request.getDescricao(), authentication.getName());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(chamado.getId())
                .toUri();
        return ResponseEntity.created(location).body(chamado);
    }

    @GetMapping("/usuario/{id}")
    public List<Chamado> listarChamadosDoUsuario(
            @PathVariable("id") String id, Authentication authentication) {
        if (!id.equals(authentication.getName()) && !ehTecnico(authentication)) {
            throw new AccessDeniedException("A consulta de chamados de outro usuário não é permitida.");
        }
        return chamadoService.listarChamadosDoUsuario(id);
    }

    @GetMapping("/{id}")
    public Chamado consultarChamado(@PathVariable("id") String id, Authentication authentication) {
        Chamado chamado = chamadoService.buscarPorId(id);
        if (!authentication.getName().equals(chamado.getSolicitante()) && !ehTecnico(authentication)) {
            throw new AccessDeniedException("A consulta deste chamado não é permitida.");
        }
        return chamado;
    }

    @PutMapping("/{id}/resolver")
    public Chamado resolverChamado(
            @PathVariable("id") String id,
            @Valid @RequestBody ResolverChamadoRequest request,
            Authentication authentication) {
        return chamadoService.resolverChamado(id, request.getSolucao(), authentication.getName());
    }

    private boolean ehTecnico(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_TECNICO"));
    }
}
