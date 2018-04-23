/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author maria
 */
@Entity
@Table(name = "componentes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Componentes.findAll", query = "SELECT c FROM Componentes c"),
    @NamedQuery(name = "Componentes.findByCodComponente", query = "SELECT c FROM Componentes c WHERE c.codComponente = :codComponente"),
    @NamedQuery(name = "Componentes.findByCosto", query = "SELECT c FROM Componentes c WHERE c.costo = :costo"),
    @NamedQuery(name = "Componentes.findByFecha", query = "SELECT c FROM Componentes c WHERE c.fecha = :fecha")})
public class Componentes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "codComponente")
    private Integer codComponente;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "costo")
    private Double costo;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @ManyToMany(mappedBy = "componentesList")
    private List<Paquete> paqueteList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "componentes")
    private Estadia estadia;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "componentes")
    private Entretenimiento entretenimiento;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "componentes")
    private Pasajes pasajes;

    public Componentes() {
    }

    public Componentes(Integer codComponente) {
        this.codComponente = codComponente;
    }

    public Integer getCodComponente() {
        return codComponente;
    }

    public void setCodComponente(Integer codComponente) {
        this.codComponente = codComponente;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @XmlTransient
    public List<Paquete> getPaqueteList() {
        return paqueteList;
    }

    public void setPaqueteList(List<Paquete> paqueteList) {
        this.paqueteList = paqueteList;
    }

    public Estadia getEstadia() {
        return estadia;
    }

    public void setEstadia(Estadia estadia) {
        this.estadia = estadia;
    }

    public Entretenimiento getEntretenimiento() {
        return entretenimiento;
    }

    public void setEntretenimiento(Entretenimiento entretenimiento) {
        this.entretenimiento = entretenimiento;
    }

    public Pasajes getPasajes() {
        return pasajes;
    }

    public void setPasajes(Pasajes pasajes) {
        this.pasajes = pasajes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codComponente != null ? codComponente.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Componentes)) {
            return false;
        }
        Componentes other = (Componentes) object;
        if ((this.codComponente == null && other.codComponente != null) || (this.codComponente != null && !this.codComponente.equals(other.codComponente))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Componentes[ codComponente=" + codComponente + " ]";
    }
    
}
