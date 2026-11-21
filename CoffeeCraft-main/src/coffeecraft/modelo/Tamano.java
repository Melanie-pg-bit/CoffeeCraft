package coffeecraft.modelo;

public enum Tamano {
    PEQUENO("Pequeño", 1.0, 0),
    MEDIANO("Mediano", 1.25, 1),
    GRANDE("Grande", 1.5, 2);

    private final String etiqueta;
    private final double factorPrecio;
    private final int minutosExtra;

    Tamano(String etiqueta, double factorPrecio, int minutosExtra) {
        this.etiqueta = etiqueta;
        this.factorPrecio = factorPrecio;
        this.minutosExtra = minutosExtra;
    }

    public double getFactorPrecio() {
        return factorPrecio;
    }

    public int getMinutosExtra() {
        return minutosExtra;
    }

    public String toString() {
        return etiqueta;
    }
}