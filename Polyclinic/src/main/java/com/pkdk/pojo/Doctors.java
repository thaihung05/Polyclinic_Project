/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.pojo;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "doctors")
@NamedQueries({
    @NamedQuery(name = "Doctors.findAll", query = "SELECT d FROM Doctors d"),
    @NamedQuery(name = "Doctors.findById", query = "SELECT d FROM Doctors d WHERE d.id = :id"),
    @NamedQuery(name = "Doctors.findByBio", query = "SELECT d FROM Doctors d WHERE d.bio = :bio"),
    @NamedQuery(name = "Doctors.findByConsultationFee", query = "SELECT d FROM Doctors d WHERE d.consultationFee = :consultationFee"),
    @NamedQuery(name = "Doctors.findByAvailableOnline", query = "SELECT d FROM Doctors d WHERE d.availableOnline = :availableOnline"),
    @NamedQuery(name = "Doctors.findByRating", query = "SELECT d FROM Doctors d WHERE d.rating = :rating")})
public class Doctors implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 500)
    @Column(name = "bio")
    private String bio;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "consultation_fee")
    private BigDecimal consultationFee;
    @Basic(optional = false)
    @NotNull
    @Column(name = "available_online")
    private boolean availableOnline;
    @Column(name = "rating")
    private BigDecimal rating;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "doctorId")
    private Collection<Appointments> appointmentsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "doctorId")
    private Collection<DoctorSchedules> doctorSchedulesCollection;
    @JoinColumn(name = "specialty_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Specialties specialtyId;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @OneToOne(optional = false)
    private Users userId;

    public Doctors() {
    }

    public Doctors(Integer id) {
        this.id = id;
    }

    public Doctors(Integer id, BigDecimal consultationFee, boolean availableOnline) {
        this.id = id;
        this.consultationFee = consultationFee;
        this.availableOnline = availableOnline;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public boolean getAvailableOnline() {
        return availableOnline;
    }

    public void setAvailableOnline(boolean availableOnline) {
        this.availableOnline = availableOnline;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Collection<Appointments> getAppointmentsCollection() {
        return appointmentsCollection;
    }

    public void setAppointmentsCollection(Collection<Appointments> appointmentsCollection) {
        this.appointmentsCollection = appointmentsCollection;
    }

    public Collection<DoctorSchedules> getDoctorSchedulesCollection() {
        return doctorSchedulesCollection;
    }

    public void setDoctorSchedulesCollection(Collection<DoctorSchedules> doctorSchedulesCollection) {
        this.doctorSchedulesCollection = doctorSchedulesCollection;
    }

    public Specialties getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(Specialties specialtyId) {
        this.specialtyId = specialtyId;
    }

    public Users getUserId() {
        return userId;
    }

    public void setUserId(Users userId) {
        this.userId = userId;
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
        if (!(object instanceof Doctors)) {
            return false;
        }
        Doctors other = (Doctors) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pkdk.pojo.Doctors[ id=" + id + " ]";
    }
    
}
