package com.empresa.contabilidade.alvara_monitor.entities;

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
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate expLicenseFiredept;
    private LocalDate expLicenseSurveillance;
    private LocalDate expLicensePolice;
    private LocalDate expLicenseOperating;
}
