package coffeecraft.modelo;

public class Leche extends Agregado {
    private String tipo; // entera, deslactosada, almendra, avena...

    public Leche(String tipo) {
        super("Leche " + tipo, 2.00, 1);
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public double calcularPrecio() {
        return precio;
    }

    public int calcularTiempo() {
        return tiempo;
    }
}