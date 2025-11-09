package com.mycompany.learnmate.controller;
import com.mycompany.learnmate.entities.AsignacionAsignaturas;
import com.mycompany.learnmate.entities.Cursos;
import com.mycompany.learnmate.entities.Tareas;
import com.mycompany.learnmate.services.AsignacionAsignaturasFacadeLocal;
import com.mycompany.learnmate.services.CursosFacadeLocal;
import com.mycompany.learnmate.services.TareasFacadeLocal;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author yeico
 */
@Named(value = "contollerActividad")
@SessionScoped
public class contollerActividad implements Serializable {
    
    List<SelectItem> curso ;
    List<SelectItem> listaA;
    
    Cursos css = new Cursos();
    Tareas ta = new Tareas();
    AsignacionAsignaturas asig = new AsignacionAsignaturas();
    
    @EJB
    CursosFacadeLocal cfl;
    
    @EJB
    TareasFacadeLocal tfl;
    
    @EJB
    AsignacionAsignaturasFacadeLocal aafl;
    

    /**
     * Creates a new instance of contollerActividad
     */
    public contollerActividad() {
        
    }

    public Cursos getCss() {
        return css;
    }

    public void setCss(Cursos css) {
        this.css = css;
    }

    public Tareas getTa() {
        return ta;
    }

    public void setTa(Tareas ta) {
        this.ta = ta;
    }

    public AsignacionAsignaturasFacadeLocal getAafl() {
        return aafl;
    }

    public void setAafl(AsignacionAsignaturasFacadeLocal aafl) {
        this.aafl = aafl;
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
    public void asignarAct (){
            AsignacionAsignaturas as = aafl.find(asig.getAsignacionId());
           ta.setAsignacionId(as);
           tfl.create(ta);
           ta=new Tareas ();
       
    }
    public List<SelectItem> listarAsignaciones (){
        listaA = new ArrayList<>();
        try{
            for(AsignacionAsignaturas a : aafl.findAll() ){
                listaA.add(new SelectItem (a.getAsignacionId(),a.getAsignacionId().toString() ));
            }
         return listaA; 
        }        
        catch (Exception e){
            return null;
        }
        
    }
    
    
    
}
