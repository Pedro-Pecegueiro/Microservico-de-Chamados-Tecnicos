package com.pfc.thindesk.controller;

import com.pfc.thindesk.dto.ChamadoFormulario;
import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.service.ChamadoNaoEncontradoException;
import com.pfc.thindesk.service.ChamadoService;
import com.pfc.thindesk.service.RegraChamadoException;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/chamados")
public class ChamadoWebController {

    private final ChamadoService chamadoService;

    public ChamadoWebController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    @GetMapping
    public String listar(Model model, Authentication authentication) {
        boolean admin = ehAdmin(authentication);
        model.addAttribute("admin", admin);
        model.addAttribute("chamados", admin
                ? chamadoService.listarChamados()
                : chamadoService.listarChamadosDoUsuario(authentication.getName()));
        return "chamados";
    }

    @GetMapping({"/novo", "/novo/"})
    public String novo(Model model) {
        model.addAttribute("form", new ChamadoFormulario());
        return "chamados/novo";
    }

    @PostMapping({"/novo", "/novo/"})
    public String criar(
            @Valid @ModelAttribute("form") ChamadoFormulario form,
            BindingResult resultado,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            return "chamados/novo";
        }

        chamadoService.abrirChamado(form.getTitulo(), form.getDescricao(), authentication.getName());
        redirectAttributes.addFlashAttribute("sucesso", "Chamado aberto com sucesso.");
        return "redirect:/chamados";
    }

    @GetMapping({"/editar/{id}", "/atualizar/{id}"})
    public String editar(
            @PathVariable String id,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Chamado chamado = chamadoService.buscarPorId(id);
            verificarAcesso(chamado, authentication);
            if (!"ABERTO".equalsIgnoreCase(chamado.getStatus())) {
                redirectAttributes.addFlashAttribute(
                        "erro", "Chamados resolvidos não podem ser alterados.");
                return "redirect:/chamados";
            }

            ChamadoFormulario form = new ChamadoFormulario();
            form.setTitulo(chamado.getTitulo());
            form.setDescricao(chamado.getDescricao());
            model.addAttribute("form", form);
            model.addAttribute("chamadoId", id);
            return "chamados/atualizar";
        } catch (ChamadoNaoEncontradoException exception) {
            redirectAttributes.addFlashAttribute("erro", "O chamado solicitado não foi encontrado.");
            return "redirect:/chamados";
        }
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(
            @PathVariable String id,
            @Valid @ModelAttribute("form") ChamadoFormulario form,
            BindingResult resultado,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            model.addAttribute("chamadoId", id);
            return "chamados/atualizar";
        }

        try {
            verificarAcesso(chamadoService.buscarPorId(id), authentication);
            chamadoService.atualizarChamado(id, form.getTitulo(), form.getDescricao());
            redirectAttributes.addFlashAttribute("sucesso", "Chamado atualizado com sucesso.");
        } catch (ChamadoNaoEncontradoException | RegraChamadoException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/chamados";
    }

    @PostMapping("/resolver/{id}")
    public String resolver(
            @PathVariable String id,
            @RequestParam String solucao,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (solucao == null || solucao.isBlank() || solucao.length() > 1000) {
            redirectAttributes.addFlashAttribute("erro", "Informe uma solução com até 1000 caracteres.");
            return "redirect:/chamados";
        }
        try {
            chamadoService.resolverChamado(id, solucao, authentication.getName());
            redirectAttributes.addFlashAttribute("sucesso", "Chamado resolvido com sucesso.");
        } catch (ChamadoNaoEncontradoException | RegraChamadoException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/chamados";
    }

    @PostMapping("/cancelar/{id}")
    public String cancelar(
            @PathVariable String id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            verificarAcesso(chamadoService.buscarPorId(id), authentication);
            chamadoService.cancelarChamado(id);
            redirectAttributes.addFlashAttribute("sucesso", "Chamado aberto cancelado.");
        } catch (ChamadoNaoEncontradoException | RegraChamadoException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/chamados";
    }

    /**
     * Compatibilidade com links antigos: um GET nunca exclui nem altera um chamado.
     */
    @GetMapping("/cancelar/{id}")
    public String cancelarLinkAntigo(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(
                "erro", "Para cancelar, use o botão da lista. Chamados resolvidos não podem ser alterados.");
        return "redirect:/chamados";
    }

    private void verificarAcesso(Chamado chamado, Authentication authentication) {
        if (!ehAdmin(authentication)) {
            throw new AccessDeniedException("Somente o ADMIN pode editar ou cancelar chamados.");
        }
    }

    private boolean ehAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
