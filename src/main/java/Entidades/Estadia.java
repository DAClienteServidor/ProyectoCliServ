/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author maria
 */
@Entity
@Table(name = "estadia")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Estadia.findAll", query = "SELECT e FROM Estadia e"),
    @NamedQuery(name = "Estadia.findByCod", query = "SELECT e FROM Estadia e WHERE e.cod = :cod"),
    @NamedQuery(name = "Estadia.findByCiudad", query = "SELECT e FROM Estadia e WHERE e.ciudad = :ciudad"),
    @NamedQuery(name = "Estadia.findByNombre", query = "SELECT e FROM Estadia e WHERE e.nombre = :nombre"),
    @NamedQuery(name = "Estadia.findByDireccion", query = "SELECT e FROM Estadia e WHERE e.direccion = :direccion"),
    @NamedQuery(name = "Estadia.findByEstrellas", query = "SELECT e FROM Estadia e WHERE e.estrellas = :estrellas"),
    @NamedQuery(name = "Estadia.findByTelefono", query = "SELECT e FROM Estadia e WHERE e.telefono = :telefono")})
public class Estadia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "cod")
    private Integer cod;
    @Size(max = 45)
    @Column(name = "ciudad")
    private String ciudad;
    @Size(max = 45)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 45)
    @Column(name = "direccion")
    private String direccion;
    @Column(name = "estrellas")
    private Integer estrellas;
    @Size(max = 45)
    @Column(name = "telefono")
    private String telefono;
    @JoinColumn(name = "cod", referencedColumnName = "codComponente", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Componentes componentes;

    public Estadia() {
    }

    public Estadia(Integer cod) {
        this.cod = cod;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getEstrellas() {
        return estrellas;
    }

    public void setEstrellas(Integer estrellas) {
        this.estrellas = estrellas;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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
        if (!(object instanceof Estadia)) {
            return false;
        }
        Estadia other = (Estadia) object;
        if ((this.cod == null && other.cod != null) || (this.cod != null && !this.cod.equals(other.cod))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Estadia[ cod=" + cod + " ]";
    }
    
}
