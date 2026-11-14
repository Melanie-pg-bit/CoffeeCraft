package coffeecraft.modelo;

public class Frappe extends Bebida {
    private static final long serialVersionUID = 1L;

    private int cubosHielo;

    public Frappe(Tamano tamano, Grano grano, int cubosHielo) {
        // El frappé siempre se sirve frío
        super(TipoBebida.FRAPPE.getNombre(), tamano, Temperatura.FRIO, grano);
        this.cubosHielo = cubosHielo;
    }

    /** Sobrecarga: 5 cubos de hielo. */
    public Frappe(Tamano tamano, Grano grano) {
        this(tamano, grano, 5);
    }

    /** Sobrecarga: frappé mediano. */
    public Frappe(Grano grano) {
        this(Tamano.MEDIANO, grano);
    }

    public int getCubosHielo() {
        return cubosHielo;
    }

    public double calcularPrecio() {
        return super.calcularPrecio() + 4.00 + cubosHielo * 0.20;
    }

    public int calcularTiempo() {
        return super.calcularTiempo() + 3;
    }

    public TipoBebida getTipo() {
        return TipoBebida.FRAPPE;
    }

    public String getDescripcion() {
        return descripcionBase() + " | Cubos de hielo: " + cubosHielo;
    }
}