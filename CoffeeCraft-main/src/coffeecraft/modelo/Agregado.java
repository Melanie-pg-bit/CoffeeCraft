package coffeecraft.modelo;

import java.io.Serializable;

public abstract class Agregado implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String nombre;
    protected double precio;
    protected int tiempo;

    public Agregado(String nombre, double precio, int tiempo) {
        this.nombre = nombre;
        this.precio = precio;
        this.tiempo = tiempo;
    }

    public String getNombre() {
        return nombre;
    }

    public double calcularPrecio() {
        return precio;
    }

    public int calcularTiempo() {
        return tiempo;
    }

    /** Categoría legible (Leche, Jarabe, Topping), usada para agrupar en la interfaz. */
    public String getCategoria() {
        return getClass().getSimpleName();
    }

    public String toString() {
        return nombre;
    }
}