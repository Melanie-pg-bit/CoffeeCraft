package coffeecraft;

import coffeecraft.gui.Contexto;
import coffeecraft.gui.Estilo;
import coffeecraft.gui.VentanaPrincipal;
import coffeecraft.modelo.Catalogo;
import coffeecraft.persistencia.DatosApp;
import coffeecraft.persistencia.RepositorioBinario;
import coffeecraft.servicio.DatosIniciales;
import java.awt.Font;
import java.io.File;
import java.util.Collections;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

/** Punto de entrada de la aplicación con interfaz gráfica. */
public class MainGUI {

    public static void main(String[] args) {
        configurarApariencia();

        final Catalogo catalogo = new Catalogo();
        final File archivo = new File("coffeecraft.dat");
        final Contexto ctx = new Contexto(cargarOCrearDatos(catalogo, archivo), catalogo, archivo);

        SwingUtilities.invokeLater(() -> new VentanaPrincipal(ctx).setVisible(true));
    }

    /** Si existe coffeecraft.dat se cargan los datos guardados; si no, se crean datos de ejemplo. */
    private static DatosApp cargarOCrearDatos(Catalogo catalogo, File archivo) {
        if (archivo.exists()) {
            try {
                return RepositorioBinario.cargar(archivo);
            } catch (Exception e) {
                System.out.println("No se pudo leer " + archivo.getName() + ", se crean datos nuevos: " + e);
            }
        }
        DatosApp datos = DatosIniciales.crear(catalogo);
        try {
            RepositorioBinario.guardar(datos, archivo);
        } catch (java.io.IOException e) {
            System.out.println("No se pudo guardar el archivo inicial: " + e.getMessage());
        }
        return datos;
    }

    private static void configurarApariencia() {
        try {
            MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme() {
                @Override
                public String getName() {
                    return "CoffeeCraft";
                }

                @Override
                protected ColorUIResource getPrimary1() {
                    return new ColorUIResource(0x8B5A33);
                }

                @Override
                protected ColorUIResource getPrimary2() {
                    return new ColorUIResource(0xE8A265);
                }

                @Override
                protected ColorUIResource getPrimary3() {
                    return new ColorUIResource(0xF6DDC3);
                }

                @Override
                protected ColorUIResource getSecondary1() {
                    return new ColorUIResource(0xC9B5A0);
                }

                @Override
                protected ColorUIResource getSecondary2() {
                    return new ColorUIResource(0xDCCBB8);
                }

                @Override
                protected ColorUIResource getSecondary3() {
                    return new ColorUIResource(0xFBF4EC);
                }
            });
            UIManager.setLookAndFeel(new MetalLookAndFeel());
            for (Object clave : Collections.list(UIManager.getDefaults().keys())) {
                if (clave.toString().endsWith(".font")) {
                    UIManager.put(clave, new FontUIResource(Estilo.fuente(Font.PLAIN, 13)));
                }
            }
        } catch (Exception e) {
            System.out.println("No se pudo aplicar la apariencia: " + e.getMessage());
        }
    }
}