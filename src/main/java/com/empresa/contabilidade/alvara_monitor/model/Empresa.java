package com.empresa.contabilidade.alvara_monitor.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "empresas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private LocalDate vencBombeiros;
    private LocalDate vencVigilancia;
    private LocalDate vencPolicia;
    private LocalDate vencFuncionamento;
}
