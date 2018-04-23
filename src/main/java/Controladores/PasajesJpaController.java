/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Controladores.exceptions.IllegalOrphanException;
import Controladores.exceptions.NonexistentEntityException;
import Controladores.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Componentes;
import Entidades.Pasajes;
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
public class PasajesJpaController implements Serializable {

    public PasajesJpaController() {
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

    public void create(Pasajes pasajes) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        Componentes componentesOrphanCheck = pasajes.getComponentes();
        if (componentesOrphanCheck != null) {
            Pasajes oldPasajesOfComponentes = componentesOrphanCheck.getPasajes();
            if (oldPasajesOfComponentes != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Componentes " + componentesOrphanCheck + " already has an item of type Pasajes whose componentes column cannot be null. Please make another selection for the componentes field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Componentes componentes = pasajes.getComponentes();
            if (componentes != null) {
                componentes = em.getReference(componentes.getClass(), componentes.getCodComponente());
                pasajes.setComponentes(componentes);
            }
            em.persist(pasajes);
            if (componentes != null) {
                componentes.setPasajes(pasajes);
                componentes = em.merge(componentes);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPasajes(pasajes.getCod()) != null) {
                throw new PreexistingEntityException("Pasajes " + pasajes + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pasajes pasajes) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pasajes persistentPasajes = em.find(Pasajes.class, pasajes.getCod());
            Componentes componentesOld = persistentPasajes.getComponentes();
            Componentes componentesNew = pasajes.getComponentes();
            List<String> illegalOrphanMessages = null;
            if (componentesNew != null && !componentesNew.equals(componentesOld)) {
                Pasajes oldPasajesOfComponentes = componentesNew.getPasajes();
                if (oldPasajesOfComponentes != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Componentes " + componentesNew + " already has an item of type Pasajes whose componentes column cannot be null. Please make another selection for the componentes field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (componentesNew != null) {
                componentesNew = em.getReference(componentesNew.getClass(), componentesNew.getCodComponente());
                pasajes.setComponentes(componentesNew);
            }
            pasajes = em.merge(pasajes);
            if (componentesOld != null && !componentesOld.equals(componentesNew)) {
                componentesOld.setPasajes(null);
                componentesOld = em.merge(componentesOld);
            }
            if (componentesNew != null && !componentesNew.equals(componentesOld)) {
                componentesNew.setPasajes(pasajes);
                componentesNew = em.merge(componentesNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pasajes.getCod();
                if (findPasajes(id) == null) {
                    throw new NonexistentEntityException("The pasajes with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pasajes pasajes;
            try {
                pasajes = em.getReference(Pasajes.class, id);
                pasajes.getCod();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pasajes with id " + id + " no longer exists.", enfe);
            }
            Componentes componentes = pasajes.getComponentes();
            if (componentes != null) {
                componentes.setPasajes(null);
                componentes = em.merge(componentes);
            }
            em.remove(pasajes);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pasajes> findPasajesEntities() {
        return findPasajesEntities(true, -1, -1);
    }

    public List<Pasajes> findPasajesEntities(int maxResults, int firstResult) {
        return findPasajesEntities(false, maxResults, firstResult);
    }

    private List<Pasajes> findPasajesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pasajes.class));
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

    public Pasajes findPasajes(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pasajes.class, id);
        } finally {
            em.close();
        }
    }

    public int getPasajesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pasajes> rt = cq.from(Pasajes.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
