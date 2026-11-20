package coffeecraft.modelo;

public class DescuentoFrecuente implements Descuento {
    private static final long serialVersionUID = 1L;

    private double porcentaje; // 0 a 100

    public DescuentoFrecuente(double porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        }
        this.porcentaje = porcentaje;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    @Override
    public double aplicar(double total) {
        return total - (total * porcentaje / 100.0);
    }

    @Override
    public String getDescripcion() {
        return String.format("Cliente frecuente (%.0f%%)", porcentaje);
    }

    @Override
    public String toString() {
        return getDescripcion();
    }
}