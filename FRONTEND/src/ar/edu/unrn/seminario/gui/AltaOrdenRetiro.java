package ar.edu.unrn.seminario.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ar.edu.unrn.seminario.api.IApi;
import ar.edu.unrn.seminario.dto.PedidoDonacionDTO;
import ar.edu.unrn.seminario.dto.UsuarioDTO;

public class AltaOrdenRetiro extends JFrame {

    private JPanel contentPane;
    private JComboBox<String> pedidoComboBox;
    private JComboBox<String> voluntarioComboBox;

    private List<PedidoDonacionDTO> pedidos = new ArrayList<>();
    private List<UsuarioDTO> usuarios = new ArrayList<>();

    public AltaOrdenRetiro(IApi api) {
        setTitle("Alta Orden de Retiro");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 500, 220);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JLabel pedidoLabel = new JLabel("Pedido:");
        pedidoLabel.setBounds(20, 20, 80, 16);
        contentPane.add(pedidoLabel);

        pedidoComboBox = new JComboBox<>();
        pedidoComboBox.setBounds(120, 16, 340, 25);
        contentPane.add(pedidoComboBox);

        JLabel voluntarioLabel = new JLabel("Voluntario:");
        voluntarioLabel.setBounds(20, 60, 80, 16);
        contentPane.add(voluntarioLabel);

        voluntarioComboBox = new JComboBox<>();
        voluntarioComboBox.setBounds(120, 56, 340, 25);
        contentPane.add(voluntarioComboBox);

        // cargar pedidos
        this.pedidos = api.obtenerPedidosDonacion();
        for (PedidoDonacionDTO p : this.pedidos) {
            pedidoComboBox.addItem(p.getId() + " - " + p.getDescripcion());
        }

        // cargar usuarios voluntarios
        this.usuarios = api.obtenerUsuarios();
        for (UsuarioDTO u : this.usuarios) {
            if ("VOLUNTARIO".equals(u.getRol())) {
                voluntarioComboBox.addItem(u.getUsername() + " - " + u.getNombre());
            }
        }

        JButton aceptarButton = new JButton("Aceptar");
        aceptarButton.setBounds(260, 120, 100, 25);
        aceptarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (pedidoComboBox.getSelectedIndex() == -1) {
                        JOptionPane.showMessageDialog(null, "Seleccione un pedido", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (voluntarioComboBox.getSelectedIndex() == -1) {
                        JOptionPane.showMessageDialog(null, "Seleccione un voluntario", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String pedidoSel = (String) pedidoComboBox.getSelectedItem();
                    Integer pedidoId = Integer.parseInt(pedidoSel.split(" - ")[0]);
                    String volSel = (String) voluntarioComboBox.getSelectedItem();
                    String voluntarioUsername = volSel.split(" - ")[0];

                    api.crearOrdenRetiro(null, pedidoId, voluntarioUsername, null);
                    JOptionPane.showMessageDialog(null, "Orden creada con exito", "Info", JOptionPane.INFORMATION_MESSAGE);
                    setVisible(false);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        contentPane.add(aceptarButton);

        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.setBounds(370, 120, 90, 25);
        cancelarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        contentPane.add(cancelarButton);
    }
}
