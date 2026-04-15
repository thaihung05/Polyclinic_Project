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
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "medicines")
@NamedQueries({
    @NamedQuery(name = "Medicines.findAll", query = "SELECT m FROM Medicines m"),
    @NamedQuery(name = "Medicines.findById", query = "SELECT m FROM Medicines m WHERE m.id = :id"),
    @NamedQuery(name = "Medicines.findByCode", query = "SELECT m FROM Medicines m WHERE m.code = :code"),
    @NamedQuery(name = "Medicines.findByName", query = "SELECT m FROM Medicines m WHERE m.name = :name"),
    @NamedQuery(name = "Medicines.findByGenericName", query = "SELECT m FROM Medicines m WHERE m.genericName = :genericName"),
    @NamedQuery(name = "Medicines.findByCategory", query = "SELECT m FROM Medicines m WHERE m.category = :category"),
    @NamedQuery(name = "Medicines.findByUnit", query = "SELECT m FROM Medicines m WHERE m.unit = :unit"),
    @NamedQuery(name = "Medicines.findByConcentration", query = "SELECT m FROM Medicines m WHERE m.concentration = :concentration"),
    @NamedQuery(name = "Medicines.findByManufacturer", query = "SELECT m FROM Medicines m WHERE m.manufacturer = :manufacturer"),
    @NamedQuery(name = "Medicines.findByStockQuantity", query = "SELECT m FROM Medicines m WHERE m.stockQuantity = :stockQuantity"),
    @NamedQuery(name = "Medicines.findByExpiryDate", query = "SELECT m FROM Medicines m WHERE m.expiryDate = :expiryDate"),
    @NamedQuery(name = "Medicines.findByPrice", query = "SELECT m FROM Medicines m WHERE m.price = :price"),
    @NamedQuery(name = "Medicines.findByIsActive", query = "SELECT m FROM Medicines m WHERE m.isActive = :isActive")})
public class Medicines implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "code")
    private String code;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;
    @Size(max = 255)
    @Column(name = "generic_name")
    private String genericName;
    @Size(max = 100)
    @Column(name = "category")
    private String category;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "unit")
    private String unit;
    @Size(max = 100)
    @Column(name = "concentration")
    private String concentration;
    @Size(max = 255)
    @Column(name = "manufacturer")
    private String manufacturer;
    @Basic(optional = false)
    @NotNull
    @Column(name = "stock_quantity")
    private int stockQuantity;
    @Column(name = "expiry_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private BigDecimal price;
    @Basic(optional = false)
    @NotNull
    @Column(name = "is_active")
    private boolean isActive;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "medicineId")
    private Collection<PrescriptionItems> prescriptionItemsCollection;

    public Medicines() {
    }

    public Medicines(Integer id) {
        this.id = id;
    }

    public Medicines(Integer id, String code, String name, String unit, int stockQuantity, BigDecimal price, boolean isActive) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.isActive = isActive;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Collection<PrescriptionItems> getPrescriptionItemsCollection() {
        return prescriptionItemsCollection;
    }

    public void setPrescriptionItemsCollection(Collection<PrescriptionItems> prescriptionItemsCollection) {
        this.prescriptionItemsCollection = prescriptionItemsCollection;
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
        if (!(object instanceof Medicines)) {
            return false;
        }
        Medicines other = (Medicines) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pkdk.pojo.Medicines[ id=" + id + " ]";
    }
    
}
