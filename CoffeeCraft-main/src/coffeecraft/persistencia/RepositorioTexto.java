package coffeecraft.persistencia;

import coffeecraft.modelo.Bebida;
import coffeecraft.modelo.Pedido;
import coffeecraft.modelo.Usuario;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/** Entrada y salida de archivos de texto: exportar/importar CSV y generar tickets. */
public class RepositorioTexto {

    private static final String SEPARADOR = ";";
    private static final String CODIFICACION = "UTF-8";

    private RepositorioTexto() {
    }

    private static String limpiar(String texto) {
        return texto == null ? "" : texto.replace(SEPARADOR, ",").replace("\n", " ").trim();
    }

    public static void exportarClientes(List<Usuario> usuarios, File archivo) throws IOException {
        try (PrintWriter salida = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(archivo), CODIFICACION))) {
            salida.println("id;nombre;email;telefono;direccion;pedidos;gastado");
            for (Usuario u : usuarios) {
                salida.println(u.getId() + SEPARADOR + limpiar(u.getNombre()) + SEPARADOR
                        + limpiar(u.getEmail()) + SEPARADOR + limpiar(u.getTelefono()) + SEPARADOR
                        + limpiar(u.getDireccion()) + SEPARADOR + u.cantidadPedidos() + SEPARADOR
                        + String.format(java.util.Locale.US, "%.2f", u.totalGastado()));
            }
        }
    }

    /**
     * Lee un CSV de clientes (nombre;email;telefono;direccion, con o sin columna id al inicio).
     * Las líneas inválidas se omiten. Los ids se asignan desde {@code primerId}.
     */
    public static ArrayList<Usuario> importarClientes(File archivo, int primerId) throws IOException {
        ArrayList<Usuario> nuevos = new ArrayList<>();
        try (BufferedReader entrada = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo), CODIFICACION))) {
            String linea = entrada.readLine();
            int siguiente = primerId;
            while (linea != null) {
                linea = linea.trim();
                if (!linea.isEmpty() && !linea.toLowerCase().startsWith("id;")
                        && !linea.toLowerCase().startsWith("nombre;")) {
                    String[] partes = linea.split(SEPARADOR, -1);
                    int desde = esNumero(partes[0]) && partes.length >= 3 ? 1 : 0;
                    if (partes.length - desde >= 2 && !partes[desde].trim().isEmpty()) {
                        String nombre = partes[desde].trim();
                        String email = partes[desde + 1].trim();
                        String tel = partes.length > desde + 2 ? partes[desde + 2].trim() : "";
                        String dir = partes.length > desde + 3 ? partes[desde + 3].trim() : "";
                        nuevos.add(new Usuario(siguiente++, nombre, email, tel, dir));
                    }
                }
                linea = entrada.readLine();
            }
        }
        return nuevos;
    }

    private static boolean esNumero(String s) {
        try {
            Integer.parseInt(s.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void exportarPedidos(List<Usuario> usuarios, File archivo) throws IOException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try (PrintWriter salida = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(archivo), CODIFICACION))) {
            salida.println("pedido;cliente;fecha;estado;bebidas;subtotal;descuento;total;minutos");
            for (Usuario u : usuarios) {
                for (Pedido p : u.verHistorial()) {
                    salida.println(p.getId() + SEPARADOR + limpiar(u.getNombre()) + SEPARADOR
                            + formato.format(p.getFecha()) + SEPARADOR + p.getEstado() + SEPARADOR
                            + p.cantidadBebidas() + SEPARADOR
                            + String.format(java.util.Locale.US, "%.2f", p.getSubtotal()) + SEPARADOR
                            + String.format(java.util.Locale.US, "%.2f", p.getMontoDescuento()) + SEPARADOR
                            + String.format(java.util.Locale.US, "%.2f", p.calcularTotal()) + SEPARADOR
                            + p.calcularTiempoEntrega());
                }
            }
        }
    }

    /** Escribe un ticket legible del pedido. */
    public static void exportarTicket(Usuario cliente, Pedido pedido, File archivo) throws IOException {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try (PrintWriter salida = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(archivo), CODIFICACION))) {
            salida.println("=========== COFFEECRAFT ===========");
            salida.println("Pedido #" + pedido.getId() + "  -  " + formato.format(pedido.getFecha()));
            salida.println("Cliente: " + cliente.getNombre());
            salida.println("Estado:  " + pedido.getEstado());
            salida.println("-----------------------------------");
            int n = 1;
            for (Bebida b : pedido.getBebidas()) {
                salida.println(n++ + ". " + b.getDescripcion());
                salida.println(String.format("     S/ %.2f   (%d min)", b.calcularPrecio(), b.calcularTiempo()));
            }
            salida.println("-----------------------------------");
            salida.println(String.format("Subtotal:   S/ %.2f", pedido.getSubtotal()));
            salida.println(String.format("%s: -S/ %.2f", pedido.getDescuento().getDescripcion(),
                    pedido.getMontoDescuento()));
            salida.println(String.format("TOTAL:      S/ %.2f", pedido.calcularTotal()));
            salida.println("Entrega estimada: " + pedido.calcularTiempoEntrega() + " min");
            salida.println("===================================");
        }
    }
}