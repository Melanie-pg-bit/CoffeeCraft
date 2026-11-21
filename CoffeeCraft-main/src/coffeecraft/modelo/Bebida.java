package coffeecraft.modelo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Bebida personalizable. Los {@link Agregado}s se relacionan por agregación
 * (existen por sí solos en el catálogo); la bebida pertenece a un {@link Pedido}
 * por composición.
 */
public abstract class Bebida implements Calculable, Personalizable, Serializable {
    private static final long serialVersionUID = 1L;

    protected static final double PRECIO_BASE = 8.00;
    protected static final int TIEMPO_BASE = 2;

    protected String nombre;
    protected Tamano tamano;
    protected Temperatura temperatura;
    protected Grano grano;
    protected ArrayList<Agregado> agregados;

    public Bebida(String nombre, Tamano tamano, Temperatura temperatura, Grano grano) {
        this.nombre = nombre;
        this.tamano = tamano;
        this.temperatura = temperatura;
        this.grano = grano;
        this.agregados = new ArrayList<>();
    }

    public void agregarAgregado(Agregado a) {
        if (a != null) {
            agregados.add(a);
        }
    }

    public void quitarAgregado(Agregado a) {
        agregados.remove(a);
    }

    public double calcularPrecio() {
        double total = PRECIO_BASE * tamano.getFactorPrecio();
        total += grano.getCostoTotal();
        for (Agregado a : agregados) {
            total += a.calcularPrecio();
        }
        return total;
    }

    public int calcularTiempo() {
        int total = TIEMPO_BASE + tamano.getMinutosExtra() + grano.getTiempoExtra();
        for (Agregado a : agregados) {
            total += a.calcularTiempo();
        }
        return total;
    }

    public abstract String getDescripcion();

    public abstract TipoBebida getTipo();

    public String getNombre() {
        return nombre;
    }

    public Tamano getTamano() {
        return tamano;
    }

    public Temperatura getTemperatura() {
        return temperatura;
    }

    public Grano getGrano() {
        return grano;
    }

    public ArrayList<Agregado> getAgregados() {
        return new ArrayList<>(agregados);
    }

    protected String descripcionBase() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombre).append(" ").append(tamano)
          .append(" ").append(temperatura)
          .append(" | Grano: ").append(grano);
        if (!agregados.isEmpty()) {
            sb.append(" | Agregados: ");
            for (int i = 0; i < agregados.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(agregados.get(i).getNombre());
            }
        }
        return sb.toString();
    }

    public String toString() {
        return getDescripcion();
    }
}