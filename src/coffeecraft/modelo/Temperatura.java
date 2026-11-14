package coffeecraft.modelo;

public enum Temperatura {
    CALIENTE("Caliente"),
    TIBIO("Tibio"),
    FRIO("Frío");

    private final String etiqueta;

    Temperatura(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String toString() {
        return etiqueta;
    }
}