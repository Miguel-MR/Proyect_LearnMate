package com.mycompany.learnmate.controller;

import com.mycompany.learnmate.entities.Tareas;
import com.mycompany.learnmate.entities.Cursos;
import com.mycompany.learnmate.entities.AsignacionAsignaturas;
import com.mycompany.learnmate.services.TareasFacadeLocal;
import com.mycompany.learnmate.services.CursosFacadeLocal;
import com.mycompany.learnmate.services.AsignacionAsignaturasFacadeLocal;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named(value = "tareasController")
@SessionScoped
public class ControllerTareas implements Serializable {

    @EJB
    private TareasFacadeLocal tareasFacade;

    @EJB
    private CursosFacadeLocal cursosFacade;

    @EJB
    private AsignacionAsignaturasFacadeLocal asignacionAsignaturasFacade;

    private Tareas tareaNueva;
    private List<Tareas> listaTareas;
    private List<Cursos> listaCursos;
    private List<AsignacionAsignaturas> listaAsignaciones;

    // Variables auxiliares para los selectOneMenu
    private Integer idCursoSeleccionado;
    private Integer idAsignacionSeleccionada;

    @PostConstruct
    public void init() {
        tareaNueva = new Tareas();
        listaTareas = tareasFacade.findAll();
        listaCursos = cursosFacade.findAll();
        listaAsignaciones = asignacionAsignaturasFacade.findAll();
    }

    // -----------------------------
    // Getters y Setters
    // -----------------------------
    public Tareas getTareaNueva() {
        return tareaNueva;
    }

    public void setTareaNueva(Tareas tareaNueva) {
        this.tareaNueva = tareaNueva;
    }

    public List<Tareas> getListaTareas() {
        return listaTareas;
    }

    public List<Cursos> getListaCursos() {
        return listaCursos;
    }

    public List<AsignacionAsignaturas> getListaAsignaciones() {
        return listaAsignaciones;
    }

    public Integer getIdCursoSeleccionado() {
        return idCursoSeleccionado;
    }

    public void setIdCursoSeleccionado(Integer idCursoSeleccionado) {
        this.idCursoSeleccionado = idCursoSeleccionado;
    }

    public Integer getIdAsignacionSeleccionada() {
        return idAsignacionSeleccionada;
    }

    public void setIdAsignacionSeleccionada(Integer idAsignacionSeleccionada) {
        this.idAsignacionSeleccionada = idAsignacionSeleccionada;
    }

    // -----------------------------
    // Métodos de negocio
    // -----------------------------

    public void crearTarea() {
        try {
            // Asociar asignación seleccionada
            if (idAsignacionSeleccionada != null) {
                AsignacionAsignaturas asignacion = asignacionAsignaturasFacade.find(idAsignacionSeleccionada);
                tareaNueva.setAsignacionId(asignacion);
            }

            // Fecha actual si no se especifica
            if (tareaNueva.getFechaEntrega() == null) {
                tareaNueva.setFechaEntrega(new Date());
            }

            tareasFacade.create(tareaNueva);
            listaTareas = tareasFacade.findAll();

            tareaNueva = new Tareas(); // limpiar formulario

            System.out.println("✅ Tarea creada correctamente");

        } catch (Exception e) {
            System.err.println("❌ Error al crear la tarea: " + e.getMessage());
        }
    }

    public void eliminarTarea(Tareas tarea) {
        try {
            tareasFacade.remove(tarea);
            listaTareas = tareasFacade.findAll();
            System.out.println("🗑️ Tarea eliminada correctamente");
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar tarea: " + e.getMessage());
        }
    }
}
