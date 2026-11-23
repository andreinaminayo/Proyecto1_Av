package epn.eda2;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * CONTROLADOR: conecta la Vista con el Modelo. Escucha las acciones de la Vista,
 * consulta/ejecuta el Modelo y le dice a la Vista qué mostrar. Contiene además el main.
 */
public class Controlador {
    private final Modelo modelo;
    private final Vista vista;

    private int[] ejemplosActuales; // ejemplos (índices del Modelo) del tema abierto
    private int varianteActual;

    public Controlador(Modelo modelo, Vista vista) {
        this.modelo = modelo;
        this.vista = vista;

        int n = modelo.numTemas();
        String[] titulos = new String[n], resumenes = new String[n], botones = new String[n];
        for (int i = 0; i < n; i++) {
            titulos[i] = modelo.tituloTema(i);
            resumenes[i] = modelo.resumenTema(i);
            botones[i] = modelo.botonTema(i);
        }
        vista.cargarTemas(titulos, resumenes, botones);

        vista.alIngresar(vista::mostrarIndice);
        vista.alVolverPortada(vista::mostrarPortada);
        vista.alVolverIndice(vista::mostrarIndice);
        vista.alAbrirTema(this::abrirTema);
        vista.alCambiarVariante(this::elegirVariante);
        vista.alEjecutar(this::ejecutar);
    }

    private void abrirTema(int tema) {
        ejemplosActuales = modelo.ejemplosDelTema(tema);
        String[] variantes = new String[ejemplosActuales.length];
        for (int i = 0; i < variantes.length; i++) {
            variantes[i] = modelo.dato(ejemplosActuales[i], Modelo.TITULO);
        }
        // Un tema con un solo ejemplo muestra su propio título; Grafos muestra el del tema
        String titulo = variantes.length == 1 ? variantes[0] : modelo.tituloTema(tema);
        vista.prepararEjemplo(titulo, variantes);
        elegirVariante(0);
        vista.mostrarEjemplo();
    }

    private void elegirVariante(int i) {
        varianteActual = i;
        int e = ejemplosActuales[i];
        vista.mostrarDatos(modelo.dato(e, Modelo.DESCRIPCION), modelo.dato(e, Modelo.ENTRADA),
                modelo.dato(e, Modelo.ETIQUETA_PARAM), modelo.dato(e, Modelo.PARAM));
    }

    private void ejecutar() {
        try {
            vista.setSalida(modelo.ejecutar(ejemplosActuales[varianteActual], vista.getEntrada(), vista.getParam()));
        } catch (RuntimeException ex) {
            vista.setSalida("⚠ Error: " + ex.getMessage()
                    + "\n\nRevisa el formato de la entrada y vuelve a intentar.");
        }
    }

    public static void main(String[] args) {
        try {
            // Metal respeta los colores de los botones en todos los sistemas
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignorada) {
            // se usa el aspecto por defecto
        }
        SwingUtilities.invokeLater(() -> {
            Vista vista = new Vista();
            new Controlador(new Modelo(), vista);
            vista.setVisible(true);
        });
    }
}
