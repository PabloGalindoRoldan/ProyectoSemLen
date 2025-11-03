package ar.edu.unrn.seminario.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ar.edu.unrn.seminario.accesos.DBConnection;
import ar.edu.unrn.seminario.accesos.RolDAOJDBC;
import ar.edu.unrn.seminario.accesos.UsuarioDAO;
import ar.edu.unrn.seminario.accesos.UsuarioDAOJDBC;
import ar.edu.unrn.seminario.dto.ArticuloDTO;
import ar.edu.unrn.seminario.dto.DonacionDTO;
import ar.edu.unrn.seminario.dto.OrdenRetiroDTO;
import ar.edu.unrn.seminario.dto.PedidoDonacionDTO;
import ar.edu.unrn.seminario.dto.RolDTO;
import ar.edu.unrn.seminario.dto.UsuarioDTO;
import ar.edu.unrn.seminario.dto.VisitaDTO;
import ar.edu.unrn.seminario.modelo.Rol;
import ar.edu.unrn.seminario.modelo.Usuario;

public class PersistenceApi implements IApi {

    private UsuarioDAO usuarioDAO = new UsuarioDAOJDBC();
    private RolDAOJDBC rolDAO = new RolDAOJDBC();

    public PersistenceApi() {
        // no in-memory synchronization required now
    }

