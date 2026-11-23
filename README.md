# Estructura de Datos y Algoritmos II - EPN (proyecto NetBeans / Maven)

## Abrir y ejecutar
1. NetBeans -> **File -> Open Project** -> selecciona la carpeta `proyecto-eda2`.
2. Clic derecho en el proyecto -> **Run** (clase principal: `epn.eda2.Controlador`).

## Logos
Carpeta `imagenes/` junto a `pom.xml`: `logo_epn.png` y `logo_facultad.png`.
Si no existen se muestra un recuadro punteado.

## Estructura (Modelo - Vista - Controlador, 3 clases)
- `Modelo`      - datos del índice (temas y ejemplos) y todos los algoritmos. No conoce la interfaz.
- `Vista`       - ventana con portada, índice y pantalla de ejemplo. Solo muestra datos y avisa de los clics.
- `Controlador` - une ambos: responde a la Vista, llama al Modelo y actualiza la Vista. Contiene `main`.

El tema **Grafos** tiene un único botón; dentro, un selector permite elegir
Ordenamiento topológico, Viajante (TSM), Prim, Kruskal o Dijkstra.
