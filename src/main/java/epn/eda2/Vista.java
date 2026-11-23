package epn.eda2;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.File;
import java.util.function.IntConsumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * VISTA: toda la interfaz gráfica (portada, índice y pantalla de ejemplo) en una sola ventana.
 * No contiene lógica de algoritmos ni datos: solo muestra lo que el Controlador le indica
 * y avisa de las acciones del usuario mediante los "alXxx(...)" que el Controlador registra.
 */
public class Vista extends JFrame {

    // ---- estilo ----
    private static final Color FONDO = new Color(0x0B1F3A);
    private static final Color TARJETA = new Color(0x13305A);
    private static final Color ACENTO = new Color(0xF2C230);
    private static final Color TEXTO = Color.WHITE;
    private static final Color TEXTO_SUAVE = new Color(0xB8C7DE);
    private static final Color CONSOLA_FONDO = new Color(0x08141F);
    private static final Color CONSOLA_TEXTO = new Color(0xC9F2C7);
    private static final Font TITULO = new Font("SansSerif", Font.BOLD, 34);
    private static final Font SUBTITULO = new Font("SansSerif", Font.BOLD, 24);
    private static final Font NORMAL = new Font("SansSerif", Font.PLAIN, 16);
    private static final Font NEGRITA = new Font("SansSerif", Font.BOLD, 16);
    private static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    // ---- navegación ----
    private final CardLayout cartas = new CardLayout();
    private final JPanel raiz = new JPanel(cartas);

    // ---- portada / índice ----
    private final JButton btnIngresar = boton("Ingresar", ACENTO, FONDO);
    private final JButton btnVolverPortada = boton("← Portada", TARJETA, TEXTO);
    private final JPanel listaTemas = new JPanel();
    private IntConsumer alAbrirTema = t -> { };

    // ---- pantalla de ejemplo ----
    private final JButton btnVolverIndice = boton("← Índice", TARJETA, TEXTO);
    private final JButton btnEjecutar = boton("▶ Ejecutar", ACENTO, FONDO);
    private final JLabel titulo = new JLabel();
    private final JComboBox<String> selector = new JComboBox<>();
    private final JPanel filaSelector = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    private final JTextArea descripcion = new JTextArea();
    private final JTextArea entrada = new JTextArea();
    private final JTextArea salida = new JTextArea();
    private final JLabel etiquetaEntrada = etiquetaSeccion("Entrada (puedes editarla)");
    private final JScrollPane scrollEntrada = new JScrollPane(entrada);
    private final JLabel etiquetaParam = new JLabel();
    private final JTextField param = new JTextField(10);
    private final JPanel panelEntrada = new JPanel(new BorderLayout(0, 6));
    private final JSplitPane division;
    private IntConsumer alCambiarVariante = i -> { };
    private boolean cargandoSelector;

    public Vista() {
        super("Estructura de Datos y Algoritmos II - EPN");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(820, 600));
        setSize(1050, 740);
        setLocationRelativeTo(null);

        listaTemas.setLayout(new BoxLayout(listaTemas, BoxLayout.Y_AXIS));
        listaTemas.setOpaque(false);

