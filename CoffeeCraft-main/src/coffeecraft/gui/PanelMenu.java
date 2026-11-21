package coffeecraft.gui;

import coffeecraft.modelo.TipoBebida;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/** Menú con las bebidas base. Cada tarjeta lleva al diseñador con ese tipo ya elegido. */
public class PanelMenu extends JPanel {
    private static final Color[] LIQUIDOS = {
        new Color(0x3B2314), new Color(0xB98A5E), new Color(0x9A6B43), new Color(0x4F3322), new Color(0x8A5A3C)
    };

    private final Navegador navegador;
    private final Personalizador personalizador;

    public PanelMenu(Contexto ctx, Navegador navegador, Personalizador personalizador) {
        this.navegador = navegador;
        this.personalizador = personalizador;

        setLayout(new BorderLayout(0, 16));
        setBackground(Estilo.CREMA);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        add(Estilo.encabezado("Menú de CoffeeCraft",
                "Elige una bebida base y personalízala a tu gusto"), BorderLayout.NORTH);

        JPanel rejilla = new JPanel(new GridLayout(0, 3, 18, 18));
        rejilla.setOpaque(false);
        for (TipoBebida tipo : TipoBebida.values()) {
            rejilla.add(crearTarjeta(tipo));
        }
        rejilla.add(crearTarjetaLibre());

        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setOpaque(false);
        envoltorio.add(rejilla, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(envoltorio);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel crearTarjeta(final TipoBebida tipo) {
        Estilo.Tarjeta t = new Estilo.Tarjeta(new BorderLayout(0, 12));
        t.add(new IconoTaza(LIQUIDOS[tipo.ordinal()], tipo.soloFrio(), !tipo.soloFrio()), BorderLayout.NORTH);

        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));
        JLabel nombre = new JLabel(tipo.getNombre());
        nombre.setFont(Estilo.fuente(Font.BOLD, 18));
        nombre.setForeground(Estilo.TEXTO);
        JLabel descripcion = new JLabel("<html><div style='width:210px'>" + tipo.getDescripcion() + "</div></html>");
        descripcion.setForeground(Estilo.TEXTO_SUAVE);
        texto.add(nombre);
        texto.add(javax.swing.Box.createVerticalStrut(4));
        texto.add(descripcion);
        t.add(texto, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        JLabel precio = new JLabel("<html><span style='color:#8C7B6B'>Desde</span> <b>"
                + Estilo.moneda(tipo.precioDesde()) + "</b></html>");
        pie.add(precio, BorderLayout.WEST);
        JButton boton = Estilo.boton("Personalizar", Estilo.NARANJA);
        boton.addActionListener(e -> {
            personalizador.personalizar(tipo);
            navegador.ir(VentanaPrincipal.DISENAR);
        });
        pie.add(boton, BorderLayout.EAST);
        t.add(pie, BorderLayout.SOUTH);
        return t;
    }

    private JPanel crearTarjetaLibre() {
        Estilo.Tarjeta t = new Estilo.Tarjeta(new BorderLayout(0, 12));
        JLabel titulo = new JLabel("<html><div style='width:200px;text-align:center'>Crea la tuya<br>"
                + "<span style='font-size:11px;color:#8C7B6B'>Elige grano, tamaño, temperatura y agregados</span>"
                + "</div></html>", JLabel.CENTER);
        titulo.setFont(Estilo.fuente(Font.BOLD, 20));
        titulo.setForeground(Estilo.MARRON);
        t.add(titulo, BorderLayout.CENTER);
        JButton boton = Estilo.boton("Diseñar desde cero", Estilo.MARRON);
        boton.addActionListener(e -> navegador.ir(VentanaPrincipal.DISENAR));
        t.add(boton, BorderLayout.SOUTH);
        return t;
    }
}
