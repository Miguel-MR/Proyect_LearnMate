package com.mycompany.learnmate.controller;

import com.mycompany.learnmate.entities.EntregasTareas;
import com.mycompany.learnmate.entities.Estudiantes;
import com.mycompany.learnmate.entities.Tareas;
import com.mycompany.learnmate.services.EntregasTareasFacadeLocal;
import com.mycompany.learnmate.services.EstudiantesFacadeLocal;
import com.mycompany.learnmate.services.TareasFacadeLocal;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@Named(value = "controllerEntregasTareas")
@ViewScoped
public class contollerActividad implements Serializable {

    private EntregasTareas entrega = new EntregasTareas();
    private List<EntregasTareas> listaEntregas;

    // Variables temporales del formulario
    private Integer idEstudianteTemp;
    private Integer idTareaTemp;
    private String archivoTemp;
    private BigDecimal calificacionTemp;
    private String observacionesTemp;
    private Date fechaEntregaTemp;

    @EJB
    private EntregasTareasFacadeLocal entregasEJB;

    @EJB
    private EstudiantesFacadeLocal estudiantesEJB;

    @EJB
    private TareasFacadeLocal tareasEJB;

    @PostConstruct
    public void init() {
        try {
            listaEntregas = entregasEJB.findAll();
        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error al cargar entregas: " + e.getMessage());
        }
    }

    // ========================
    // MÉTODO GUARDAR / EDITAR
    // ========================
    public String guardar() {
        try {
            // ===== VALIDACIONES =====
            if (entrega.getFechaEntrega() == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Debe ingresar la fecha de entrega.");
                return null;
            }

            if (entrega.getArchivoEntregado() == null || entrega.getArchivoEntregado().trim().isEmpty()) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Debe adjuntar un archivo.");
                return null;
            }

            if (idEstudianteTemp == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Debe seleccionar un estudiante.");
                return null;
            }

            if (idTareaTemp == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Debe seleccionar una tarea.");
                return null;
            }

            // ===== RELACIONES =====
            Estudiantes estudiante = estudiantesEJB.find(idEstudianteTemp);
            Tareas tarea = tareasEJB.find(idTareaTemp);

            if (estudiante == null || tarea == null) {
                mostrarMensaje(FacesMessage.SEVERITY_FATAL, "Error: estudiante o tarea no encontrados.");
                return null;
            }

            entrega.setEstudianteId(estudiante);
            entrega.setTareaId(tarea);

            // ===== CREAR O EDITAR =====
            if (entrega.getEntregaId() == null) {
                entregasEJB.create(entrega);
                mostrarMensaje(FacesMessage.SEVERITY_INFO, "Entrega registrada correctamente.");
            } else {
                entregasEJB.edit(entrega);
                mostrarMensaje(FacesMessage.SEVERITY_INFO, "Entrega actualizada correctamente.");
            }

            // ===== REFRESCAR LISTA =====
            listaEntregas = entregasEJB.findAll();
            entrega = new EntregasTareas(); // limpiar formulario

            return "entregasTareas.xhtml?faces-redirect=true";

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje(FacesMessage.SEVERITY_FATAL, "Error al guardar: " + e.getMessage());
            return null;
        }
    }

    // ========================
    // MÉTODO ELIMINAR
    // ========================
    public void eliminar(EntregasTareas e) {
        try {
            entregasEJB.remove(e);
            listaEntregas.remove(e);
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "Entrega eliminada correctamente.");
        } catch (Exception ex) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error al eliminar: " + ex.getMessage());
        }
    }

    // ========================
    // MÉTODOS AUXILIARES
    // ========================
    private void mostrarMensaje(FacesMessage.Severity tipo, String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(tipo, msg, null));
    }

    // ========================
    // GETTERS Y SETTERS
    // ========================
    public EntregasTareas getEntrega() {
        return entrega;
    }

    public void setEntrega(EntregasTareas entrega) {
        this.entrega = entrega;
    }

    public List<EntregasTareas> getListaEntregas() {
        return listaEntregas;
    }

    public void setListaEntregas(List<EntregasTareas> listaEntregas) {
        this.listaEntregas = listaEntregas;
    }

    public Integer getIdEstudianteTemp() {
        return idEstudianteTemp;
    }

    public void setIdEstudianteTemp(Integer idEstudianteTemp) {
        this.idEstudianteTemp = idEstudianteTemp;
    }

    public Integer getIdTareaTemp() {
        return idTareaTemp;
    }

    public void setIdTareaTemp(Integer idTareaTemp) {
        this.idTareaTemp = idTareaTemp;
    }

    public String getArchivoTemp() {
        return archivoTemp;
    }

    public void setArchivoTemp(String archivoTemp) {
        this.archivoTemp = archivoTemp;
    }

    public BigDecimal getCalificacionTemp() {
        return calificacionTemp;
    }

    public void setCalificacionTemp(BigDecimal calificacionTemp) {
        this.calificacionTemp = calificacionTemp;
    }

    public String getObservacionesTemp() {
        return observacionesTemp;
    }

    public void setObservacionesTemp(String observacionesTemp) {
        this.observacionesTemp = observacionesTemp;
    }

    public Date getFechaEntregaTemp() {
        return fechaEntregaTemp;
    }

    public void setFechaEntregaTemp(Date fechaEntregaTemp) {
        this.fechaEntregaTemp = fechaEntregaTemp;
    }
}
