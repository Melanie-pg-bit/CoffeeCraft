package coffeecraft.modelo;

public class Americano extends Bebida {
    private static final long serialVersionUID = 1L;

    private int aguaMl;

    public Americano(Tamano tamano, Temperatura temperatura, Grano grano, int aguaMl) {
        super(TipoBebida.AMERICANO.getNombre(), tamano, temperatura, grano);
        this.aguaMl = aguaMl;
    }

    /** Sobrecarga: 150 ml de agua. */
    public Americano(Tamano tamano, Temperatura temperatura, Grano grano) {
        this(tamano, temperatura, grano, 150);
    }

    /** Sobrecarga: americano pequeño y caliente. */
    public Americano(Grano grano) {
        this(Tamano.PEQUENO, Temperatura.CALIENTE, grano);
    }

    public int getAguaMl() {
        return aguaMl;
    }

    public double calcularPrecio() {
        return super.calcularPrecio() + (aguaMl / 100.0) * 0.30;
    }

    public int calcularTiempo() {
        return super.calcularTiempo() + 1;
    }

    public TipoBebida getTipo() {
        return TipoBebida.AMERICANO;
    }

    public String getDescripcion() {
        return descripcionBase() + " | Agua: " + aguaMl + " ml";
    }
}