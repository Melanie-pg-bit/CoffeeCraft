package coffeecraft.gui;

import coffeecraft.modelo.Agregado;
import coffeecraft.modelo.Bebida;
import coffeecraft.modelo.Descuento;
import coffeecraft.modelo.Grano;
import coffeecraft.modelo.Pedido;
import coffeecraft.modelo.SinDescuento;
import coffeecraft.modelo.Tamano;
import coffeecraft.modelo.Temperatura;
import coffeecraft.modelo.TipoBebida;
import coffeecraft.modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;

/**
 * Pantalla principal de CoffeeCraft: se arma la bebida (tipo, grano, tamaño, temperatura,
 * agregados) y el precio y el tiempo se recalculan en vivo con cada cambio.
 */
public class PanelDisenador extends JPanel implements Actualizable, Personalizador {
    private final Contexto ctx;

    final JComboBox<TipoBebida> cmbTipo = new JComboBox<>(TipoBebida.values());
    final JComboBox<Grano> cmbGrano = new JComboBox<>();
    final JSlider sliderExtra = new JSlider(1, 4, 1);
    final JComboBox<Usuario> cmbCliente = new JComboBox<>();
    final DefaultListModel<Bebida> carrito = new DefaultListModel<>();
    final JList<Bebida> listaCarrito = new JList<>(carrito);
    final JButton btnAgregar = Estilo.boton("Agregar al pedido", Estilo.NARANJA);
    final JButton btnQuitar = Estilo.boton("Quitar", Estilo.MARRON);
    final JButton btnConfirmar = Estilo.boton("Confirmar pedido", Estilo.VERDE);

    private final Map<JRadioButton, Tamano> radiosTamano = new LinkedHashMap<>();
    private final Map<JRadioButton, Temperatura> radiosTemperatura = new LinkedHashMap<>();
    private final Map<JCheckBox, Agregado> casillas = new LinkedHashMap<>();

    private final JLabel lblExtra = Estilo.etiqueta("");
    private final JLabel lblPrecio = new JLabel("S/ 0.00");
    private final JLabel lblTiempo = new JLabel("0 min");
    private final JLabel lblBase = new JLabel();
    private final JLabel lblGrano = new JLabel();
    private final JLabel lblAgregados = new JLabel();
    private final JLabel lblDescripcion = new JLabel();
    private final JLabel lblPedido = new JLabel();

    private boolean cargando = true;
    private boolean frioForzado = false;

    private final ActionListener alCambiar = e -> {
        if (!cargando) {
            refrescar();
        }
    };

