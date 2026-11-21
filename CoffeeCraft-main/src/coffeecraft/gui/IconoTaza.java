package coffeecraft.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.CubicCurve2D;
import javax.swing.JComponent;

public class IconoTaza extends JComponent {
    private final Color liquido;
    private final boolean vaso;
    private final boolean caliente;
    
    public IconoTaza(Color liquido, boolean vaso, boolean caliente) {
        this.liquido = liquido;
        this.vaso = vaso;
        this.caliente = caliente;
        setPreferredSize(new Dimension(200, 130));
        setOpaque(false);
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Estilo.antialias(g2);
        int w = getWidth();
        int h = getHeight();
        g2.setColor(new Color(0xF6E7D8));
        g2.fillRoundRect(0, 0, w, h, 16, 16);
        int cx = w / 2;
        int cy = h / 2 + 8;
        if (vaso) {
            dibujarVaso(g2, cx, cy);
        } else {
            dibujarTaza(g2, cx, cy);
        }
        g2.dispose();
    }

    private void dibujarTaza(Graphics2D g2, int cx, int cy) {
        g2.setColor(new Color(0xD7BFA6)); // platillo
        g2.fillOval(cx - 52, cy + 22, 104, 14);

        g2.setColor(Color.WHITE); // asa
        g2.setStroke(new BasicStroke(7f));
        g2.drawArc(cx + 22, cy - 14, 28, 26, -90, 180);
        g2.setStroke(new BasicStroke(1f));

        g2.setColor(Color.WHITE); // cuerpo
        g2.fillRoundRect(cx - 34, cy - 26, 68, 54, 28, 28);
        g2.fillRect(cx - 34, cy - 26, 68, 20);
        g2.fillOval(cx - 34, cy - 32, 68, 14);

        g2.setColor(liquido); // café
        g2.fillOval(cx - 30, cy - 30, 60, 10);

        if (caliente) {
            g2.setColor(new Color(0xB7A593));
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = -1; i <= 1; i++) {
                int x = cx + i * 14;
                g2.draw(new CubicCurve2D.Float(x, cy - 38, x - 8, cy - 46, x + 8, cy - 54, x, cy - 62));
            }
            g2.setStroke(new BasicStroke(1f));
        }
    }

    private void dibujarVaso(Graphics2D g2, int cx, int cy) {
        g2.setColor(Estilo.ROJO); // pajilla
        g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx + 8, cy - 52, cx + 2, cy + 10);
        g2.setStroke(new BasicStroke(1f));

        Polygon vasoForma = new Polygon(
                new int[]{cx - 30, cx + 30, cx + 22, cx - 22},
                new int[]{cy - 36, cy - 36, cy + 30, cy + 30}, 4);
        g2.setColor(new Color(255, 255, 255, 230));
        g2.fillPolygon(vasoForma);

        Polygon cafe = new Polygon(
                new int[]{cx - 28, cx + 28, cx + 22, cx - 22},
                new int[]{cy - 20, cy - 20, cy + 30, cy + 30}, 4);
        g2.setColor(liquido);
        g2.fillPolygon(cafe);

        g2.setColor(new Color(255, 255, 255, 170)); // cubos de hielo
        g2.fillRoundRect(cx - 18, cy - 14, 16, 16, 5, 5);
        g2.fillRoundRect(cx + 2, cy - 6, 16, 16, 5, 5);
        g2.fillRoundRect(cx - 12, cy + 8, 14, 14, 5, 5);

        g2.setColor(new Color(0xD7BFA6));
        g2.drawPolygon(vasoForma);
    }
}
