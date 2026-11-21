package coffeecraft.modelo;

/** Ciclo de vida de un pedido. */
public enum EstadoPedido {
    PENDIENTE("Pendiente"),
    EN_PREPARACION("En preparación"),
    LISTO("Listo"),
    ENTREGADO("Entregado");

    private final String etiqueta;

    EstadoPedido(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /** Devuelve el siguiente estado del ciclo (o el mismo si ya está entregado). */
    public EstadoPedido siguiente() {
        return ordinal() < values().length - 1 ? values()[ordinal() + 1] : this;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}