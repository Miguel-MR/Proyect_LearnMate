package com.mycompany.learnmate.beans;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.sql.*;

@ManagedBean(name = "dashboardBean")
@ViewScoped
public class DashboardBean implements Serializable {
    private int totalUsuarios;
    private int totalEstudiantes;
    private int totalProfesores;
    private int totalAcudientes;
    private int totalGrados; 
            

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    private void cargarDatos() {
        String url = "jdbc:mysql://localhost:3306/learnmate";
        String user = "Hanson";
        String password = "AVNS_88RZ5HT69ePxZtpqmpR";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Total usuarios
            PreparedStatement psUsuarios = conn.prepareStatement("SELECT COUNT(*) FROM usuarios");
            ResultSet rsUsuarios = psUsuarios.executeQuery(); 
            if (rsUsuarios.next()) totalUsuarios = rsUsuarios.getInt(1);

            // Estudiantes
            PreparedStatement psEstudiantes = conn.prepareStatement("SELECT COUNT(*) FROM usuarios WHERE rol = 'estudiantes'");
            ResultSet rsEstudiantes = psEstudiantes.executeQuery();
            if (rsEstudiantes.next()) totalEstudiantes = rsEstudiantes.getInt(1);

            // Profesores
            PreparedStatement psProfesores = conn.prepareStatement("SELECT COUNT(*) FROM usuarios WHERE rol = 'profesores'");
            ResultSet rsProfesores = psProfesores.executeQuery();
            if (rsProfesores.next()) totalProfesores = rsProfesores.getInt(1);

            // Acudientes
            PreparedStatement psAcudientes = conn.prepareStatement("SELECT COUNT(*) FROM usuarios WHERE rol = 'acudientes'");
            ResultSet rsAcudientes = psAcudientes.executeQuery();
            if (rsAcudientes.next()) totalAcudientes = rsAcudientes.getInt(1);            
            // Cursos
            PreparedStatement psCursos = conn.prepareStatement("SELECT COUNT(*) FROM grados");
            ResultSet rsGrados = psCursos.executeQuery();
            if (rsGrados.next ()) totalGrados = rsGrados.getInt(1);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public int getTotalUsuarios() { return totalUsuarios; }
    public int getTotalEstudiantes() { return totalEstudiantes; }
    public int getTotalProfesores() { return totalProfesores; }
    public int getTotalAcudientes() { return totalAcudientes; }
    public int getTotalGrados() { return totalGrados; }
}
