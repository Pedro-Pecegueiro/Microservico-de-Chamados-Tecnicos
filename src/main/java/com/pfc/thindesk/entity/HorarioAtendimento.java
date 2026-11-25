package com.pfc.thindesk.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "horariosAtendimento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioAtendimento {

    @Id
    private String id;

    @NotBlank(message = "Informe o setor.")
    @Size(max = 80, message = "O setor deve ter no máximo 80 caracteres.")
    private String setor;

    @NotBlank(message = "Informe o dia da semana.")
    @Size(max = 30, message = "O dia deve ter no máximo 30 caracteres.")
    private String diaSemana;

    @NotBlank(message = "Informe o horário de início.")
    private String horarioInicio;

    @NotBlank(message = "Informe o horário de término.")
    private String horarioFim;
}