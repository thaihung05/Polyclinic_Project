/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.pojo;

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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "lab_results")
@NamedQueries({
    @NamedQuery(name = "LabResults.findAll", query = "SELECT l FROM LabResults l"),
    @NamedQuery(name = "LabResults.findById", query = "SELECT l FROM LabResults l WHERE l.id = :id"),
    @NamedQuery(name = "LabResults.findByTestName", query = "SELECT l FROM LabResults l WHERE l.testName = :testName"),
    @NamedQuery(name = "LabResults.findByTestCode", query = "SELECT l FROM LabResults l WHERE l.testCode = :testCode"),
    @NamedQuery(name = "LabResults.findByResult", query = "SELECT l FROM LabResults l WHERE l.result = :result"),
    @NamedQuery(name = "LabResults.findByUnit", query = "SELECT l FROM LabResults l WHERE l.unit = :unit"),
    @NamedQuery(name = "LabResults.findByIsAbnormal", query = "SELECT l FROM LabResults l WHERE l.isAbnormal = :isAbnormal"),
    @NamedQuery(name = "LabResults.findByPerformedAt", query = "SELECT l FROM LabResults l WHERE l.performedAt = :performedAt"),
    @NamedQuery(name = "LabResults.findByNgayTao", query = "SELECT l FROM LabResults l WHERE l.ngayTao = :ngayTao")})
public class LabResults implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "test_name")
    private String testName;
    @Size(max = 50)
    @Column(name = "test_code")
    private String testCode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "result")
    private String result;
    @Size(max = 50)
    @Column(name = "unit")
    private String unit;
    @Column(name = "is_abnormal")
    private Boolean isAbnormal;
    @Column(name = "performed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date performedAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ngay_tao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayTao;
    @JoinColumn(name = "appointment_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Appointments appointmentId;

    public LabResults() {
    }

    public LabResults(Integer id) {
        this.id = id;
    }

    public LabResults(Integer id, String testName, String result, Date ngayTao) {
        this.id = id;
        this.testName = testName;
        this.result = result;
        this.ngayTao = ngayTao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getIsAbnormal() {
        return isAbnormal;
    }

    public void setIsAbnormal(Boolean isAbnormal) {
        this.isAbnormal = isAbnormal;
    }

    public Date getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(Date performedAt) {
        this.performedAt = performedAt;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Appointments getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Appointments appointmentId) {
        this.appointmentId = appointmentId;
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
        if (!(object instanceof LabResults)) {
            return false;
        }
        LabResults other = (LabResults) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pkdk.pojo.LabResults[ id=" + id + " ]";
    }
    
}
