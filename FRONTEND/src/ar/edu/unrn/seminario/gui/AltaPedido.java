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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import ar.edu.unrn.seminario.api.IApi;
import ar.edu.unrn.seminario.dto.UsuarioDTO;

public class AltaPedido extends JFrame {

    private JPanel contentPane;
    private JTextField descripcionField;
    private JTextField solicitanteField;
    private JTextField observacionesField;
    private JCheckBox necesitaVehiculoCheckBox;
    private JComboBox<String> donanteComboBox;

    private List<UsuarioDTO> usuarios = new ArrayList<>();

    public AltaPedido(IApi api) {
        setTitle("Alta Pedido de Donacion");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 480, 320);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel descripcionLabel = new JLabel("Descripcion:");
        descripcionLabel.setBounds(20, 20, 100, 16);
        contentPane.add(descripcionLabel);

        descripcionField = new JTextField();
        descripcionField.setBounds(140, 17, 300, 22);
        contentPane.add(descripcionField);

        JLabel solicitanteLabel = new JLabel("Solicitante:");
        solicitanteLabel.setBounds(20, 55, 100, 16);
        contentPane.add(solicitanteLabel);

        solicitanteField = new JTextField();
        solicitanteField.setBounds(140, 52, 300, 22);
        contentPane.add(solicitanteField);

        JLabel observacionesLabel = new JLabel("Observaciones:");
        observacionesLabel.setBounds(20, 90, 100, 16);
        contentPane.add(observacionesLabel);

        observacionesField = new JTextField();
        observacionesField.setBounds(140, 87, 300, 22);
        contentPane.add(observacionesField);

        necesitaVehiculoCheckBox = new JCheckBox("Necesita vehiculo");
        necesitaVehiculoCheckBox.setBounds(140, 120, 200, 25);
        contentPane.add(necesitaVehiculoCheckBox);

        JLabel donanteLabel = new JLabel("Donante:");
        donanteLabel.setBounds(20, 160, 100, 16);
        contentPane.add(donanteLabel);

        donanteComboBox = new JComboBox<>();
        donanteComboBox.setBounds(140, 155, 300, 25);
        contentPane.add(donanteComboBox);

        // load users and populate only DONANTE role
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
                    String solicitante = solicitanteField.getText().trim();
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

                    // donor username is before ' - ' in the combo item
                    String selected = (String) donanteComboBox.getSelectedItem();
                    String donanteUsername = selected.split(" - ")[0];

                    // id null -> API will auto-assign
                    api.crearPedidoDonacion(null, descripcion, solicitante, observaciones, necesitaVehiculo,
                            donanteUsername, true);

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
        aceptarButton.setBounds(240, 230, 100, 25);
        contentPane.add(aceptarButton);

        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        cancelarButton.setBounds(350, 230, 100, 25);
        contentPane.add(cancelarButton);
    }
}
