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
@Table(name = "cultivos")
public class Cultivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer cantidadInicial;

    private LocalDate fechaTrasplante;
    private Integer cantidadCosechada;
    private LocalDate fechaFinCultivo;
    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_cultivo_id", nullable = false)
    private FichaCultivo fichaCultivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "huerto_id", nullable = false)
    private Huerto huerto;

    public Cultivo() {
    }

    public Long getId() {
        return id;
    }

    public Integer getCantidadInicial() {
        return cantidadInicial;
    }

    public void setCantidadInicial(Integer cantidadInicial) {
        this.cantidadInicial = cantidadInicial;
    }

    public LocalDate getFechaTrasplante() {
        return fechaTrasplante;
    }

    public void setFechaTrasplante(LocalDate fechaTrasplante) {
        this.fechaTrasplante = fechaTrasplante;
    }

    public Integer getCantidadCosechada() {
        return cantidadCosechada;
    }

    public void setCantidadCosechada(Integer cantidadCosechada) {
        this.cantidadCosechada = cantidadCosechada;
    }

    public LocalDate getFechaFinCultivo() {
        return fechaFinCultivo;
    }

    public void setFechaFinCultivo(LocalDate fechaFinCultivo) {
        this.fechaFinCultivo = fechaFinCultivo;
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

    public Huerto getHuerto() {
        return huerto;
    }

    public void setHuerto(Huerto huerto) {
        this.huerto = huerto;
    }
}
