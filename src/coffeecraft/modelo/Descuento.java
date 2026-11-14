package coffeecraft.modelo;

import java.io.Serializable;

/** Estrategia de descuento aplicable al total de un pedido (patrón Strategy). */
public interface Descuento extends Serializable {
    double aplicar(double total);

    String getDescripcion();
}