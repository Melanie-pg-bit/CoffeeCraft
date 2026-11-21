package coffeecraft.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Ventana principal: barra lateral de navegación y un panel central que cambia de pantalla. */
public class VentanaPrincipal extends JFrame implements Navegador {
    public static final String DASHBOARD = "DASHBOARD";
    public static final String MENU = "MENU";
    public static final String DISENAR = "DISENAR";
    public static final String CLIENTES = "CLIENTES";
    public static final String PEDIDOS = "PEDIDOS";

    private final Contexto ctx;
    private final CardLayout cartas = new CardLayout();
    private final JPanel contenido = new JPanel(cartas);
    private final JPanel navegacion = new JPanel();
    private final Map<String, BotonNav> botones = new LinkedHashMap<>();
    private final Map<String, JPanel> pantallas = new HashMap<>();

    public VentanaPrincipal(Contexto ctx) {
        super("CoffeeCraft - Café a tu medida");
        this.ctx = ctx;
        ctx.setVentana(this);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                VentanaPrincipal.this.ctx.guardar(); // se guarda antes de cerrar
            }
        });

        setLayout(new BorderLayout());
        add(crearBarraLateral(), BorderLayout.WEST);
        contenido.setBackground(Estilo.CREMA);
        add(contenido, BorderLayout.CENTER);

        PanelDisenador disenador = new PanelDisenador(ctx);
        agregarPantalla(DASHBOARD, "Dashboard", new PanelDashboard(ctx));
        agregarPantalla(MENU, "Menú", new PanelMenu(ctx, this, disenador));
        agregarPantalla(DISENAR, "Diseñar bebida", disenador);
        agregarPantalla(CLIENTES, "Clientes", new PanelClientes(ctx));
        agregarPantalla(PEDIDOS, "Pedidos", new PanelPedidos(ctx));

        setMinimumSize(new Dimension(1100, 700));
        setSize(1240, 800);
        setLocationRelativeTo(null);
        ir(MENU);
    }

    private JPanel crearBarraLateral() {
        JPanel lateral = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, Estilo.MARRON_OSCURO, 0, getHeight(), new Color(0xA0693A)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        lateral.setPreferredSize(new Dimension(230, 0));

        JPanel logo = new JPanel(new GridLayout(0, 1, 0, 2));
        logo.setOpaque(false);
        logo.setBorder(BorderFactory.createEmptyBorder(28, 24, 24, 24));
        JLabel nombre = new JLabel("CoffeeCraft");
        nombre.setFont(Estilo.fuente(Font.BOLD, 24));
        nombre.setForeground(Color.WHITE);
        JLabel lema = new JLabel("Café a tu medida");
        lema.setFont(Estilo.fuente(Font.PLAIN, 12));
        lema.setForeground(new Color(0xF3D9BD));
        logo.add(nombre);
        logo.add(lema);
        lateral.add(logo, BorderLayout.NORTH);

        navegacion.setOpaque(false);
        navegacion.setLayout(new BoxLayout(navegacion, BoxLayout.Y_AXIS));
        navegacion.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        lateral.add(navegacion, BorderLayout.CENTER);

        JLabel pie = new JLabel("<html><div style='text-align:center'>Morales · Gómez · Paredes<br>"
                + "EPIS - UNSA 2026</div></html>", SwingConstants.CENTER);
        pie.setFont(Estilo.fuente(Font.PLAIN, 11));
        pie.setForeground(new Color(0xF3D9BD));
        pie.setBorder(BorderFactory.createEmptyBorder(12, 8, 20, 8));
        lateral.add(pie, BorderLayout.SOUTH);
        return lateral;
    }

    private void agregarPantalla(final String clave, String texto, JPanel panel) {
        contenido.add(panel, clave);
        pantallas.put(clave, panel);
        if (panel instanceof Actualizable) {
            ctx.registrar((Actualizable) panel);
        }
        BotonNav boton = new BotonNav(texto);
        boton.addActionListener(e -> ir(clave));
        botones.put(clave, boton);
        navegacion.add(boton);
        navegacion.add(javax.swing.Box.createVerticalStrut(6));
    }

    @Override
    public void ir(String pantalla) {
        cartas.show(contenido, pantalla);
        for (Map.Entry<String, BotonNav> e : botones.entrySet()) {
            e.getValue().setActivo(e.getKey().equals(pantalla));
        }
        JPanel panel = pantallas.get(pantalla);
        if (panel instanceof Actualizable) {
            ((Actualizable) panel).actualizar();
        }
    }

    /** Botón de la barra lateral; el activo se resalta. */
    private static class BotonNav extends JButton {
        private boolean activo;

        BotonNav(String texto) {
            super(texto);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(Estilo.fuente(Font.BOLD, 14));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 10));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setPreferredSize(new Dimension(200, 44));
            setAlignmentX(LEFT_ALIGNMENT);
        }

        void setActivo(boolean activo) {
            this.activo = activo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Estilo.antialias(g2);
            if (activo) {
                g2.setColor(new Color(0xE8, 0xA2, 0x65, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            } else if (getModel().isRollover()) {
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}