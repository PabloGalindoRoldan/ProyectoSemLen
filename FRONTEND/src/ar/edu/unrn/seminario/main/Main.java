package ar.edu.unrn.seminario.main;

import java.awt.EventQueue;


import ar.edu.unrn.seminario.api.IApi;
import ar.edu.unrn.seminario.api.PersistenceApi;
import ar.edu.unrn.seminario.gui.VentanaPrincipal;

public class Main {

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					IApi api = new PersistenceApi();
					VentanaPrincipal frame = new VentanaPrincipal(api);

					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}