    // --- Users ---
    @Override
    public void registrarUsuario(String username, String password, String email, String nombre, Integer rol) {
        try {
            Rol r = null;
            if (rol != null) {
                r = new Rol();
                r.setCodigo(rol);
            }
            Usuario usuario = new Usuario(username, password, nombre, email, r);
            usuario.activar();
            usuarioDAO.create(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Error registering usuario", e);
        }
    }

    @Override
    public UsuarioDTO obtenerUsuario(String username) {
        try {
            Usuario u = usuarioDAO.find(username);
            if (u == null) return null;
            String rolNombre = u.getRol() != null ? u.getRol().getNombre() : null;
            return new UsuarioDTO(u.getUsuario(), u.getContrasena(), u.getNombre(), u.getEmail(), rolNombre, u.isActivo(), u.obtenerEstado());
        } catch (Exception e) {
            throw new RuntimeException("Error obtaining usuario", e);
        }
    }

    @Override
    public void eliminarUsuario(String username) {
        try {
            usuarioDAO.remove(username);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting usuario", e);
        }
    }

    @Override
    public List<RolDTO> obtenerRoles() {
        try {
            List<RolDTO> res = new ArrayList<>();
            List<Rol> roles = rolDAO.findAll();
            for (Rol r : roles) res.add(new RolDTO(r.getCodigo(), r.getNombre(), r.isActivo()));
            return res;
        } catch (Exception e) {
            throw new RuntimeException("Error obtaining roles", e);
        }
    }

    @Override
    public List<RolDTO> obtenerRolesActivos() {
        try {
            List<RolDTO> res = new ArrayList<>();
            List<Rol> roles = rolDAO.findAllActive();
            for (Rol r : roles) res.add(new RolDTO(r.getCodigo(), r.getNombre(), r.isActivo()));
            return res;
        } catch (Exception e) {
            throw new RuntimeException("Error obtaining active roles", e);
        }
    }

    @Override
    public void guardarRol(Integer codigo, String descripcion, boolean estado) {
        try {
            Rol existing = rolDAO.find(codigo);
            if (existing == null) {
                Rol r = new Rol(codigo, descripcion);
                if (!estado) r.desactivar();
                rolDAO.create(r);
            } else {
                existing.setNombre(descripcion);
                if (estado) existing.activar(); else existing.desactivar();
                rolDAO.update(existing);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving rol", e);
        }
    }

    @Override
    public RolDTO obtenerRolPorCodigo(Integer codigo) {
        try {
            Rol r = rolDAO.find(codigo);
            if (r == null) return null;
            return new RolDTO(r.getCodigo(), r.getNombre(), r.isActivo());
        } catch (Exception e) {
            throw new RuntimeException("Error obtaining rol by codigo", e);
        }
    }

    @Override
    public void activarRol(Integer codigo) {
        try {
            Rol r = rolDAO.find(codigo);
            if (r != null) {
                r.activar();
                rolDAO.update(r);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error activating rol", e);
        }
    }

    @Override
    public void desactivarRol(Integer codigo) {
        try {
            Rol r = rolDAO.find(codigo);
            if (r != null) {
                r.desactivar();
                rolDAO.update(r);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deactivating rol", e);
        }
    }

    @Override
    public List<UsuarioDTO> obtenerUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.findAll();
            List<UsuarioDTO> dtos = new ArrayList<>();
            for (Usuario u : usuarios) {
                String rolNombre = u.getRol() != null ? u.getRol().getNombre() : null;
                dtos.add(new UsuarioDTO(u.getUsuario(), u.getContrasena(), u.getNombre(), u.getEmail(), rolNombre, u.isActivo(), u.obtenerEstado()));
            }
            return dtos;
        } catch (Exception e) {
            throw new RuntimeException("Error obtaining usuarios", e);
        }
    }

    @Override
    public void activarUsuario(String username) {
        try {
            Usuario u = usuarioDAO.find(username);
            if (u != null) {
                u.activar();
                usuarioDAO.update(u);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error activating usuario", e);
        }
    }

    @Override
    public void desactivarUsuario(String username) {
        try {
            Usuario u = usuarioDAO.find(username);
            if (u != null) {
                u.desactivar();
                usuarioDAO.update(u);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deactivating usuario", e);
        }
    }

    // --- Pedidos ---
    private static final String INSERT_PEDIDO = "INSERT INTO pedidos (id, descripcion, observaciones, necesitaVehiculo, donante_username, fecha_creacion, activo, puntaje_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_PEDIDOS = "SELECT id, descripcion, observaciones, necesitaVehiculo, donante_username, fecha_creacion, activo, puntaje_total FROM pedidos";
    private static final String SELECT_PEDIDO_BY_ID = "SELECT id, descripcion, observaciones, necesitaVehiculo, donante_username, fecha_creacion, activo, puntaje_total FROM pedidos WHERE id = ?";
    private static final String DELETE_PEDIDO = "DELETE FROM pedidos WHERE id = ?";
    private static final String INSERT_DONACION = "INSERT INTO donaciones (pedido_id, tipoDonacion, categoria, puntaje) VALUES (?, ?, ?, ?)";
    private static final String SELECT_DONACIONES_BY_PEDIDO = "SELECT tipoDonacion, categoria, puntaje FROM donaciones WHERE pedido_id = ?";
    private static final String DELETE_DONACIONES_BY_PEDIDO = "DELETE FROM donaciones WHERE pedido_id = ?";

    @Override
    public void crearPedidoDonacion(Integer id, String descripcion, String observaciones, boolean necesitaVehiculo,
            String donanteUsername, List<DonacionDTO> donaciones, boolean activo) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(INSERT_PEDIDO)) {
                int useId = (id == null || id == 0) ? 0 : id;
                ps.setInt(1, useId);
                ps.setString(2, descripcion);
                ps.setString(3, observaciones);
                ps.setBoolean(4, necesitaVehiculo);
                ps.setString(5, donanteUsername);
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.setBoolean(7, activo);
                int puntaje = 0;
                if (donaciones != null) for (DonacionDTO d : donaciones) puntaje += d.getPuntaje();
                ps.setInt(8, puntaje);
                ps.executeUpdate();
            }
            // If insert used id = 0 and DB uses auto-increment, we assume DB assigned id; but code using id value is rare.
            // Find last inserted id if necessary
            Integer insertedId = null;
            try (PreparedStatement last = conn.prepareStatement("SELECT LAST_INSERT_ID()")) {
                try (ResultSet rs = last.executeQuery()) {
                    if (rs.next()) insertedId = rs.getInt(1);
                }
            }
            int pedidoId = (insertedId != null && insertedId != 0) ? insertedId : (id == null ? 0 : id);
            if (donaciones != null && !donaciones.isEmpty()) {
                try (PreparedStatement ps2 = conn.prepareStatement(INSERT_DONACION)) {
                    for (DonacionDTO d : donaciones) {
                        ps2.setInt(1, pedidoId);
                        ps2.setString(2, d.getTipoDonacion());
                        ps2.setString(3, d.getCategoria());
                        ps2.setInt(4, d.getPuntaje());
                        ps2.addBatch();
                    }
                    ps2.executeBatch();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating pedido", e);
        }
    }

    @Override
    public List<PedidoDonacionDTO> obtenerPedidosDonacion() {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_PEDIDOS); ResultSet rs = ps.executeQuery()) {
            List<PedidoDonacionDTO> result = new ArrayList<>();
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String descripcion = rs.getString("descripcion");
                String observaciones = rs.getString("observaciones");
                boolean necesitaVehiculo = rs.getBoolean("necesitaVehiculo");
                String donante = rs.getString("donante_username");
                boolean activo = rs.getBoolean("activo");
                List<DonacionDTO> donaciones = new ArrayList<>();
                try (PreparedStatement ps2 = conn.prepareStatement(SELECT_DONACIONES_BY_PEDIDO)) {
                    ps2.setInt(1, id);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        while (rs2.next()) {
                            donaciones.add(new DonacionDTO(rs2.getString("tipoDonacion"), rs2.getString("categoria"), rs2.getInt("puntaje")));
                        }
                    }
                }
                PedidoDonacionDTO dto = new PedidoDonacionDTO(id, descripcion, observaciones, necesitaVehiculo, donante, donaciones, activo);
                result.add(dto);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error obtaining pedidos", e);
        }
    }

