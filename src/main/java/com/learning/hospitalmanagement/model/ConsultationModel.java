package com.learning.hospitalmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "consultation")
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "notes")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "patientid", referencedColumnName = "id")
    private PatientModel patientModel;
}
