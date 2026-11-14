package coffeecraft.modelo;

/**
 * Describe cada tipo de bebida base del menú y sabe fabricar la bebida concreta.
 * Sirve de "fábrica" para que la interfaz no dependa de las subclases.
 */
public enum TipoBebida {
    ESPRESSO("Espresso", "Shots", 1, 4, 1,
            "Café concentrado e intenso, la base de todas las bebidas."),
    LATTE("Latte", "Espuma (ml)", 20, 120, 60,
            "Espresso suave con leche vaporizada y una capa de espuma."),
    CAPPUCCINO("Cappuccino", "Capas de espuma", 1, 4, 2,
            "Partes iguales de espresso, leche y espuma cremosa."),
    AMERICANO("Americano", "Agua (ml)", 50, 300, 150,
            "Espresso alargado con agua caliente, ligero y limpio."),
    FRAPPE("Frappé", "Cubos de hielo", 3, 10, 5,
            "Café batido con hielo, siempre servido frío.");

    private static final Grano GRANO_REFERENCIA = new Grano("Referencia", "-", 0, 0);

    private final String nombre;
    private final String etiquetaExtra;
    private final int minExtra;
    private final int maxExtra;
    private final int extraPorDefecto;
    private final String descripcion;

    TipoBebida(String nombre, String etiquetaExtra, int minExtra, int maxExtra,
               int extraPorDefecto, String descripcion) {
        this.nombre = nombre;
        this.etiquetaExtra = etiquetaExtra;
        this.minExtra = minExtra;
        this.maxExtra = maxExtra;
        this.extraPorDefecto = extraPorDefecto;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEtiquetaExtra() {
        return etiquetaExtra;
    }

    public int getMinExtra() {
        return minExtra;
    }

    public int getMaxExtra() {
        return maxExtra;
    }

    public int getExtraPorDefecto() {
        return extraPorDefecto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /** El frappé solo puede servirse frío. */
    public boolean soloFrio() {
        return this == FRAPPE;
    }

    public Bebida crear(Tamano tamano, Temperatura temperatura, Grano grano, int extra) {
        switch (this) {
            case ESPRESSO:
                return new Espresso(tamano, temperatura, grano, extra);
            case LATTE:
                return new Latte(tamano, temperatura, grano, extra);
            case CAPPUCCINO:
                return new Cappuccino(tamano, temperatura, grano, extra);
            case AMERICANO:
                return new Americano(tamano, temperatura, grano, extra);
            case FRAPPE:
                return new Frappe(tamano, grano, extra);
            default:
                throw new IllegalStateException("Tipo desconocido: " + this);
        }
    }

    /** Precio de la versión más sencilla (tamaño pequeño, sin agregados). */
    public double precioDesde() {
        Temperatura t = soloFrio() ? Temperatura.FRIO : Temperatura.CALIENTE;
        return crear(Tamano.PEQUENO, t, GRANO_REFERENCIA, extraPorDefecto).calcularPrecio();
    }

    public String toString() {
        return nombre;
    }
}