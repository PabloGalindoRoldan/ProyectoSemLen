package ar.edu.unrn.seminario.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import ar.edu.unrn.seminario.api.IApi;
import ar.edu.unrn.seminario.dto.DonacionDTO;
import ar.edu.unrn.seminario.dto.UsuarioDTO;

public class AltaPedido extends JFrame {

    private JPanel contentPane;
    private JTextField descripcionField;
    private JTextField observacionesField;
    private JCheckBox necesitaVehiculoCheckBox;
    private JComboBox<String> donanteComboBox;
    private JTable donacionesTable;
    private DefaultTableModel donacionesModel;
    private JComboBox<String> tipoComboBox;
    private JTextField categoriaField;
    private JTextField puntajeField;

    private List<UsuarioDTO> usuarios = new ArrayList<>();

    private static final String[] TIPOS = new String[] { "ROPA", "CALZADO", "ALIMENTOS", "JUGUETES", "MUEBLES", "ELECTRONICA", "HIGIENE", "MEDICAMENTOS", "OTRO" };

    public AltaPedido(IApi api) {
        setTitle("Alta Pedido de Donacion");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 480);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel descripcionLabel = new JLabel("Descripcion:");
        descripcionLabel.setBounds(20, 20, 100, 16);
        contentPane.add(descripcionLabel);

        descripcionField = new JTextField();
        descripcionField.setBounds(140, 17, 420, 22);
        contentPane.add(descripcionField);

        JLabel observacionesLabel = new JLabel("Observaciones:");
        observacionesLabel.setBounds(20, 50, 100, 16);
        contentPane.add(observacionesLabel);

        observacionesField = new JTextField();
        observacionesField.setBounds(140, 47, 420, 22);
        contentPane.add(observacionesField);

        necesitaVehiculoCheckBox = new JCheckBox("Necesita vehiculo");
        necesitaVehiculoCheckBox.setBounds(140, 120, 200, 25);
        contentPane.add(necesitaVehiculoCheckBox);

        JLabel donanteLabel = new JLabel("Donante:");
        donanteLabel.setBounds(20, 160, 100, 16);
        contentPane.add(donanteLabel);

        donanteComboBox = new JComboBox<>();
        donanteComboBox.setBounds(140, 155, 200, 25);
        contentPane.add(donanteComboBox);

        // donations inputs
        JLabel tipoLabel = new JLabel("Tipo:");
        tipoLabel.setBounds(20, 200, 60, 16);
        contentPane.add(tipoLabel);

        tipoComboBox = new JComboBox<>(TIPOS);
        tipoComboBox.setBounds(80, 195, 140, 25);
        contentPane.add(tipoComboBox);

        JLabel categoriaLabel = new JLabel("Categoria:");
        categoriaLabel.setBounds(230, 200, 70, 16);
        contentPane.add(categoriaLabel);

        categoriaField = new JTextField();
        categoriaField.setBounds(300, 195, 120, 22);
        contentPane.add(categoriaField);

        JLabel puntajeLabel = new JLabel("Puntaje:");
        puntajeLabel.setBounds(430, 200, 60, 16);
        contentPane.add(puntajeLabel);

        puntajeField = new JTextField();
        puntajeField.setBounds(490, 195, 70, 22);
        contentPane.add(puntajeField);

        JButton addDonacionButton = new JButton("Agregar");
        addDonacionButton.setBounds(490, 225, 70, 25);
        addDonacionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String tipo = (String) tipoComboBox.getSelectedItem();
                    String categoria = categoriaField.getText().trim();
                    String puntajeText = puntajeField.getText().trim();
                    if (categoria.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Categoria requerida", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int puntaje = 0;
                    try {
                        puntaje = Integer.parseInt(puntajeText);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Puntaje debe ser un numero entero", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Object[] row = new Object[] { tipo, categoria, puntaje };
                    donacionesModel.addRow(row);
                    // clear inputs
                    categoriaField.setText("");
                    puntajeField.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error agregando donacion: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        contentPane.add(addDonacionButton);

        String[] cols = new String[] { "Tipo", "Categoria", "Puntaje" };
        donacionesModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        donacionesTable = new JTable(donacionesModel);
        JScrollPane scroll = new JScrollPane(donacionesTable);
        scroll.setBounds(20, 260, 540, 120);
        contentPane.add(scroll);

        JButton removeDonacionButton = new JButton("Quitar");
        removeDonacionButton.setBounds(420, 225, 70, 25);
        removeDonacionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = donacionesTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(null, "Seleccione una donacion para quitar", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                donacionesModel.removeRow(row);
            }
        });
        contentPane.add(removeDonacionButton);

        this.usuarios = api.obtenerUsuarios();
        for (UsuarioDTO u : this.usuarios) {
            if ("DONANTE".equals(u.getRol())) {
                donanteComboBox.addItem(u.getUsername() + " - " + u.getNombre());
            }
        }

        JButton aceptarButton = new JButton("Aceptar");
        aceptarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String descripcion = descripcionField.getText().trim();
                    String observaciones = observacionesField.getText().trim();
                    boolean necesitaVehiculo = necesitaVehiculoCheckBox.isSelected();

                    if (descripcion.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Descripcion requerida", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (donanteComboBox.getSelectedIndex() == -1) {
                        JOptionPane.showMessageDialog(null, "Seleccione un donante", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String selected = (String) donanteComboBox.getSelectedItem();
                    String donanteUsername = selected.split(" - ")[0];

                    List<DonacionDTO> donaciones = new ArrayList<>();
                    for (int i = 0; i < donacionesModel.getRowCount(); i++) {
                        String tipo = (String) donacionesModel.getValueAt(i, 0);
                        String categoria = (String) donacionesModel.getValueAt(i, 1);
                        int puntaje = (Integer) donacionesModel.getValueAt(i, 2);
                        donaciones.add(new DonacionDTO(tipo, categoria, puntaje));
                    }
                    
                    if (donaciones.isEmpty()) {
                        JOptionPane.showMessageDialog(
                            null,
                            "El pedido debe contener al menos una donación.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }

                    api.crearPedidoDonacion(null, descripcion, observaciones, necesitaVehiculo,
                            donanteUsername, donaciones, true);

                    JOptionPane.showMessageDialog(null, "Pedido creado con exito", "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                    setVisible(false);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        aceptarButton.setBounds(360, 400, 100, 25);
        contentPane.add(aceptarButton);

        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        cancelarButton.setBounds(480, 400, 100, 25);
        contentPane.add(cancelarButton);
    }
}