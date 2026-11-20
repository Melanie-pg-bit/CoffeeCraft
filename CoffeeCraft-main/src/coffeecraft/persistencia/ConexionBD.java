package coffeecraft.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Conexión JDBC. Por defecto usa SQLite (archivo coffeecraft.db); para eso hay que agregar
 * el jar del driver (sqlite-jdbc) al classpath del proyecto. Para MySQL basta cambiar la URL,
 * por ejemplo: jdbc:mysql://localhost:3306/coffeecraft?user=root&password=...
 */
public class ConexionBD {
    public static final String URL_POR_DEFECTO = "jdbc:sqlite:coffeecraft.db";

    private final String url;

    public ConexionBD() {
        this(URL_POR_DEFECTO);
    }

    public ConexionBD(String url) {
        this.url = url;
    }

    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /** Crea las tablas si todavía no existen. */
    public void crearTablas() throws SQLException {
        try (Connection c = abrir(); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS clientes ("
                    + "id INTEGER PRIMARY KEY, "
                    + "nombre VARCHAR(100) NOT NULL, "
                    + "email VARCHAR(100), "
                    + "telefono VARCHAR(30), "
                    + "direccion VARCHAR(150))");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS pedidos ("
                    + "id INTEGER PRIMARY KEY, "
                    + "cliente_id INTEGER NOT NULL, "
                    + "fecha VARCHAR(30), "
                    + "estado VARCHAR(20), "
                    + "detalle VARCHAR(1000), "
                    + "total DOUBLE, "
                    + "minutos INTEGER, "
                    + "FOREIGN KEY (cliente_id) REFERENCES clientes(id))");
        }
    }
}