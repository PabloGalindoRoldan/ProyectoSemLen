package ar.edu.unrn.seminario.gui;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JComboBox;

import com.toedter.calendar.JDateChooser;

import ar.edu.unrn.seminario.api.IApi;
import ar.edu.unrn.seminario.dto.ArticuloDTO;
import ar.edu.unrn.seminario.dto.VisitaDTO;

public class AltaVisita extends JFrame {

    private JPanel contentPane;
    private JTextField visitanteField;
    private JDateChooser dateChooser; 
    private JTextField timeField;
    private JTextField motivoField;
    //private JTextField cantidadField;
    private JTextField articuloNombreField;
    private JTextField articuloCantidadField;
    private JTextField observacionesField;
    private JCheckBox visitaFinalCheckBox;

    private JTable articulosTable;
    private DefaultTableModel articulosModel;

    private IApi api;
    private Integer ordenId; 

    private JComboBox<String> tipoArticuloCombo; 

    private static final String[] TIPOS = new String[] { "ROPA", "CALZADO", "ALIMENTOS", "JUGUETES", "MUEBLES", "ELECTRONICA", "HIGIENE", "MEDICAMENTOS", "OTRO" };

    public AltaVisita(IApi api, Integer ordenId) {
        this.api = api;
        this.ordenId = ordenId;

        setTitle("Alta Visita");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 480);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel visitanteLabel = new JLabel("Visitante:");
        visitanteLabel.setBounds(20, 20, 80, 16);
        contentPane.add(visitanteLabel);

        visitanteField = new JTextField();
        visitanteField.setBounds(120, 16, 200, 22);
        contentPane.add(visitanteField);

        JLabel fechaLabel = new JLabel("Fecha:");
        fechaLabel.setBounds(20, 55, 180, 16);
        contentPane.add(fechaLabel);

        dateChooser = new JDateChooser();
        dateChooser.setBounds(120, 52, 200, 22);
        contentPane.add(dateChooser);

        timeField = new JTextField();
        timeField.setBounds(330, 52, 120, 22);
        timeField.setToolTipText("HH:mm:ss (opcional)");
        contentPane.add(timeField);

        JLabel motivoLabel = new JLabel("Motivo:");
        motivoLabel.setBounds(20, 90, 80, 16);
        contentPane.add(motivoLabel);

        motivoField = new JTextField();
        motivoField.setBounds(120, 87, 420, 22);
        contentPane.add(motivoField);

        /*JLabel cantidadLabel = new JLabel("Cantidad bienes:");
        cantidadLabel.setBounds(20, 125, 100, 16);
        contentPane.add(cantidadLabel);

        cantidadField = new JTextField();
        cantidadField.setBounds(140, 122, 80, 22);
        contentPane.add(cantidadField);*/

        JLabel articuloNombreLabel = new JLabel("Articulo nombre:");
        articuloNombreLabel.setBounds(20, 160, 100, 16);
        contentPane.add(articuloNombreLabel);

        articuloNombreField = new JTextField();
        articuloNombreField.setBounds(130, 157, 140, 22);
        contentPane.add(articuloNombreField);

        JLabel tipoArticuloLabel = new JLabel("Tipo:");
        tipoArticuloLabel.setBounds(280, 160, 40, 16);
        contentPane.add(tipoArticuloLabel);

        tipoArticuloCombo = new JComboBox<>(TIPOS);
        tipoArticuloCombo.setBounds(320, 157, 120, 22);
        contentPane.add(tipoArticuloCombo);

        JLabel articuloCantLabel = new JLabel("Cantidad:");
        articuloCantLabel.setBounds(450, 160, 60, 16);
        contentPane.add(articuloCantLabel);

        articuloCantidadField = new JTextField();
        articuloCantidadField.setBounds(510, 157, 60, 22);
        contentPane.add(articuloCantidadField);

