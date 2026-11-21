package coffeecraft.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

/** Gráfico de barras verticales dibujado con Java2D a partir de un arreglo de valores. */
public class GraficoBarras extends JComponent {
    private String[] etiquetas = new String[0];
    private double[] valores = new double[0];
    private Color[] colores = new Color[0];

    public GraficoBarras() {
        setPreferredSize(new Dimension(360, 210));
        setOpaque(false);
    }

    public void setDatos(String[] etiquetas, double[] valores, Color[] colores) {
        this.etiquetas = etiquetas;
        this.valores = valores;
        this.colores = colores;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Estilo.antialias(g2);
        int ancho = getWidth();
        int alto = getHeight();
        int arriba = 24;
        int abajo = 30;
        int n = valores.length;
        if (n == 0) {
            g2.dispose();
            return;
        }

        double maximo = 1;
        for (double v : valores) {
            maximo = Math.max(maximo, v);
        }

        int hueco = ancho / n;
        int anchoBarra = Math.min(54, (int) (hueco * 0.6));
        int base = alto - abajo;
        g2.setColor(Estilo.BORDE);
        g2.drawLine(0, base, ancho, base);

        for (int i = 0; i < n; i++) {
            int altoBarra = (int) ((base - arriba) * (valores[i] / maximo));
            int x = i * hueco + (hueco - anchoBarra) / 2;
            int y = base - altoBarra;
            g2.setColor(colores[i % colores.length]);
            g2.fillRoundRect(x, y, anchoBarra, Math.max(altoBarra, 2), 10, 10);

            g2.setFont(Estilo.fuente(Font.BOLD, 12));
            g2.setColor(Estilo.TEXTO);
            String valor = String.valueOf((int) valores[i]);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(valor, x + (anchoBarra - fm.stringWidth(valor)) / 2, y - 6);

            g2.setFont(Estilo.fuente(Font.PLAIN, 11));
            g2.setColor(Estilo.TEXTO_SUAVE);
            fm = g2.getFontMetrics();
            g2.drawString(etiquetas[i], i * hueco + (hueco - fm.stringWidth(etiquetas[i])) / 2, alto - 10);
        }
        g2.dispose();
    }
}
