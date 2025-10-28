package ar.edu.unrn.seminario.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import ar.edu.unrn.seminario.api.IApi;
import ar.edu.unrn.seminario.dto.PedidoDonacionDTO;

public class ListadoPedidos extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel model;
    private IApi api;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ListadoPedidos(IApi api) {
        this.api = api;
        setTitle("Listado de Pedidos de Donacion");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 900, 420);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        String[] columns = new String[] { "ID", "FechaCreacion", "Descripcion", "Solicitante", "Donante", "Observaciones", "NecesitaVehiculo", "Activo" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(10, 10, 860, 320);
        contentPane.add(scroll);

        JButton refreshButton = new JButton("Refrescar");
        refreshButton.setBounds(10, 340, 120, 25);
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
        contentPane.add(refreshButton);

        JButton eliminarButton = new JButton("Eliminar Seleccion");
        eliminarButton.setBounds(140, 340, 160, 25);
        eliminarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Integer id = (Integer) model.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(null, "Confirma eliminar pedido id=" + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        api.eliminarPedidoDonacion(id);
                        loadData();
                        JOptionPane.showMessageDialog(null, "Pedido eliminado", "Info", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        contentPane.add(eliminarButton);

        loadData();
    }

    private void loadData() {
        // clear
        model.setRowCount(0);
        try {
            List<PedidoDonacionDTO> pedidos = api.obtenerPedidosDonacion();
            for (PedidoDonacionDTO p : pedidos) {
                String fecha = "";
                if (p.getFechaCreacion() != null) {
                    fecha = p.getFechaCreacion().format(FORMATTER);
                }
                Object[] row = new Object[] { p.getId(), fecha, p.getDescripcion(), p.getSolicitante(), p.getDonanteUsername(), p.getObservaciones(), p.isNecesitaVehiculo(), p.isActivo() };
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener pedidos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}