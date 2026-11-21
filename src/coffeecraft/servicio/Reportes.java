package coffeecraft.servicio;

import coffeecraft.modelo.Bebida;
import coffeecraft.modelo.EstadoPedido;
import coffeecraft.modelo.Pedido;
import coffeecraft.modelo.Tamano;
import coffeecraft.modelo.TipoBebida;
import coffeecraft.modelo.Usuario;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reportes y estadísticas calculados con arreglos (unidimensionales y bidimensionales),
 * con algoritmos propios de ordenamiento y búsqueda.
 */
public class Reportes {

    private Reportes() {
    }

    /** Une los historiales de todos los clientes en una sola lista de pedidos. */
    public static ArrayList<Pedido> todosLosPedidos(List<Usuario> usuarios) {
        ArrayList<Pedido> todos = new ArrayList<>();
        for (Usuario u : usuarios) {
            todos.addAll(u.verHistorial());
        }
        return todos;
    }

    /** Cantidad de bebidas vendidas por tipo (índice = ordinal de TipoBebida). */
    public static int[] bebidasPorTipo(List<Usuario> usuarios) {
        int[] conteo = new int[TipoBebida.values().length];
        for (Pedido p : todosLosPedidos(usuarios)) {
            for (Bebida b : p.getBebidas()) {
                conteo[b.getTipo().ordinal()]++;
            }
        }
        return conteo;
    }

    /** Matriz de ingresos: filas = tipo de bebida, columnas = tamaño. */
    public static double[][] ventasPorTipoYTamano(List<Usuario> usuarios) {
        double[][] matriz = new double[TipoBebida.values().length][Tamano.values().length];
        for (Pedido p : todosLosPedidos(usuarios)) {
            for (Bebida b : p.getBebidas()) {
                matriz[b.getTipo().ordinal()][b.getTamano().ordinal()] += b.calcularPrecio();
            }
        }
        return matriz;
    }

    /** Cantidad de pedidos por estado (índice = ordinal de EstadoPedido). */
    public static int[] pedidosPorEstado(List<Usuario> usuarios) {
        int[] conteo = new int[EstadoPedido.values().length];
        for (Pedido p : todosLosPedidos(usuarios)) {
            conteo[p.getEstado().ordinal()]++;
        }
        return conteo;
    }

    /** Total de cada pedido, en un arreglo. */
    public static double[] totalesPorPedido(List<Usuario> usuarios) {
        ArrayList<Pedido> pedidos = todosLosPedidos(usuarios);
        double[] totales = new double[pedidos.size()];
        for (int i = 0; i < totales.length; i++) {
            totales[i] = pedidos.get(i).calcularTotal();
        }
        return totales;
    }

    public static double suma(double[] a) {
        double s = 0;
        for (double v : a) {
            s += v;
        }
        return s;
    }

    public static double promedio(double[] a) {
        return a.length == 0 ? 0 : suma(a) / a.length;
    }

    /** Mediana: ordena una copia del arreglo para no alterar el original. */
    public static double mediana(double[] a) {
        if (a.length == 0) {
            return 0;
        }
        double[] copia = Arrays.copyOf(a, a.length);
        ordenarBurbuja(copia);
        int mitad = copia.length / 2;
        return copia.length % 2 == 0 ? (copia[mitad - 1] + copia[mitad]) / 2.0 : copia[mitad];
    }

    /** Ordenamiento burbuja ascendente. */
    public static void ordenarBurbuja(double[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            boolean huboCambio = false;
            for (int j = 0; j < a.length - 1 - i; j++) {
                if (a[j] > a[j + 1]) {
                    double aux = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = aux;
                    huboCambio = true;
                }
            }
            if (!huboCambio) {
                break;
            }
        }
    }
    /** Ordenamiento por selección: clientes de mayor a menor gasto total. */
    public static void ordenarPorGasto(Usuario[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            int mayor = i;
            for (int j = i + 1; j < a.length; j++) {
                if (a[j].totalGastado() > a[mayor].totalGastado()) {
                    mayor = j;
                }
            }
            Usuario aux = a[i];
            a[i] = a[mayor];
            a[mayor] = aux;
        }
    }

    /** Los n clientes que más han gastado. */
    public static Usuario[] topClientes(List<Usuario> usuarios, int n) {
        Usuario[] arreglo = usuarios.toArray(new Usuario[0]);
        ordenarPorGasto(arreglo);
        return Arrays.copyOf(arreglo, Math.min(n, arreglo.length));
    }

    /** Búsqueda binaria en un arreglo ordenado de forma ascendente; devuelve -1 si no existe. */
    public static int busquedaBinaria(int[] ordenado, int clave) {
        int inicio = 0;
        int fin = ordenado.length - 1;
        while (inicio <= fin) {
            int medio = (inicio + fin) / 2;
            if (ordenado[medio] == clave) {
                return medio;
            } else if (ordenado[medio] < clave) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }
        return -1;
    }

    /** Búsqueda lineal de un cliente por id en un arreglo; devuelve null si no está. */
    public static Usuario buscarPorId(Usuario[] arreglo, int id) {
        for (Usuario u : arreglo) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }
}