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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author maria
 */
@Entity
@Table(name = "paquete")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Paquete.findAll", query = "SELECT p FROM Paquete p"),
    @NamedQuery(name = "Paquete.findByIdPaquete", query = "SELECT p FROM Paquete p WHERE p.idPaquete = :idPaquete"),
    @NamedQuery(name = "Paquete.findByFechaF", query = "SELECT p FROM Paquete p WHERE p.fechaF = :fechaF"),
    @NamedQuery(name = "Paquete.findByFechaI", query = "SELECT p FROM Paquete p WHERE p.fechaI = :fechaI"),
    @NamedQuery(name = "Paquete.findByCostoTot", query = "SELECT p FROM Paquete p WHERE p.costoTot = :costoTot"),
    @NamedQuery(name = "Paquete.findByFormaPago", query = "SELECT p FROM Paquete p WHERE p.formaPago = :formaPago"),
    @NamedQuery(name = "Paquete.findByEstado", query = "SELECT p FROM Paquete p WHERE p.estado = :estado"),
    @NamedQuery(name = "Paquete.findByOrigen", query = "SELECT p FROM Paquete p WHERE p.origen = :origen"),
    @NamedQuery(name = "Paquete.findByPermitido", query = "SELECT p FROM Paquete p WHERE p.permitido = :permitido"),
    @NamedQuery(name = "Paquete.findByCanPersonas", query = "SELECT p FROM Paquete p WHERE p.canPersonas = :canPersonas")})
public class Paquete implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idPaquete")
    private Integer idPaquete;
    @Column(name = "fecha_F")
    @Temporal(TemporalType.DATE)
    private Date fechaF;
    @Column(name = "fecha_I")
    @Temporal(TemporalType.DATE)
    private Date fechaI;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "costo_tot")
    private Double costoTot;
    @Size(max = 45)
    @Column(name = "forma_pago")
    private String formaPago;
    @Column(name = "estado")
    private Boolean estado;
    @Size(max = 45)
    @Column(name = "origen")
    private String origen;
    @Column(name = "permitido")
    private Boolean permitido;
    @Column(name = "can_personas")
    private Integer canPersonas;
    @JoinTable(name = "tiene", joinColumns = {
        @JoinColumn(name = "Paquete_idPaquete", referencedColumnName = "idPaquete")}, inverseJoinColumns = {
        @JoinColumn(name = "componentes_codComponente", referencedColumnName = "codComponente")})
    @ManyToMany
    private List<Componentes> componentesList;
    @JoinColumn(name = "Usuarios_dni", referencedColumnName = "dni")
    @ManyToOne(optional = false)
    private Usuarios usuariosdni;

    public Paquete() {
    }

    public Paquete(Integer idPaquete) {
        this.idPaquete = idPaquete;
    }

    public Integer getIdPaquete() {
        return idPaquete;
    }

    public void setIdPaquete(Integer idPaquete) {
        this.idPaquete = idPaquete;
    }

    public Date getFechaF() {
        return fechaF;
    }

    public void setFechaF(Date fechaF) {
        this.fechaF = fechaF;
    }

    public Date getFechaI() {
        return fechaI;
    }

    public void setFechaI(Date fechaI) {
        this.fechaI = fechaI;
    }

    public Double getCostoTot() {
        return costoTot;
    }

    public void setCostoTot(Double costoTot) {
        this.costoTot = costoTot;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public Boolean getPermitido() {
        return permitido;
    }

    public void setPermitido(Boolean permitido) {
        this.permitido = permitido;
    }

    public Integer getCanPersonas() {
        return canPersonas;
    }

    public void setCanPersonas(Integer canPersonas) {
        this.canPersonas = canPersonas;
    }

    @XmlTransient
    public List<Componentes> getComponentesList() {
        return componentesList;
    }

    public void setComponentesList(List<Componentes> componentesList) {
        this.componentesList = componentesList;
    }

    public Usuarios getUsuariosdni() {
        return usuariosdni;
    }

    public void setUsuariosdni(Usuarios usuariosdni) {
        this.usuariosdni = usuariosdni;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPaquete != null ? idPaquete.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Paquete)) {
            return false;
        }
        Paquete other = (Paquete) object;
        if ((this.idPaquete == null && other.idPaquete != null) || (this.idPaquete != null && !this.idPaquete.equals(other.idPaquete))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Paquete[ idPaquete=" + idPaquete + " ]";
    }
    
}