        division = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        raiz.add(construirPortada(), "portada");
        raiz.add(construirIndice(), "indice");
        raiz.add(construirEjemplo(), "ejemplo");
        setContentPane(raiz);
        mostrarPortada();
    }

    // =====================================================================
    // API para el Controlador
    // =====================================================================

    public void alIngresar(Runnable r) {
        btnIngresar.addActionListener(e -> r.run());
    }

    public void alVolverPortada(Runnable r) {
        btnVolverPortada.addActionListener(e -> r.run());
    }

    public void alVolverIndice(Runnable r) {
        btnVolverIndice.addActionListener(e -> r.run());
    }

    public void alEjecutar(Runnable r) {
        btnEjecutar.addActionListener(e -> r.run());
    }

    public void alAbrirTema(IntConsumer c) {
        alAbrirTema = c;
    }

    public void alCambiarVariante(IntConsumer c) {
        alCambiarVariante = c;
    }

    public void mostrarPortada() {
        cartas.show(raiz, "portada");
    }

    public void mostrarIndice() {
        cartas.show(raiz, "indice");
    }

    public void mostrarEjemplo() {
        cartas.show(raiz, "ejemplo");
    }

    /** Dibuja las tarjetas del índice: cada tema tiene UN botón. */
    public void cargarTemas(String[] titulos, String[] resumenes, String[] botones) {
        listaTemas.removeAll();
        for (int i = 0; i < titulos.length; i++) {
            listaTemas.add(tarjetaTema(i, titulos[i], resumenes[i], botones[i]));
            listaTemas.add(Box.createVerticalStrut(12));
        }
        listaTemas.revalidate();
        listaTemas.repaint();
    }

    /** Prepara la pantalla de ejemplo de un tema; el selector solo se muestra si hay más de una variante. */
    public void prepararEjemplo(String tituloTema, String[] variantes) {
        titulo.setText(tituloTema);
        cargandoSelector = true;
        selector.removeAllItems();
        for (String v : variantes) {
            selector.addItem(v);
        }
        cargandoSelector = false;
        filaSelector.setVisible(variantes.length > 1);
    }

    /** Muestra los datos de la variante elegida (null oculta la entrada o el parámetro). */
    public void mostrarDatos(String desc, String textoEntrada, String etiqueta, String valorParam) {
        descripcion.setText(desc);
        boolean hayEntrada = textoEntrada != null;
        entrada.setText(hayEntrada ? textoEntrada : "");
        entrada.setCaretPosition(0);
        etiquetaEntrada.setVisible(hayEntrada);
        scrollEntrada.setVisible(hayEntrada);
        boolean hayParam = etiqueta != null;
        etiquetaParam.setText(hayParam ? etiqueta : "");
        etiquetaParam.setVisible(hayParam);
        param.setText(hayParam ? valorParam : "");
        param.setVisible(hayParam);
        salida.setText("");
        division.setResizeWeight(hayEntrada ? 0.35 : 0);
        SwingUtilities.invokeLater(() -> {
            if (hayEntrada) {
                division.setDividerLocation(0.35);
            } else {
                division.setDividerLocation(panelEntrada.getPreferredSize().height + 6);
            }
        });
    }

    public String getEntrada() {
        return entrada.getText();
    }

    public String getParam() {
        return param.getText();
    }

    public void setSalida(String texto) {
        salida.setText(texto);
        salida.setCaretPosition(0);
    }

    // =====================================================================
    // Construcción de pantallas
    // =====================================================================

    private JPanel construirPortada() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(FONDO);
        p.setBorder(new EmptyBorder(20, 30, 30, 30));

        JPanel logos = new JPanel(new BorderLayout());
        logos.setOpaque(false);
        logos.add(logo("imagenes/logo_epn.png", "Logo EPN", 150), BorderLayout.WEST);
        logos.add(logo("imagenes/logo_facultad.png", "Logo Facultad", 150), BorderLayout.EAST);
        p.add(logos, BorderLayout.NORTH);

        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(TARJETA);
        tarjeta.setBorder(new CompoundBorder(new LineBorder(ACENTO, 3, true), new EmptyBorder(30, 50, 30, 50)));
        tarjeta.add(centrado(etiqueta("Escuela Politécnica Nacional", TITULO, TEXTO)));
        tarjeta.add(Box.createVerticalStrut(12));
        tarjeta.add(centrado(etiqueta("Estructura de Datos y Algoritmos II", SUBTITULO, ACENTO)));
        tarjeta.add(Box.createVerticalStrut(24));
        tarjeta.add(centrado(etiqueta("Andreina Minayo", SUBTITULO, TEXTO_SUAVE)));
        tarjeta.add(Box.createVerticalStrut(30));
        tarjeta.add(centrado(btnIngresar));

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.add(tarjeta);
        p.add(centro, BorderLayout.CENTER);
        return p;
    }

    private JPanel construirIndice() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(FONDO);
        p.setBorder(new EmptyBorder(16, 30, 20, 30));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.add(logo("imagenes/logo_epn.png", "Logo EPN", 90), BorderLayout.WEST);
        JPanel titulos = new JPanel(new BorderLayout());
        titulos.setOpaque(false);
        titulos.add(etiqueta("Índice", TITULO, TEXTO), BorderLayout.CENTER);
        titulos.add(etiqueta("Estructura de Datos y Algoritmos II", NORMAL, ACENTO), BorderLayout.SOUTH);
        cabecera.add(titulos, BorderLayout.CENTER);
        cabecera.add(logo("imagenes/logo_facultad.png", "Logo Facultad", 90), BorderLayout.EAST);
        p.add(cabecera, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(listaTemas);
        scroll.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        p.add(scroll, BorderLayout.CENTER);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pie.setOpaque(false);
        pie.add(btnVolverPortada);
        p.add(pie, BorderLayout.SOUTH);
        return p;
    }

    private JPanel construirEjemplo() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(FONDO);
        p.setBorder(new EmptyBorder(14, 24, 16, 24));

        // Cabecera: volver + título + selector (solo si el tema tiene varias variantes) + descripción
        JPanel cab = new JPanel();
        cab.setLayout(new BoxLayout(cab, BoxLayout.Y_AXIS));
        cab.setOpaque(false);
        JPanel fila = new JPanel(new BorderLayout(14, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.add(btnVolverIndice, BorderLayout.WEST);
        titulo.setFont(SUBTITULO);
        titulo.setForeground(ACENTO);
        fila.add(titulo, BorderLayout.CENTER);
        cab.add(fila);
        cab.add(Box.createVerticalStrut(8));

        filaSelector.setOpaque(false);
        filaSelector.setAlignmentX(LEFT_ALIGNMENT);
        JLabel lbl = new JLabel("Algoritmo:");
        lbl.setFont(NEGRITA);
        lbl.setForeground(TEXTO);
        selector.setFont(NORMAL);
        selector.addActionListener(e -> {
            if (!cargandoSelector && selector.getSelectedIndex() >= 0) {
                alCambiarVariante.accept(selector.getSelectedIndex());
            }
        });
        filaSelector.add(lbl);
        filaSelector.add(selector);
        cab.add(filaSelector);
        cab.add(Box.createVerticalStrut(8));

        descripcion.setEditable(false);
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);
        descripcion.setOpaque(false);
        descripcion.setFont(NORMAL);
        descripcion.setForeground(TEXTO);
        descripcion.setAlignmentX(LEFT_ALIGNMENT);
        cab.add(descripcion);
        p.add(cab, BorderLayout.NORTH);

        // Entrada + controles
        panelEntrada.setOpaque(false);
        estilizar(entrada, false);
        panelEntrada.add(etiquetaEntrada, BorderLayout.NORTH);
        panelEntrada.add(scrollEntrada, BorderLayout.CENTER);
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        controles.setOpaque(false);
        etiquetaParam.setFont(NEGRITA);
        etiquetaParam.setForeground(TEXTO);
        param.setFont(MONO);
        controles.add(etiquetaParam);
        controles.add(param);
        controles.add(btnEjecutar);
        panelEntrada.add(controles, BorderLayout.SOUTH);

        // Salida
        JPanel panelSalida = new JPanel(new BorderLayout(0, 6));
        panelSalida.setOpaque(false);
        estilizar(salida, true);
        salida.setEditable(false);
        panelSalida.add(etiquetaSeccion("Resultado"), BorderLayout.NORTH);
        panelSalida.add(new JScrollPane(salida), BorderLayout.CENTER);

        division.setOrientation(JSplitPane.VERTICAL_SPLIT);
        division.setTopComponent(panelEntrada);
        division.setBottomComponent(panelSalida);
        division.setOpaque(false);
        division.setBorder(null);
        p.add(division, BorderLayout.CENTER);
        return p;
    }

    private JPanel tarjetaTema(int indice, String titulo, String resumen, String textoBoton) {
        JPanel p = new JPanel(new BorderLayout(14, 6));
        p.setBackground(TARJETA);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, ACENTO),
                new EmptyBorder(12, 16, 12, 16)));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        JLabel num = new JLabel((indice + 1) + ".", SwingConstants.CENTER);
        num.setFont(TITULO);
        num.setForeground(ACENTO);
        p.add(num, BorderLayout.WEST);

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);
        JLabel t = new JLabel(titulo);
        t.setFont(SUBTITULO);
        t.setForeground(TEXTO);
        centro.add(t);
        JLabel d = new JLabel(resumen);
        d.setFont(NORMAL);
        d.setForeground(TEXTO_SUAVE);
        centro.add(d);
        centro.add(Box.createVerticalStrut(8));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botones.setOpaque(false);
        botones.setAlignmentX(LEFT_ALIGNMENT);
        JButton b = boton("▶ " + textoBoton, ACENTO, FONDO);
        b.addActionListener(ev -> alAbrirTema.accept(indice));
        botones.add(b);
        centro.add(botones);
        p.add(centro, BorderLayout.CENTER);
        return p;
    }

    // =====================================================================
    // Utilidades de estilo
    // =====================================================================

    /** Logo escalado; si el archivo no existe muestra un recuadro punteado con la etiqueta. */
    private static JLabel logo(String ruta, String textoAlterno, int tam) {
        JLabel l = new JLabel("", SwingConstants.CENTER);
        l.setPreferredSize(new Dimension(tam, tam));
        File f = new File(ruta);
        ImageIcon icono = f.isFile() ? new ImageIcon(f.getPath()) : null;
        if (icono != null && icono.getIconWidth() > 0) {
            double esc = Math.min((double) tam / icono.getIconWidth(), (double) tam / icono.getIconHeight());
            l.setIcon(new ImageIcon(icono.getImage().getScaledInstance(
                    Math.max(1, (int) (icono.getIconWidth() * esc)),
                    Math.max(1, (int) (icono.getIconHeight() * esc)), Image.SCALE_SMOOTH)));
        } else {
            l.setText(textoAlterno);
            l.setFont(NORMAL);
            l.setForeground(TEXTO_SUAVE);
            l.setBorder(BorderFactory.createDashedBorder(TEXTO_SUAVE, 2f, 7f, 6f, true));
        }
        return l;
    }

    private static JComponent centrado(JComponent c) {
        c.setAlignmentX(CENTER_ALIGNMENT);
        return c;
    }

    private static JLabel etiqueta(String texto, Font fuente, Color color) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(fuente);
        l.setForeground(color);
        return l;
    }

    private static JLabel etiquetaSeccion(String t) {
        JLabel l = new JLabel(t);
        l.setFont(NEGRITA);
        l.setForeground(TEXTO_SUAVE);
        return l;
    }

    private static JButton boton(String texto, Color fondo, Color letra) {
        JButton b = new JButton(texto);
        b.setFont(NEGRITA);
        b.setBackground(fondo);
        b.setForeground(letra);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 22, 10, 22));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static void estilizar(JTextArea a, boolean consola) {
        a.setFont(MONO);
        a.setBackground(consola ? CONSOLA_FONDO : TARJETA);
        a.setForeground(consola ? CONSOLA_TEXTO : TEXTO);
        a.setCaretColor(TEXTO);
        a.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }
}
