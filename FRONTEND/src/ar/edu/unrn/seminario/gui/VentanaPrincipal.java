package ar.edu.unrn.seminario.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ar.edu.unrn.seminario.api.IApi;
import ar.edu.unrn.seminario.api.MemoryApi;

public class VentanaPrincipal extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IApi api = new MemoryApi();
					VentanaPrincipal frame = new VentanaPrincipal(api);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VentanaPrincipal(IApi api) {
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu usuarioMenu = new JMenu("Usuarios");
		menuBar.add(usuarioMenu);

		JMenuItem altaUsuarioMenuItem = new JMenuItem("Alta/Modificación");
		altaUsuarioMenuItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				AltaUsuario alta = new AltaUsuario(api);
				alta.setLocationRelativeTo(null);
				alta.setVisible(true);
			}
			
		});
		usuarioMenu.add(altaUsuarioMenuItem);

		JMenuItem listadoUsuarioMenuItem = new JMenuItem("Listado");
		listadoUsuarioMenuItem.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				ListadoUsuario listado= new ListadoUsuario(api);
				listado.setLocationRelativeTo(null);
				listado.setVisible(true);
			}
			
		});
		usuarioMenu.add(listadoUsuarioMenuItem);

		JMenu configuracionMenu = new JMenu("Configuración");
		menuBar.add(configuracionMenu);

		JMenuItem salirMenuItem = new JMenuItem("Salir");
		configuracionMenu.add(salirMenuItem);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}

}
