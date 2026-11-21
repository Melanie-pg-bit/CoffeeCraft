package coffeecraft.gui;

/** Permite que una pantalla pida mostrar otra sin conocer la ventana principal. */
public interface Navegador {
    void ir(String pantalla);
}
