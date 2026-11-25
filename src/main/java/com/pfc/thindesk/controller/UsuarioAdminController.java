package com.pfc.thindesk.controller;

import com.pfc.thindesk.dto.CriarContaRequest;
import com.pfc.thindesk.service.RegraChamadoException;
import com.pfc.thindesk.service.UsuarioContaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioAdminController {

    private final UsuarioContaService contaService;

    public UsuarioAdminController(UsuarioContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping
    public String listar(Model model) {
        prepararTela(model);
        return "usuarios";
    }

    @PostMapping
    public String criar(
            @Valid @ModelAttribute("form") CriarContaRequest form,
            BindingResult resultado,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            prepararLista(model);
            return "usuarios";
        }
        try {
            contaService.criarUsuario(form);
            redirectAttributes.addFlashAttribute("sucesso", "Conta de usuário criada.");
            return "redirect:/usuarios";
        } catch (RegraChamadoException exception) {
            resultado.rejectValue("usuario", "usuario.duplicado", exception.getMessage());
            prepararLista(model);
            return "usuarios";
        }
    }

    @PostMapping("/desativar/{id}")
    public String desativar(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            contaService.desativar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Conta desativada.");
        } catch (RegraChamadoException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/usuarios";
    }

    private void prepararTela(Model model) {
        model.addAttribute("form", new CriarContaRequest());
        prepararLista(model);
    }

    private void prepararLista(Model model) {
        model.addAttribute("contas", contaService.listar());
    }
}
