package com.mycompany.learnmate.controller;

import com.mycompany.learnmate.entities.*;
import com.mycompany.learnmate.services.*;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.math.BigDecimal;

@Named(value = "controllerTareas")
@ViewScoped
public class ControllerTareas implements Serializable {

    // =========================
    // ==== INYECCIONES EJB ====
    // =========================
    @EJB
    private TareasFacadeLocal tareasFacade;
    @EJB
    private EntregasTareasFacadeLocal entregasTareasFacade;
    @EJB
    private AsignacionAsignaturasFacadeLocal asignacionAsignaturasFacade;
    @EJB
    private CursosFacadeLocal cursosFacade;

    // =========================
    // ==== ATRIBUTOS ==========
    // =========================
    private Tareas tareaActual;
    private EntregasTareas entregaActual;

    private List<Tareas> listaTareas;
    private List<EntregasTareas> listaEntregas;
    private List<AsignacionAsignaturas> listaAsignaciones;
    private List<Cursos> listaCursos;

    private Integer tareaId;
    private Integer idAsignaturaSeleccionada;
    private BigDecimal calificacion;
    private String observaciones;
    private String archivoEntregado;

    // =========================
    // ==== INICIALIZACIÓN =====
    // =========================
    @PostConstruct
    public void init() {
        try {
            listaTareas = tareasFacade.findAll();
            listaEntregas = entregasTareasFacade.findAll();
            listaAsignaciones = asignacionAsignaturasFacade.findAll();
            listaCursos = cursosFacade.findAll();

            // Leer parámetro desde la URL (para edición)
            String idParam = FacesContext.getCurrentInstance()
                    .getExternalContext()
                    .getRequestParameterMap()
                    .get("tareaId");

            if (idParam != null && !idParam.trim().isEmpty()) {
                tareaId = Integer.parseInt(idParam);
                tareaActual = tareasFacade.find(tareaId);
            } else {
                tareaActual = new Tareas();
            }

            entregaActual = new EntregasTareas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // ==== MÉTODOS DE TAREA ===
    // =========================

    /** Crear una nueva tarea */
    public void crearTarea() {
        try {
            if (idAsignaturaSeleccionada == null) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Debe seleccionar una asignatura.");
                return;
            }

            AsignacionAsignaturas asignacion = asignacionAsignaturasFacade.find(idAsignaturaSeleccionada);
            tareaActual.setAsignacionId(asignacion);

            if (tareaActual.getFechaEntrega() == null) {
                tareaActual.setFechaEntrega(new Date());
            }

            tareasFacade.create(tareaActual);
            listaTareas = tareasFacade.findAll();
            limpiarTarea();

            mostrarMensaje(FacesMessage.SEVERITY_INFO, "✅ Tarea creada correctamente.");
        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error al crear la tarea: " + e.getMessage());
        }
    }

    /** Actualizar tarea existente */
    public void actualizarTarea() {
        try {
            if (tareaActual == null || tareaActual.getTareaId() == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No hay tarea seleccionada para actualizar.");
                return;
            }

            if (idAsignaturaSeleccionada != null) {
                AsignacionAsignaturas asignacion = asignacionAsignaturasFacade.find(idAsignaturaSeleccionada);
                tareaActual.setAsignacionId(asignacion);
            }

            tareasFacade.edit(tareaActual);
            listaTareas = tareasFacade.findAll();
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "✏️ Tarea actualizada correctamente.");

        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error al actualizar la tarea: " + e.getMessage());
        }
    }

