/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author maria
 */
@Entity
@Table(name = "entretenimiento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Entretenimiento.findAll", query = "SELECT e FROM Entretenimiento e"),
    @NamedQuery(name = "Entretenimiento.findByCod", query = "SELECT e FROM Entretenimiento e WHERE e.cod = :cod"),
    @NamedQuery(name = "Entretenimiento.findByTipo", query = "SELECT e FROM Entretenimiento e WHERE e.tipo = :tipo"),
    @NamedQuery(name = "Entretenimiento.findByHora", query = "SELECT e FROM Entretenimiento e WHERE e.hora = :hora"),
    @NamedQuery(name = "Entretenimiento.findByDireccion", query = "SELECT e FROM Entretenimiento e WHERE e.direccion = :direccion"),
    @NamedQuery(name = "Entretenimiento.findByDescripcion", query = "SELECT e FROM Entretenimiento e WHERE e.descripcion = :descripcion")})
public class Entretenimiento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod")
    private Integer cod;
    @Size(max = 45)
    @Column(name = "tipo")
    private String tipo;
    @Column(name = "hora")
    @Temporal(TemporalType.TIME)
    private Date hora;
    @Size(max = 45)
    @Column(name = "direccion")
    private String direccion;
    @Size(max = 100)
    @Column(name = "descripcion")
    private String descripcion;
    @JoinColumn(name = "cod", referencedColumnName = "codComponente", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Componentes componentes;

    public Entretenimiento() {
    }

    public Entretenimiento(Integer cod) {
        this.cod = cod;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getHora() {
        return hora;
    }

    public void setHora(Date hora) {
        this.hora = hora;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Componentes getComponentes() {
        return componentes;
    }

    public void setComponentes(Componentes componentes) {
        this.componentes = componentes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cod != null ? cod.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Entretenimiento)) {
            return false;
        }
        Entretenimiento other = (Entretenimiento) object;
        if ((this.cod == null && other.cod != null) || (this.cod != null && !this.cod.equals(other.cod))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Entretenimiento[ cod=" + cod + " ]";
    }
    
}
