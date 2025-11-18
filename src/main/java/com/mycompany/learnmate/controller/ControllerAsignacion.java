package com.mycompany.learnmate.controller;
import com.mycompany.learnmate.entities.AsignacionAsignaturas;
import com.mycompany.learnmate.entities.Asignaturas;
import com.mycompany.learnmate.entities.Cursos;
import com.mycompany.learnmate.entities.Profesores;
import com.mycompany.learnmate.services.AsignacionAsignaturasFacadeLocal;
import com.mycompany.learnmate.services.AsignaturasFacadeLocal;
import com.mycompany.learnmate.services.CursosFacadeLocal;
import com.mycompany.learnmate.services.ProfesoresFacadeLocal;
import com.sun.org.apache.bcel.internal.generic.Select;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
/**
 *
 * @author yeico
 */
@Named(value = "controllerAsignacion")
@ViewScoped
public class ControllerAsignacion implements Serializable{
    
    List<SelectItem> docente;
    List<SelectItem> curso;
    List<SelectItem> materia;
    
    Asignaturas mat = new Asignaturas();
    Cursos cur = new Cursos();
    Profesores porf = new Profesores();
    AsignacionAsignaturas asig = new AsignacionAsignaturas();
    
    @EJB
    CursosFacadeLocal cfl ;
    
    @EJB
    AsignaturasFacadeLocal afl;
    
    @EJB
    ProfesoresFacadeLocal pfl;
    
    @EJB
    AsignacionAsignaturasFacadeLocal gfl;
    
  
    public ControllerAsignacion() {
    }

    public Asignaturas getMat() {
        return mat;
    }

    public void setMat(Asignaturas mat) {
        this.mat = mat;
    }

    public Cursos getCur() {
        return cur;
    }

    public void setCur(Cursos cur) {
        this.cur = cur;
    }

    public Profesores getPorf() {
        return porf;
    }

    public void setPorf(Profesores porf) {
        this.porf = porf;
    }

    public AsignacionAsignaturas getAsig() {
        return asig;
    }

    public void setAsig(AsignacionAsignaturas asig) {
        this.asig = asig;
    }
    
    
    public List<SelectItem> listarCursos (){
        curso = new ArrayList<>();
        try{
            for(Cursos c : cfl.findAll() ){
                curso.add(new SelectItem (c.getCursoId(),c.getNombreCurso() ));
            }
         return curso; 
        }        
        catch (Exception e){
            return null;
        }       
    }
    public List<SelectItem> listarProfesor (){
        docente = new ArrayList<>();
        try{
            for(Profesores p : pfl.findAll() ){
                docente.add(new SelectItem (p.getProfesorId(),p.getPersona().getPrimerNombre() + " " + 
                        p.getPersona().getSegundoNombre()+ " " + p.getPersona().getPrimerApellido()));
            }
         return docente; 
        }        
        catch (Exception e){
            return null;
        }
        
    }
    public List<SelectItem> listarMateria (){
        materia = new ArrayList<>();
        try{
            for(Asignaturas a : afl.findAll() ){
                materia.add(new SelectItem (a.getAsignaturaId(),a.getNombreAsignatura()));
            }
         return materia; 
        }        
        catch (Exception e){
            return null;
        }
        
    }
    
    public void crearAsignacion () {
        
        asig.setAsignaturaId(mat);
        asig.setCursoId(cur);
        asig.setProfesorId(porf);
        gfl.create(asig);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Asignacion Creada Exitosamente", null));
        this.mat = new Asignaturas();
        this.cur = new Cursos();
        this.porf = new Profesores();

    
    }
    
    
    
}
