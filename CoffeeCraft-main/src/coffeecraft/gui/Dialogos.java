package coffeecraft.gui;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/** Mensajes y selectores de archivo usados por varias pantallas. */
public final class Dialogos {
    private Dialogos() {
    }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "CoffeeCraft", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void aviso(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "CoffeeCraft", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "CoffeeCraft", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component padre, String mensaje) {
        return JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /** Devuelve el archivo elegido para leer, o null si se canceló. */
    public static File elegirParaAbrir(Component padre, String descripcion, String extension) {
        JFileChooser selector = new JFileChooser(new File("."));
        selector.setFileFilter(new FileNameExtensionFilter(descripcion, extension));
        return selector.showOpenDialog(padre) == JFileChooser.APPROVE_OPTION ? selector.getSelectedFile() : null;
    }

    /** Devuelve el archivo elegido para guardar (con extensión), o null si se canceló. */
    public static File elegirParaGuardar(Component padre, String nombreSugerido,
                                         String descripcion, String extension) {
        JFileChooser selector = new JFileChooser(new File("."));
        selector.setSelectedFile(new File(nombreSugerido));
        selector.setFileFilter(new FileNameExtensionFilter(descripcion, extension));
        if (selector.showSaveDialog(padre) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith("." + extension)) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + "." + extension);
        }
        if (archivo.exists() && !confirmar(padre, "El archivo " + archivo.getName()
                + " ya existe.\n¿Deseas reemplazarlo?")) {
            return null;
        }
        return archivo;
    }
}