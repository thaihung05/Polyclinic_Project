/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "prescription_items")
@NamedQueries({
    @NamedQuery(name = "PrescriptionItems.findAll", query = "SELECT p FROM PrescriptionItems p"),
    @NamedQuery(name = "PrescriptionItems.findById", query = "SELECT p FROM PrescriptionItems p WHERE p.id = :id"),
    @NamedQuery(name = "PrescriptionItems.findByQuantity", query = "SELECT p FROM PrescriptionItems p WHERE p.quantity = :quantity"),
    @NamedQuery(name = "PrescriptionItems.findByDosage", query = "SELECT p FROM PrescriptionItems p WHERE p.dosage = :dosage"),
    @NamedQuery(name = "PrescriptionItems.findByDurationDays", query = "SELECT p FROM PrescriptionItems p WHERE p.durationDays = :durationDays"),
    @NamedQuery(name = "PrescriptionItems.findByInstructions", query = "SELECT p FROM PrescriptionItems p WHERE p.instructions = :instructions"),
    @NamedQuery(name = "PrescriptionItems.findByUnitPrice", query = "SELECT p FROM PrescriptionItems p WHERE p.unitPrice = :unitPrice")})
public class PrescriptionItems implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "quantity")
    private int quantity;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "dosage")
    private String dosage;
    @Basic(optional = false)
    @NotNull
    @Column(name = "duration_days")
    private int durationDays;
    @Size(max = 500)
    @Column(name = "instructions")
    private String instructions;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    @JoinColumn(name = "medicine_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Medicines medicineId;
    @JoinColumn(name = "prescription_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    @JsonIgnore
    private Prescriptions prescriptionId;

    public PrescriptionItems() {
    }

    public PrescriptionItems(Integer id) {
        this.id = id;
    }

    public PrescriptionItems(Integer id, int quantity, String dosage, int durationDays, BigDecimal unitPrice) {
        this.id = id;
        this.quantity = quantity;
        this.dosage = dosage;
        this.durationDays = durationDays;
        this.unitPrice = unitPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Medicines getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Medicines medicineId) {
        this.medicineId = medicineId;
    }

    public Prescriptions getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Prescriptions prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PrescriptionItems)) {
            return false;
        }
        PrescriptionItems other = (PrescriptionItems) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pkdk.pojo.PrescriptionItems[ id=" + id + " ]";
    }
    
}
