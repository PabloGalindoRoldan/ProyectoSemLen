package ar.edu.unrn.seminario.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import ar.edu.unrn.seminario.api.IApi;
import ar.edu.unrn.seminario.dto.VisitaDTO;
import ar.edu.unrn.seminario.dto.OrdenRetiroDTO;

public class ListadoOrdenes extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel model;
    private IApi api;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ListadoOrdenes(IApi api) {
        this.api = api;
        setTitle("Listado de Ordenes de Retiro");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 900, 420);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        String[] columns = new String[] { "ID", "FechaGeneracion", "Estado", "PedidoId", "Voluntario", "#Visitas" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(10, 10, 860, 300);
        contentPane.add(scroll);

        JButton refreshButton = new JButton("Refrescar");
        refreshButton.setBounds(10, 320, 120, 25);
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
        contentPane.add(refreshButton);

        JButton eliminarButton = new JButton("Eliminar Seleccion");
        eliminarButton.setBounds(140, 320, 160, 25);
        eliminarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) { JOptionPane.showMessageDialog(null, "Seleccione fila", "Error", JOptionPane.ERROR_MESSAGE); return; }
                Integer id = (Integer) model.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(null, "Confirma eliminar orden id=" + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try { api.eliminarOrdenRetiro(id); loadData(); JOptionPane.showMessageDialog(null, "Orden eliminada", "Info", JOptionPane.INFORMATION_MESSAGE); } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
                }
            }
        });
        contentPane.add(eliminarButton);

        JButton verVisitasButton = new JButton("Ver Visitas");
        verVisitasButton.setBounds(310, 320, 120, 25);
        verVisitasButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) { JOptionPane.showMessageDialog(null, "Seleccione fila", "Error", JOptionPane.ERROR_MESSAGE); return; }
                Integer id = (Integer) model.getValueAt(row, 0);
                try {
                    OrdenRetiroDTO orden = api.obtenerOrdenRetiroPorId(id);
                    if (orden == null) { JOptionPane.showMessageDialog(null, "Orden no encontrada", "Error", JOptionPane.ERROR_MESSAGE); return; }
                    List<VisitaDTO> visitas = orden.getVisitas();
                    if (visitas == null || visitas.isEmpty()) { JOptionPane.showMessageDialog(null, "No hay visitas", "Info", JOptionPane.INFORMATION_MESSAGE); return; }
                    JDialog dialog = new JDialog(ListadoOrdenes.this, "Visitas de Orden " + id, true);
                    dialog.setSize(500, 300);
                    dialog.setLocationRelativeTo(ListadoOrdenes.this);
                    String[] cols = new String[] { "ID", "Visitante", "FechaHora", "CantidadBienes", "VisitaFinal" };
                    DefaultTableModel dm = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r,int c){ return false; } };
                    JTable t = new JTable(dm);
                    for (VisitaDTO v : visitas) {
                        Object[] r = new Object[] { v.getId(), v.getVisitante(), v.getFechaHora() == null ? "" : v.getFechaHora().format(FORMATTER), v.getCantidadBienesRecogidos(), v.isVisitaFinal() };
                        dm.addRow(r);
                    }
                    JScrollPane sc = new JScrollPane(t);
                    sc.setBounds(10,10,480,220);
                    dialog.getContentPane().setLayout(null);
                    dialog.getContentPane().add(sc);
                    JButton close = new JButton("Cerrar"); close.setBounds(380,235,100,25); close.addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e){ dialog.dispose(); }});
                    dialog.getContentPane().add(close);
                    dialog.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        contentPane.add(verVisitasButton);

        JButton agregarVisitaButton = new JButton("Agregar Visita");
        agregarVisitaButton.setBounds(440, 320, 140, 25);
        agregarVisitaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) { JOptionPane.showMessageDialog(null, "Seleccione fila", "Error", JOptionPane.ERROR_MESSAGE); return; }
                Integer id = (Integer) model.getValueAt(row, 0);
                AltaVisita alta = new AltaVisita(api, id);
                alta.setLocationRelativeTo(ListadoOrdenes.this);
                alta.setVisible(true);
                loadData();
            }
        });
        contentPane.add(agregarVisitaButton);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            List<OrdenRetiroDTO> list = api.obtenerOrdenesRetiro();
            for (OrdenRetiroDTO o : list) {
                int visitasCount = o.getVisitas() == null ? 0 : o.getVisitas().size();
                String fecha = "";
                if (o.getFechaGeneracion() != null) fecha = o.getFechaGeneracion().format(FORMATTER);
                Object[] row = new Object[] { o.getIdOrdenes(), fecha, o.getEstado(), o.getPedidoId(), o.getVoluntarioUsername(), visitasCount };
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener ordenes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
