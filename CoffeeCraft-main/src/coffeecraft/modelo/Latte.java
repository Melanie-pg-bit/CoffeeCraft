package coffeecraft.modelo;

public class Latte extends Bebida {
    private static final long serialVersionUID = 1L;

    private int espumaMl;

    public Latte(Tamano tamano, Temperatura temperatura, Grano grano, int espumaMl) {
        super(TipoBebida.LATTE.getNombre(), tamano, temperatura, grano);
        this.espumaMl = espumaMl;
    }

    /** Sobrecarga: 60 ml de espuma. */
    public Latte(Tamano tamano, Temperatura temperatura, Grano grano) {
        this(tamano, temperatura, grano, 60);
    }

    /** Sobrecarga: latte mediano y caliente. */
    public Latte(Grano grano) {
        this(Tamano.MEDIANO, Temperatura.CALIENTE, grano);
    }

    public int getEspumaMl() {
        return espumaMl;
    }

    public double calcularPrecio() {
        return super.calcularPrecio() + 3.00 + (espumaMl / 50.0);
    }

    public int calcularTiempo() {
        return super.calcularTiempo() + 2 + (espumaMl / 50);
    }

    public TipoBebida getTipo() {
        return TipoBebida.LATTE;
    }

    public String getDescripcion() {
        return descripcionBase() + " | Espuma: " + espumaMl + " ml";
    }
}