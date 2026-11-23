package epn.eda2;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeSet;

/**
 * MODELO: contiene los datos del índice (temas y ejemplos) y todos los algoritmos.
 * No conoce ni la Vista ni el Controlador. Cada algoritmo devuelve el texto del resultado.
 */
public class Modelo {

    // Índices de los campos de un ejemplo
    public static final int TITULO = 0, DESCRIPCION = 1, ENTRADA = 2, ETIQUETA_PARAM = 3, PARAM = 4;

    private static final String GRAFO_PONDERADO =
            "# Grafo no dirigido: origen destino peso\n"
            + "A B 7\nA D 5\nB C 8\nB D 9\nB E 7\nC E 5\n"
            + "D E 15\nD F 6\nE F 8\nE G 9\nF G 11\n";

    /** Ejemplos: {título, descripción, entrada por defecto, etiqueta del parámetro, parámetro por defecto}. */
    private static final String[][] EJEMPLOS = {
        /* 0 */ {"Búsqueda y ordenamiento",
            "Compara búsqueda lineal vs. binaria y Bubble Sort vs. Merge Sort sobre un arreglo aleatorio "
            + "de tamaño n. Se miden comparaciones y tiempo real para ver cómo crece cada algoritmo.",
            null, "Tamaño n (10 - 20000):", "5000"},
        /* 1 */ {"Tabla de crecimiento y conteo de operaciones",
            "Muestra cuántas operaciones requiere cada clase de complejidad (log n, n, n log n, n², n³, 2ⁿ) "
            + "y cuenta las operaciones reales de tres bucles típicos: O(n), O(n²) y O(log n).",
            null, "Valor máximo de n (1 - 1000):", "32"},
        /* 2 */ {"Suma de subconjuntos: encontrar vs. verificar",
            "Problema NP-completo clásico: ¿algún subconjunto de los números suma el objetivo? Encontrar la "
            + "solución exige explorar hasta 2ⁿ subconjuntos; verificar un certificado toma tiempo polinomial. "
            + "Máximo 22 números.",
            "3 34 4 12 5 2 27 19 8 41", "Suma objetivo:", "58"},
        /* 3 */ {"Ordenamiento topológico",
            "Grafo DIRIGIDO de prerrequisitos (algoritmo de Kahn): cada línea 'A B' significa que A va antes "
            + "que B. Prueba añadir un ciclo (por ejemplo 'EDA2 Prog1') para ver cómo se detecta.",
            "# prerrequisito materia\nProg1 Prog2\nProg2 ED1\nDiscretas ED1\nED1 EDA2\n"
            + "Calculo Algoritmos\nProg2 Algoritmos\nAlgoritmos EDA2\n",
            null, null},
        /* 4 */ {"Viajante (TSM)",
            "Ruta cerrada más corta que visita todas las ciudades. Fuerza bruta explora (n-1)! rutas "
            + "(óptima); el vecino más cercano es una heurística rápida. Formato: ciudad ciudad distancia. "
            + "Máximo 10 ciudades.",
            "A B 12\nA C 10\nA D 19\nA E 8\nB C 3\nB D 7\nB E 2\nC D 6\nC E 20\nD E 4\n",
            null, null},
        /* 5 */ {"Prim",
            "Árbol de expansión mínima: crece desde un vértice inicial escogiendo siempre la arista más "
            + "barata que conecta con un vértice nuevo (cola de prioridad).",
            GRAFO_PONDERADO, "Vértice inicial:", "A"},
        /* 6 */ {"Kruskal",
            "Árbol de expansión mínima: ordena las aristas por peso y acepta cada una si no forma ciclo "
            + "(estructura Union-Find).",
            GRAFO_PONDERADO, null, null},
        /* 7 */ {"Dijkstra",
            "Camino mínimo desde un origen hacia todos los demás vértices (pesos no negativos).",
            GRAFO_PONDERADO, "Vértice origen:", "A"},
    };

    private static final String[] TEMAS = {
        "Eficiencia algorítmica",
        "Análisis de complejidad",
        "P, NP, NP-Hard, NP-Completo",
        "Grafos"};

