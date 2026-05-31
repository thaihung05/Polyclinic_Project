/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "prescriptions")
@NamedQueries({
    @NamedQuery(name = "Prescriptions.findAll", query = "SELECT p FROM Prescriptions p"),
    @NamedQuery(name = "Prescriptions.findById", query = "SELECT p FROM Prescriptions p WHERE p.id = :id"),
    @NamedQuery(name = "Prescriptions.findByNgayTao", query = "SELECT p FROM Prescriptions p WHERE p.ngayTao = :ngayTao"),
    @NamedQuery(name = "Prescriptions.findByNote", query = "SELECT p FROM Prescriptions p WHERE p.note = :note")})
public class Prescriptions implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "ngay_tao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayTao;
    @Size(max = 250)
    @Column(name = "note")
    private String note;
    @Column(name = "is_dispensed", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDispensed = false;
    @JsonIgnoreProperties({"patientId","doctorId","prescriptionId"})
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "prescriptionId")
    private PrescriptionReservations prescriptionReservations;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JsonIgnoreProperties({"prescriptionId"})
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "prescriptionId", fetch = FetchType.EAGER)
    private Collection<PrescriptionItems> prescriptionItemsCollection;
    @JoinColumn(name = "medical_record_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private MedicalRecords medicalRecordId;
    @Column(name = "is_paid", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isPaid = false;
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    @ManyToOne(optional = true)
    private Payments paymentId;

    public Prescriptions() {
    }

    public Prescriptions(Integer id) {
        this.id = id;
    }

    public Prescriptions(Integer id, Date ngayTao) {
        this.id = id;
        this.ngayTao = ngayTao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }


    public Collection<PrescriptionItems> getPrescriptionItemsCollection() {
        return prescriptionItemsCollection;
    }

    public void setPrescriptionItemsCollection(Collection<PrescriptionItems> prescriptionItemsCollection) {
        this.prescriptionItemsCollection = prescriptionItemsCollection;
    }

    public MedicalRecords getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(MedicalRecords medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public Payments getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Payments paymentId) {
        this.paymentId = paymentId;
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
        if (!(object instanceof Prescriptions)) {
            return false;
        }
        Prescriptions other = (Prescriptions) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pkdk.pojo.Prescriptions[ id=" + id + " ]";
    }

    /**
     * @return the isDispensed
     */
    public Boolean getIsDispensed() {
        return isDispensed;
    }

    /**
     * @param isDispensed the isDispensed to set
     */
    public void setIsDispensed(Boolean isDispensed) {
        this.isDispensed = isDispensed;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public PrescriptionReservations getPrescriptionReservations() {
        return prescriptionReservations;
    }

    public void setPrescriptionReservations(PrescriptionReservations prescriptionReservations) {
        this.prescriptionReservations = prescriptionReservations;
    }

}
