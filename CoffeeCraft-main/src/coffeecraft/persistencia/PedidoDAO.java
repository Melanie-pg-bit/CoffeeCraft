package coffeecraft.persistencia;

import coffeecraft.modelo.Bebida;
import coffeecraft.modelo.Pedido;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

/** Acceso a la tabla pedidos mediante JDBC. Guarda un resumen de cada pedido. */
public class PedidoDAO {
    private final ConexionBD conexion;

    public PedidoDAO(ConexionBD conexion) {
        this.conexion = conexion;
    }

    public void insertar(Pedido p, int clienteId) throws SQLException {
        String sql = "INSERT INTO pedidos (id, cliente_id, fecha, estado, detalle, total, minutos) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = conexion.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            enlazar(ps, p, clienteId);
            ps.executeUpdate();
        }
    }

    /** Variante para usar dentro de una transacción ya abierta. */
    void insertar(Connection c, Pedido p, int clienteId) throws SQLException {
        String sql = "INSERT INTO pedidos (id, cliente_id, fecha, estado, detalle, total, minutos) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            enlazar(ps, p, clienteId);
            ps.executeUpdate();
        }
    }

    private void enlazar(PreparedStatement ps, Pedido p, int clienteId) throws SQLException {
        StringBuilder detalle = new StringBuilder();
        for (Bebida b : p.getBebidas()) {
            if (detalle.length() > 0) {
                detalle.append(" || ");
            }
            detalle.append(b.getDescripcion());
        }
        ps.setInt(1, p.getId());
        ps.setInt(2, clienteId);
        ps.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(p.getFecha()));
        ps.setString(4, p.getEstado().name());
        ps.setString(5, detalle.toString());
        ps.setDouble(6, p.calcularTotal());
        ps.setInt(7, p.calcularTiempoEntrega());
    }

    public void eliminarDeCliente(int clienteId) throws SQLException {
        try (Connection c = conexion.abrir();
             PreparedStatement ps = c.prepareStatement("DELETE FROM pedidos WHERE cliente_id = ?")) {
            ps.setInt(1, clienteId);
            ps.executeUpdate();
        }
    }

    public int contar() throws SQLException {
        try (Connection c = conexion.abrir();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM pedidos")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Suma de los totales de todos los pedidos (consulta agregada de SQL). */
    public double sumaTotales() throws SQLException {
        try (Connection c = conexion.abrir();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(total), 0) FROM pedidos")) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }
}