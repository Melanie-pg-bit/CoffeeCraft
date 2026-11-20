package coffeecraft.persistencia;

import coffeecraft.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/** Acceso a la tabla clientes mediante JDBC (consultas preparadas). */
public class ClienteDAO {
    private final ConexionBD conexion;

    public ClienteDAO(ConexionBD conexion) {
        this.conexion = conexion;
    }

    public void insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO clientes (id, nombre, email, telefono, direccion) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = conexion.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, u.getId());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getTelefono());
            ps.setString(5, u.getDireccion());
            ps.executeUpdate();
        }
    }

    public void actualizar(Usuario u) throws SQLException {
        String sql = "UPDATE clientes SET nombre = ?, email = ?, telefono = ?, direccion = ? WHERE id = ?";
        try (Connection c = conexion.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getTelefono());
            ps.setString(4, u.getDireccion());
            ps.setInt(5, u.getId());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection c = conexion.abrir()) {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM pedidos WHERE cliente_id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM clientes WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }

    public boolean existe(int id) throws SQLException {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM clientes WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Lista los clientes guardados (sin sus pedidos). */
    public ArrayList<Usuario> listar() throws SQLException {
        ArrayList<Usuario> lista = new ArrayList<>();
        try (Connection c = conexion.abrir();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT id, nombre, email, telefono, direccion FROM clientes ORDER BY id")) {
            while (rs.next()) {
                lista.add(new Usuario(rs.getInt("id"), rs.getString("nombre"), rs.getString("email"),
                        rs.getString("telefono"), rs.getString("direccion")));
            }
        }
        return lista;
    }
}