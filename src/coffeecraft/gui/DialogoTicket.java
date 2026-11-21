package coffeecraft.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** Muestra el ticket de un pedido en una ventana de solo lectura. */
public class DialogoTicket extends JDialog {

    DialogoTicket(Window padre, int numeroPedido, String texto) {
        super(padre, "Ticket del pedido #" + numeroPedido, ModalityType.APPLICATION_MODAL);

        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setForeground(Estilo.TEXTO);
        area.setBackground(Estilo.BLANCO);
        area.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));

        JButton cerrar = Estilo.boton("Cerrar", Estilo.MARRON);
        cerrar.addActionListener(e -> dispose());
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pie.setOpaque(false);
        pie.add(cerrar);

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBackground(Estilo.CREMA);
        contenido.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        contenido.add(scroll, BorderLayout.CENTER);
        contenido.add(pie, BorderLayout.SOUTH);
        setContentPane(contenido);
        setSize(620, 440);
        setLocationRelativeTo(padre);
    }

    public static void mostrar(Window padre, int numeroPedido, String texto) {
        new DialogoTicket(padre, numeroPedido, texto).setVisible(true);
    }
}