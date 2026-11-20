package coffeecraft.persistencia;

import coffeecraft.modelo.Pedido;
import coffeecraft.modelo.Usuario;
import java.io.Serializable;
import java.util.ArrayList;

/** Contenedor de todos los datos de la aplicación (es lo que se guarda en el archivo binario). */
public class DatosApp implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<Usuario> usuarios = new ArrayList<>();

    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }

    public int siguienteIdUsuario() {
        int max = 0;
        for (Usuario u : usuarios) {
            max = Math.max(max, u.getId());
        }
        return max + 1;
    }

    public Usuario buscarUsuario(int id) {
        for (Usuario u : usuarios) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    /** Debe llamarse tras cargar datos para que los nuevos pedidos no repitan identificadores. */
    public void sincronizarContadores() {
        int max = 0;
        for (Usuario u : usuarios) {
            for (Pedido p : u.verHistorial()) {
                max = Math.max(max, p.getId());
            }
        }
        Pedido.sincronizarContador(max);
    }
}