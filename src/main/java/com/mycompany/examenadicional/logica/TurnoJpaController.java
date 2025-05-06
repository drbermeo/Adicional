/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.examenadicional.logica;

import com.mycompany.examenadicional.logica.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Diana Bermeo C
 */
public class TurnoJpaController implements Serializable {

    public TurnoJpaController() {
          this.emf =Persistence.createEntityManagerFactory("TURNOSUP");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Turno turno) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Vehiculo vehiculo = turno.getVehiculo();
            if (vehiculo != null) {
                vehiculo = em.getReference(vehiculo.getClass(), vehiculo.getIdVehi());
                turno.setVehiculo(vehiculo);
            }
            em.persist(turno);
            if (vehiculo != null) {
                vehiculo.getTurnos().add(turno);
                vehiculo = em.merge(vehiculo);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Turno turno) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Turno persistentTurno = em.find(Turno.class, turno.getId());
            Vehiculo vehiculoOld = persistentTurno.getVehiculo();
            Vehiculo vehiculoNew = turno.getVehiculo();
            if (vehiculoNew != null) {
                vehiculoNew = em.getReference(vehiculoNew.getClass(), vehiculoNew.getIdVehi());
                turno.setVehiculo(vehiculoNew);
            }
            turno = em.merge(turno);
            if (vehiculoOld != null && !vehiculoOld.equals(vehiculoNew)) {
                vehiculoOld.getTurnos().remove(turno);
                vehiculoOld = em.merge(vehiculoOld);
            }
            if (vehiculoNew != null && !vehiculoNew.equals(vehiculoOld)) {
                vehiculoNew.getTurnos().add(turno);
                vehiculoNew = em.merge(vehiculoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = turno.getId();
                if (findTurno(id) == null) {
                    throw new NonexistentEntityException("The turno with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Turno turno;
            try {
                turno = em.getReference(Turno.class, id);
                turno.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The turno with id " + id + " no longer exists.", enfe);
            }
            Vehiculo vehiculo = turno.getVehiculo();
            if (vehiculo != null) {
                vehiculo.getTurnos().remove(turno);
                vehiculo = em.merge(vehiculo);
            }
            em.remove(turno);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Turno> findTurnoEntities() {
        return findTurnoEntities(true, -1, -1);
    }

    public List<Turno> findTurnoEntities(int maxResults, int firstResult) {
        return findTurnoEntities(false, maxResults, firstResult);
    }

    private List<Turno> findTurnoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Turno.class));
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

    public Turno findTurno(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Turno.class, id);
        } finally {
            em.close();
        }
    }

    public int getTurnoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Turno> rt = cq.from(Turno.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    public boolean existeTurnoMismoDia(Vehiculo vehiculo, Date fecha) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Turno> query = em.createQuery(
                "SELECT t FROM Turno t WHERE t.vehiculo = :vehiculo AND t.fecha = :fecha", Turno.class);
            query.setParameter("vehiculo", vehiculo);
            query.setParameter("fecha", fecha);
            return !query.getResultList().isEmpty();
        } finally {
            em.close();
        }
    }

    public boolean existeTurnoMismoAnden(Vehiculo vehiculo, String anden) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Turno> query = em.createQuery(
                "SELECT t FROM Turno t WHERE t.vehiculo = :vehiculo AND t.anden = :anden", Turno.class);
            query.setParameter("vehiculo", vehiculo);
            query.setParameter("anden", anden);
            return !query.getResultList().isEmpty();
        } finally {
            em.close();
        }
    }

    public boolean crearTurno(Turno turno) {
        EntityManager em = getEntityManager();

        if (existeTurnoMismoDia(turno.getVehiculo(), turno.getFecha())) {
            System.out.println("Ya existe un turno para ese vehículo en la misma fecha.");
            return false;
        }

        if (existeTurnoMismoAnden(turno.getVehiculo(), turno.getAnden())) {
            System.out.println("Ya existe un turno para ese vehículo en el mismo andén.");
            return false;
        }

        try {
            em.getTransaction().begin();
            em.persist(turno);
            em.getTransaction().commit();
            return true;
        } finally {
            em.close();
        }
    }

    public List<Turno> listarTurnos() {
        EntityManager em = getEntityManager();
        return em.createQuery("SELECT t FROM Turno t", Turno.class).getResultList();
    }
    
}
