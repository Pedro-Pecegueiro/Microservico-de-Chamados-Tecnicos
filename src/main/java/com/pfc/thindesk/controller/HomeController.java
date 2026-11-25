package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.service.ChamadoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class HomeController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.of("America/Sao_Paulo"));

    private final ChamadoService chamadoService;
    public HomeController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public ModelAndView home(Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        List<Chamado> chamados = admin
                ? chamadoService.listarChamados()
                : chamadoService.listarChamadosDoUsuario(authentication.getName());
        ModelAndView modelAndView = new ModelAndView("layout");
        modelAndView.addObject("totalChamados", chamados.size());
        modelAndView.addObject("chamadosAbertos", chamados.stream()
                .filter(chamado -> "ABERTO".equalsIgnoreCase(chamado.getStatus()))
                .count());
        modelAndView.addObject("chamadosResolvidos", chamados.stream()
                .filter(chamado -> "RESOLVIDO".equalsIgnoreCase(chamado.getStatus()))
                .count());
        modelAndView.addObject("chamadosRecentes", chamados.stream()
                .limit(5)
                .map(chamado -> new ChamadoDashboard(
                        chamado.getTitulo(),
                        chamado.getDescricao(),
                        chamado.getSolicitante(),
                        formatarData(chamado.getDataAbertura()),
                        chamado.getStatus()))
                .toList());
        return modelAndView;
    }

    private static String formatarData(Instant data) {
        return data == null ? "—" : FORMATO_DATA.format(data);
    }

    private record ChamadoDashboard(
            String titulo,
            String descricao,
            String solicitante,
            String dataAbertura,
            String status) {
    }
}
