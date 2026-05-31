/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "prescription_reservations")
@NamedQueries({
    @NamedQuery(name = "PrescriptionReservations.findAll", query = "SELECT p FROM PrescriptionReservations p"),
    @NamedQuery(name = "PrescriptionReservations.findById", query = "SELECT p FROM PrescriptionReservations p WHERE p.id = :id"),
    @NamedQuery(name = "PrescriptionReservations.findByReservedAt", query = "SELECT p FROM PrescriptionReservations p WHERE p.reservedAt = :reservedAt"),
    @NamedQuery(name = "PrescriptionReservations.findByExpiresAt", query = "SELECT p FROM PrescriptionReservations p WHERE p.expiresAt = :expiresAt"),
    @NamedQuery(name = "PrescriptionReservations.findByIsPaid", query = "SELECT p FROM PrescriptionReservations p WHERE p.isPaid = :isPaid"),
    @NamedQuery(name = "PrescriptionReservations.findByIsExpired", query = "SELECT p FROM PrescriptionReservations p WHERE p.isExpired = :isExpired"),
    @NamedQuery(name = "PrescriptionReservations.findByIsDispensed", query = "SELECT p FROM PrescriptionReservations p WHERE p.isDispensed = :isDispensed")})
public class PrescriptionReservations implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "reserved_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reservedAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "expires_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;
    @Column(name = "is_paid", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isPaid = false;
    @Column(name = "is_expired", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isExpired = false;
    @Column(name = "is_dispensed", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDispensed = false;
    @JsonIgnoreProperties({"prescriptionReservationsCollection", "appointmentsCollection", "doctorSchedulesCollection"})
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Doctors doctorId;
    @JsonIgnoreProperties({"prescriptionReservationsCollection", "appointmentsCollection"})
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Patients patientId;
    @JsonIgnoreProperties({"medicalRecordId", "paymentId", "prescriptionReservations"})
    @JoinColumn(name = "prescription_id", referencedColumnName = "id")
    @OneToOne(optional = false)
    private Prescriptions prescriptionId;

    public PrescriptionReservations() {
    }

    public PrescriptionReservations(Integer id) {
        this.id = id;
    }

    public PrescriptionReservations(Integer id, Date reservedAt, Date expiresAt, boolean isPaid, boolean isExpired, boolean isDispensed) {
        this.id = id;
        this.reservedAt = reservedAt;
        this.expiresAt = expiresAt;
        this.isPaid = isPaid;
        this.isExpired = isExpired;
        this.isDispensed = isDispensed;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(Date reservedAt) {
        this.reservedAt = reservedAt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public Boolean getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(Boolean isExpired) {
        this.isExpired = isExpired;
    }

    public Boolean getIsDispensed() {
        return isDispensed;
    }

    public void setIsDispensed(Boolean isDispensed) {
        this.isDispensed = isDispensed;
    }

    public Doctors getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Doctors doctorId) {
        this.doctorId = doctorId;
    }

    public Patients getPatientId() {
        return patientId;
    }

    public void setPatientId(Patients patientId) {
        this.patientId = patientId;
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
        if (!(object instanceof PrescriptionReservations)) {
            return false;
        }
        PrescriptionReservations other = (PrescriptionReservations) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pkdk.pojo.PrescriptionReservations[ id=" + id + " ]";
    }
    
}
