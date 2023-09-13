package com.learning.hospitalmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "appointment")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "patientid", referencedColumnName = "id")
    private PatientModel patientModel;

    @ManyToOne
    @JoinColumn(name = "providerid", referencedColumnName = "id")
    private ProviderModel providerModel;

    @Column(name = "date")
    private String date;

    @Column(name = "time")
    private String time;

    @Column(name = "status")
    private Integer status;

    @Column(name = "notes")
    private  String notes;

}
