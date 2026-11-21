package coffeecraft.gui;

import coffeecraft.modelo.EstadoPedido;
import coffeecraft.modelo.Pedido;
import coffeecraft.modelo.Usuario;
import coffeecraft.persistencia.RepositorioTexto;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/** Seguimiento de pedidos: filtros, avance de estado, ticket en pantalla y exportación a archivos de texto. */
public class PanelPedidos extends JPanel implements Actualizable {
    private final Contexto ctx;
    private final ModeloPedidos modelo = new ModeloPedidos();
    final JTable tabla = new JTable(modelo);
    final JTextField txtBuscar = Estilo.campo(16);
    final JComboBox<Object> cmbEstado = new JComboBox<>();
    final JButton btnAvanzar = Estilo.boton("Avanzar estado", Estilo.VERDE);
    private final JLabel lblResumen = new JLabel();

    public PanelPedidos(Contexto ctx) {
        this.ctx = ctx;
        setLayout(new BorderLayout(0, 16));
        setBackground(Estilo.CREMA);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        cmbEstado.addItem("Todos los estados");
        for (EstadoPedido e : EstadoPedido.values()) {
            cmbEstado.addItem(e);
        }

        JPanel arriba = new JPanel(new BorderLayout(0, 14));
        arriba.setOpaque(false);
        arriba.add(Estilo.encabezado("Pedidos", "Sigue cada pedido desde que se pide hasta que se entrega"),
                BorderLayout.NORTH);

        JPanel herramientas = new JPanel(new BorderLayout());
        herramientas.setOpaque(false);
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izquierda.setOpaque(false);
        izquierda.add(new JLabel("Cliente:"));
        izquierda.add(txtBuscar);
        izquierda.add(cmbEstado);
        herramientas.add(izquierda, BorderLayout.WEST);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derecha.setOpaque(false);
        JButton btnTicket = Estilo.boton("Ver ticket", Estilo.AZUL);
        JButton btnGuardarTicket = Estilo.boton("Guardar ticket", Estilo.MARRON);
        JButton btnExportar = Estilo.boton("Exportar CSV", Estilo.MARRON);
        derecha.add(btnAvanzar);
        derecha.add(btnTicket);
        derecha.add(btnGuardarTicket);
        derecha.add(btnExportar);
        herramientas.add(derecha, BorderLayout.EAST);
        arriba.add(herramientas, BorderLayout.CENTER);
        add(arriba, BorderLayout.NORTH);

        Estilo.tabla(tabla);
        tabla.setAutoCreateRowSorter(true);
        tabla.getColumnModel().getColumn(3).setCellRenderer(new RenderEstado());
        int[] anchos = {45, 160, 140, 160, 70, 90, 90, 90, 110};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        add(Estilo.scrollTabla(tabla), BorderLayout.CENTER);

        lblResumen.setForeground(Estilo.TEXTO_SUAVE);
        add(lblResumen, BorderLayout.SOUTH);

        Estilo.alCambiar(txtBuscar, this::filtrar);
        cmbEstado.addActionListener(e -> filtrar());
        btnAvanzar.addActionListener(e -> avanzarEstado());
        btnTicket.addActionListener(e -> verTicket());
        btnGuardarTicket.addActionListener(e -> guardarTicket());
        btnExportar.addActionListener(e -> exportarCSV());

        filtrar();
    }

    @Override
    public void actualizar() {
        filtrar();
    }

    /** Aplica el filtro por cliente y por estado; los pedidos más recientes salen primero. */
    void filtrar() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        Object estadoElegido = cmbEstado.getSelectedItem();
        List<Fila> visibles = new ArrayList<>();
        int total = 0;
        for (Usuario u : ctx.getDatos().getUsuarios()) {
            for (Pedido p : u.verHistorial()) {
                total++;
                boolean porNombre = texto.isEmpty() || u.getNombre().toLowerCase().contains(texto);
                boolean porEstado = !(estadoElegido instanceof EstadoPedido) || p.getEstado() == estadoElegido;
                if (porNombre && porEstado) {
                    visibles.add(new Fila(u, p));
                }
            }
        }
        Collections.sort(visibles, (a, b) -> b.pedido.getId() - a.pedido.getId());
        modelo.setFilas(visibles);