    private static final String[] RESUMENES = {
        "Medir y comparar algoritmos por tiempo y número de operaciones.",
        "Notación Big-O y crecimiento de funciones.",
        "Clases de problemas: resolver rápido vs. verificar rápido.",
        "Ordenamiento topológico, TSM, árboles de expansión mínima (Prim, Kruskal) y Dijkstra."};

    /** Texto del único botón de cada tema (Grafos tiene UN solo botón para todos sus algoritmos). */
    private static final String[] BOTONES = {
        "Búsqueda y ordenamiento",
        "Tabla de crecimiento y conteo de operaciones",
        "Suma de subconjuntos",
        "Grafos"};

    /** Ejemplos (índices de EJEMPLOS) que pertenecen a cada tema. */
    private static final int[][] EJEMPLOS_DE_TEMA = {{0}, {1}, {2}, {3, 4, 5, 6, 7}};

    public int numTemas() {
        return TEMAS.length;
    }

    public String tituloTema(int tema) {
        return TEMAS[tema];
    }

    public String resumenTema(int tema) {
        return RESUMENES[tema];
    }

    public String botonTema(int tema) {
        return BOTONES[tema];
    }

    /** Índices de los ejemplos del tema (más de uno = la Vista muestra un selector). */
    public int[] ejemplosDelTema(int tema) {
        return EJEMPLOS_DE_TEMA[tema];
    }

    /** Devuelve un campo de un ejemplo (ver constantes TITULO, DESCRIPCION, ...). */
    public String dato(int ejemplo, int campo) {
        return EJEMPLOS[ejemplo][campo];
    }

    /** Ejecuta el algoritmo del ejemplo indicado y devuelve el texto del resultado. */
    public String ejecutar(int ejemplo, String entrada, String param) {
        switch (ejemplo) {
            case 0: return eficiencia(entrada, param);
            case 1: return complejidad(entrada, param);
            case 2: return subconjuntos(entrada, param);
            case 3: return topologico(entrada, param);
            case 4: return tsp(entrada, param);
            case 5: return prim(entrada, param);
            case 6: return kruskal(entrada, param);
            case 7: return dijkstra(entrada, param);
            default: throw new IllegalArgumentException("Ejemplo desconocido: " + ejemplo);
        }
    }

    // ===================================================================
    // Algoritmos
    // ===================================================================
    private static final int INF = Integer.MAX_VALUE / 4;

