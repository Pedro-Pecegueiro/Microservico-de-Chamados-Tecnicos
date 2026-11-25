package com.pfc.thindesk.controller;

import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.service.HorarioAtendimentoService;
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

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/ajustes-horarios")
public class HorarioAtendimentoController {

    private final HorarioAtendimentoService horarioAtendimentoService;

    public HorarioAtendimentoController(HorarioAtendimentoService horarioAtendimentoService) {
        this.horarioAtendimentoService = horarioAtendimentoService;
    }

    @GetMapping
    public String listar(Model model) {
        prepararLista(model);
        model.addAttribute("novoHorario", new HorarioAtendimento());
        model.addAttribute("modoEdicao", false);
        return "ajustes-horarios";
    }

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable String id,
            Model model,
            RedirectAttributes redirectAttributes) {
        return horarioAtendimentoService.buscarPorId(id)
                .map(horario -> {
                    prepararLista(model);
                    model.addAttribute("novoHorario", horario);
                    model.addAttribute("modoEdicao", true);
                    return "ajustes-horarios";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("erro", "Horário não encontrado.");
                    return "redirect:/ajustes-horarios";
                });
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("novoHorario") HorarioAtendimento horario,
            BindingResult resultado,
            Model model,
            RedirectAttributes redirectAttributes) {
        validarIntervalo(horario, resultado);
        if (resultado.hasErrors()) {
            prepararLista(model);
            model.addAttribute("modoEdicao", horario.getId() != null && !horario.getId().isBlank());
            return "ajustes-horarios";
        }

        horario.setSetor(horario.getSetor().trim());
        horario.setDiaSemana(horario.getDiaSemana().trim());
        horarioAtendimentoService.salvar(horario);
        redirectAttributes.addFlashAttribute("sucesso", "Horário salvo com sucesso.");
        return "redirect:/ajustes-horarios";
    }

    @PostMapping("/deletar/{id}")
    public String deletar(
            @PathVariable String id,
            RedirectAttributes redirectAttributes) {
        if (horarioAtendimentoService.buscarPorId(id).isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Horário não encontrado.");
        } else {
            horarioAtendimentoService.deletar(id);
            redirectAttributes.addFlashAttribute("sucesso", "Horário removido.");
        }
        return "redirect:/ajustes-horarios";
    }

    /**
     * Links antigos de exclusão não alteram dados via GET.
     */
    @GetMapping("/deletar/{id}")
    public String linkAntigoDeExclusao(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(
                "erro", "Use o botão Deletar na lista para confirmar a remoção.");
        return "redirect:/ajustes-horarios";
    }

    private void prepararLista(Model model) {
        model.addAttribute("horarios", horarioAtendimentoService.listarTodos());
    }

    private void validarIntervalo(HorarioAtendimento horario, BindingResult resultado) {
        if (horario.getHorarioInicio() == null || horario.getHorarioFim() == null
                || horario.getHorarioInicio().isBlank() || horario.getHorarioFim().isBlank()) {
            return;
        }
        try {
            LocalTime inicio = LocalTime.parse(horario.getHorarioInicio());
            LocalTime fim = LocalTime.parse(horario.getHorarioFim());
            if (!fim.isAfter(inicio)) {
                resultado.rejectValue("horarioFim", "horarioFim.invalido",
                        "O horário de término deve ser depois do horário de início.");
            }
        } catch (DateTimeParseException exception) {
            resultado.rejectValue("horarioFim", "horario.invalido",
                    "Informe horários válidos.");
        }
    }
}