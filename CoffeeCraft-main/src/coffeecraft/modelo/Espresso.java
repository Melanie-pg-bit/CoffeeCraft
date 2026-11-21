package coffeecraft.modelo;

public class Espresso extends Bebida {
    private static final long serialVersionUID = 1L;

    private int shots;

    public Espresso(Tamano tamano, Temperatura temperatura, Grano grano, int shots) {
        super(TipoBebida.ESPRESSO.getNombre(), tamano, temperatura, grano);
        if (shots < 1) throw new IllegalArgumentException("Debe haber al menos 1 shot");
        this.shots = shots;
    }

    /** Sobrecarga: un solo shot. */
    public Espresso(Tamano tamano, Temperatura temperatura, Grano grano) {
        this(tamano, temperatura, grano, 1);
    }

    /** Sobrecarga: espresso pequeño y caliente con el grano indicado. */
    public Espresso(Grano grano) {
        this(Tamano.PEQUENO, Temperatura.CALIENTE, grano);
    }

    public int getShots() {
        return shots;
    }

    public double calcularPrecio() {
        return super.calcularPrecio() + (shots - 1) * 2.50;
    }

    public int calcularTiempo() {
        return super.calcularTiempo() + shots;
    }

    public TipoBebida getTipo() {
        return TipoBebida.ESPRESSO;
    }

    public String getDescripcion() {
        return descripcionBase() + " | Shots: " + shots;
    }
}