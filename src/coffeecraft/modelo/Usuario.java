package coffeecraft.modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Pedidos previos necesarios para ser cliente frecuente. */
    public static final int PEDIDOS_PARA_FRECUENTE = 3;
    public static final double PORCENTAJE_FRECUENTE = 10;

    private int id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;
    private ArrayList<Pedido> historial;

    public Usuario(int id, String nombre, String email, String telefono, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.historial = new ArrayList<>();
    }

    /** Sobrecarga: usuario solo con los datos mínimos del diagrama UML. */
    public Usuario(int id, String nombre, String email) {
        this(id, nombre, email, "", "");
    }

    /** Crea un pedido nuevo (con el descuento que le corresponda) y lo registra en el historial. */
    public Pedido crearPedido() {
        Pedido p = new Pedido(Pedido.siguienteId(), descuentoActual());
        registrarPedido(p);
        return p;
    }

    /** Agrega al historial un pedido ya construido (datos de ejemplo, carga desde BD, etc.). */
    public void registrarPedido(Pedido p) {
        if (p != null) {
            historial.add(p);
        }
    }
    public ArrayList<Pedido> verHistorial() {
        return new ArrayList<>(historial);
    }
    public boolean esFrecuente() {
        return historial.size() >= PEDIDOS_PARA_FRECUENTE;
    }
    public Descuento descuentoActual() {
        return esFrecuente() ? new DescuentoFrecuente(PORCENTAJE_FRECUENTE) : new SinDescuento();
    }
    public int cantidadPedidos() {
        return historial.size();
    }
    public double totalGastado() {
        double total = 0;
        for (Pedido p : historial) {
            total += p.calcularTotal();
        }
        return total;
    }
    public int getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    @Override
    public String toString() {
        return nombre;
    }
}