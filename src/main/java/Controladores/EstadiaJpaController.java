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
import Entidades.Estadia;
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
public class EstadiaJpaController implements Serializable {

    public EstadiaJpaController() {
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

    public void create(Estadia estadia) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        Componentes componentesOrphanCheck = estadia.getComponentes();
        if (componentesOrphanCheck != null) {
            Estadia oldEstadiaOfComponentes = componentesOrphanCheck.getEstadia();
            if (oldEstadiaOfComponentes != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Componentes " + componentesOrphanCheck + " already has an item of type Estadia whose componentes column cannot be null. Please make another selection for the componentes field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Componentes componentes = estadia.getComponentes();
            if (componentes != null) {
                componentes = em.getReference(componentes.getClass(), componentes.getCodComponente());
                estadia.setComponentes(componentes);
            }
            em.persist(estadia);
            if (componentes != null) {
                componentes.setEstadia(estadia);
                componentes = em.merge(componentes);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEstadia(estadia.getCod()) != null) {
                throw new PreexistingEntityException("Estadia " + estadia + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Estadia estadia) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estadia persistentEstadia = em.find(Estadia.class, estadia.getCod());
            Componentes componentesOld = persistentEstadia.getComponentes();
            Componentes componentesNew = estadia.getComponentes();
            List<String> illegalOrphanMessages = null;
            if (componentesNew != null && !componentesNew.equals(componentesOld)) {
                Estadia oldEstadiaOfComponentes = componentesNew.getEstadia();
                if (oldEstadiaOfComponentes != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Componentes " + componentesNew + " already has an item of type Estadia whose componentes column cannot be null. Please make another selection for the componentes field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (componentesNew != null) {
                componentesNew = em.getReference(componentesNew.getClass(), componentesNew.getCodComponente());
                estadia.setComponentes(componentesNew);
            }
            estadia = em.merge(estadia);
            if (componentesOld != null && !componentesOld.equals(componentesNew)) {
                componentesOld.setEstadia(null);
                componentesOld = em.merge(componentesOld);
            }
            if (componentesNew != null && !componentesNew.equals(componentesOld)) {
                componentesNew.setEstadia(estadia);
                componentesNew = em.merge(componentesNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = estadia.getCod();
                if (findEstadia(id) == null) {
                    throw new NonexistentEntityException("The estadia with id " + id + " no longer exists.");
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
            Estadia estadia;
            try {
                estadia = em.getReference(Estadia.class, id);
                estadia.getCod();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estadia with id " + id + " no longer exists.", enfe);
            }
            Componentes componentes = estadia.getComponentes();
            if (componentes != null) {
                componentes.setEstadia(null);
                componentes = em.merge(componentes);
            }
            em.remove(estadia);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Estadia> findEstadiaEntities() {
        return findEstadiaEntities(true, -1, -1);
    }

    public List<Estadia> findEstadiaEntities(int maxResults, int firstResult) {
        return findEstadiaEntities(false, maxResults, firstResult);
    }

    private List<Estadia> findEstadiaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Estadia.class));
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

    public Estadia findEstadia(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estadia.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstadiaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Estadia> rt = cq.from(Estadia.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
