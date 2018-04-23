/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controladores;

import Controladores.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Usuarios;
import Entidades.Componentes;
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
public class PaqueteJpaController implements Serializable {

    public PaqueteJpaController() {
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

    public void create(Paquete paquete) {
        if (paquete.getComponentesList() == null) {
            paquete.setComponentesList(new ArrayList<Componentes>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuarios usuariosdni = paquete.getUsuariosdni();
            if (usuariosdni != null) {
                usuariosdni = em.getReference(usuariosdni.getClass(), usuariosdni.getDni());
                paquete.setUsuariosdni(usuariosdni);
            }
            List<Componentes> attachedComponentesList = new ArrayList<Componentes>();
            for (Componentes componentesListComponentesToAttach : paquete.getComponentesList()) {
                componentesListComponentesToAttach = em.getReference(componentesListComponentesToAttach.getClass(), componentesListComponentesToAttach.getCodComponente());
                attachedComponentesList.add(componentesListComponentesToAttach);
            }
            paquete.setComponentesList(attachedComponentesList);
            em.persist(paquete);
            if (usuariosdni != null) {
                usuariosdni.getPaqueteList().add(paquete);
                usuariosdni = em.merge(usuariosdni);
            }
            for (Componentes componentesListComponentes : paquete.getComponentesList()) {
                componentesListComponentes.getPaqueteList().add(paquete);
                componentesListComponentes = em.merge(componentesListComponentes);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Paquete paquete) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Paquete persistentPaquete = em.find(Paquete.class, paquete.getIdPaquete());
            Usuarios usuariosdniOld = persistentPaquete.getUsuariosdni();
            Usuarios usuariosdniNew = paquete.getUsuariosdni();
            List<Componentes> componentesListOld = persistentPaquete.getComponentesList();
            List<Componentes> componentesListNew = paquete.getComponentesList();
            if (usuariosdniNew != null) {
                usuariosdniNew = em.getReference(usuariosdniNew.getClass(), usuariosdniNew.getDni());
                paquete.setUsuariosdni(usuariosdniNew);
            }
            List<Componentes> attachedComponentesListNew = new ArrayList<Componentes>();
            for (Componentes componentesListNewComponentesToAttach : componentesListNew) {
                componentesListNewComponentesToAttach = em.getReference(componentesListNewComponentesToAttach.getClass(), componentesListNewComponentesToAttach.getCodComponente());
                attachedComponentesListNew.add(componentesListNewComponentesToAttach);
            }
            componentesListNew = attachedComponentesListNew;
            paquete.setComponentesList(componentesListNew);
            paquete = em.merge(paquete);
            if (usuariosdniOld != null && !usuariosdniOld.equals(usuariosdniNew)) {
                usuariosdniOld.getPaqueteList().remove(paquete);
                usuariosdniOld = em.merge(usuariosdniOld);
            }
            if (usuariosdniNew != null && !usuariosdniNew.equals(usuariosdniOld)) {
                usuariosdniNew.getPaqueteList().add(paquete);
                usuariosdniNew = em.merge(usuariosdniNew);
            }
            for (Componentes componentesListOldComponentes : componentesListOld) {
                if (!componentesListNew.contains(componentesListOldComponentes)) {
                    componentesListOldComponentes.getPaqueteList().remove(paquete);
                    componentesListOldComponentes = em.merge(componentesListOldComponentes);
                }
            }
            for (Componentes componentesListNewComponentes : componentesListNew) {
                if (!componentesListOld.contains(componentesListNewComponentes)) {
                    componentesListNewComponentes.getPaqueteList().add(paquete);
                    componentesListNewComponentes = em.merge(componentesListNewComponentes);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = paquete.getIdPaquete();
                if (findPaquete(id) == null) {
                    throw new NonexistentEntityException("The paquete with id " + id + " no longer exists.");
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
            Paquete paquete;
            try {
                paquete = em.getReference(Paquete.class, id);
                paquete.getIdPaquete();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The paquete with id " + id + " no longer exists.", enfe);
            }
            Usuarios usuariosdni = paquete.getUsuariosdni();
            if (usuariosdni != null) {
                usuariosdni.getPaqueteList().remove(paquete);
                usuariosdni = em.merge(usuariosdni);
            }
            List<Componentes> componentesList = paquete.getComponentesList();
            for (Componentes componentesListComponentes : componentesList) {
                componentesListComponentes.getPaqueteList().remove(paquete);
                componentesListComponentes = em.merge(componentesListComponentes);
            }
            em.remove(paquete);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Paquete> findPaqueteEntities() {
        return findPaqueteEntities(true, -1, -1);
    }

    public List<Paquete> findPaqueteEntities(int maxResults, int firstResult) {
        return findPaqueteEntities(false, maxResults, firstResult);
    }

    private List<Paquete> findPaqueteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Paquete.class));
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

    public Paquete findPaquete(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Paquete.class, id);
        } finally {
            em.close();
        }
    }

    public int getPaqueteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Paquete> rt = cq.from(Paquete.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
