package coffeecraft.gui;

import coffeecraft.modelo.Catalogo;
import coffeecraft.persistencia.DatosApp;
import coffeecraft.persistencia.RepositorioBinario;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Datos compartidos por todas las pantallas: los clientes con sus pedidos, el catálogo
 * y el archivo binario donde se guarda todo. Avisa a las pantallas cuando algo cambia.
 */
public class Contexto {
    private final DatosApp datos;
    private final Catalogo catalogo;
    private final File archivo;
    private final ArrayList<Actualizable> vistas = new ArrayList<>();
    private Component ventana;

    public Contexto(DatosApp datos, Catalogo catalogo, File archivo) {
        this.datos = datos;
        this.catalogo = catalogo;
        this.archivo = archivo;
    }

    public DatosApp getDatos() {
        return datos;
    }

    public Catalogo getCatalogo() {
        return catalogo;
    }

    public void setVentana(Component ventana) {
        this.ventana = ventana;
    }

    public void registrar(Actualizable vista) {
        vistas.add(vista);
    }

    /** Pide a todas las pantallas que se redibujen con los datos actuales. */
    public void actualizarTodo() {
        for (Actualizable v : vistas) {
            v.actualizar();
        }
    }

    /** Guarda todo en el archivo binario. Devuelve false (y avisa) si falla. */
    public boolean guardar() {
        try {
            RepositorioBinario.guardar(datos, archivo);
            return true;
        } catch (IOException e) {
            Dialogos.error(ventana, "No se pudo guardar " + archivo.getName() + ":\n" + e.getMessage());
            return false;
        }
    }
}