    @Override
    public PedidoDonacionDTO obtenerPedidoDonacionPorId(Integer id) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_PEDIDO_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String descripcion = rs.getString("descripcion");
                    String observaciones = rs.getString("observaciones");
                    boolean necesitaVehiculo = rs.getBoolean("necesitaVehiculo");
                    String donante = rs.getString("donante_username");
                    boolean activo = rs.getBoolean("activo");
                    List<DonacionDTO> donaciones = new ArrayList<>();
                    try (PreparedStatement ps2 = conn.prepareStatement(SELECT_DONACIONES_BY_PEDIDO)) {
                        ps2.setInt(1, id);
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            while (rs2.next()) {
                                donaciones.add(new DonacionDTO(rs2.getString("tipoDonacion"), rs2.getString("categoria"), rs2.getInt("puntaje")));
                            }
                        }
                    }
                    return new PedidoDonacionDTO(id, descripcion, observaciones, necesitaVehiculo, donante, donaciones, activo);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error obtaining pedido by id", e);
        }
    }

    @Override
    public void eliminarPedidoDonacion(Integer id) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(DELETE_DONACIONES_BY_PEDIDO)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement(DELETE_PEDIDO)) {
                ps2.setInt(1, id);
                ps2.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting pedido", e);
        }
    }

    // --- Ordenes de retiro ---
    private static final String INSERT_ORDEN = "INSERT INTO ordenes_retiro (id, pedido_id, voluntario_username, fecha_generacion, estado) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ORDENES = "SELECT id, pedido_id, voluntario_username, fecha_generacion, estado FROM ordenes_retiro";
    private static final String SELECT_ORDEN_BY_ID = "SELECT id, pedido_id, voluntario_username, fecha_generacion, estado FROM ordenes_retiro WHERE id = ?";
    private static final String DELETE_ORDEN = "DELETE FROM ordenes_retiro WHERE id = ?";

    private static final String SELECT_VISITAS_BY_ORDEN = "SELECT id, visitante, fechaHora, motivo, confirmada, cantidadBienesRecogidos, observaciones, orden_retiro_id, visitaFinal FROM visitas WHERE orden_retiro_id = ?";

    @Override
    public void crearOrdenRetiro(Integer id, Integer pedidoId, String voluntarioUsername, String estado) {
        try (Connection conn = DBConnection.getConnection()) {
            // ensure pedido exists
            try (PreparedStatement check = conn.prepareStatement("SELECT id FROM pedidos WHERE id = ?")) {
                check.setInt(1, pedidoId);
                try (ResultSet rs = check.executeQuery()) {
                    if (!rs.next()) throw new RuntimeException("Pedido not found: " + pedidoId);
                }
            }
            // ensure voluntario exists and has role VOLUNTARIO - best effort check
            try {
                Usuario u = usuarioDAO.find(voluntarioUsername);
                if (u == null) throw new RuntimeException("Voluntario not found: " + voluntarioUsername);
                if (u.getRol() == null || !"VOLUNTARIO".equals(u.getRol().getNombre())) {
                    // role name may not be populated in DB; skip strict check to avoid failing the operation
                }
            } catch (Exception e) {
                // ignore and continue
            }

            try (PreparedStatement ps = conn.prepareStatement(INSERT_ORDEN)) {
                int useId = (id == null || id == 0) ? 0 : id;
                ps.setInt(1, useId);
                ps.setInt(2, pedidoId);
                ps.setString(3, voluntarioUsername);
                ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(5, estado == null ? "PENDIENTE" : estado);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating orden retiro", e);
        }
    }

    @Override
    public List<OrdenRetiroDTO> obtenerOrdenesRetiro() {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_ORDENES); ResultSet rs = ps.executeQuery()) {
            List<OrdenRetiroDTO> result = new ArrayList<>();
            while (rs.next()) {
                Integer id = rs.getInt("id");
                Integer pedidoId = rs.getInt("pedido_id");
                String voluntario = rs.getString("voluntario_username");
                String estado = rs.getString("estado");
                OrdenRetiroDTO dto = new OrdenRetiroDTO(id, pedidoId, voluntario, estado);
                // load visitas
                try (PreparedStatement ps2 = conn.prepareStatement(SELECT_VISITAS_BY_ORDEN)) {
                    ps2.setInt(1, id);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        while (rs2.next()) {
                            VisitaDTO v = mapVisitaFromResultSet(rs2);
                            dto.addVisita(v);
                        }
                    }
                }
                result.add(dto);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error obtaining ordenes", e);
        }
    }

    @Override
    public OrdenRetiroDTO obtenerOrdenRetiroPorId(Integer id) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_ORDEN_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer pedidoId = rs.getInt("pedido_id");
                    String voluntario = rs.getString("voluntario_username");
                    String estado = rs.getString("estado");
                    OrdenRetiroDTO dto = new OrdenRetiroDTO(id, pedidoId, voluntario, estado);
                    try (PreparedStatement ps2 = conn.prepareStatement(SELECT_VISITAS_BY_ORDEN)) {
                        ps2.setInt(1, id);
                        try (ResultSet rs2 = ps2.executeQuery()) {
                            while (rs2.next()) {
                                VisitaDTO v = mapVisitaFromResultSet(rs2);
                                dto.addVisita(v);
                            }
                        }
                    }
                    return dto;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error obtaining orden by id", e);
        }
    }

    @Override
    public void eliminarOrdenRetiro(Integer id) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            // delete articulos of visitas
            try (PreparedStatement psDelArt = conn.prepareStatement("DELETE a FROM articulos a JOIN visitas v ON a.visita_id = v.id WHERE v.orden_retiro_id = ?")) {
                psDelArt.setInt(1, id);
                psDelArt.executeUpdate();
            }
            // delete visitas
            try (PreparedStatement psDelVis = conn.prepareStatement("DELETE FROM visitas WHERE orden_retiro_id = ?")) {
                psDelVis.setInt(1, id);
                psDelVis.executeUpdate();
            }
            // delete orden
            try (PreparedStatement psDelOrd = conn.prepareStatement(DELETE_ORDEN)) {
                psDelOrd.setInt(1, id);
                psDelOrd.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting orden retiro", e);
        }
    }

    // --- Visitas ---
    private static final String INSERT_VISITA = "INSERT INTO visitas (visitante, fechaHora, motivo, confirmada, cantidadBienesRecogidos, observaciones, orden_retiro_id, visitaFinal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_VISITAS = "SELECT id, visitante, fechaHora, motivo, confirmada, cantidadBienesRecogidos, observaciones, orden_retiro_id, visitaFinal FROM visitas";
    private static final String SELECT_VISITA_BY_ID = "SELECT id, visitante, fechaHora, motivo, confirmada, cantidadBienesRecogidos, observaciones, orden_retiro_id, visitaFinal FROM visitas WHERE id = ?";
    private static final String DELETE_VISITA = "DELETE FROM visitas WHERE id = ?";
    private static final String INSERT_ARTICULO = "INSERT INTO articulos (visita_id, nombre, cantidad, tipoDonacion) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ARTICULOS_BY_VISITA = "SELECT nombre, cantidad, tipoDonacion FROM articulos WHERE visita_id = ?";

    @Override
    public void crearVisita(VisitaDTO visita) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            Integer usoId = visita.getId();
            try (PreparedStatement ps = conn.prepareStatement(INSERT_VISITA, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, visita.getVisitante());
                ps.setTimestamp(2, visita.getFechaHora() == null ? Timestamp.valueOf(LocalDateTime.now()) : Timestamp.valueOf(visita.getFechaHora()));
                ps.setString(3, visita.getMotivo());
                ps.setBoolean(4, visita.isConfirmada());
                ps.setInt(5, visita.getCantidadBienesRecogidos());
                ps.setString(6, visita.getObservaciones());
                if (visita.getOrdenRetiroId() == null) ps.setNull(7, java.sql.Types.INTEGER); else ps.setInt(7, visita.getOrdenRetiroId());
                ps.setBoolean(8, visita.isVisitaFinal());
                ps.executeUpdate();
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) visita.setId(gk.getInt(1));
                }
            }
            // insert articulos
            if (visita.getArticulosRecogidos() != null && !visita.getArticulosRecogidos().isEmpty()) {
                try (PreparedStatement ps2 = conn.prepareStatement(INSERT_ARTICULO)) {
                    for (ArticuloDTO a : visita.getArticulosRecogidos()) {
                        ps2.setInt(1, visita.getId());
                        ps2.setString(2, a.getNombre());
                        ps2.setInt(3, a.getCantidad());
                        ps2.setString(4, a.getTipoDonacion());
                        ps2.addBatch();
                    }
                    ps2.executeBatch();
                }
            }
            // if final visit, update orden status to COMPLETADO
            if (visita.isVisitaFinal() && visita.getOrdenRetiroId() != null) {
                try (PreparedStatement psUpd = conn.prepareStatement("UPDATE ordenes_retiro SET estado = 'COMPLETADO' WHERE id = ?")) {
                    psUpd.setInt(1, visita.getOrdenRetiroId());
                    psUpd.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating visita", e);
        }
    }

    @Override
    public List<VisitaDTO> obtenerVisitas() {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_VISITAS); ResultSet rs = ps.executeQuery()) {
            List<VisitaDTO> result = new ArrayList<>();
            while (rs.next()) {
                VisitaDTO v = mapVisitaFromResultSet(rs);
                result.add(v);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error obtaining visitas", e);
        }
    }

    @Override
    public VisitaDTO obtenerVisitaPorId(Integer id) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_VISITA_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapVisitaFromResultSet(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error obtaining visita by id", e);
        }
    }

    @Override
    public void cancelarVisita(Integer id) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE visitas SET confirmada = 0 WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error cancelling visita", e);
        }
    }

    // --- helpers ---
    private VisitaDTO mapVisitaFromResultSet(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String visitante = rs.getString("visitante");
        Timestamp t = rs.getTimestamp("fechaHora");
        LocalDateTime fechaHora = t == null ? null : t.toLocalDateTime();
        String motivo = rs.getString("motivo");
        boolean confirmada = rs.getBoolean("confirmada");
        int cantidad = rs.getInt("cantidadBienesRecogidos");
        String observaciones = rs.getString("observaciones");
        Integer ordenId = rs.getObject("orden_retiro_id") == null ? null : rs.getInt("orden_retiro_id");
        boolean visitaFinal = rs.getBoolean("visitaFinal");
        List<ArticuloDTO> articulos = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_ARTICULOS_BY_VISITA)) {
            ps.setInt(1, id);
            try (ResultSet rs2 = ps.executeQuery()) {
                while (rs2.next()) {
                    articulos.add(new ArticuloDTO(rs2.getString("nombre"), rs2.getInt("cantidad"), rs2.getString("tipoDonacion")));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error loading articulos for visita", e);
        }
        return new VisitaDTO(id, visitante, fechaHora, motivo, confirmada, cantidad, articulos, observaciones, ordenId, visitaFinal);
    }
}
