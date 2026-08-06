package com.olalla.plantplan.entity;

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
@Table(name = "crops")
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer initialQuantity;

    private LocalDate transplantDate;
    private Integer harvestedQuantity;
    private LocalDate endDate;
    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_sheet_id", nullable = false)
    private CropSheet cropSheet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "garden_id", nullable = false)
    private Garden garden;

    public Crop() {
    }

    public Long getId() {
        return id;
    }

    public Integer getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public LocalDate getTransplantDate() {
        return transplantDate;
    }

    public void setTransplantDate(LocalDate transplantDate) {
        this.transplantDate = transplantDate;
    }

    public Integer getHarvestedQuantity() {
        return harvestedQuantity;
    }

    public void setHarvestedQuantity(Integer harvestedQuantity) {
        this.harvestedQuantity = harvestedQuantity;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public CropSheet getCropSheet() {
        return cropSheet;
    }

    public void setCropSheet(CropSheet cropSheet) {
        this.cropSheet = cropSheet;
    }

    public Garden getGarden() {
        return garden;
    }

    public void setGarden(Garden garden) {
        this.garden = garden;
    }
}
