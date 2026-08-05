package com.olalla.huertoplanner.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fichas_cultivo")
public class FichaCultivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String variedad;

    @Enumerated(EnumType.STRING)
    private Month mesInicioSiembra;

    @Enumerated(EnumType.STRING)
    private Month mesFinSiembra;

    @Enumerated(EnumType.STRING)
    private Month mesInicioTrasplante;

    @Enumerated(EnumType.STRING)
    private Month mesFinTrasplante;

    @Enumerated(EnumType.STRING)
    private Month mesInicioCosecha;

    @Enumerated(EnumType.STRING)
    private Month mesFinCosecha;

    private Integer frecuenciaRiego;

    @Enumerated(EnumType.STRING)
    private ExposicionSolar exposicionSolar;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "fichaCultivo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Semilla> semillas = new ArrayList<>();

    @OneToMany(mappedBy = "fichaCultivo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plantel> planteles = new ArrayList<>();

    @OneToMany(mappedBy = "fichaCultivo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cultivo> cultivos = new ArrayList<>();

    public FichaCultivo() {
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getVariedad() {
        return variedad;
    }

    public void setVariedad(String variedad) {
        this.variedad = variedad;
    }

    public Month getMesInicioSiembra() {
        return mesInicioSiembra;
    }

    public void setMesInicioSiembra(Month mesInicioSiembra) {
        this.mesInicioSiembra = mesInicioSiembra;
    }

    public Month getMesFinSiembra() {
        return mesFinSiembra;
    }

    public void setMesFinSiembra(Month mesFinSiembra) {
        this.mesFinSiembra = mesFinSiembra;
    }

    public Month getMesInicioTrasplante() {
        return mesInicioTrasplante;
    }

    public void setMesInicioTrasplante(Month mesInicioTrasplante) {
        this.mesInicioTrasplante = mesInicioTrasplante;
    }

    public Month getMesFinTrasplante() {
        return mesFinTrasplante;
    }

    public void setMesFinTrasplante(Month mesFinTrasplante) {
        this.mesFinTrasplante = mesFinTrasplante;
    }

    public Month getMesInicioCosecha() {
        return mesInicioCosecha;
    }

    public void setMesInicioCosecha(Month mesInicioCosecha) {
        this.mesInicioCosecha = mesInicioCosecha;
    }

    public Month getMesFinCosecha() {
        return mesFinCosecha;
    }

    public void setMesFinCosecha(Month mesFinCosecha) {
        this.mesFinCosecha = mesFinCosecha;
    }

    public Integer getFrecuenciaRiego() {
        return frecuenciaRiego;
    }

    public void setFrecuenciaRiego(Integer frecuenciaRiego) {
        this.frecuenciaRiego = frecuenciaRiego;
    }

    public ExposicionSolar getExposicionSolar() {
        return exposicionSolar;
    }

    public void setExposicionSolar(ExposicionSolar exposicionSolar) {
        this.exposicionSolar = exposicionSolar;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Semilla> getSemillas() {
        return semillas;
    }

    public void setSemillas(List<Semilla> semillas) {
        this.semillas = semillas;
    }

    public List<Plantel> getPlanteles() {
        return planteles;
    }

    public void setPlanteles(List<Plantel> planteles) {
        this.planteles = planteles;
    }

    public List<Cultivo> getCultivos() {
        return cultivos;
    }

    public void setCultivos(List<Cultivo> cultivos) {
        this.cultivos = cultivos;
    }
}
