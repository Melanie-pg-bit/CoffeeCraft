package coffeecraft.gui;

import coffeecraft.modelo.Usuario;
import coffeecraft.persistencia.RepositorioTexto;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

/** Gestión de clientes: tabla con búsqueda y filtro, altas, ediciones, bajas e importación/exportación CSV. */
public class PanelClientes extends JPanel implements Actualizable {
    private final Contexto ctx;
    private final ModeloClientes modelo = new ModeloClientes();
    final JTable tabla = new JTable(modelo);
    final JTextField txtBuscar = Estilo.campo(18);
    final JComboBox<String> cmbFiltro = new JComboBox<>(new String[]{"Todos", "Frecuentes", "Regulares"});
    private final JLabel lblConteo = new JLabel();

    public PanelClientes(Contexto ctx) {
        this.ctx = ctx;
        setLayout(new BorderLayout(0, 16));
        setBackground(Estilo.CREMA);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Parte de arriba: título, búsqueda y botones
        JPanel arriba = new JPanel(new BorderLayout(0, 14));
        arriba.setOpaque(false);
        arriba.add(Estilo.encabezado("Clientes", "Registra, busca y administra a tus clientes"), BorderLayout.NORTH);

        JPanel herramientas = new JPanel(new BorderLayout());
        herramientas.setOpaque(false);
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izquierda.setOpaque(false);
        izquierda.add(new JLabel("Buscar:"));
        izquierda.add(txtBuscar);
        izquierda.add(cmbFiltro);
        herramientas.add(izquierda, BorderLayout.WEST);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derecha.setOpaque(false);
        JButton btnNuevo = Estilo.boton("Nuevo", Estilo.VERDE);
        JButton btnEditar = Estilo.boton("Editar", Estilo.AZUL);
        JButton btnEliminar = Estilo.boton("Eliminar", Estilo.ROJO);
        JButton btnImportar = Estilo.boton("Importar CSV", Estilo.MARRON);
        JButton btnExportar = Estilo.boton("Exportar CSV", Estilo.MARRON);
        derecha.add(btnNuevo);
        derecha.add(btnEditar);
        derecha.add(btnEliminar);
        derecha.add(btnImportar);
        derecha.add(btnExportar);
        herramientas.add(derecha, BorderLayout.EAST);
        arriba.add(herramientas, BorderLayout.CENTER);
        add(arriba, BorderLayout.NORTH);

        // Tabla
        Estilo.tabla(tabla);
        tabla.setAutoCreateRowSorter(true);
        tabla.getColumnModel().getColumn(7).setCellRenderer(new RenderTipo());
        int[] anchos = {45, 160, 200, 135, 230, 80, 100, 90};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        add(Estilo.scrollTabla(tabla), BorderLayout.CENTER);

        lblConteo.setForeground(Estilo.TEXTO_SUAVE);
        add(lblConteo, BorderLayout.SOUTH);

        // Eventos
        Estilo.alCambiar(txtBuscar, this::filtrar);
        cmbFiltro.addActionListener(e -> filtrar());
        btnNuevo.addActionListener(e -> nuevo());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnImportar.addActionListener(e -> importar());
        btnExportar.addActionListener(e -> exportar());
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editar();
                }
            }
        });

        filtrar();
    }

    @Override
    public void actualizar() {
        filtrar();
    }

    /** Aplica el texto de búsqueda y el filtro de tipo a la lista de clientes. */
    void filtrar() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        String filtro = (String) cmbFiltro.getSelectedItem();
        List<Usuario> todos = ctx.getDatos().getUsuarios();
        List<Usuario> visibles = new ArrayList<>();
        for (Usuario u : todos) {
            boolean coincide = texto.isEmpty()
                    || u.getNombre().toLowerCase().contains(texto)
                    || u.getEmail().toLowerCase().contains(texto)
                    || u.getTelefono().toLowerCase().contains(texto);
            boolean tipo = "Todos".equals(filtro)
                    || ("Frecuentes".equals(filtro) && u.esFrecuente())
                    || ("Regulares".equals(filtro) && !u.esFrecuente());
            if (coincide && tipo) {
                visibles.add(u);
            }
        }
        modelo.setFilas(visibles);
        lblConteo.setText("Mostrando " + visibles.size() + " de " + todos.size() + " cliente(s)");
    }

    private Usuario clienteSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Dialogos.aviso(this, "Selecciona un cliente de la tabla.");
            return null;
        }
        return modelo.getUsuario(tabla.convertRowIndexToModel(fila));
    }

    private java.awt.Window ventana() {
        return SwingUtilities.getWindowAncestor(this);
    }

    private void nuevo() {
        Usuario u = new Usuario(ctx.getDatos().siguienteIdUsuario(), "", "", "", "");
        if (DialogoCliente.mostrar(ventana(), u, true)) {
            ctx.getDatos().getUsuarios().add(u);
            ctx.guardar();
            ctx.actualizarTodo();
        }
    }

    private void editar() {
        Usuario u = clienteSeleccionado();
        if (u != null && DialogoCliente.mostrar(ventana(), u, false)) {
            ctx.guardar();
            ctx.actualizarTodo();
        }
    }

    private void eliminar() {
        Usuario u = clienteSeleccionado();
        if (u == null) {
            return;
        }
        if (Dialogos.confirmar(this, "Se eliminará a " + u.getNombre() + " y sus " + u.cantidadPedidos()
                + " pedido(s).\n¿Deseas continuar?")) {
            ctx.getDatos().getUsuarios().remove(u);
            ctx.guardar();
            ctx.actualizarTodo();
        }
    }

    private void importar() {
        File archivo = Dialogos.elegirParaAbrir(this, "Archivos CSV", "csv");
        if (archivo == null) {
            return;
        }
        try {
            ArrayList<Usuario> leidos = RepositorioTexto.importarClientes(archivo, ctx.getDatos().siguienteIdUsuario());
            int agregados = 0;
            for (Usuario nuevo : leidos) {
                if (!existeCorreo(nuevo.getEmail())) {
                    ctx.getDatos().getUsuarios().add(nuevo);
                    agregados++;
                }
            }
            ctx.guardar();
            ctx.actualizarTodo();
            Dialogos.info(this, agregados + " cliente(s) importado(s).\n"
                    + (leidos.size() - agregados) + " omitido(s) por tener un correo ya registrado.");
        } catch (IOException e) {
            Dialogos.error(this, "No se pudo leer el archivo:\n" + e.getMessage());
        }
    }

    private boolean existeCorreo(String correo) {
        if (correo == null || correo.isEmpty()) {
            return false;
        }
        for (Usuario u : ctx.getDatos().getUsuarios()) {
            if (u.getEmail().equalsIgnoreCase(correo)) {
                return true;
            }
        }
        return false;
    }

    private void exportar() {
        File archivo = Dialogos.elegirParaGuardar(this, "clientes.csv", "Archivos CSV", "csv");
        if (archivo == null) {
            return;
        }
        try {
            RepositorioTexto.exportarClientes(ctx.getDatos().getUsuarios(), archivo);
            Dialogos.info(this, "Clientes exportados a:\n" + archivo.getAbsolutePath());
        } catch (IOException e) {
            Dialogos.error(this, "No se pudo escribir el archivo:\n" + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ modelo y renderer

    /** Modelo de la tabla: cada fila es un Usuario. */
    private static class ModeloClientes extends AbstractTableModel {
        private final String[] columnas = {"ID", "Nombre", "Correo", "Teléfono", "Dirección", "Pedidos", "Gastado", "Tipo"};
        private List<Usuario> filas = new ArrayList<>();

        void setFilas(List<Usuario> filas) {
            this.filas = filas;
            fireTableDataChanged();
        }

        Usuario getUsuario(int fila) {
            return filas.get(fila);
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
                case 5:
                    return Integer.class;
                case 6:
                    return Double.class;
                default:
                    return String.class;
            }
        }

        @Override
        public Object getValueAt(int fila, int columna) {
            Usuario u = filas.get(fila);
            switch (columna) {
                case 0:
                    return u.getId();
                case 1:
                    return u.getNombre();
                case 2:
                    return u.getEmail();
                case 3:
                    return u.getTelefono();
                case 4:
                    return u.getDireccion();
                case 5:
                    return u.cantidadPedidos();
                case 6:
                    return u.totalGastado();
                default:
                    return u.esFrecuente() ? "Frecuente" : "Regular";
            }
        }
    }

    /** Pinta "Frecuente" en verde y en negrita. */
    private static class RenderTipo extends Estilo.CeldaRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel,
                                                       boolean foco, int fila, int columna) {
            super.getTableCellRendererComponent(t, valor, sel, foco, fila, columna);
            if ("Frecuente".equals(valor)) {
                setForeground(Estilo.VERDE);
                setFont(Estilo.fuente(Font.BOLD, 13));
            }
            return this;
        }
    }
}