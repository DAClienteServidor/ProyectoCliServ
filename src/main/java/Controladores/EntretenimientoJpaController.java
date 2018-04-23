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
import Entidades.Entretenimiento;
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
public class EntretenimientoJpaController implements Serializable {

    public EntretenimientoJpaController() {
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

    public void create(Entretenimiento entretenimiento) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        Componentes componentesOrphanCheck = entretenimiento.getComponentes();
        if (componentesOrphanCheck != null) {
            Entretenimiento oldEntretenimientoOfComponentes = componentesOrphanCheck.getEntretenimiento();
            if (oldEntretenimientoOfComponentes != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Componentes " + componentesOrphanCheck + " already has an item of type Entretenimiento whose componentes column cannot be null. Please make another selection for the componentes field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Componentes componentes = entretenimiento.getComponentes();
            if (componentes != null) {
                componentes = em.getReference(componentes.getClass(), componentes.getCodComponente());
                entretenimiento.setComponentes(componentes);
            }
            em.persist(entretenimiento);
            if (componentes != null) {
                componentes.setEntretenimiento(entretenimiento);
                componentes = em.merge(componentes);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEntretenimiento(entretenimiento.getCod()) != null) {
                throw new PreexistingEntityException("Entretenimiento " + entretenimiento + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Entretenimiento entretenimiento) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Entretenimiento persistentEntretenimiento = em.find(Entretenimiento.class, entretenimiento.getCod());
            Componentes componentesOld = persistentEntretenimiento.getComponentes();
            Componentes componentesNew = entretenimiento.getComponentes();
            List<String> illegalOrphanMessages = null;
            if (componentesNew != null && !componentesNew.equals(componentesOld)) {
                Entretenimiento oldEntretenimientoOfComponentes = componentesNew.getEntretenimiento();
                if (oldEntretenimientoOfComponentes != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Componentes " + componentesNew + " already has an item of type Entretenimiento whose componentes column cannot be null. Please make another selection for the componentes field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (componentesNew != null) {
                componentesNew = em.getReference(componentesNew.getClass(), componentesNew.getCodComponente());
                entretenimiento.setComponentes(componentesNew);
            }
            entretenimiento = em.merge(entretenimiento);
            if (componentesOld != null && !componentesOld.equals(componentesNew)) {
                componentesOld.setEntretenimiento(null);
                componentesOld = em.merge(componentesOld);
            }
            if (componentesNew != null && !componentesNew.equals(componentesOld)) {
                componentesNew.setEntretenimiento(entretenimiento);
                componentesNew = em.merge(componentesNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = entretenimiento.getCod();
                if (findEntretenimiento(id) == null) {
                    throw new NonexistentEntityException("The entretenimiento with id " + id + " no longer exists.");
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
            Entretenimiento entretenimiento;
            try {
                entretenimiento = em.getReference(Entretenimiento.class, id);
                entretenimiento.getCod();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The entretenimiento with id " + id + " no longer exists.", enfe);
            }
            Componentes componentes = entretenimiento.getComponentes();
            if (componentes != null) {
                componentes.setEntretenimiento(null);
                componentes = em.merge(componentes);
            }
            em.remove(entretenimiento);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Entretenimiento> findEntretenimientoEntities() {
        return findEntretenimientoEntities(true, -1, -1);
    }

    public List<Entretenimiento> findEntretenimientoEntities(int maxResults, int firstResult) {
        return findEntretenimientoEntities(false, maxResults, firstResult);
    }

    private List<Entretenimiento> findEntretenimientoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Entretenimiento.class));
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

    public Entretenimiento findEntretenimiento(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Entretenimiento.class, id);
        } finally {
            em.close();
        }
    }

    public int getEntretenimientoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Entretenimiento> rt = cq.from(Entretenimiento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