    public PanelDisenador(Contexto ctx) {
        this.ctx = ctx;
        setLayout(new BorderLayout(0, 16));
        setBackground(Estilo.CREMA);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        add(Estilo.encabezado("Diseña tu bebida",
                "Cada cambio recalcula el precio y el tiempo de entrega al instante"), BorderLayout.NORTH);

        for (Grano g : ctx.getCatalogo().getGranos()) {
            cmbGrano.addItem(g);
        }
        cmbGrano.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean sel, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, sel, foco);
                if (valor instanceof Grano) {
                    Grano g = (Grano) valor;
                    setText(g + "   +" + Estilo.moneda(g.getPrecioExtra()));
                }
                return this;
            }
        });

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        GridBagConstraints cc = new GridBagConstraints();
        cc.fill = GridBagConstraints.BOTH;
        cc.weighty = 1;
        cc.gridy = 0;
        cc.gridx = 0;
        cc.weightx = 0.56;
        cc.insets = new Insets(0, 0, 0, 18);
        centro.add(crearPanelIngredientes(), cc);
        cc.gridx = 1;
        cc.weightx = 0.44;
        cc.insets = new Insets(0, 0, 0, 0);
        centro.add(crearColumnaDerecha(), cc);
        add(centro, BorderLayout.CENTER);

        cmbTipo.addActionListener(e -> {
            if (!cargando) {
                ajustarExtra();
                refrescar();
            }
        });
        cmbGrano.addActionListener(alCambiar);
        cmbCliente.addActionListener(e -> {
            if (!cargando) {
                refrescarPedido();
            }
        });
        sliderExtra.addChangeListener(e -> {
            if (!cargando) {
                actualizarEtiquetaExtra();
                refrescar();
            }
        });
        btnAgregar.addActionListener(e -> agregarAlCarrito());
        btnQuitar.addActionListener(e -> quitarDelCarrito());
        btnConfirmar.addActionListener(e -> confirmar());

        ajustarExtra();
        actualizar();
        cargando = false;
        refrescar();
    }

    // ------------------------------------------------------------------ construcción de la interfaz

    private JPanel crearPanelIngredientes() {
        Estilo.Tarjeta t = new Estilo.Tarjeta(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(0, 0, 12, 0);

        t.add(Estilo.seccion("Ingredientes"), c);
        c.gridy++;
        agregarFila(t, c, "TIPO DE BEBIDA", cmbTipo);
        agregarFila(t, c, "GRANO", cmbGrano);
        agregarFila(t, c, "TAMAÑO", crearRadios(Tamano.values(), radiosTamano, Tamano.MEDIANO));
        agregarFila(t, c, "TEMPERATURA", crearRadios(Temperatura.values(), radiosTemperatura, Temperatura.CALIENTE));

        sliderExtra.setOpaque(false);
        sliderExtra.setPaintTicks(true);
        JPanel extra = new JPanel(new BorderLayout(0, 2));
        extra.setOpaque(false);
        extra.add(lblExtra, BorderLayout.NORTH);
        extra.add(sliderExtra, BorderLayout.CENTER);
        t.add(extra, c);
        c.gridy++;

        JPanel agregados = new JPanel(new GridLayout(0, 2, 10, 4));
        agregados.setOpaque(false);
        for (Agregado a : ctx.getCatalogo().getAgregados()) {
            JCheckBox casilla = new JCheckBox(String.format("%s  (+%.2f)", a.getNombre(), a.calcularPrecio()));
            casilla.setOpaque(false);
            casilla.setForeground(Estilo.TEXTO);
            casilla.addActionListener(alCambiar);
            casillas.put(casilla, a);
            agregados.add(casilla);
        }
        agregarFila(t, c, "AGREGADOS", agregados);

        c.gridy++;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 0);
        JPanel relleno = new JPanel();
        relleno.setOpaque(false);
        t.add(relleno, c);
        return t;
    }

    private void agregarFila(JPanel destino, GridBagConstraints c, String titulo, JComponent contenido) {
        JPanel fila = new JPanel(new BorderLayout(0, 4));
        fila.setOpaque(false);
        fila.add(Estilo.etiqueta(titulo), BorderLayout.NORTH);
        fila.add(contenido, BorderLayout.CENTER);
        destino.add(fila, c);
        c.gridy++;
    }

    private <T> JPanel crearRadios(T[] valores, Map<JRadioButton, T> mapa, T inicial) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        fila.setOpaque(false);
        ButtonGroup grupo = new ButtonGroup();
        for (T valor : valores) {
            JRadioButton radio = new JRadioButton(valor.toString());
            radio.setOpaque(false);
            radio.setForeground(Estilo.TEXTO);
            radio.setSelected(valor.equals(inicial));
            radio.addActionListener(alCambiar);
            grupo.add(radio);
            mapa.put(radio, valor);
            fila.add(radio);
        }
        return fila;
    }

    private JPanel crearColumnaDerecha() {
        JPanel columna = new JPanel(new GridBagLayout());
        columna.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 0;
        c.weighty = 0;
        c.insets = new Insets(0, 0, 16, 0);
        columna.add(crearTarjetaResumen(), c);
        c.gridy = 1;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 0);
        columna.add(crearTarjetaPedido(), c);
        return columna;
    }

    private JPanel crearTarjetaResumen() {
        Estilo.Tarjeta t = new Estilo.Tarjeta(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 10, 0);

        t.add(Estilo.seccion("Resumen en vivo"), c);

        lblPrecio.setFont(Estilo.fuente(Font.BOLD, 38));
        lblPrecio.setForeground(Estilo.MARRON);
        lblTiempo.setFont(Estilo.fuente(Font.BOLD, 38));
        lblTiempo.setForeground(Estilo.AZUL);
        JPanel cifras = new JPanel(new GridLayout(1, 2, 12, 0));
        cifras.setOpaque(false);
        cifras.add(bloque("PRECIO FINAL", lblPrecio));
        cifras.add(bloque("TIEMPO DE ENTREGA", lblTiempo));
        c.gridy++;
        t.add(cifras, c);

        c.gridy++;
        t.add(filaDesglose("Bebida base (según tamaño)", lblBase), c);
        c.gridy++;
        t.add(filaDesglose("Grano", lblGrano), c);
        c.gridy++;
        t.add(filaDesglose("Agregados", lblAgregados), c);

        lblDescripcion.setForeground(Estilo.TEXTO_SUAVE);
        c.gridy++;
        t.add(lblDescripcion, c);

        c.gridy++;
        c.insets = new Insets(4, 0, 0, 0);
        t.add(btnAgregar, c);
        return t;
    }

    private JPanel bloque(String titulo, JLabel valor) {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setOpaque(false);
        p.add(Estilo.etiqueta(titulo), BorderLayout.NORTH);
        p.add(valor, BorderLayout.CENTER);
        return p;
    }

    private JPanel filaDesglose(String nombre, JLabel valor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(nombre);
        l.setForeground(Estilo.TEXTO_SUAVE);
        valor.setFont(Estilo.fuente(Font.BOLD, 13));
        valor.setForeground(Estilo.TEXTO);
        p.add(l, BorderLayout.WEST);
        p.add(valor, BorderLayout.EAST);
        return p;
    }

    private JPanel crearTarjetaPedido() {
        Estilo.Tarjeta t = new Estilo.Tarjeta(new BorderLayout(0, 10));

        JPanel arriba = new JPanel(new BorderLayout(0, 4));
        arriba.setOpaque(false);
        arriba.add(Estilo.seccion("Pedido del cliente"), BorderLayout.NORTH);
        arriba.add(cmbCliente, BorderLayout.CENTER);
        t.add(arriba, BorderLayout.NORTH);

        listaCarrito.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaCarrito.setFixedCellHeight(34);
        listaCarrito.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean sel, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, sel, foco);
                if (valor instanceof Bebida) {
                    Bebida b = (Bebida) valor;
                    setText(b.getNombre() + " " + b.getTamano() + " · " + b.getTemperatura()
                            + " · " + Estilo.moneda(b.calcularPrecio()));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
        JScrollPane scroll = new JScrollPane(listaCarrito);
        scroll.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));
        t.add(scroll, BorderLayout.CENTER);

        JPanel abajo = new JPanel(new BorderLayout(0, 10));
        abajo.setOpaque(false);
        abajo.add(lblPedido, BorderLayout.NORTH);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnQuitar);
        botones.add(btnConfirmar);
        abajo.add(botones, BorderLayout.SOUTH);
        t.add(abajo, BorderLayout.SOUTH);
        return t;
    }

    // ------------------------------------------------------------------ lógica

    /** Empieza a personalizar el tipo indicado (llamado desde el menú). */
    public void personalizar(TipoBebida tipo) {
        cmbTipo.setSelectedItem(tipo);
    }

    /** Recarga la lista de clientes (por si se agregaron o eliminaron) y el resumen del pedido. */
    public void actualizar() {
        boolean estabaCargando = cargando;
        cargando = true;
        Usuario elegido = (Usuario) cmbCliente.getSelectedItem();
        cmbCliente.removeAllItems();
        Usuario nuevoElegido = null;
        for (Usuario u : ctx.getDatos().getUsuarios()) {
            cmbCliente.addItem(u);
            if (elegido != null && u.getId() == elegido.getId()) {
                nuevoElegido = u;
            }
        }
        if (nuevoElegido != null) {
            cmbCliente.setSelectedItem(nuevoElegido);
        }
        cargando = estabaCargando;
        refrescarPedido();
    }

    /** Ajusta el control deslizante y la temperatura según el tipo de bebida elegido. */
    private void ajustarExtra() {
        boolean antes = cargando;
        cargando = true;
        TipoBebida tipo = (TipoBebida) cmbTipo.getSelectedItem();
        sliderExtra.getModel().setRangeProperties(tipo.getExtraPorDefecto(), 0,
                tipo.getMinExtra(), tipo.getMaxExtra(), false);
        int paso = Math.max(1, (tipo.getMaxExtra() - tipo.getMinExtra()) / 4);
        sliderExtra.setMajorTickSpacing(paso);
        sliderExtra.setSnapToTicks(false);

        for (Map.Entry<JRadioButton, Temperatura> e : radiosTemperatura.entrySet()) {
            e.getKey().setEnabled(!tipo.soloFrio());
        }
        if (tipo.soloFrio()) {
            seleccionar(radiosTemperatura, Temperatura.FRIO);
            frioForzado = true;
        } else if (frioForzado) {
            seleccionar(radiosTemperatura, Temperatura.CALIENTE);
            frioForzado = false;
        }
        actualizarEtiquetaExtra();
        cargando = antes;
    }

    private void actualizarEtiquetaExtra() {
        TipoBebida tipo = (TipoBebida) cmbTipo.getSelectedItem();
        lblExtra.setText(tipo.getEtiquetaExtra().toUpperCase() + ":  " + sliderExtra.getValue());
    }

    private <T> T seleccionado(Map<JRadioButton, T> mapa) {
        for (Map.Entry<JRadioButton, T> e : mapa.entrySet()) {
            if (e.getKey().isSelected()) {
                return e.getValue();
            }
        }
        return null;
    }

    private <T> void seleccionar(Map<JRadioButton, T> mapa, T valor) {
        for (Map.Entry<JRadioButton, T> e : mapa.entrySet()) {
            e.getKey().setSelected(e.getValue().equals(valor));
        }
    }

    /** Arma la bebida con lo que está elegido en pantalla (polimorfismo: puede ser cualquier tipo). */
    Bebida construirBebida() {
        TipoBebida tipo = (TipoBebida) cmbTipo.getSelectedItem();
        Grano grano = (Grano) cmbGrano.getSelectedItem();
        Temperatura temperatura = tipo.soloFrio() ? Temperatura.FRIO : seleccionado(radiosTemperatura);
        Bebida bebida = tipo.crear(seleccionado(radiosTamano), temperatura, grano, sliderExtra.getValue());
        for (Map.Entry<JCheckBox, Agregado> e : casillas.entrySet()) {
            if (e.getKey().isSelected()) {
                bebida.agregarAgregado(e.getValue());
            }
        }
        return bebida;
    }

    private void refrescar() {
        Bebida b = construirBebida();
        double precio = b.calcularPrecio();
        double grano = b.getGrano().getCostoTotal();
        double extras = 0;
        for (Agregado a : b.getAgregados()) {
            extras += a.calcularPrecio();
        }
        lblPrecio.setText(Estilo.moneda(precio));
        lblTiempo.setText(b.calcularTiempo() + " min");
        lblBase.setText(Estilo.moneda(precio - grano - extras));
        lblGrano.setText(Estilo.moneda(grano));
        lblAgregados.setText(Estilo.moneda(extras));
        lblDescripcion.setText("<html><div style='width:300px'>" + b.getDescripcion() + "</div></html>");
    }

    private void refrescarPedido() {
        double subtotal = 0;
        int minutos = 0;
        for (int i = 0; i < carrito.size(); i++) {
            subtotal += carrito.get(i).calcularPrecio();
            minutos += carrito.get(i).calcularTiempo();
        }
        Usuario u = (Usuario) cmbCliente.getSelectedItem();
        Descuento d = (u == null) ? new SinDescuento() : u.descuentoActual();
        double total = d.aplicar(subtotal);
        lblPedido.setText("<html>Subtotal: " + Estilo.moneda(subtotal) + "<br>"
                + d.getDescripcion() + ": -" + Estilo.moneda(subtotal - total) + "<br>"
                + "<b>Total: " + Estilo.moneda(total) + "  ·  Entrega: " + minutos + " min</b></html>");
    }

    void agregarAlCarrito() {
        carrito.addElement(construirBebida());
        refrescarPedido();
    }

    private void quitarDelCarrito() {
        int indice = listaCarrito.getSelectedIndex();
        if (indice < 0) {
            Dialogos.aviso(this, "Selecciona una bebida de la lista para quitarla.");
            return;
        }
        carrito.remove(indice);
        refrescarPedido();
    }

    /** Crea el pedido del cliente con las bebidas del carrito y guarda los datos. */
    Pedido registrarPedido() {
        Usuario u = (Usuario) cmbCliente.getSelectedItem();
        if (u == null) {
            throw new IllegalStateException("Primero registra un cliente en la pantalla Clientes.");
        }
        if (carrito.isEmpty()) {
            throw new IllegalStateException("Agrega al menos una bebida al pedido.");
        }
        Pedido pedido = u.crearPedido();
        for (int i = 0; i < carrito.size(); i++) {
            pedido.agregarBebida(carrito.get(i));
        }
        carrito.clear();
        ctx.guardar();
        ctx.actualizarTodo();
        return pedido;
    }

    private void confirmar() {
        Usuario u = (Usuario) cmbCliente.getSelectedItem();
        try {
            Pedido p = registrarPedido();
            Dialogos.info(this, String.format("Pedido #%d registrado para %s.%nTotal: %s (%s)%nEntrega estimada: %d min",
                    p.getId(), u.getNombre(), Estilo.moneda(p.calcularTotal()),
                    p.getDescuento().getDescripcion(), p.calcularTiempoEntrega()));
        } catch (IllegalStateException e) {
            Dialogos.aviso(this, e.getMessage());
        }
    }
}
