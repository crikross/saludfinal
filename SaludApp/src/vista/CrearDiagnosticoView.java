package vista;

import controlador.ControladorDiagnostico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import modelo.Diagnostico;

public class CrearDiagnosticoView extends JFrame {

    private JTextField txtUsuarioId;
    private JTable tableDatos;
    private JTextArea txtDiagnostico;
    private JButton btnGenerarDiagnostico;

    private void mostrarDatosUsuarioEnTabla(List<Object[]> datosUsuario) {
        // Crear un modelo para la tabla
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nombre");
        model.addColumn("Correo");
        model.addColumn("Fecha de Registro");

        for (Object[] fila : datosUsuario) {
            model.addRow(fila);
        }

        // Establecer el modelo de la tabla
        tableDatos.setModel(model);
    }

    public CrearDiagnosticoView() {
        setTitle("Crear Diagnóstico");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        JLabel lblUsuarioId = new JLabel("ID Usuario:");
        lblUsuarioId.setBounds(20, 20, 100, 25);
        add(lblUsuarioId);

        txtUsuarioId = new JTextField();
        txtUsuarioId.setBounds(120, 20, 150, 25);
        add(txtUsuarioId);

        btnGenerarDiagnostico = new JButton("Generar Diagnóstico");
        btnGenerarDiagnostico.setBounds(300, 20, 200, 25);
        add(btnGenerarDiagnostico);

        tableDatos = new JTable();
        JScrollPane scrollPane = new JScrollPane(tableDatos);
        scrollPane.setBounds(20, 60, 650, 200);
        add(scrollPane);

        JLabel lblDiagnostico = new JLabel("Diagnóstico:");
        lblDiagnostico.setBounds(20, 280, 100, 25);
        add(lblDiagnostico);

        txtDiagnostico = new JTextArea();
        txtDiagnostico.setLineWrap(true);
        txtDiagnostico.setWrapStyleWord(true);
        JScrollPane scrollDiagnostico = new JScrollPane(txtDiagnostico);
        scrollDiagnostico.setBounds(20, 310, 650, 140);
        add(scrollDiagnostico);

        btnGenerarDiagnostico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarDiagnostico();
            }
        });

        setLocationRelativeTo(null);
    }

    private void generarDiagnostico() {
        try {
            // Obtener el ID del usuario desde el campo de texto
            int usuarioId = Integer.parseInt(txtUsuarioId.getText());

            // Obtener los datos completos del usuario
            ControladorDiagnostico controlador = new ControladorDiagnostico();
            List<Object[]> datosUsuario = controlador.obtenerDatosCompletosUsuario(usuarioId);

            // Si no se encontraron datos, mostrar un mensaje
            if (datosUsuario.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron datos para el usuario.");
                return;
            }

            // Mostrar los datos en la tabla
            mostrarDatosUsuarioEnTabla(datosUsuario);

            // Generar el diagnóstico
            Diagnostico diagnostico = controlador.generarDiagnostico(usuarioId, datosUsuario);

            // Mostrar el diagnóstico en el área de texto
            txtDiagnostico.setText(diagnostico.getDetalle());

            // Guardar el diagnóstico en la base de datos
            controlador.guardarDiagnostico(diagnostico);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un ID de usuario válido.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ocurrió un error al generar el diagnóstico.");
        }
    }

}
