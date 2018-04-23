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
@Table(name = "pasajes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pasajes.findAll", query = "SELECT p FROM Pasajes p"),
    @NamedQuery(name = "Pasajes.findByCod", query = "SELECT p FROM Pasajes p WHERE p.cod = :cod"),
    @NamedQuery(name = "Pasajes.findByTipo", query = "SELECT p FROM Pasajes p WHERE p.tipo = :tipo"),
    @NamedQuery(name = "Pasajes.findByHora", query = "SELECT p FROM Pasajes p WHERE p.hora = :hora"),
    @NamedQuery(name = "Pasajes.findByOrigen", query = "SELECT p FROM Pasajes p WHERE p.origen = :origen"),
    @NamedQuery(name = "Pasajes.findByDestino", query = "SELECT p FROM Pasajes p WHERE p.destino = :destino")})
public class Pasajes implements Serializable {

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
    @Column(name = "origen")
    private String origen;
    @Size(max = 45)
    @Column(name = "destino")
    private String destino;
    @JoinColumn(name = "cod", referencedColumnName = "codComponente", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Componentes componentes;

    public Pasajes() {
    }

    public Pasajes(Integer cod) {
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

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
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
        if (!(object instanceof Pasajes)) {
            return false;
        }
        Pasajes other = (Pasajes) object;
        if ((this.cod == null && other.cod != null) || (this.cod != null && !this.cod.equals(other.cod))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Pasajes[ cod=" + cod + " ]";
    }
    
}
