/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.examenadicional.logica;

import com.mycompany.examenadicional.logica.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

/**
 *
 * @author Diana Bermeo C
 */
public class PropietarioJpaController implements Serializable {

    public PropietarioJpaController() {
        this.emf =Persistence.createEntityManagerFactory("TURNOSUP");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Propietario propietario) {
        if (propietario.getVehiculos() == null) {
            propietario.setVehiculos(new ArrayList<Vehiculo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Vehiculo> attachedVehiculos = new ArrayList<Vehiculo>();
            for (Vehiculo vehiculosVehiculoToAttach : propietario.getVehiculos()) {
                vehiculosVehiculoToAttach = em.getReference(vehiculosVehiculoToAttach.getClass(), vehiculosVehiculoToAttach.getIdVehi());
                attachedVehiculos.add(vehiculosVehiculoToAttach);
            }
            propietario.setVehiculos(attachedVehiculos);
            em.persist(propietario);
            for (Vehiculo vehiculosVehiculo : propietario.getVehiculos()) {
                Propietario oldPropietarioOfVehiculosVehiculo = vehiculosVehiculo.getPropietario();
                vehiculosVehiculo.setPropietario(propietario);
                vehiculosVehiculo = em.merge(vehiculosVehiculo);
                if (oldPropietarioOfVehiculosVehiculo != null) {
                    oldPropietarioOfVehiculosVehiculo.getVehiculos().remove(vehiculosVehiculo);
                    oldPropietarioOfVehiculosVehiculo = em.merge(oldPropietarioOfVehiculosVehiculo);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Propietario propietario) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Propietario persistentPropietario = em.find(Propietario.class, propietario.getIdPro());
            List<Vehiculo> vehiculosOld = persistentPropietario.getVehiculos();
            List<Vehiculo> vehiculosNew = propietario.getVehiculos();
            List<Vehiculo> attachedVehiculosNew = new ArrayList<Vehiculo>();
            for (Vehiculo vehiculosNewVehiculoToAttach : vehiculosNew) {
                vehiculosNewVehiculoToAttach = em.getReference(vehiculosNewVehiculoToAttach.getClass(), vehiculosNewVehiculoToAttach.getIdVehi());
                attachedVehiculosNew.add(vehiculosNewVehiculoToAttach);
            }
            vehiculosNew = attachedVehiculosNew;
            propietario.setVehiculos(vehiculosNew);
            propietario = em.merge(propietario);
            for (Vehiculo vehiculosOldVehiculo : vehiculosOld) {
                if (!vehiculosNew.contains(vehiculosOldVehiculo)) {
                    vehiculosOldVehiculo.setPropietario(null);
                    vehiculosOldVehiculo = em.merge(vehiculosOldVehiculo);
                }
            }
            for (Vehiculo vehiculosNewVehiculo : vehiculosNew) {
                if (!vehiculosOld.contains(vehiculosNewVehiculo)) {
                    Propietario oldPropietarioOfVehiculosNewVehiculo = vehiculosNewVehiculo.getPropietario();
                    vehiculosNewVehiculo.setPropietario(propietario);
                    vehiculosNewVehiculo = em.merge(vehiculosNewVehiculo);
                    if (oldPropietarioOfVehiculosNewVehiculo != null && !oldPropietarioOfVehiculosNewVehiculo.equals(propietario)) {
                        oldPropietarioOfVehiculosNewVehiculo.getVehiculos().remove(vehiculosNewVehiculo);
                        oldPropietarioOfVehiculosNewVehiculo = em.merge(oldPropietarioOfVehiculosNewVehiculo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = propietario.getIdPro();
                if (findPropietario(id) == null) {
                    throw new NonexistentEntityException("The propietario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Propietario propietario;
            try {
                propietario = em.getReference(Propietario.class, id);
                propietario.getIdPro();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The propietario with id " + id + " no longer exists.", enfe);
            }
            List<Vehiculo> vehiculos = propietario.getVehiculos();
            for (Vehiculo vehiculosVehiculo : vehiculos) {
                vehiculosVehiculo.setPropietario(null);
                vehiculosVehiculo = em.merge(vehiculosVehiculo);
            }
            em.remove(propietario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Propietario> findPropietarioEntities() {
        return findPropietarioEntities(true, -1, -1);
    }

    public List<Propietario> findPropietarioEntities(int maxResults, int firstResult) {
        return findPropietarioEntities(false, maxResults, firstResult);
    }

    private List<Propietario> findPropietarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Propietario.class));
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

    public Propietario findPropietario(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Propietario.class, id);
        } finally {
            em.close();
        }
    }

    public int getPropietarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Propietario> rt = cq.from(Propietario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    public Propietario findByCedula(String cedula) {
    EntityManager em = getEntityManager();
    try {
        return em.createQuery("SELECT c FROM Propietario c WHERE c.cedula = :cedula", Propietario.class)
                .setParameter("cedula", cedula)
                .getSingleResult(); // Obtiene el cliente si existe
    } catch (NoResultException e) {
        return null; // Retorna null si no se encuentra
    } finally {
        em.close();
    }
}
    
    
}
