package coffeecraft.persistencia;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/** Guarda y carga todos los datos como objetos serializados en un archivo binario (.dat). */
public class RepositorioBinario {

    private RepositorioBinario() {
    }

    public static void guardar(DatosApp datos, File archivo) throws IOException {
        try (ObjectOutputStream salida = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(archivo)))) {
            salida.writeObject(datos);
        }
    }

    public static DatosApp cargar(File archivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrada = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(archivo)))) {
            DatosApp datos = (DatosApp) entrada.readObject();
            datos.sincronizarContadores();
            return datos;
        }
    }
} 