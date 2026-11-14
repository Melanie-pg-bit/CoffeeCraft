package coffeecraft.modelo;

import java.io.Serializable;
import java.util.Objects;

public class Grano implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String origen;
    private double precioExtra;
    private int tiempoExtra;

    public Grano(String nombre, String origen, double precioExtra, int tiempoExtra) {
        this.nombre = nombre;
        this.origen = origen;
        this.precioExtra = precioExtra;
        this.tiempoExtra = tiempoExtra;
    }

    public String getNombre() {
        return nombre;
    }

    public String getOrigen() {
        return origen;
    }

    public double getPrecioExtra() {
        return precioExtra;
    }

    public int getTiempoExtra() {
        return tiempoExtra;
    }

    public double getCostoTotal() {
        return precioExtra;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grano)) return false;
        Grano otro = (Grano) o;
        return nombre.equals(otro.nombre) && origen.equals(otro.origen);
    }

    public int hashCode() {
        return Objects.hash(nombre, origen);
    }

    public String toString() {
        return nombre + " (" + origen + ")";
    }
}