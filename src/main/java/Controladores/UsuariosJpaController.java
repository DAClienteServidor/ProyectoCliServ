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
import Entidades.Roles;
import java.util.ArrayList;
import java.util.List;
import Entidades.Paquete;
import Entidades.Usuarios;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JOptionPane;

/**
 *
 * @author maria
 */
public class UsuariosJpaController implements Serializable {

    public UsuariosJpaController() {
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

    public void create(Usuarios usuarios) throws PreexistingEntityException, Exception {
        if (usuarios.getRolesList() == null) {
            usuarios.setRolesList(new ArrayList<Roles>());
        }
        if (usuarios.getPaqueteList() == null) {
            usuarios.setPaqueteList(new ArrayList<Paquete>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Roles> attachedRolesList = new ArrayList<Roles>();
            for (Roles rolesListRolesToAttach : usuarios.getRolesList()) {
                rolesListRolesToAttach = em.getReference(rolesListRolesToAttach.getClass(), rolesListRolesToAttach.getClave());
                attachedRolesList.add(rolesListRolesToAttach);
            }
            usuarios.setRolesList(attachedRolesList);
            List<Paquete> attachedPaqueteList = new ArrayList<Paquete>();
            for (Paquete paqueteListPaqueteToAttach : usuarios.getPaqueteList()) {
                paqueteListPaqueteToAttach = em.getReference(paqueteListPaqueteToAttach.getClass(), paqueteListPaqueteToAttach.getIdPaquete());
                attachedPaqueteList.add(paqueteListPaqueteToAttach);
            }
            usuarios.setPaqueteList(attachedPaqueteList);
            em.persist(usuarios);
            for (Roles rolesListRoles : usuarios.getRolesList()) {
                rolesListRoles.getUsuariosList().add(usuarios);
                rolesListRoles = em.merge(rolesListRoles);
            }
            for (Paquete paqueteListPaquete : usuarios.getPaqueteList()) {
                Usuarios oldUsuariosdniOfPaqueteListPaquete = paqueteListPaquete.getUsuariosdni();
                paqueteListPaquete.setUsuariosdni(usuarios);
                paqueteListPaquete = em.merge(paqueteListPaquete);
                if (oldUsuariosdniOfPaqueteListPaquete != null) {
                    oldUsuariosdniOfPaqueteListPaquete.getPaqueteList().remove(paqueteListPaquete);
                    oldUsuariosdniOfPaqueteListPaquete = em.merge(oldUsuariosdniOfPaqueteListPaquete);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuarios(usuarios.getDni()) != null) {
                throw new PreexistingEntityException("Usuarios " + usuarios + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuarios usuarios) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuarios persistentUsuarios = em.find(Usuarios.class, usuarios.getDni());
            List<Roles> rolesListOld = persistentUsuarios.getRolesList();
            List<Roles> rolesListNew = usuarios.getRolesList();
            List<Paquete> paqueteListOld = persistentUsuarios.getPaqueteList();
            List<Paquete> paqueteListNew = usuarios.getPaqueteList();
            List<String> illegalOrphanMessages = null;
            for (Paquete paqueteListOldPaquete : paqueteListOld) {
                if (!paqueteListNew.contains(paqueteListOldPaquete)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Paquete " + paqueteListOldPaquete + " since its usuariosdni field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Roles> attachedRolesListNew = new ArrayList<Roles>();
            for (Roles rolesListNewRolesToAttach : rolesListNew) {
                rolesListNewRolesToAttach = em.getReference(rolesListNewRolesToAttach.getClass(), rolesListNewRolesToAttach.getClave());
                attachedRolesListNew.add(rolesListNewRolesToAttach);
            }
            rolesListNew = attachedRolesListNew;
            usuarios.setRolesList(rolesListNew);
            List<Paquete> attachedPaqueteListNew = new ArrayList<Paquete>();
            for (Paquete paqueteListNewPaqueteToAttach : paqueteListNew) {
                paqueteListNewPaqueteToAttach = em.getReference(paqueteListNewPaqueteToAttach.getClass(), paqueteListNewPaqueteToAttach.getIdPaquete());
                attachedPaqueteListNew.add(paqueteListNewPaqueteToAttach);
            }
            paqueteListNew = attachedPaqueteListNew;
            usuarios.setPaqueteList(paqueteListNew);
            usuarios = em.merge(usuarios);
            for (Roles rolesListOldRoles : rolesListOld) {
                if (!rolesListNew.contains(rolesListOldRoles)) {
                    rolesListOldRoles.getUsuariosList().remove(usuarios);
                    rolesListOldRoles = em.merge(rolesListOldRoles);
                }
            }
            for (Roles rolesListNewRoles : rolesListNew) {
                if (!rolesListOld.contains(rolesListNewRoles)) {
                    rolesListNewRoles.getUsuariosList().add(usuarios);
                    rolesListNewRoles = em.merge(rolesListNewRoles);
                }
            }
            for (Paquete paqueteListNewPaquete : paqueteListNew) {
                if (!paqueteListOld.contains(paqueteListNewPaquete)) {
                    Usuarios oldUsuariosdniOfPaqueteListNewPaquete = paqueteListNewPaquete.getUsuariosdni();
                    paqueteListNewPaquete.setUsuariosdni(usuarios);
                    paqueteListNewPaquete = em.merge(paqueteListNewPaquete);
                    if (oldUsuariosdniOfPaqueteListNewPaquete != null && !oldUsuariosdniOfPaqueteListNewPaquete.equals(usuarios)) {
                        oldUsuariosdniOfPaqueteListNewPaquete.getPaqueteList().remove(paqueteListNewPaquete);
                        oldUsuariosdniOfPaqueteListNewPaquete = em.merge(oldUsuariosdniOfPaqueteListNewPaquete);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = usuarios.getDni();
                if (findUsuarios(id) == null) {
                    throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuarios usuarios;
            try {
                usuarios = em.getReference(Usuarios.class, id);
                usuarios.getDni();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuarios with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Paquete> paqueteListOrphanCheck = usuarios.getPaqueteList();
            for (Paquete paqueteListOrphanCheckPaquete : paqueteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuarios (" + usuarios + ") cannot be destroyed since the Paquete " + paqueteListOrphanCheckPaquete + " in its paqueteList field has a non-nullable usuariosdni field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Roles> rolesList = usuarios.getRolesList();
            for (Roles rolesListRoles : rolesList) {
                rolesListRoles.getUsuariosList().remove(usuarios);
                rolesListRoles = em.merge(rolesListRoles);
            }
            em.remove(usuarios);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuarios> findUsuariosEntities() {
        return findUsuariosEntities(true, -1, -1);
    }

    public List<Usuarios> findUsuariosEntities(int maxResults, int firstResult) {
        return findUsuariosEntities(false, maxResults, firstResult);
    }

    private List<Usuarios> findUsuariosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuarios.class));
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

    public Usuarios findUsuarios(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuarios.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuariosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuarios> rt = cq.from(Usuarios.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
