package coffeecraft.gui;

import coffeecraft.modelo.EstadoPedido;
import coffeecraft.modelo.Tamano;
import coffeecraft.modelo.TipoBebida;
import coffeecraft.modelo.Usuario;
import coffeecraft.persistencia.ConexionBD;
import coffeecraft.persistencia.SincronizadorBD;
import coffeecraft.servicio.Reportes;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/** Resumen del negocio: indicadores, gráficos y reportes calculados con arreglos. */
public class PanelDashboard extends JPanel implements Actualizable {
    private static final Color[] COLORES_TIPO = {
        new Color(0x6F4E37), new Color(0xA67B5B), new Color(0xC68B59), new Color(0x8B5A33), new Color(0xE8A265)
    };
    private static final Color[] COLORES_ESTADO = {Estilo.NARANJA, Estilo.AZUL, Estilo.VERDE, Estilo.MARRON};

    private final Contexto ctx;
    private final JLabel lblClientes = numeroGrande(Estilo.TEXTO);
    private final JLabel lblPedidos = numeroGrande(Estilo.AZUL);
    private final JLabel lblIngresos = numeroGrande(Estilo.VERDE);
    private final JLabel lblPromedio = numeroGrande(Estilo.MARRON);
    private final GraficoBarras graficoTipos = new GraficoBarras();
    private final GraficoBarras graficoEstados = new GraficoBarras();
    private final JPanel listaTop = new JPanel(new GridLayout(0, 1, 0, 10));
    private final DefaultTableModel modeloMatriz = new DefaultTableModel(
            new String[]{"Bebida", "Pequeño", "Mediano", "Grande"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    public PanelDashboard(Contexto ctx) {
        this.ctx = ctx;
        setLayout(new BorderLayout(0, 16));
        setBackground(Estilo.CREMA);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.add(Estilo.encabezado("Dashboard", "Resumen de clientes, pedidos e ingresos"), BorderLayout.WEST);
        JButton btnBD = Estilo.boton("Sincronizar con base de datos", Estilo.MARRON);
        btnBD.addActionListener(e -> sincronizarBD());
        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setOpaque(false);
        derecha.add(btnBD, BorderLayout.NORTH);
        cabecera.add(derecha, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);

        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 16, 16);

        c.gridy = 0;
        c.gridwidth = 1;
        c.gridx = 0;
        contenido.add(tarjetaKpi("Clientes", lblClientes), c);
        c.gridx = 1;
        contenido.add(tarjetaKpi("Pedidos", lblPedidos), c);
        c.gridx = 2;
        contenido.add(tarjetaKpi("Ingresos", lblIngresos), c);
        c.gridx = 3;
        c.insets = new Insets(0, 0, 16, 0);
        contenido.add(tarjetaKpi("Ticket promedio", lblPromedio), c);

        c.gridy = 1;
        c.gridwidth = 2;
        c.weighty = 1;
        c.gridx = 0;
        c.insets = new Insets(0, 0, 16, 16);
        contenido.add(tarjetaConContenido("Bebidas vendidas por tipo", graficoTipos), c);
        c.gridx = 2;
        c.insets = new Insets(0, 0, 16, 0);
        contenido.add(tarjetaConContenido("Pedidos por estado", graficoEstados), c);

        listaTop.setOpaque(false);
        JTable tablaMatriz = new JTable(modeloMatriz);
        Estilo.tabla(tablaMatriz);
        tablaMatriz.setRowHeight(28);
        tablaMatriz.setEnabled(false);
        graficoTipos.setPreferredSize(new java.awt.Dimension(360, 170));
        graficoEstados.setPreferredSize(new java.awt.Dimension(360, 170));
        c.gridy = 2;
        c.gridx = 0;
        c.insets = new Insets(0, 0, 0, 16);
        JPanel arribaTop = new JPanel(new BorderLayout()); // evita que las filas se estiren
        arribaTop.setOpaque(false);
        arribaTop.add(listaTop, BorderLayout.NORTH);
        contenido.add(tarjetaConContenido("Mejores clientes", arribaTop), c);
        c.gridx = 2;
        c.insets = new Insets(0, 0, 0, 0);
        contenido.add(tarjetaConContenido("Ingresos por bebida y tamaño", Estilo.scrollTabla(tablaMatriz)), c);

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        actualizar();
    }

    private static JLabel numeroGrande(Color color) {
        JLabel l = new JLabel("0");
        l.setFont(Estilo.fuente(Font.BOLD, 28));
        l.setForeground(color);
        return l;
    }

    private static JPanel tarjetaKpi(String titulo, JLabel valor) {
        Estilo.Tarjeta t = new Estilo.Tarjeta(new BorderLayout(0, 6));
        t.add(Estilo.etiqueta(titulo.toUpperCase()), BorderLayout.NORTH);
        t.add(valor, BorderLayout.CENTER);
        return t;
    }

    private static JPanel tarjetaConContenido(String titulo, java.awt.Component contenido) {
        Estilo.Tarjeta t = new Estilo.Tarjeta(new BorderLayout(0, 10));
        t.add(Estilo.seccion(titulo), BorderLayout.NORTH);
        t.add(contenido, BorderLayout.CENTER);
        return t;
    }

    @Override
    public void actualizar() {
        List<Usuario> usuarios = ctx.getDatos().getUsuarios();
        double[] totales = Reportes.totalesPorPedido(usuarios);
        lblClientes.setText(String.valueOf(usuarios.size()));
        lblPedidos.setText(String.valueOf(totales.length));
        lblIngresos.setText(Estilo.moneda(Reportes.suma(totales)));
        lblPromedio.setText(Estilo.moneda(Reportes.promedio(totales)));

        TipoBebida[] tipos = TipoBebida.values();
        int[] porTipo = Reportes.bebidasPorTipo(usuarios);
        String[] etiquetasTipo = new String[tipos.length];
        double[] valoresTipo = new double[tipos.length];
        for (int i = 0; i < tipos.length; i++) {
            etiquetasTipo[i] = tipos[i].getNombre();
            valoresTipo[i] = porTipo[i];
        }
        graficoTipos.setDatos(etiquetasTipo, valoresTipo, COLORES_TIPO);

        EstadoPedido[] estados = EstadoPedido.values();
        int[] porEstado = Reportes.pedidosPorEstado(usuarios);
        String[] etiquetasEstado = new String[estados.length];
        double[] valoresEstado = new double[estados.length];
        for (int i = 0; i < estados.length; i++) {
            etiquetasEstado[i] = estados[i].getEtiqueta();
            valoresEstado[i] = porEstado[i];
        }
        graficoEstados.setDatos(etiquetasEstado, valoresEstado, COLORES_ESTADO);

        listaTop.removeAll();
        Usuario[] top = Reportes.topClientes(usuarios, 3);
        for (int i = 0; i < top.length; i++) {
            JPanel fila = new JPanel(new BorderLayout(12, 0));
            fila.setOpaque(false);
            JLabel puesto = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            puesto.setFont(Estilo.fuente(Font.BOLD, 14));
            puesto.setForeground(Color.WHITE);
            puesto.setOpaque(true);
            puesto.setBackground(i == 0 ? Estilo.NARANJA : Estilo.MARRON);
            puesto.setPreferredSize(new java.awt.Dimension(28, 28));
            JPanel centrado = new JPanel(new GridBagLayout()); // mantiene la insignia en 28x28
            centrado.setOpaque(false);
            centrado.add(puesto);
            JLabel nombre = new JLabel("<html><b>" + top[i].getNombre() + "</b><br><span style='color:#8C7B6B'>"
                    + top[i].cantidadPedidos() + " pedidos</span></html>");
            JLabel gasto = new JLabel(Estilo.moneda(top[i].totalGastado()));
            gasto.setFont(Estilo.fuente(Font.BOLD, 14));
            gasto.setForeground(Estilo.VERDE);
            fila.add(centrado, BorderLayout.WEST);
            fila.add(nombre, BorderLayout.CENTER);
            fila.add(gasto, BorderLayout.EAST);
            listaTop.add(fila);
        }
        listaTop.revalidate();
        listaTop.repaint();

        double[][] matriz = Reportes.ventasPorTipoYTamano(usuarios);
        modeloMatriz.setRowCount(0);
        for (int i = 0; i < tipos.length; i++) {
            Object[] fila = new Object[1 + Tamano.values().length];
            fila[0] = tipos[i].getNombre();
            for (int j = 0; j < Tamano.values().length; j++) {
                fila[j + 1] = Estilo.moneda(matriz[i][j]);
            }
            modeloMatriz.addRow(fila);
        }
    }

    private void sincronizarBD() {
        try {
            String mensaje = new SincronizadorBD(new ConexionBD()).sincronizar(ctx.getDatos().getUsuarios());
            Dialogos.info(this, mensaje);
        } catch (SQLException e) {
            Dialogos.error(this, "No se pudo sincronizar con la base de datos.\n" + e.getMessage()
                    + "\n\nVerifica que el driver sqlite-jdbc esté agregado al proyecto.");
        }
    }
}