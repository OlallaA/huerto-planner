package com.olalla.plantplan.entity;

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
@Table(name = "crop_sheets")
public class CropSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String variety;

    @Enumerated(EnumType.STRING)
    private Month sowingStartMonth;

    @Enumerated(EnumType.STRING)
    private Month sowingEndMonth;

    @Enumerated(EnumType.STRING)
    private Month transplantStartMonth;

    @Enumerated(EnumType.STRING)
    private Month transplantEndMonth;

    @Enumerated(EnumType.STRING)
    private Month harvestStartMonth;

    @Enumerated(EnumType.STRING)
    private Month harvestEndMonth;

    private Integer wateringFrequencyDays;

    @Enumerated(EnumType.STRING)
    private SunExposure sunExposure;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cropSheet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seed> seeds = new ArrayList<>();

    @OneToMany(mappedBy = "cropSheet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seedling> seedlings = new ArrayList<>();

    @OneToMany(mappedBy = "cropSheet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Crop> crops = new ArrayList<>();

    public CropSheet() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public Month getSowingStartMonth() {
        return sowingStartMonth;
    }

    public void setSowingStartMonth(Month sowingStartMonth) {
        this.sowingStartMonth = sowingStartMonth;
    }

    public Month getSowingEndMonth() {
        return sowingEndMonth;
    }

    public void setSowingEndMonth(Month sowingEndMonth) {
        this.sowingEndMonth = sowingEndMonth;
    }

    public Month getTransplantStartMonth() {
        return transplantStartMonth;
    }

    public void setTransplantStartMonth(Month transplantStartMonth) {
        this.transplantStartMonth = transplantStartMonth;
    }

    public Month getTransplantEndMonth() {
        return transplantEndMonth;
    }

    public void setTransplantEndMonth(Month transplantEndMonth) {
        this.transplantEndMonth = transplantEndMonth;
    }

    public Month getHarvestStartMonth() {
        return harvestStartMonth;
    }

    public void setHarvestStartMonth(Month harvestStartMonth) {
        this.harvestStartMonth = harvestStartMonth;
    }

    public Month getHarvestEndMonth() {
        return harvestEndMonth;
    }

    public void setHarvestEndMonth(Month harvestEndMonth) {
        this.harvestEndMonth = harvestEndMonth;
    }

    public Integer getWateringFrequencyDays() {
        return wateringFrequencyDays;
    }

    public void setWateringFrequencyDays(Integer wateringFrequencyDays) {
        this.wateringFrequencyDays = wateringFrequencyDays;
    }

    public SunExposure getSunExposure() {
        return sunExposure;
    }

    public void setSunExposure(SunExposure sunExposure) {
        this.sunExposure = sunExposure;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Seed> getSeeds() {
        return seeds;
    }

    public void setSeeds(List<Seed> seeds) {
        this.seeds = seeds;
    }

    public List<Seedling> getSeedlings() {
        return seedlings;
    }

    public void setSeedlings(List<Seedling> seedlings) {
        this.seedlings = seedlings;
    }

    public List<Crop> getCrops() {
        return crops;
    }

    public void setCrops(List<Crop> crops) {
        this.crops = crops;
    }
}
