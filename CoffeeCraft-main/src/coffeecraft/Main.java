package coffeecraft;

import coffeecraft.modelo.Bebida;
import coffeecraft.modelo.Catalogo;
import coffeecraft.modelo.Pedido;
import coffeecraft.modelo.Tamano;
import coffeecraft.modelo.Temperatura;
import coffeecraft.modelo.TipoBebida;
import coffeecraft.modelo.Usuario;
import coffeecraft.persistencia.DatosApp;
import coffeecraft.persistencia.RepositorioBinario;
import coffeecraft.persistencia.RepositorioTexto;
import coffeecraft.servicio.DatosIniciales;
import coffeecraft.servicio.Reportes;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/** Demostración por consola del avance 1 (todavía sin interfaz gráfica). */
public class Main {
    public static void main(String[] args) throws IOException {
        Catalogo catalogo = new Catalogo();
        DatosApp datos = DatosIniciales.crear(catalogo);
        Usuario cliente = datos.getUsuarios().get(0);

        System.out.println("=== 1. Diseñar una bebida a medida ===");
        Bebida latte = TipoBebida.LATTE.crear(Tamano.GRANDE, Temperatura.CALIENTE,
                catalogo.getGrano("Geisha"), 80);
        latte.agregarAgregado(catalogo.getAgregado("Leche avena"));
        latte.agregarAgregado(catalogo.getAgregado("Jarabe de vainilla"));
        System.out.println(latte);
        System.out.printf("Precio: S/ %.2f | Tiempo: %d min%n%n", latte.calcularPrecio(), latte.calcularTiempo());

        System.out.println("=== 2. Crear pedido para " + cliente + " ===");
        Pedido pedido = cliente.crearPedido();
        pedido.agregarBebida(latte);
        pedido.agregarBebida(TipoBebida.FRAPPE.crear(Tamano.MEDIANO, Temperatura.FRIO,
                catalogo.getGrano("Typica"), 6));
        System.out.println(pedido);
        System.out.println("Descuento: " + pedido.getDescuento());
        System.out.println("Entrega estimada: " + pedido.calcularTiempoEntrega() + " min\n");

        System.out.println("=== 3. Reportes con arreglos ===");
        int[] porTipo = Reportes.bebidasPorTipo(datos.getUsuarios());
        for (TipoBebida t : TipoBebida.values()) {
            System.out.println(t + ": " + porTipo[t.ordinal()] + " vendidas");
        }
        double[] totales = Reportes.totalesPorPedido(datos.getUsuarios());
        System.out.printf("Promedio por pedido: S/ %.2f | Mediana: S/ %.2f%n", Reportes.promedio(totales),
                Reportes.mediana(totales));
        System.out.println("Top 3 clientes: " + Arrays.toString(Reportes.topClientes(datos.getUsuarios(), 3)));

        System.out.println("\n=== 4. Archivos ===");
        File dat = new File("coffeecraft.dat");
        RepositorioBinario.guardar(datos, dat);
        System.out.println("Guardado binario: " + dat.getAbsolutePath());
        File csv = new File("clientes.csv");
        RepositorioTexto.exportarClientes(datos.getUsuarios(), csv);
        System.out.println("Exportado CSV:    " + csv.getAbsolutePath());
        File ticket = new File("ticket_pedido_" + pedido.getId() + ".txt");
        RepositorioTexto.exportarTicket(cliente, pedido, ticket);
        System.out.println("Ticket de texto:  " + ticket.getAbsolutePath());
    }
}