    static int entero(String s, int defecto) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return defecto;
        }
    }

    // ===================================================================
    // 1. Eficiencia algorítmica
    // ===================================================================
    String eficiencia(String entrada, String param) {
        int n = Math.max(10, Math.min(entero(param, 5000), 20000));
        Random r = new Random(42);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = r.nextInt(n * 10);
        }
        int objetivo = a[n - 1];
        StringBuilder sb = new StringBuilder();
        sb.append("n = ").append(n).append("  (buscando el valor ").append(objetivo).append(")\n\n");

        // Búsqueda lineal O(n)
        long t = System.nanoTime();
        long cLin = 0;
        for (int i = 0; i < n; i++) {
            cLin++;
            if (a[i] == objetivo) {
                break;
            }
        }
        double msLin = (System.nanoTime() - t) / 1e6;

        // Merge sort O(n log n)
        int[] m = a.clone();
        long[] cMerge = {0};
        t = System.nanoTime();
        mergeSort(m, new int[n], 0, n, cMerge);
        double msMerge = (System.nanoTime() - t) / 1e6;

        // Búsqueda binaria O(log n) sobre el arreglo ya ordenado
        t = System.nanoTime();
        long cBin = 0;
        int lo = 0, hi = n - 1;
        while (lo <= hi) {
            cBin++;
            int mid = (lo + hi) >>> 1;
            if (m[mid] == objetivo) {
                break;
            } else if (m[mid] < objetivo) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        double msBin = (System.nanoTime() - t) / 1e6;

        // Bubble sort O(n^2)
        int[] b = a.clone();
        long cBub = 0;
        t = System.nanoTime();
        boolean cambio = true;
        for (int i = 0; i < n - 1 && cambio; i++) {
            cambio = false;
            for (int j = 0; j < n - 1 - i; j++) {
                cBub++;
                if (b[j] > b[j + 1]) {
                    int tmp = b[j];
                    b[j] = b[j + 1];
                    b[j + 1] = tmp;
                    cambio = true;
                }
            }
        }
        double msBub = (System.nanoTime() - t) / 1e6;

        sb.append(String.format(Locale.US, "%-28s %-9s %14s %12s%n", "Algoritmo", "Clase", "Comparaciones", "Tiempo(ms)"));
        sb.append("-".repeat(66)).append('\n');
        sb.append(fila("Búsqueda lineal", "O(n)", cLin, msLin));
        sb.append(fila("Búsqueda binaria", "O(log n)", cBin, msBin));
        sb.append(fila("Bubble Sort", "O(n²)", cBub, msBub));
        sb.append(fila("Merge Sort", "O(n log n)", cMerge[0], msMerge));
        sb.append("\nConclusión: Bubble Sort hizo ").append(cBub / Math.max(1, cMerge[0]))
          .append("x más comparaciones que Merge Sort.\n")
          .append("Prueba con n = 1000, 5000, 10000 y 20000 y observa cómo se disparan los tiempos de O(n²).\n");
        return sb.toString();
    }

    private static String fila(String nombre, String clase, long comps, double ms) {
        return String.format(Locale.US, "%-28s %-9s %14d %12.3f%n", nombre, clase, comps, ms);
    }

    private static void mergeSort(int[] a, int[] tmp, int lo, int hi, long[] c) {
        if (hi - lo < 2) {
            return;
        }
        int mid = (lo + hi) / 2;
        mergeSort(a, tmp, lo, mid, c);
        mergeSort(a, tmp, mid, hi, c);
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi) {
            c[0]++;
            tmp[k++] = a[i] <= a[j] ? a[i++] : a[j++];
        }
        while (i < mid) {
            tmp[k++] = a[i++];
        }
        while (j < hi) {
            tmp[k++] = a[j++];
        }
        System.arraycopy(tmp, lo, a, lo, hi - lo);
    }

    // ===================================================================
    // 2. Análisis de complejidad
    // ===================================================================
    String complejidad(String entrada, String param) {
        int max = Math.max(1, Math.min(entero(param, 32), 1000));
        TreeSet<Integer> tamanos = new TreeSet<>();
        for (int k = 1; k <= max; k *= 2) {
            tamanos.add(k);
        }
        tamanos.add(max);

        StringBuilder sb = new StringBuilder("Número de operaciones según la complejidad\n\n");
        sb.append(String.format(Locale.US, "%6s %8s %10s %12s %14s %s%n", "n", "log2 n", "n log n", "n^2", "n^3", "2^n"));
        sb.append("-".repeat(80)).append('\n');
        for (int n : tamanos) {
            double log = Math.log(n) / Math.log(2);
            long nlogn = Math.round(n * log);
            long n2 = (long) n * n;
            long n3 = (long) n * n * n;
            sb.append(String.format(Locale.US, "%6d %8.2f %10d %12d %14d %s%n", n, log, nlogn, n2, n3, potencia2(n)));
        }

        // Conteo real de operaciones
        int n = max;
        long lineal = 0, cuadratico = 0, logaritmico = 0;
        for (int i = 0; i < n; i++) {
            lineal++;
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cuadratico++;
            }
        }
        for (int i = n; i > 1; i /= 2) {
            logaritmico++;
        }
        sb.append("\nConteo real de operaciones con n = ").append(n).append(":\n");
        sb.append("  for (i<n)                    -> ").append(lineal).append("  = O(n)\n");
        sb.append("  for (i<n) for (j<n)          -> ").append(cuadratico).append("  = O(n²)\n");
        sb.append("  for (i=n; i>1; i/=2)         -> ").append(logaritmico).append("  = O(log n)\n");
        sb.append("\nOrden de crecimiento:  O(1) < O(log n) < O(n) < O(n log n) < O(n²) < O(n³) < O(2ⁿ) < O(n!)\n");
        return sb.toString();
    }

    private static String potencia2(int n) {
        BigInteger p = BigInteger.ONE.shiftLeft(n);
        String s = p.toString();
        return s.length() <= 15 ? s : "≈10^" + (s.length() - 1) + " (" + s.length() + " dígitos)";
    }

    // ===================================================================
    // 3. P, NP, NP-Hard, NP-Completo  (Suma de subconjuntos)
    // ===================================================================
    String subconjuntos(String entrada, String param) {
        String[] tokens = entrada.trim().split("[\\s,;]+");
        int n = tokens.length;
        if (entrada.trim().isEmpty() || n > 22) {
            throw new IllegalArgumentException("Ingresa entre 1 y 22 números enteros.");
        }
        int[] v = new int[n];
        for (int i = 0; i < n; i++) {
            v[i] = Integer.parseInt(tokens[i]);
        }
        int objetivo = entero(param, 0);

        long inicio = System.nanoTime();
        long examinados = 0;
        long encontrada = -1;
        long total = 1L << n;
        for (long mask = 0; mask < total; mask++) {
            examinados++;
            long suma = 0;
            for (int i = 0; i < n; i++) {
                if ((mask >> i & 1) == 1) {
                    suma += v[i];
                }
            }
            if (suma == objetivo) {
                encontrada = mask;
                break;
            }
        }
        double msBusqueda = (System.nanoTime() - inicio) / 1e6;

        StringBuilder sb = new StringBuilder();
        sb.append("Conjunto: ").append(Arrays.toString(v)).append("   Objetivo: ").append(objetivo).append("\n\n");
        sb.append("1) BÚSQUEDA (resolver) - exponencial O(2ⁿ · n)\n");
        sb.append(String.format(Locale.US, "   Subconjuntos examinados: %d de %d posibles  (%.3f ms)%n", examinados, total, msBusqueda));
        if (encontrada < 0) {
            sb.append("   Resultado: NO existe ningún subconjunto con esa suma.\n");
        } else {
            List<Integer> cert = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((encontrada >> i & 1) == 1) {
                    cert.add(v[i]);
                }
            }
            sb.append("   Certificado encontrado: ").append(cert).append('\n');
            long t = System.nanoTime();
            long suma = 0;
            for (int x : cert) {
                suma += x;
            }
            double us = (System.nanoTime() - t) / 1e3;
            sb.append("\n2) VERIFICACIÓN del certificado - polinomial O(n)\n");
            sb.append("   Suma = ").append(suma).append(suma == objetivo ? "  ✔ correcto" : "  ✘ incorrecto");
            sb.append(String.format(Locale.US, "  (%.1f µs, %d sumas)%n", us, cert.size()));
        }
        sb.append("\nCLASES:\n");
        sb.append("  P            : se resuelven en tiempo polinomial (ej. ordenar, Dijkstra, Prim, Kruskal).\n");
        sb.append("  NP           : una solución se VERIFICA en tiempo polinomial (como arriba).\n");
        sb.append("  NP-Hard      : al menos tan difíciles como todo NP (no tienen que estar en NP; ej. TSM de optimización).\n");
        sb.append("  NP-Completo  : están en NP y son NP-Hard (Suma de subconjuntos, SAT, TSM de decisión).\n");
        sb.append("  Sabemos P ⊆ NP; si algún problema NP-completo estuviera en P, entonces P = NP (abierto).\n");
        return sb.toString();
    }

    // ===================================================================
    // 4. Grafos
    // ===================================================================
    // Grafo cargado por leer(): vértices por nombre y aristas {u, v, peso}
    private final List<String> nombres = new ArrayList<>();
    private final Map<String, Integer> mapa = new HashMap<>();
    private final List<int[]> aristas = new ArrayList<>();

    private int id(String s) {
        Integer i = mapa.get(s);
        if (i == null) {
            i = nombres.size();
            nombres.add(s);
            mapa.put(s, i);
        }
        return i;
    }

    private int indice(String s) {
        Integer i = mapa.get(s);
        return i == null ? -1 : i;
    }

    private int n() {
        return nombres.size();
    }

    private String nombre(int i) {
        return nombres.get(i);
    }


    private void leer(String texto) {
        nombres.clear();
        mapa.clear();
        aristas.clear();
        for (String linea : texto.split("\\R")) {
            String l = linea.trim();
            int c = l.indexOf('#');
            if (c >= 0) {
                l = l.substring(0, c).trim();
            }
            if (l.isEmpty()) {
                continue;
            }
            String[] t = l.split("\\s+");
            if (t.length < 2) {
                throw new IllegalArgumentException("Línea inválida: \"" + linea + "\"");
            }
            int u = id(t[0]);
            int v = id(t[1]);
            int w = t.length > 2 ? Integer.parseInt(t[2]) : 1;
            aristas.add(new int[] {u, v, w});
        }
        if (n() == 0) {
            throw new IllegalArgumentException("El grafo está vacío.");
        }
    }

    private List<List<int[]>> adyacencia() {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n(); i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] e : aristas) {
            adj.get(e[0]).add(new int[] {e[1], e[2]});
            adj.get(e[1]).add(new int[] {e[0], e[2]});
        }
        return adj;
    }

    // ---- Ordenamiento topológico (Kahn) ----
    String topologico(String entrada, String param) {
        leer(entrada);
        int n = n();
        int[] grado = new int[n];
        List<List<Integer>> sig = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            sig.add(new ArrayList<>());
        }
        for (int[] e : aristas) {
            sig.get(e[0]).add(e[1]);
            grado[e[1]]++;
        }
        ArrayDeque<Integer> cola = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (grado[i] == 0) {
                cola.add(i);
            }
        }
        StringBuilder sb = new StringBuilder("Algoritmo de Kahn (grafo dirigido, " + n + " vértices)\n\n");
        List<String> orden = new ArrayList<>();
        int paso = 1;
        while (!cola.isEmpty()) {
            int u = cola.poll();
            orden.add(nombre(u));
            sb.append(paso++).append(". Se saca ").append(nombre(u)).append(" (grado de entrada 0)");
            StringBuilder liberados = new StringBuilder();
            for (int v : sig.get(u)) {
                if (--grado[v] == 0) {
                    cola.add(v);
                    liberados.append(' ').append(nombre(v));
                }
            }
            if (liberados.length() > 0) {
                sb.append(" -> queda libre:").append(liberados);
            }
            sb.append('\n');
        }
        sb.append('\n');
        if (orden.size() < n) {
            sb.append("✘ El grafo tiene al menos un CICLO: no existe orden topológico.\n");
            sb.append("  Vértices atrapados en ciclos o dependientes de ellos: ");
            List<String> resto = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (grado[i] > 0) {
                    resto.add(nombre(i));
                }
            }
            sb.append(resto).append('\n');
        } else {
            sb.append("✔ Orden topológico: ").append(String.join(" → ", orden)).append('\n');
        }
        sb.append("Complejidad: O(V + E)\n");
        return sb.toString();
    }

    // ---- Viajante (TSM) ----
    String tsp(String entrada, String param) {
        leer(entrada);
        int n = n();
        if (n < 2 || n > 10) {
            throw new IllegalArgumentException("Se necesitan entre 2 y 10 ciudades (hay " + n + ").");
        }
        int[][] d = new int[n][n];
        for (int[] fila : d) {
            Arrays.fill(fila, INF);
        }
        for (int i = 0; i < n; i++) {
            d[i][i] = 0;
        }
        for (int[] e : aristas) {
            d[e[0]][e[1]] = Math.min(d[e[0]][e[1]], e[2]);
            d[e[1]][e[0]] = Math.min(d[e[1]][e[0]], e[2]);
        }

        // Fuerza bruta con poda
        int[] ruta = new int[n];
        boolean[] vis = new boolean[n];
        vis[0] = true;
        int[] mejorCosto = {INF};
        int[][] mejorRuta = {null};
        long[] contador = {0};
        long t = System.nanoTime();
        tspRec(d, n, ruta, vis, 1, 0, contador, mejorCosto, mejorRuta);
        double ms = (System.nanoTime() - t) / 1e6;

        StringBuilder sb = new StringBuilder("Problema del viajante - " + n + " ciudades, salida desde " + nombre(0) + "\n\n");
        sb.append("1) FUERZA BRUTA (óptimo) - O(n!)\n");
        if (mejorRuta[0] == null) {
            sb.append("   No existe un ciclo que visite todas las ciudades con las aristas dadas.\n");
        } else {
            sb.append("   Ruta: ").append(formatoRuta(mejorRuta[0])).append('\n');
            sb.append("   Costo total: ").append(mejorCosto[0]).append('\n');
        }
        sb.append(String.format(Locale.US, "   Rutas completas evaluadas: %d  (%.3f ms)%n", contador[0], ms));

        // Vecino más cercano
        boolean[] usada = new boolean[n];
        int[] nn = new int[n];
        int actual = 0, costo = 0;
        usada[0] = true;
        boolean ok = true;
        for (int k = 1; k < n; k++) {
            int mejor = -1;
            for (int j = 0; j < n; j++) {
                if (!usada[j] && d[actual][j] < INF && (mejor < 0 || d[actual][j] < d[actual][mejor])) {
                    mejor = j;
                }
            }
            if (mejor < 0) {
                ok = false;
                break;
            }
            nn[k] = mejor;
            usada[mejor] = true;
            costo += d[actual][mejor];
            actual = mejor;
        }
        sb.append("\n2) HEURÍSTICA VECINO MÁS CERCANO - O(n²)\n");
        if (!ok || d[actual][0] >= INF) {
            sb.append("   No logró cerrar el ciclo con las aristas dadas.\n");
        } else {
            costo += d[actual][0];
            sb.append("   Ruta: ").append(formatoRuta(nn)).append('\n');
            sb.append("   Costo total: ").append(costo).append('\n');
            if (mejorRuta[0] != null) {
                sb.append(costo == mejorCosto[0] ? "   Coincide con el óptimo.\n"
                        : "   No es óptima: " + (costo - mejorCosto[0]) + " unidades por encima del óptimo.\n");
            }
        }
        sb.append("\nEl TSM es NP-Hard: no se conoce algoritmo polinomial que garantice el óptimo.\n");
        return sb.toString();
    }

    private void tspRec(int[][] d, int n, int[] ruta, boolean[] vis, int nivel, int costo,
                               long[] contador, int[] mejorCosto, int[][] mejorRuta) {
        if (costo >= mejorCosto[0]) {
            return;
        }
        if (nivel == n) {
            if (d[ruta[n - 1]][0] < INF) {
                int total = costo + d[ruta[n - 1]][0];
                contador[0]++;
                if (total < mejorCosto[0]) {
                    mejorCosto[0] = total;
                    mejorRuta[0] = ruta.clone();
                }
            }
            return;
        }
        for (int j = 1; j < n; j++) {
            if (!vis[j] && d[ruta[nivel - 1]][j] < INF) {
                vis[j] = true;
                ruta[nivel] = j;
                tspRec(d, n, ruta, vis, nivel + 1, costo + d[ruta[nivel - 1]][j], contador, mejorCosto, mejorRuta);
                vis[j] = false;
            }
        }
    }

    private String formatoRuta(int[] ruta) {
        StringBuilder sb = new StringBuilder();
        for (int c : ruta) {
            sb.append(nombre(c)).append(" → ");
        }
        return sb.append(nombre(ruta[0])).toString();
    }

    // ---- Prim ----
    String prim(String entrada, String param) {
        leer(entrada);
        int n = n();
        int inicio = param == null || param.trim().isEmpty() ? 0 : indice(param.trim());
        if (inicio < 0) {
            throw new IllegalArgumentException("El vértice \"" + param.trim() + "\" no existe en el grafo.");
        }
        List<List<int[]>> adj = adyacencia();
        boolean[] visto = new boolean[n];
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt((int[] a) -> a[0]));
        pq.add(new int[] {0, inicio, -1}); // {peso, destino, origen}
        StringBuilder sb = new StringBuilder("Algoritmo de Prim desde " + nombre(inicio) + "\n\n");
        int total = 0, aceptados = 0;
        while (!pq.isEmpty()) {
            int[] e = pq.poll();
            if (visto[e[1]]) {
                continue;
            }
            visto[e[1]] = true;
            if (e[2] >= 0) {
                aceptados++;
                total += e[0];
                sb.append(aceptados).append(". Arista ").append(nombre(e[2])).append(" - ").append(nombre(e[1]))
                  .append("  (peso ").append(e[0]).append(")\n");
            }
            for (int[] vecino : adj.get(e[1])) {
                if (!visto[vecino[0]]) {
                    pq.add(new int[] {vecino[1], vecino[0], e[1]});
                }
            }
        }
        sb.append("\nPeso total del árbol: ").append(total).append('\n');
        if (aceptados < n - 1) {
            sb.append("⚠ El grafo no es conexo: se obtuvo un árbol solo para la componente de ").append(nombre(inicio)).append(".\n");
        }
        sb.append("Complejidad: O(E log V)\n");
        return sb.toString();
    }

    // ---- Kruskal ----
    String kruskal(String entrada, String param) {
        leer(entrada);
        int n = n();
        List<int[]> orden = new ArrayList<>(aristas);
        orden.sort(Comparator.comparingInt(a -> a[2]));
        int[] padre = new int[n];
        for (int i = 0; i < n; i++) {
            padre[i] = i;
        }
        StringBuilder sb = new StringBuilder("Algoritmo de Kruskal (aristas ordenadas por peso)\n\n");
        int total = 0, aceptados = 0;
        for (int[] e : orden) {
            int a = raiz(padre, e[0]), b = raiz(padre, e[1]);
            String desc = nombre(e[0]) + " - " + nombre(e[1]) + "  (peso " + e[2] + ")";
            if (a != b) {
                padre[a] = b;
                total += e[2];
                aceptados++;
                sb.append("  ✔ acepta  ").append(desc).append('\n');
            } else {
                sb.append("  ✘ rechaza ").append(desc).append("  -> formaría un ciclo\n");
            }
        }
        sb.append("\nAristas en el árbol: ").append(aceptados).append("   Peso total: ").append(total).append('\n');
        if (aceptados < n - 1) {
            sb.append("⚠ El grafo no es conexo: se obtuvo un bosque de expansión mínima.\n");
        }
        sb.append("Complejidad: O(E log E)\n");
        return sb.toString();
    }

    private static int raiz(int[] padre, int x) {
        while (padre[x] != x) {
            padre[x] = padre[padre[x]];
            x = padre[x];
        }
        return x;
    }

    // ---- Dijkstra ----
    String dijkstra(String entrada, String param) {
        leer(entrada);
        int n = n();
        for (int[] e : aristas) {
            if (e[2] < 0) {
                throw new IllegalArgumentException("Dijkstra no admite pesos negativos.");
            }
        }
        int origen = param == null || param.trim().isEmpty() ? 0 : indice(param.trim());
        if (origen < 0) {
            throw new IllegalArgumentException("El vértice \"" + param.trim() + "\" no existe en el grafo.");
        }
        List<List<int[]>> adj = adyacencia();
        int[] dist = new int[n];
        int[] previo = new int[n];
        Arrays.fill(dist, INF);
        Arrays.fill(previo, -1);
        dist[origen] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt((int[] a) -> a[0]));
        pq.add(new int[] {0, origen});
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int u = cur[1];
            if (cur[0] > dist[u]) {
                continue;
            }
            for (int[] v : adj.get(u)) {
                if (dist[u] + v[1] < dist[v[0]]) {
                    dist[v[0]] = dist[u] + v[1];
                    previo[v[0]] = u;
                    pq.add(new int[] {dist[v[0]], v[0]});
                }
            }
        }
        StringBuilder sb = new StringBuilder("Algoritmo de Dijkstra - origen " + nombre(origen) + "\n\n");
        sb.append(String.format("%-8s %-10s %s%n", "Destino", "Distancia", "Camino"));
        sb.append("-".repeat(50)).append('\n');
        for (int i = 0; i < n; i++) {
            if (dist[i] >= INF) {
                sb.append(String.format("%-8s %-10s %s%n", nombre(i), "∞", "(inalcanzable)"));
                continue;
            }
            List<String> camino = new ArrayList<>();
            for (int x = i; x != -1; x = previo[x]) {
                camino.add(0, nombre(x));
            }
            sb.append(String.format("%-8s %-10d %s%n", nombre(i), dist[i], String.join(" → ", camino)));
        }
        sb.append("\nComplejidad: O((V + E) log V)\n");
        return sb.toString();
    }
}
