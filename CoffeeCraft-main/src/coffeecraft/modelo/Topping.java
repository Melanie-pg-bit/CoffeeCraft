package coffeecraft.modelo;

public class Topping extends Agregado {
    private String tipo; // crema batida, canela, chocolate...

    public Topping(String tipo) {
        super("Topping de " + tipo, 1.00, 1);
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