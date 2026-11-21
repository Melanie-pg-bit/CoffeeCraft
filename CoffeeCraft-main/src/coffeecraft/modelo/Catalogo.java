package coffeecraft.modelo;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

/** Catálogo de granos y agregados disponibles, indexados por nombre con HashMap. */
public class Catalogo {
    // LinkedHashMap es un HashMap que conserva el orden de inserción (útil para la interfaz).
    private HashMap<String, Grano> granos = new LinkedHashMap<>();
    private HashMap<String, Agregado> agregados = new LinkedHashMap<>();

    public Catalogo() {
        cargarPorDefecto();
    }

    private void cargarPorDefecto() {
        agregarGrano(new Grano("Arábica", "Etiopía", 2.50, 1));
        agregarGrano(new Grano("Typica", "Perú", 1.50, 0));
        agregarGrano(new Grano("Robusta", "Vietnam", 1.00, 0));
        agregarGrano(new Grano("Geisha", "Panamá", 4.50, 2));

        agregarAgregado(new Leche("entera"));
        agregarAgregado(new Leche("deslactosada"));
        agregarAgregado(new Leche("almendra"));
        agregarAgregado(new Leche("avena"));
        agregarAgregado(new Jarabe("vainilla"));
        agregarAgregado(new Jarabe("caramelo"));
        agregarAgregado(new Jarabe("avellana"));
        agregarAgregado(new Topping("canela"));
        agregarAgregado(new Topping("crema batida"));
        agregarAgregado(new Topping("chocolate"));
    }

    public void agregarGrano(Grano g) {
        granos.put(g.getNombre(), g);
    }

    public void agregarAgregado(Agregado a) {
        agregados.put(a.getNombre(), a);
    }

    public Grano getGrano(String nombre) {
        return granos.get(nombre);
    }

    public Agregado getAgregado(String nombre) {
        return agregados.get(nombre);
    }

    public Collection<Grano> getGranos() {
        return granos.values();
    }

    public Collection<Agregado> getAgregados() {
        return agregados.values();
    }
}