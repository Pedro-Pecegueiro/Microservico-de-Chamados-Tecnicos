package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClienteWebController {

    private final ClienteService clienteService;

    public ClienteWebController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/clientes")
    public String listar(Model model) {
        preparar(model);
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("modoEdicao", false);
        return "clientes";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editar(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        return clienteService.buscarPorId(id).map(cliente -> {
            preparar(model);
            model.addAttribute("cliente", cliente);
            model.addAttribute("modoEdicao", true);
            return "clientes";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado.");
            return "redirect:/clientes";
        });
    }

    @PostMapping("/clientes/salvar")
    public String salvar(
            @Valid @ModelAttribute("cliente") Cliente cliente,
            BindingResult resultado,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            preparar(model);
            model.addAttribute("modoEdicao", cliente.getId() != null && !cliente.getId().isBlank());
            return "clientes";
        }
        cliente.setNome(cliente.getNome().trim());
        cliente.setTelefone(cliente.getTelefone() == null ? "" : cliente.getTelefone().trim());
        cliente.setSetor(cliente.getSetor().trim());
        clienteService.novoCliente(cliente);
        redirectAttributes.addFlashAttribute("sucesso", "Cliente salvo com sucesso.");
        return "redirect:/clientes";
    }

    @PostMapping("/clientes/excluir/{id}")
    public String excluir(@PathVariable String id, RedirectAttributes redirectAttributes) {
        if (clienteService.buscarPorId(id).isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado.");
        } else {
            clienteService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Cliente excluído.");
        }
        return "redirect:/clientes";
    }

    private void preparar(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
    }
}
