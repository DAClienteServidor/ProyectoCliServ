/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Controladores.exceptions.IllegalOrphanException;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import Entidades.Componentes;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Estadia;
import Entidades.Entretenimiento;
import Entidades.Pasajes;
import Entidades.Paquete;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JOptionPane;

/**
 *
 * @author maria
 */
public class ComponentesJpaController implements Serializable {

    public ComponentesJpaController() {
        try{
            this.emf = Persistence.createEntityManagerFactory("com.mycompany_proyectoCliServ_war_1.0-SNAPSHOTPU");
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error al conectar");
        } 
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Componentes componentes) throws PreexistingEntityException, Exception {
        if (componentes.getPaqueteList() == null) {
            componentes.setPaqueteList(new ArrayList<Paquete>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estadia estadia = componentes.getEstadia();
            if (estadia != null) {
                estadia = em.getReference(estadia.getClass(), estadia.getCod());
                componentes.setEstadia(estadia);
            }
            Entretenimiento entretenimiento = componentes.getEntretenimiento();
            if (entretenimiento != null) {
                entretenimiento = em.getReference(entretenimiento.getClass(), entretenimiento.getCod());
                componentes.setEntretenimiento(entretenimiento);
            }
            Pasajes pasajes = componentes.getPasajes();
            if (pasajes != null) {
                pasajes = em.getReference(pasajes.getClass(), pasajes.getCod());
                componentes.setPasajes(pasajes);
            }
            List<Paquete> attachedPaqueteList = new ArrayList<Paquete>();
            for (Paquete paqueteListPaqueteToAttach : componentes.getPaqueteList()) {
                paqueteListPaqueteToAttach = em.getReference(paqueteListPaqueteToAttach.getClass(), paqueteListPaqueteToAttach.getIdPaquete());
                attachedPaqueteList.add(paqueteListPaqueteToAttach);
            }
            componentes.setPaqueteList(attachedPaqueteList);
            em.persist(componentes);
            if (estadia != null) {
                Componentes oldComponentesOfEstadia = estadia.getComponentes();
                if (oldComponentesOfEstadia != null) {
                    oldComponentesOfEstadia.setEstadia(null);
                    oldComponentesOfEstadia = em.merge(oldComponentesOfEstadia);
                }
                estadia.setComponentes(componentes);
                estadia = em.merge(estadia);
            }
            if (entretenimiento != null) {
                Componentes oldComponentesOfEntretenimiento = entretenimiento.getComponentes();
                if (oldComponentesOfEntretenimiento != null) {
                    oldComponentesOfEntretenimiento.setEntretenimiento(null);
                    oldComponentesOfEntretenimiento = em.merge(oldComponentesOfEntretenimiento);
                }
                entretenimiento.setComponentes(componentes);
                entretenimiento = em.merge(entretenimiento);
            }
            if (pasajes != null) {
                Componentes oldComponentesOfPasajes = pasajes.getComponentes();
                if (oldComponentesOfPasajes != null) {
                    oldComponentesOfPasajes.setPasajes(null);
                    oldComponentesOfPasajes = em.merge(oldComponentesOfPasajes);
                }
                pasajes.setComponentes(componentes);
                pasajes = em.merge(pasajes);
            }
            for (Paquete paqueteListPaquete : componentes.getPaqueteList()) {
                paqueteListPaquete.getComponentesList().add(componentes);
                paqueteListPaquete = em.merge(paqueteListPaquete);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findComponentes(componentes.getCodComponente()) != null) {
                throw new PreexistingEntityException("Componentes " + componentes + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Componentes componentes) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Componentes persistentComponentes = em.find(Componentes.class, componentes.getCodComponente());
            Estadia estadiaOld = persistentComponentes.getEstadia();
            Estadia estadiaNew = componentes.getEstadia();
            Entretenimiento entretenimientoOld = persistentComponentes.getEntretenimiento();
            Entretenimiento entretenimientoNew = componentes.getEntretenimiento();
            Pasajes pasajesOld = persistentComponentes.getPasajes();
            Pasajes pasajesNew = componentes.getPasajes();
            List<Paquete> paqueteListOld = persistentComponentes.getPaqueteList();
            List<Paquete> paqueteListNew = componentes.getPaqueteList();
            List<String> illegalOrphanMessages = null;
            if (estadiaOld != null && !estadiaOld.equals(estadiaNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Estadia " + estadiaOld + " since its componentes field is not nullable.");
            }
            if (entretenimientoOld != null && !entretenimientoOld.equals(entretenimientoNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Entretenimiento " + entretenimientoOld + " since its componentes field is not nullable.");
            }
            if (pasajesOld != null && !pasajesOld.equals(pasajesNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Pasajes " + pasajesOld + " since its componentes field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (estadiaNew != null) {
                estadiaNew = em.getReference(estadiaNew.getClass(), estadiaNew.getCod());
                componentes.setEstadia(estadiaNew);
            }
            if (entretenimientoNew != null) {
                entretenimientoNew = em.getReference(entretenimientoNew.getClass(), entretenimientoNew.getCod());
                componentes.setEntretenimiento(entretenimientoNew);
            }
            if (pasajesNew != null) {
                pasajesNew = em.getReference(pasajesNew.getClass(), pasajesNew.getCod());
                componentes.setPasajes(pasajesNew);
            }
            List<Paquete> attachedPaqueteListNew = new ArrayList<Paquete>();
            for (Paquete paqueteListNewPaqueteToAttach : paqueteListNew) {
                paqueteListNewPaqueteToAttach = em.getReference(paqueteListNewPaqueteToAttach.getClass(), paqueteListNewPaqueteToAttach.getIdPaquete());
                attachedPaqueteListNew.add(paqueteListNewPaqueteToAttach);
            }
            paqueteListNew = attachedPaqueteListNew;
            componentes.setPaqueteList(paqueteListNew);
            componentes = em.merge(componentes);
            if (estadiaNew != null && !estadiaNew.equals(estadiaOld)) {
                Componentes oldComponentesOfEstadia = estadiaNew.getComponentes();
                if (oldComponentesOfEstadia != null) {
                    oldComponentesOfEstadia.setEstadia(null);
                    oldComponentesOfEstadia = em.merge(oldComponentesOfEstadia);
                }
                estadiaNew.setComponentes(componentes);
                estadiaNew = em.merge(estadiaNew);
            }
            if (entretenimientoNew != null && !entretenimientoNew.equals(entretenimientoOld)) {
                Componentes oldComponentesOfEntretenimiento = entretenimientoNew.getComponentes();
                if (oldComponentesOfEntretenimiento != null) {
                    oldComponentesOfEntretenimiento.setEntretenimiento(null);
                    oldComponentesOfEntretenimiento = em.merge(oldComponentesOfEntretenimiento);
                }
                entretenimientoNew.setComponentes(componentes);
                entretenimientoNew = em.merge(entretenimientoNew);
            }
            if (pasajesNew != null && !pasajesNew.equals(pasajesOld)) {
                Componentes oldComponentesOfPasajes = pasajesNew.getComponentes();
                if (oldComponentesOfPasajes != null) {
                    oldComponentesOfPasajes.setPasajes(null);
                    oldComponentesOfPasajes = em.merge(oldComponentesOfPasajes);
                }
                pasajesNew.setComponentes(componentes);
                pasajesNew = em.merge(pasajesNew);
            }
            for (Paquete paqueteListOldPaquete : paqueteListOld) {
                if (!paqueteListNew.contains(paqueteListOldPaquete)) {
                    paqueteListOldPaquete.getComponentesList().remove(componentes);
                    paqueteListOldPaquete = em.merge(paqueteListOldPaquete);
                }
            }
            for (Paquete paqueteListNewPaquete : paqueteListNew) {
                if (!paqueteListOld.contains(paqueteListNewPaquete)) {
                    paqueteListNewPaquete.getComponentesList().add(componentes);
                    paqueteListNewPaquete = em.merge(paqueteListNewPaquete);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = componentes.getCodComponente();
                if (findComponentes(id) == null) {
                    throw new NonexistentEntityException("The componentes with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Componentes componentes;
            try {
                componentes = em.getReference(Componentes.class, id);
                componentes.getCodComponente();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The componentes with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Estadia estadiaOrphanCheck = componentes.getEstadia();
            if (estadiaOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Componentes (" + componentes + ") cannot be destroyed since the Estadia " + estadiaOrphanCheck + " in its estadia field has a non-nullable componentes field.");
            }
            Entretenimiento entretenimientoOrphanCheck = componentes.getEntretenimiento();
            if (entretenimientoOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Componentes (" + componentes + ") cannot be destroyed since the Entretenimiento " + entretenimientoOrphanCheck + " in its entretenimiento field has a non-nullable componentes field.");
            }
            Pasajes pasajesOrphanCheck = componentes.getPasajes();
            if (pasajesOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Componentes (" + componentes + ") cannot be destroyed since the Pasajes " + pasajesOrphanCheck + " in its pasajes field has a non-nullable componentes field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Paquete> paqueteList = componentes.getPaqueteList();
            for (Paquete paqueteListPaquete : paqueteList) {
                paqueteListPaquete.getComponentesList().remove(componentes);
                paqueteListPaquete = em.merge(paqueteListPaquete);
            }
            em.remove(componentes);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Componentes> findComponentesEntities() {
        return findComponentesEntities(true, -1, -1);
    }

    public List<Componentes> findComponentesEntities(int maxResults, int firstResult) {
        return findComponentesEntities(false, maxResults, firstResult);
    }

    private List<Componentes> findComponentesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Componentes.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Componentes findComponentes(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Componentes.class, id);
        } finally {
            em.close();
        }
    }

    public int getComponentesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Componentes> rt = cq.from(Componentes.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
