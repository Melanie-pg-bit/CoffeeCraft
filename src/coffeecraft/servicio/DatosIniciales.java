package coffeecraft.servicio;

import coffeecraft.modelo.Agregado;
import coffeecraft.modelo.Bebida;
import coffeecraft.modelo.Catalogo;
import coffeecraft.modelo.EstadoPedido;
import coffeecraft.modelo.Grano;
import coffeecraft.modelo.Pedido;
import coffeecraft.modelo.Tamano;
import coffeecraft.modelo.Temperatura;
import coffeecraft.modelo.TipoBebida;
import coffeecraft.modelo.Usuario;
import coffeecraft.persistencia.DatosApp;
import java.util.Date;
import java.util.Random;

/** Genera datos de ejemplo la primera vez que se abre la aplicación. */
public class DatosIniciales {

    private static final long UN_DIA = 24L * 60 * 60 * 1000;

    private DatosIniciales() {
    }

    public static DatosApp crear(Catalogo catalogo) {
        DatosApp datos = new DatosApp();
        Random azar = new Random(42); // semilla fija: siempre los mismos datos de ejemplo

        String[][] personas = {
            {"Hillary Morales", "hmorales@correo.com", "(054) 555-0101", "Av. Ejército 123, Arequipa"},
            {"Melany Gómez", "mgomez@correo.com", "(054) 555-0102", "Calle Mercaderes 45, Arequipa"},
            {"Melanie Paredes", "mparedes@correo.com", "(054) 555-0103", "Urb. Vallecito B-7, Arequipa"},
            {"Carlos Quispe", "cquispe@correo.com", "(054) 555-0104", "Av. Goyeneche 880, Arequipa"},
            {"Lucía Mamani", "lmamani@correo.com", "(054) 555-0105", "Calle Bolívar 210, Arequipa"},
            {"Diego Flores", "dflores@correo.com", "(054) 555-0106", "Av. Dolores 1502, Arequipa"},
            {"Valeria Condori", "vcondori@correo.com", "(054) 555-0107", "Calle Jerusalén 64, Arequipa"},
            {"Andrés Huamán", "ahuaman@correo.com", "(054) 555-0108", "Av. Parra 302, Arequipa"}
        };
        int[] cantidadPedidos = {6, 4, 5, 2, 1, 3, 0, 7};

        TipoBebida[] tipos = TipoBebida.values();
        Tamano[] tamanos = Tamano.values();
        Temperatura[] temperaturas = Temperatura.values();
        Grano[] granos = catalogo.getGranos().toArray(new Grano[0]);
        Agregado[] extras = catalogo.getAgregados().toArray(new Agregado[0]);

        for (int i = 0; i < personas.length; i++) {
            Usuario u = new Usuario(i + 1, personas[i][0], personas[i][1], personas[i][2], personas[i][3]);

            for (int k = 0; k < cantidadPedidos[i]; k++) {
                int diasAtras = (cantidadPedidos[i] - k) * 4 + azar.nextInt(3);
                Date fecha = new Date(System.currentTimeMillis() - diasAtras * UN_DIA);
                Pedido p = new Pedido(Pedido.siguienteId(), u.descuentoActual(), fecha);

                int nBebidas = 1 + azar.nextInt(3);
                for (int b = 0; b < nBebidas; b++) {
                    TipoBebida tipo = tipos[azar.nextInt(tipos.length)];
                    Tamano tamano = tamanos[azar.nextInt(tamanos.length)];
                    Temperatura temp = tipo.soloFrio() ? Temperatura.FRIO
                            : temperaturas[azar.nextInt(temperaturas.length)];
                    Grano grano = granos[azar.nextInt(granos.length)];
                    int extra = tipo.getMinExtra() + azar.nextInt(tipo.getMaxExtra() - tipo.getMinExtra() + 1);
                    Bebida bebida = tipo.crear(tamano, temp, grano, extra);
                    int nAgregados = azar.nextInt(3);
                    for (int a = 0; a < nAgregados; a++) {
                        bebida.agregarAgregado(extras[azar.nextInt(extras.length)]);
                    }
                    p.agregarBebida(bebida);
                }

                // Los pedidos antiguos ya fueron entregados; el último puede estar en cualquier estado.
                EstadoPedido objetivo = (k == cantidadPedidos[i] - 1)
                        ? EstadoPedido.values()[azar.nextInt(EstadoPedido.values().length)]
                        : EstadoPedido.ENTREGADO;
                while (p.getEstado() != objetivo) {
                    p.cambiarEstado(p.getEstado().siguiente());
                }
                u.registrarPedido(p);
            }
            datos.getUsuarios().add(u);
        }
        return datos;
    }
}