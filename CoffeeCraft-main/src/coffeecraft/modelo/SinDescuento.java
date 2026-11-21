package coffeecraft.modelo;

public class SinDescuento implements Descuento {
    private static final long serialVersionUID = 1L;

    @Override
    public double aplicar(double total) {
        return total;
    }

    @Override
    public String getDescripcion() {
        return "Sin descuento";
    }

    @Override
    public String toString() {
        return getDescripcion();
    }
}