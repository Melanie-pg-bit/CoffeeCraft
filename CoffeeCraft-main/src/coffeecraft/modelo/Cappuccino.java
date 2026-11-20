package coffeecraft.modelo;

public class Cappuccino extends Bebida {
    private static final long serialVersionUID = 1L;

    private int capasEspuma;

    public Cappuccino(Tamano tamano, Temperatura temperatura, Grano grano, int capasEspuma) {
        super(TipoBebida.CAPPUCCINO.getNombre(), tamano, temperatura, grano);
        this.capasEspuma = capasEspuma;
    }

    /** Sobrecarga: dos capas de espuma. */
    public Cappuccino(Tamano tamano, Temperatura temperatura, Grano grano) {
        this(tamano, temperatura, grano, 2);
    }

    /** Sobrecarga: cappuccino mediano y caliente. */
    public Cappuccino(Grano grano) {
        this(Tamano.MEDIANO, Temperatura.CALIENTE, grano);
    }

    public int getCapasEspuma() {
        return capasEspuma;
    }

    public double calcularPrecio() {
        return super.calcularPrecio() + 3.50 + capasEspuma * 0.50;
    }

    public int calcularTiempo() {
        return super.calcularTiempo() + 2 + capasEspuma;
    }

    public TipoBebida getTipo() {
        return TipoBebida.CAPPUCCINO;
    }

    public String getDescripcion() {
        return descripcionBase() + " | Capas de espuma: " + capasEspuma;
    }
}