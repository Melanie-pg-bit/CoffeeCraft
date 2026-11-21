package coffeecraft.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/** Pedido: compone una o más bebidas (si se elimina el pedido, desaparecen sus bebidas). */
public class Pedido implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int contador = 0;

    private int id;
    private Date fecha;
    private EstadoPedido estado;
    private ArrayList<Bebida> bebidas;
    private Descuento descuento;

    public Pedido(int id) {
        this(id, new SinDescuento());
    }

    public Pedido(int id, Descuento descuento) {
        this(id, descuento, new Date());
    }

    public Pedido(int id, Descuento descuento, Date fecha) {
        this.id = id;
        this.fecha = fecha;
        this.estado = EstadoPedido.PENDIENTE;
        this.bebidas = new ArrayList<>();
        this.descuento = descuento;
        if (id > contador) {
            contador = id;
        }
    }

    /** Genera el siguiente identificador de pedido. */
    public static synchronized int siguienteId() {
        return ++contador;
    }

    /** Sincroniza el contador tras cargar datos guardados. */
    public static synchronized void sincronizarContador(int maximoExistente) {
        if (maximoExistente > contador) {
            contador = maximoExistente;
        }
    }

    public void agregarBebida(Bebida b) {
        if (estado != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden agregar bebidas a un pedido PENDIENTE");
        }
        if (b != null) {
            bebidas.add(b);
        }
    }

    public void quitarBebida(Bebida b) {
        if (estado != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden quitar bebidas de un pedido PENDIENTE");
        }
        bebidas.remove(b);
    }

    public double getSubtotal() {
        double subtotal = 0;
        for (Bebida b : bebidas) {
            subtotal += b.calcularPrecio();
        }
        return subtotal;
    }

    public double calcularTotal() {
        return descuento.aplicar(getSubtotal());
    }

    public double getMontoDescuento() {
        return getSubtotal() - calcularTotal();
    }

    public int calcularTiempoEntrega() {
        int total = 0;
        for (Bebida b : bebidas) {
            total += b.calcularTiempo();
        }
        return total;
    }

    public void cambiarEstado(EstadoPedido e) {
        if (e.ordinal() < estado.ordinal()) {
            throw new IllegalStateException("No se puede retroceder de " + estado + " a " + e);
        }
        this.estado = e;
    }

    public void setDescuento(Descuento descuento) {
        this.descuento = descuento;
    }

    public int getId() {
        return id;
    }
    public Date getFecha() {
        return fecha;
    }
    public EstadoPedido getEstado() {
        return estado;
    }
    public ArrayList<Bebida> getBebidas() {
        return new ArrayList<>(bebidas);
    }

    public int cantidadBebidas() {
        return bebidas.size();
    }

    public Descuento getDescuento() {
        return descuento;
    }

    @Override
    public String toString() {
        return String.format("Pedido #%d [%s] - %d bebida(s) - S/ %.2f",
                id, estado, bebidas.size(), calcularTotal());
    }
}
