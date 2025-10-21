package com.mycompany.learnmate.controller;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import javax.json.*;

@Named
@RequestScoped
public class CorreoBean {

    private String asunto;
    private String mensaje;
    private List<String> correosDisponibles;
    private List<String> correosSeleccionados;

    // Getters y setters
    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public List<String> getCorreosDisponibles() { return correosDisponibles; }
    public void setCorreosDisponibles(List<String> correosDisponibles) { this.correosDisponibles = correosDisponibles; }

    public List<String> getCorreosSeleccionados() { return correosSeleccionados; }
    public void setCorreosSeleccionados(List<String> correosSeleccionados) { this.correosSeleccionados = correosSeleccionados; }

    // Cargar correos al inicializar
    @PostConstruct
    public void init() {
        cargarCorreos();
    }

    // Método para obtener correos desde la API Flask
    public void cargarCorreos() {
        try {
            URL url = new URL("http://127.0.0.1:5000/correo");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();

            JsonReader jsonReader = Json.createReader(new StringReader(jsonString.toString()));
            JsonObject jsonObject = jsonReader.readObject();
            JsonArray correosArray = jsonObject.getJsonArray("correos");

            correosDisponibles = new ArrayList<>();
            for (JsonValue correo : correosArray) {
                correosDisponibles.add(correo.toString().replace("\"", ""));
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            correosDisponibles = Collections.emptyList();
        }
    }

    // Método para enviar correos seleccionados
    public String enviarCorreos() {
        if (correosSeleccionados == null || correosSeleccionados.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar al menos un destinatario.", null));
            return null;
        }

        try {
            URL url = new URL("http://127.0.0.1:5000/enviar-correos");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            // Construir JSON de envío
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"asunto\":\"").append(escapeJson(asunto)).append("\",");
            json.append("\"mensaje\":\"").append(escapeJson(mensaje)).append("\",");
            json.append("\"correos\":[");
            for (int i = 0; i < correosSeleccionados.size(); i++) {
                json.append("\"").append(escapeJson(correosSeleccionados.get(i))).append("\"");
                if (i < correosSeleccionados.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]}");

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Correos enviados correctamente.", null));
                limpiarFormulario();
                return null; // Permanece en la misma página
            } else {
                String error = leerError(conn);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al enviar correos: " + error, null));
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Excepción: " + e.getMessage(), null));
            return null;
        }
    }

    // Escapar caracteres para JSON
    private String escapeJson(String input) {
        return input == null ? "" : input.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }

    // Leer respuesta de error del servidor
    private String leerError(HttpURLConnection conn) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
            StringBuilder error = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                error.append(line.trim());
            }
            return error.toString();
        } catch (Exception e) {
            return "Error desconocido";
        }
    }

    // Limpiar campos después del envío
    private void limpiarFormulario() {
        this.asunto = "";
        this.mensaje = "";
        this.correosSeleccionados = new ArrayList<>();
    }
}
