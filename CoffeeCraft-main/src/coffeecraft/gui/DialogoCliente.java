package coffeecraft.gui;

import coffeecraft.modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Formulario para registrar o editar un cliente, con validación de los campos. */
public class DialogoCliente extends JDialog {
    final JTextField txtNombre = Estilo.campo(26);
    final JTextField txtEmail = Estilo.campo(26);
    final JTextField txtTelefono = Estilo.campo(26);
    final JTextField txtDireccion = Estilo.campo(26);
    private final JLabel lblError = new JLabel(" ");
    private final Usuario usuario;
    private boolean aceptado = false;

    DialogoCliente(Window padre, Usuario usuario, boolean nuevo) {
        super(padre, nuevo ? "Nuevo cliente" : "Editar cliente", ModalityType.APPLICATION_MODAL);
        this.usuario = usuario;

        txtNombre.setText(usuario.getNombre());
        txtEmail.setText(usuario.getEmail());
        txtTelefono.setText(usuario.getTelefono());
        txtDireccion.setText(usuario.getDireccion());

        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setBackground(Estilo.CREMA);
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 24, 16, 24));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 12, 0);

        JLabel titulo = new JLabel(nuevo ? "Registrar cliente" : "Editar datos del cliente");
        titulo.setFont(Estilo.fuente(Font.BOLD, 18));
        titulo.setForeground(Estilo.TEXTO);
        contenido.add(titulo, c);
        agregarCampo(contenido, c, "NOMBRE COMPLETO", txtNombre);
        agregarCampo(contenido, c, "CORREO ELECTRÓNICO", txtEmail);
        agregarCampo(contenido, c, "TELÉFONO", txtTelefono);
        agregarCampo(contenido, c, "DIRECCIÓN", txtDireccion);

        lblError.setForeground(Estilo.ROJO);
        c.gridy++;
        contenido.add(lblError, c);

        JButton btnGuardar = Estilo.boton("Guardar", Estilo.VERDE);
        JButton btnCancelar = Estilo.boton("Cancelar", Estilo.TEXTO_SUAVE);
        btnGuardar.addActionListener(e -> aceptar());
        btnCancelar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnCancelar);
        botones.add(btnGuardar);
        c.gridy++;
        c.insets = new Insets(4, 0, 0, 0);
        contenido.add(botones, c);

        setContentPane(contenido);
        getRootPane().setDefaultButton(btnGuardar);
        pack();
        setLocationRelativeTo(padre);
    }

    private void agregarCampo(JPanel destino, GridBagConstraints c, String titulo, JTextField campo) {
        JPanel fila = new JPanel(new BorderLayout(0, 4));
        fila.setOpaque(false);
        fila.add(Estilo.etiqueta(titulo), BorderLayout.NORTH);
        fila.add(campo, BorderLayout.CENTER);
        c.gridy++;
        destino.add(fila, c);
    }

    /** Valida los datos y, si están bien, los copia al cliente. */
    boolean aceptar() {
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        if (nombre.isEmpty()) {
            lblError.setText("El nombre es obligatorio.");
            return false;
        }
        if (!email.isEmpty() && (!email.contains("@") || email.startsWith("@") || email.endsWith("@"))) {
            lblError.setText("El correo no es válido (debe tener el formato usuario@dominio).");
            return false;
        }
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setTelefono(txtTelefono.getText().trim());
        usuario.setDireccion(txtDireccion.getText().trim());
        aceptado = true;
        dispose();
        return true;
    }

    /** Muestra el formulario. Devuelve true si el usuario guardó los cambios. */
    public static boolean mostrar(Window padre, Usuario usuario, boolean nuevo) {
        DialogoCliente dialogo = new DialogoCliente(padre, usuario, nuevo);
        dialogo.setVisible(true);
        return dialogo.aceptado;
    }
}