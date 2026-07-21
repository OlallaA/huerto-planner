package com.olalla.huertoplanner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "planteles")
public class Plantel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer cantidadSembrada;

    private LocalDate fechaSiembra;
    private Integer cantidadTrasplantada;
    private LocalDate fechaTrasplante;
    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_cultivo_id", nullable = false)
    private FichaCultivo fichaCultivo;

    public Plantel() {
    }

    public Long getId() {
        return id;
    }

    public Integer getCantidadSembrada() {
        return cantidadSembrada;
    }

    public void setCantidadSembrada(Integer cantidadSembrada) {
        this.cantidadSembrada = cantidadSembrada;
    }

    public LocalDate getFechaSiembra() {
        return fechaSiembra;
    }

    public void setFechaSiembra(LocalDate fechaSiembra) {
        this.fechaSiembra = fechaSiembra;
    }

    public Integer getCantidadTrasplantada() {
        return cantidadTrasplantada;
    }

    public void setCantidadTrasplantada(Integer cantidadTrasplantada) {
        this.cantidadTrasplantada = cantidadTrasplantada;
    }

    public LocalDate getFechaTrasplante() {
        return fechaTrasplante;
    }

    public void setFechaTrasplante(LocalDate fechaTrasplante) {
        this.fechaTrasplante = fechaTrasplante;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public FichaCultivo getFichaCultivo() {
        return fichaCultivo;
    }

    public void setFichaCultivo(FichaCultivo fichaCultivo) {
        this.fichaCultivo = fichaCultivo;
    }
}
