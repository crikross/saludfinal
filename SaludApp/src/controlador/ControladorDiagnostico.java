package controlador;

import modelo.Diagnostico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControladorDiagnostico {

    public List<Object[]> obtenerDatosCompletosUsuario(int usuarioId) {
        List<Object[]> datosUsuario = new ArrayList<>();

        // Consulta para obtener datos de usuario, actividad física, estrés y signos vitales
        String query = "SELECT u.nombre, u.correo, u.fecha_registro, "
                     + "af.pasos, af.distancia, af.calorias_quemadas, "
                     + "e.nivel_estres, e.motivo, "
                     + "sv.pulsaciones, sv.presion_arterial "
                     + "FROM Usuarios u "
                     + "JOIN ActividadFisica af ON u.usuario_id = af.usuario_id "
                     + "JOIN Estres e ON u.usuario_id = e.usuario_id "
                     + "JOIN SignosVitales sv ON u.usuario_id = sv.usuario_id "
                     + "WHERE u.usuario_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SaludApp", "root", "root");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, usuarioId);  // Establecemos el ID del usuario
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Recuperamos los datos correctamente
                String nombre = rs.getString("nombre");
                String correo = rs.getString("correo");
                String fechaRegistro = rs.getString("fecha_registro");

                int pasos = rs.getInt("pasos");  // Usamos getInt() para campos enteros
                float distancia = rs.getFloat("distancia");  // Usamos getFloat() para campos de tipo FLOAT
                float calorias = rs.getFloat("calorias_quemadas");  // Usamos getFloat() para campos de tipo FLOAT

                float nivelEstres = rs.getFloat("nivel_estres");  // Usamos getFloat() para campos FLOAT
                String motivoEstres = rs.getString("motivo");

                int pulsaciones = rs.getInt("pulsaciones");  // Usamos getInt() para campos enteros
                int presionArterial = rs.getInt("presion_arterial");  // Usamos getInt() para campos enteros

                // Añadimos los datos a la lista
                datosUsuario.add(new Object[]{nombre, correo, fechaRegistro, pasos, distancia, calorias, nivelEstres, motivoEstres, pulsaciones, presionArterial});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datosUsuario;
    }

    // Método para generar el diagnóstico
    public Diagnostico generarDiagnostico(int usuarioId, List<Object[]> datosUsuario) {
    // diagnóstico basado en los datos del usuario
    StringBuilder diagnosticoTexto = new StringBuilder();
    String estado = "Activo";  
    String recomendacion = "Mejorar salud";  

    for (Object[] fila : datosUsuario) {
        int pasos = (int) fila[3];
        float distancia = (float) fila[4];
        float calorias = (float) fila[5];
        float nivelEstres = (float) fila[6]; 
        String motivoEstres = (String) fila[7];
        int pulsaciones = (int) fila[8];
        int presionArterial = (int) fila[9];

        // Diagnóstico basado en la actividad física
        if (pasos < 5000) {
            diagnosticoTexto.append("Baja actividad física: Se recomienda aumentar los pasos diarios.\n");
        }
        if (distancia < 3.0) {
            diagnosticoTexto.append("Baja distancia recorrida: Intente caminar más distancia diariamente.\n");
        }
        if (calorias < 200) {
            diagnosticoTexto.append("Baja quema de calorías: Intente ejercicios más intensos.\n");
        }

        // Diagnóstico basado en estrés
        if (nivelEstres > 7) {
            diagnosticoTexto.append("Estrés alto: Se recomienda técnicas de relajación o consultar a un especialista.\n");
            estado = "En riesgo";  // Cambiamos el estado si el estrés es muy alto
        }
        if (!motivoEstres.equals("No especificado")) {
            diagnosticoTexto.append("Motivo del Estrés: ").append(motivoEstres).append("\n");
        }

        // Diagnóstico basado en signos vitales
        if (pulsaciones > 100) {
            diagnosticoTexto.append("Pulsaciones altas: Posible estrés o actividad física intensa.\n");
            estado = "En riesgo";  // Cambiamos el estado si las pulsaciones son altas
        }
        if (presionArterial > 140) {
            diagnosticoTexto.append("Presión arterial elevada: Se recomienda visitar a un médico.\n");
            estado = "Crítico";  // Cambiamos el estado si la presión arterial es muy alta
            recomendacion = "Consultar a un médico para evaluación de la presión arterial";  // Recomendación más precisa
        }

        // Personalización de la recomendación
        if (pasos < 5000 || distancia < 3.0 || calorias < 200) {
            recomendacion = "Incrementar la actividad física: Hacer más ejercicio para mejorar la salud.";
        }

        if (nivelEstres > 7) {
            recomendacion += " Considerar prácticas de relajación para reducir el estrés.";
        }
        if (pulsaciones > 100) {
            recomendacion += " Realizar pausas activas o consultar a un especialista si persisten las pulsaciones altas.";
        }
    }

    // Creamos el objeto Diagnóstico con la información generada
    Timestamp fechaRegistro = new Timestamp(System.currentTimeMillis());
    Diagnostico diagnostico = new Diagnostico(0, usuarioId, "General", diagnosticoTexto.toString(), 0, recomendacion, estado, fechaRegistro);

    return diagnostico;
}


    // Método para guardar el diagnóstico en la base de datos
    public void guardarDiagnostico(Diagnostico diagnostico) {
        String query = "INSERT INTO diagnostico (usuario_id, categoria, detalle, valor, recomendacion, estado, fecha_registro) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/saludApp", "root", "root");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, diagnostico.getUsuarioId());
            stmt.setString(2, diagnostico.getCategoria());
            stmt.setString(3, diagnostico.getDetalle());
            stmt.setFloat(4, diagnostico.getValor());
            stmt.setString(5, diagnostico.getRecomendacion());
            stmt.setString(6, diagnostico.getEstado());
            stmt.setTimestamp(7, diagnostico.getFechaRegistro());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
