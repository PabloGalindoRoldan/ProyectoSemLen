package ar.edu.unrn.seminario.accesos;

import java.util.List;

import ar.edu.unrn.seminario.modelo.Usuario;

public class TestAcceso {

    public static void main(String[] args) {
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAOJDBC();

            System.out.println("Usuarios actuales en la base de datos:");
            List<Usuario> usuarios = usuarioDAO.findAll();
            for (Usuario u : usuarios) {
                System.out.println("- usuario=" + u.getUsuario() + ", nombre=" + u.getNombre() + ", email=" + u.getEmail() + ", rolCodigo=" + (u.getRol() != null ? u.getRol().getCodigo() : "null") + ", activo=" + u.isActivo());
            }

            // Ejemplo de inserción
            Usuario nuevo = new Usuario("testuser", "pass123", "Test User", "test@example.com", null);
            try {
                usuarioDAO.create(nuevo);
                System.out.println("Usuario 'testuser' creado correctamente.");
            } catch (Exception e) {
                System.out.println("No se pudo crear el usuario: " + e.getMessage());
            }

            System.out.println("Listado luego de intento de insercion:");
            usuarios = usuarioDAO.findAll();
            for (Usuario u : usuarios) {
                System.out.println("- usuario=" + u.getUsuario() + ", nombre=" + u.getNombre());
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error durante el acceso a la base de datos: " + e.getMessage());
        }
    }
}
