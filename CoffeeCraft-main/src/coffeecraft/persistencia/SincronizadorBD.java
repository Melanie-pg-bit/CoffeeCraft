package coffeecraft.persistencia;

import coffeecraft.modelo.Pedido;
import coffeecraft.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/** Copia clientes y pedidos de la aplicación a la base de datos relacional (en una transacción). */
public class SincronizadorBD {
    private final ConexionBD conexion;

    public SincronizadorBD(ConexionBD conexion) {
        this.conexion = conexion;
    }

    /** @return mensaje con la cantidad de clientes y pedidos sincronizados */
    public String sincronizar(List<Usuario> usuarios) throws SQLException {
        conexion.crearTablas();
        PedidoDAO pedidoDAO = new PedidoDAO(conexion);
        int clientes = 0;
        int pedidos = 0;

        try (Connection c = conexion.abrir()) {
            c.setAutoCommit(false);
            try {
                // Se reescribe todo: primero pedidos (por la clave foránea) y luego clientes.
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM pedidos")) {
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM clientes")) {
                    ps.executeUpdate();
                }
                String sql = "INSERT INTO clientes (id, nombre, email, telefono, direccion) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    for (Usuario u : usuarios) {
                        ps.setInt(1, u.getId());
                        ps.setString(2, u.getNombre());
                        ps.setString(3, u.getEmail());
                        ps.setString(4, u.getTelefono());
                        ps.setString(5, u.getDireccion());
                        ps.executeUpdate();
                        clientes++;
                    }
                }
                for (Usuario u : usuarios) {
                    for (Pedido p : u.verHistorial()) {
                        pedidoDAO.insertar(c, p, u.getId());
                        pedidos++;
                    }
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
        return clientes + " cliente(s) y " + pedidos + " pedido(s) guardados en la base de datos.";
    }
}