    /** Eliminar una tarea */
    public void eliminarTarea(Tareas tarea) {
        try {
            tareasFacade.remove(tarea);
            listaTareas = tareasFacade.findAll();
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "🗑️ Tarea eliminada correctamente.");
        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error al eliminar la tarea: " + e.getMessage());
        }
    }

    /** Cargar tarea para edición */
    public String prepararEdicion(Tareas tarea) {
        this.tareaActual = tarea;
        this.idAsignaturaSeleccionada = (tarea.getAsignacionId() != null)
                ? tarea.getAsignacionId().getAsignacionId()
                : null;

        return "/views/tareas/editar.xhtml?faces-redirect=true&amp;tareaId=" + tarea.getTareaId();
    }

    // =========================
    // ==== MÉTODOS DE ENTREGA =
    // =========================

    /** Registrar entrega de tarea (por estudiante) */
    public void registrarEntrega() {
        try {
            if (tareaActual == null || tareaActual.getTareaId() == null) {
                mostrarMensaje(FacesMessage.SEVERITY_WARN, "Debe seleccionar una tarea para entregar.");
                return;
            }

            HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                    .getExternalContext().getSession(false);
            Estudiantes estudiante = (Estudiantes) session.getAttribute("usuarioEstudiante");

            if (estudiante == null) {
                mostrarMensaje(FacesMessage.SEVERITY_ERROR, "No hay un estudiante logueado.");
                return;
            }

            entregaActual.setTareaId(tareaActual);
            entregaActual.setEstudianteId(estudiante);
            entregaActual.setFechaEntrega(new Date());
            entregaActual.setArchivoEntregado(archivoEntregado);
            entregaActual.setObservaciones(observaciones);
            entregaActual.setCalificacion(calificacion);

            entregasTareasFacade.create(entregaActual);
            listaEntregas = entregasTareasFacade.findAll();

            limpiarEntrega();
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "📤 Entrega registrada correctamente.");
        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error al registrar la entrega: " + e.getMessage());
        }
    }

    /** Eliminar una entrega */
    public void eliminarEntrega(EntregasTareas entrega) {
        try {
            entregasTareasFacade.remove(entrega);
            listaEntregas = entregasTareasFacade.findAll();
            mostrarMensaje(FacesMessage.SEVERITY_INFO, "🗑️ Entrega eliminada correctamente.");
        } catch (Exception e) {
            mostrarMensaje(FacesMessage.SEVERITY_ERROR, "Error al eliminar la entrega: " + e.getMessage());
        }
    }

    // =========================
    // ==== MÉTODOS AUXILIARES =
    // =========================
    private void limpiarTarea() {
        tareaActual = new Tareas();
        idAsignaturaSeleccionada = null;
    }

    private void limpiarEntrega() {
        entregaActual = new EntregasTareas();
        archivoEntregado = null;
        observaciones = null;
        calificacion = null;
    }

    private void mostrarMensaje(FacesMessage.Severity tipo, String msg) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(tipo, msg, null));
    }

    // =========================
    // ==== GETTERS & SETTERS ==
    // =========================
    public Tareas getTareaActual() { return tareaActual; }
    public void setTareaActual(Tareas tareaActual) { this.tareaActual = tareaActual; }

    public EntregasTareas getEntregaActual() { return entregaActual; }
    public void setEntregaActual(EntregasTareas entregaActual) { this.entregaActual = entregaActual; }

    public List<Tareas> getListaTareas() { return listaTareas; }
    public List<EntregasTareas> getListaEntregas() { return listaEntregas; }
    public List<AsignacionAsignaturas> getListaAsignaciones() { return listaAsignaciones; }
    public List<Cursos> getListaCursos() { return listaCursos; }

    public Integer getTareaId() { return tareaId; }
    public void setTareaId(Integer tareaId) { this.tareaId = tareaId; }

    public Integer getIdAsignaturaSeleccionada() { return idAsignaturaSeleccionada; }
    public void setIdAsignaturaSeleccionada(Integer idAsignaturaSeleccionada) { this.idAsignaturaSeleccionada = idAsignaturaSeleccionada; }

    public BigDecimal getCalificacion() { return calificacion; }
    public void setCalificacion(BigDecimal calificacion) { this.calificacion = calificacion; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getArchivoEntregado() { return archivoEntregado; }
    public void setArchivoEntregado(String archivoEntregado) { this.archivoEntregado = archivoEntregado; }
}
