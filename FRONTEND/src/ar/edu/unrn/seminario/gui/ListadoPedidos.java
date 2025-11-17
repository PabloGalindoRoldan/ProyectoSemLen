package ar.edu.unrn.seminario.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import ar.edu.unrn.seminario.api.IApi;
import ar.edu.unrn.seminario.dto.DonacionDTO;
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
        setBounds(100, 100, 950, 420);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        //defino las columnas
        String[] columns = new String[] { "ID", "FechaCreacion", "Descripcion", "Donante", "PuntajeTotal", "Observaciones", "NecesitaVehiculo", "Activo" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { //sobreescribo el metodo para que las filas no sean editables. 
                return false;
            }
        };
        
        //inicializo la tabla no editable
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(10, 10, 920, 300);
        contentPane.add(scroll); // barra de navegacion

        JButton refreshButton = new JButton("Refrescar");
        refreshButton.setBounds(10, 320, 120, 25);
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
        contentPane.add(refreshButton); // agrego boton de refresh (corre nuevamente loadData())

        JButton eliminarButton = new JButton("Eliminar Seleccion");
        eliminarButton.setBounds(140, 320, 160, 25);
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

        JButton verDonacionesButton = new JButton("Ver Donaciones");
        verDonacionesButton.setBounds(310, 320, 140, 25);
        verDonacionesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(null, "Seleccione una fila para ver sus donaciones", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Integer id = (Integer) model.getValueAt(row, 0);
                try {
                    PedidoDonacionDTO pedido = api.obtenerPedidoDonacionPorId(id);
                    if (pedido == null) {
                        JOptionPane.showMessageDialog(null, "Pedido no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    List<DonacionDTO> donaciones = pedido.getDonaciones();
                    if (donaciones == null || donaciones.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El pedido no contiene donaciones", "Info", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    JDialog dialog = new JDialog(ListadoPedidos.this, "Donaciones del Pedido " + id, true);
                    dialog.setSize(500, 300);
                    dialog.setLocationRelativeTo(ListadoPedidos.this);

                    String[] cols = new String[] { "Tipo", "Categoria", "Puntaje" };
                    DefaultTableModel dm = new DefaultTableModel(cols, 0) {
                        @Override
                        public boolean isCellEditable(int r, int c) { return false; }
                    };
                    JTable t = new JTable(dm);
                    for (DonacionDTO d : donaciones) {
                        Object[] r = new Object[] { d.getTipoDonacion(), d.getCategoria(), d.getPuntaje() };
                        dm.addRow(r);
                    }

                    JScrollPane sc = new JScrollPane(t);
                    sc.setBounds(10, 10, 480, 220);
                    dialog.getContentPane().setLayout(null);
                    dialog.getContentPane().add(sc);

                    JButton close = new JButton("Cerrar");
                    close.setBounds(380, 235, 100, 25);
                    close.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            dialog.dispose();
                        }
                    });
                    dialog.getContentPane().add(close);

                    dialog.setVisible(true);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al obtener donaciones: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        contentPane.add(verDonacionesButton);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            List<PedidoDonacionDTO> pedidos = api.obtenerPedidosDonacion(); //obtiene los pedidos de donacion
            for (PedidoDonacionDTO p : pedidos) {
                String fecha = "";
                if (p.getFechaCreacion() != null) {
                    fecha = p.getFechaCreacion().format(FORMATTER); //obtiene la fecha de creacion
                }
                //se crea una fila por pedido de donacion
                Object[] row = new Object[] { p.getId(), fecha, p.getDescripcion(), p.getDonanteUsername(), p.getPuntajeTotal(), p.getObservaciones(), p.isNecesitaVehiculo(), p.isActivo() };
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener pedidos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}