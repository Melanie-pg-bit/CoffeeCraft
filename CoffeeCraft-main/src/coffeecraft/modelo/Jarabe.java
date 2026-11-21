package coffeecraft.modelo;

public class Jarabe extends Agregado {
    private String sabor;

    public Jarabe(String sabor) {
        super("Jarabe de " + sabor, 1.50, 0);
        this.sabor = sabor;
    }

    public String getSabor() {
        return sabor;
    }

    public double calcularPrecio() {
        return precio;
    }

    public int calcularTiempo() {
        return tiempo;
    }
}