        JButton addArticuloBtn = new JButton("Agregar Articulo");
        addArticuloBtn.setBounds(20, 195, 140, 25);
        addArticuloBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nombre = articuloNombreField.getText().trim();
                String cantText = articuloCantidadField.getText().trim();
                String tipo = (String) tipoArticuloCombo.getSelectedItem();
                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Hace falta el nombre del artículo", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int c = 0;
                try { c = Integer.parseInt(cantText); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(null, "Cantidad debe ser entero", "Error", JOptionPane.ERROR_MESSAGE); return; }
                articulosModel.addRow(new Object[] { nombre, c, tipo });
                articuloNombreField.setText(""); articuloCantidadField.setText("");
            }
        });
        contentPane.add(addArticuloBtn);

        String[] cols = new String[] { "Nombre", "Cantidad", "Tipo" };
        articulosModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        articulosTable = new JTable(articulosModel);
        JScrollPane scroll = new JScrollPane(articulosTable);
        scroll.setBounds(20, 230, 560, 150);
        contentPane.add(scroll);

        JLabel observacionesLabel = new JLabel("Observaciones:");
        observacionesLabel.setBounds(20, 355, 100, 16);
        contentPane.add(observacionesLabel);

        observacionesField = new JTextField();
        observacionesField.setBounds(120, 352, 460, 22);
        contentPane.add(observacionesField);

        visitaFinalCheckBox = new JCheckBox("Visita Final (completa orden)");
        visitaFinalCheckBox.setBounds(20, 385, 240, 25);
        contentPane.add(visitaFinalCheckBox);

        JButton aceptarButton = new JButton("Aceptar");
        aceptarButton.setBounds(360, 415, 100, 25);
        aceptarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String visitante = visitanteField.getText().trim();

                    LocalDateTime fecha = null;
                    Date selected = dateChooser.getDate();
                    String timeText = timeField.getText().trim();
                    if (selected != null) {
                        LocalDateTime datePart = LocalDateTime.ofInstant(selected.toInstant(), ZoneId.systemDefault()).withHour(0).withMinute(0).withSecond(0).withNano(0);
                        LocalTime time = null;
                        if (!timeText.isEmpty()) {
                            try { time = LocalTime.parse(timeText); } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Hora con formato invalido. Use HH:mm:ss", "Error", JOptionPane.ERROR_MESSAGE); return; }
                        } else {
                            time = LocalTime.now();
                        }
                        fecha = LocalDateTime.of(datePart.toLocalDate(), time);
                    } else {
                        fecha = LocalDateTime.now();
                    }

                    String motivo = motivoField.getText().trim();
                    int cantidad = 0;
                    for (int i = 0; i < articulosModel.getRowCount(); i++) {
                        cantidad += (Integer) articulosModel.getValueAt(i, 1);
                    }
                    List<ArticuloDTO> articulos = new ArrayList<>();
                    for (int i = 0; i < articulosModel.getRowCount(); i++) {
                        String nombre = (String) articulosModel.getValueAt(i, 0);
                        int c = (Integer) articulosModel.getValueAt(i, 1);
                        String tipo = (String) articulosModel.getValueAt(i, 2);
                        articulos.add(new ArticuloDTO(nombre, c, tipo));
                    }
                    String observ = observacionesField.getText().trim();
                    boolean visitaFinal = visitaFinalCheckBox.isSelected();
                    VisitaDTO visita = new VisitaDTO(null, visitante, fecha, motivo, true, cantidad, null, observ, ordenId, visitaFinal);
                    visita.setArticulosRecogidos(articulos);
                    api.crearVisita(visita);
                    JOptionPane.showMessageDialog(null, "Visita creada", "Info", JOptionPane.INFORMATION_MESSAGE);
                    setVisible(false); dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Ocurrió un error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        contentPane.add(aceptarButton);

        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.setBounds(470, 415, 100, 25);
        cancelarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { setVisible(false); dispose(); }
        });
        contentPane.add(cancelarButton);
    }
}