        double suma = 0;
        for (Fila f : visibles) {
            suma += f.pedido.calcularTotal();
        }
        lblResumen.setText("Mostrando " + visibles.size() + " de " + total + " pedido(s)  ·  Suma: " + Estilo.moneda(suma));
    }

    private Fila filaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Dialogos.aviso(this, "Selecciona un pedido de la tabla.");
            return null;
        }
        return modelo.getFila(tabla.convertRowIndexToModel(fila));
    }

    void avanzarEstado() {
        Fila f = filaSeleccionada();
        if (f == null) {
            return;
        }
        if (f.pedido.getEstado() == EstadoPedido.ENTREGADO) {
            Dialogos.info(this, "El pedido #" + f.pedido.getId() + " ya fue entregado.");
            return;
        }
        int id = f.pedido.getId();
        f.pedido.cambiarEstado(f.pedido.getEstado().siguiente());
        ctx.guardar();
        ctx.actualizarTodo();
        seleccionarPorId(id);
    }

    private void seleccionarPorId(int id) {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if (modelo.getFila(i).pedido.getId() == id) {
                int vista = tabla.convertRowIndexToView(i);
                tabla.setRowSelectionInterval(vista, vista);
                return;
            }
        }
    }

    private void verTicket() {
        Fila f = filaSeleccionada();
        if (f != null) {
            DialogoTicket.mostrar(SwingUtilities.getWindowAncestor(this), f.pedido.getId(),
                    RepositorioTexto.generarTicket(f.usuario, f.pedido));
        }
    }

    private void guardarTicket() {
        Fila f = filaSeleccionada();
        if (f == null) {
            return;
        }
        File archivo = Dialogos.elegirParaGuardar(this, "ticket_pedido_" + f.pedido.getId() + ".txt",
                "Archivos de texto", "txt");
        if (archivo == null) {
            return;
        }
        try {
            RepositorioTexto.exportarTicket(f.usuario, f.pedido, archivo);
            Dialogos.info(this, "Ticket guardado en:\n" + archivo.getAbsolutePath());
        } catch (IOException e) {
            Dialogos.error(this, "No se pudo escribir el archivo:\n" + e.getMessage());
        }
    }

    private void exportarCSV() {
        File archivo = Dialogos.elegirParaGuardar(this, "pedidos.csv", "Archivos CSV", "csv");
        if (archivo == null) {
            return;
        }
        try {
            RepositorioTexto.exportarPedidos(ctx.getDatos().getUsuarios(), archivo);
            Dialogos.info(this, "Pedidos exportados a:\n" + archivo.getAbsolutePath());
        } catch (IOException e) {
            Dialogos.error(this, "No se pudo escribir el archivo:\n" + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ modelo y renderer

    /** Una fila de la tabla: el pedido y el cliente al que pertenece. */
    private static class Fila {
        final Usuario usuario;
        final Pedido pedido;

        Fila(Usuario usuario, Pedido pedido) {
            this.usuario = usuario;
            this.pedido = pedido;
        }
    }

    private static class ModeloPedidos extends AbstractTableModel {
        private final String[] columnas = {"N°", "Cliente", "Fecha", "Estado", "Bebidas",
            "Subtotal", "Descuento", "Total", "Entrega (min)"};
        private List<Fila> filas = new ArrayList<>();

        void setFilas(List<Fila> filas) {
            this.filas = filas;
            fireTableDataChanged();
        }

        Fila getFila(int indice) {
            return filas.get(indice);
        }

        @Override
        public int getRowCount() {
            return filas.size();
        }

        @Override
        public int getColumnCount() {
            return columnas.length;
        }

        @Override
        public String getColumnName(int columna) {
            return columnas[columna];
        }

        @Override
        public Class<?> getColumnClass(int columna) {
            switch (columna) {
                case 0:
                case 4:
                case 8:
                    return Integer.class;
                case 2:
                    return java.util.Date.class;
                case 3:
                    return EstadoPedido.class;
                case 5:
                case 6:
                case 7:
                    return Double.class;
                default:
                    return String.class;
            }
        }

        @Override
        public Object getValueAt(int fila, int columna) {
            Fila f = filas.get(fila);
            switch (columna) {
                case 0:
                    return f.pedido.getId();
                case 1:
                    return f.usuario.getNombre();
                case 2:
                    return f.pedido.getFecha();
                case 3:
                    return f.pedido.getEstado();
                case 4:
                    return f.pedido.cantidadBebidas();
                case 5:
                    return f.pedido.getSubtotal();
                case 6:
                    return f.pedido.getMontoDescuento();
                case 7:
                    return f.pedido.calcularTotal();
                default:
                    return f.pedido.calcularTiempoEntrega();
            }
        }
    }

    /** Colorea el estado del pedido. */
    private static class RenderEstado extends Estilo.CeldaRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel,
                                                       boolean foco, int fila, int columna) {
            super.getTableCellRendererComponent(t, "● " + valor, sel, foco, fila, columna);
            Color color = Estilo.TEXTO;
            if (valor == EstadoPedido.PENDIENTE) {
                color = new Color(0xD08A3C);
            } else if (valor == EstadoPedido.EN_PREPARACION) {
                color = Estilo.AZUL;
            } else if (valor == EstadoPedido.LISTO) {
                color = Estilo.VERDE;
            } else if (valor == EstadoPedido.ENTREGADO) {
                color = Estilo.MARRON;
            }
            setForeground(color);
            setFont(Estilo.fuente(Font.BOLD, 13));
            return this;
        }
    }
}