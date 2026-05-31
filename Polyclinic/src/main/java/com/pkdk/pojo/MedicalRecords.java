/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
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
@Table(name = "medical_records")
@NamedQueries({
    @NamedQuery(name = "MedicalRecords.findAll", query = "SELECT m FROM MedicalRecords m"),
    @NamedQuery(name = "MedicalRecords.findById", query = "SELECT m FROM MedicalRecords m WHERE m.id = :id"),
    @NamedQuery(name = "MedicalRecords.findByChiefComplaint", query = "SELECT m FROM MedicalRecords m WHERE m.chiefComplaint = :chiefComplaint"),
    @NamedQuery(name = "MedicalRecords.findByDiagnosis", query = "SELECT m FROM MedicalRecords m WHERE m.diagnosis = :diagnosis"),
    @NamedQuery(name = "MedicalRecords.findByTreatmentPlan", query = "SELECT m FROM MedicalRecords m WHERE m.treatmentPlan = :treatmentPlan"),
    @NamedQuery(name = "MedicalRecords.findByFollowUpDate", query = "SELECT m FROM MedicalRecords m WHERE m.followUpDate = :followUpDate"),
    @NamedQuery(name = "MedicalRecords.findByNotes", query = "SELECT m FROM MedicalRecords m WHERE m.notes = :notes")})
public class MedicalRecords implements Serializable {

    @Size(max = 500)
    @Column(name = "chief_complaint")
    private String chiefComplaint;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "diagnosis")
    private String diagnosis;
    @Size(max = 500)
    @Column(name = "treatment_plan")
    private String treatmentPlan;
    @Size(max = 200)
    @Column(name = "notes")
    private String notes;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "follow_up_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date followUpDate;
    @JoinColumn(name = "appointment_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Appointments appointmentId;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "medicalRecordId")
    private Collection<Prescriptions> prescriptionsCollection;

    public MedicalRecords() {
    }

    public MedicalRecords(Integer id) {
        this.id = id;
    }

    public MedicalRecords(Integer id, String diagnosis) {
        this.id = id;
        this.diagnosis = diagnosis;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }


    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public Date getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(Date followUpDate) {
        this.followUpDate = followUpDate;
    }


    public Appointments getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Appointments appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Collection<Prescriptions> getPrescriptionsCollection() {
        return prescriptionsCollection;
    }

    public void setPrescriptionsCollection(Collection<Prescriptions> prescriptionsCollection) {
        this.prescriptionsCollection = prescriptionsCollection;
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
        if (!(object instanceof MedicalRecords)) {
            return false;
        }
        MedicalRecords other = (MedicalRecords) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pkdk.pojo.MedicalRecords[ id=" + id + " ]";
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
}
