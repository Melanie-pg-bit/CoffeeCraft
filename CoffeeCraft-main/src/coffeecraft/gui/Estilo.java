package coffeecraft.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;

/** Colores, fuentes y componentes con el estilo visual de CoffeeCraft. */
public final class Estilo {
    public static final Color MARRON_OSCURO = new Color(0x4A2C1A);
    public static final Color MARRON = new Color(0x8B5A33);
    public static final Color NARANJA = new Color(0xE8A265);
    public static final Color CREMA = new Color(0xFBF4EC);
    public static final Color BLANCO = Color.WHITE;
    public static final Color TEXTO = new Color(0x2B1D12);
    public static final Color TEXTO_SUAVE = new Color(0x8C7B6B);
    public static final Color BORDE = new Color(0xEADBCB);
    public static final Color VERDE = new Color(0x4C9A6A);
    public static final Color ROJO = new Color(0xD9604C);
    public static final Color AZUL = new Color(0x4A90D9);

    private Estilo() {
    }

    public static Font fuente(int estilo, int tamano) {
        return new Font("SansSerif", estilo, tamano);
    }

    public static String moneda(double valor) {
        return String.format(Locale.US, "S/ %.2f", valor);
    }

    public static void antialias(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    // ---------------------------------------------------------------- etiquetas y campos

    public static JPanel encabezado(String titulo, String subtitulo) {
        JPanel p = new JPanel(new GridLayout(0, 1, 0, 2));
        p.setOpaque(false);
        JLabel t = new JLabel(titulo);
        t.setFont(fuente(Font.BOLD, 26));
        t.setForeground(TEXTO);
        JLabel s = new JLabel(subtitulo);
        s.setFont(fuente(Font.PLAIN, 13));
        s.setForeground(TEXTO_SUAVE);
        p.add(t);
        p.add(s);
        return p;
    }

    public static JLabel seccion(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.BOLD, 15));
        l.setForeground(TEXTO);
        return l;
    }

    public static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.BOLD, 12));
        l.setForeground(TEXTO_SUAVE);
        return l;
    }

    public static JTextField campo(int columnas) {
        JTextField f = new JTextField(columnas);
        f.setFont(fuente(Font.PLAIN, 13));
        f.setForeground(TEXTO);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    /** Ejecuta una acción cada vez que cambia el texto de un campo. */
    public static void alCambiar(JTextComponent campo, final Runnable accion) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                accion.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                accion.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                accion.run();
            }
        });
    }

    // ---------------------------------------------------------------- botones y tarjetas

    public static JButton boton(String texto, final Color fondo) {
        final JButton b = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                antialias(g2);
                Color c = fondo;
                if (!isEnabled()) {
                    c = new Color(0xCFC4B8);
                } else if (getModel().isPressed()) {
                    c = fondo.darker().darker();
                } else if (getModel().isRollover()) {
                    c = fondo.darker();
                }
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setRolloverEnabled(true);
        b.setForeground(Color.WHITE);
        b.setFont(fuente(Font.BOLD, 13));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        return b;
    }

    /** Panel blanco con esquinas redondeadas y borde suave. */
    public static class Tarjeta extends JPanel {
        public Tarjeta(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            antialias(g2);
            g2.setColor(BLANCO);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g2.setColor(BORDE);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ---------------------------------------------------------------- tablas

    /** Celda con relleno, filas alternadas y formato según el tipo de dato. */
    public static class CeldaRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                       boolean foco, int fila, int columna) {
            String texto;
            if (valor == null) {
                texto = "";
            } else if (valor instanceof Double) {
                texto = moneda((Double) valor);
            } else if (valor instanceof Date) {
                texto = new SimpleDateFormat("dd/MM/yyyy HH:mm").format((Date) valor);
            } else {
                texto = String.valueOf(valor);
            }
            super.getTableCellRendererComponent(tabla, texto, seleccionada, false, fila, columna);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            setFont(fuente(Font.PLAIN, 13));
            setForeground(TEXTO);
            setHorizontalAlignment(valor instanceof Number ? SwingConstants.RIGHT : SwingConstants.LEFT);
            if (!seleccionada) {
                setBackground(fila % 2 == 0 ? BLANCO : new Color(0xFFFAF4));
            }
            return this;
        }
    }

    public static void tabla(JTable t) {
        t.setRowHeight(34);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);
        t.setBackground(BLANCO);
        t.setSelectionBackground(new Color(0xF6DDC3));
        t.setSelectionForeground(TEXTO);
        CeldaRenderer celda = new CeldaRenderer();
        t.setDefaultRenderer(Object.class, celda);
        t.setDefaultRenderer(Number.class, celda);
        t.setDefaultRenderer(Double.class, celda);
        t.setDefaultRenderer(Date.class, celda);

        JTableHeader cabecera = t.getTableHeader();
        cabecera.setReorderingAllowed(false);
        cabecera.setPreferredSize(new Dimension(0, 38));
        cabecera.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean sel,
                                                           boolean foco, int fila, int columna) {
                super.getTableCellRendererComponent(tabla, valor, false, false, fila, columna);
                setOpaque(true);
                setBackground(MARRON_OSCURO);
                setForeground(Color.WHITE);
                setFont(fuente(Font.BOLD, 12));
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        });
    }

    public static JScrollPane scrollTabla(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(BORDE));
        sp.getViewport().setBackground(BLANCO);
        return sp;
    }
}
