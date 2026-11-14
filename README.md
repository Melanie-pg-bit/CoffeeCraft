# CoffeeCraft
# CoffeeCraft – Avance 1

Sistema de personalización de café en **Java puro** (sin librerías externas, salvo el driver JDBC que se agrega al final).
Permite diseñar la bebida (tipo, grano, tamaño, temperatura y agregados) y calcula al instante el precio y el tiempo de entrega.

## Reparto del avance entre las 3 integrantes

Cada parte son clases del diagrama UML. Se pueden intercambiar los nombres si prefieren otro reparto.

| Integrante | Parte | Clases (paquete) | Temas del temario |
|---|---|---|---|
| **Morales Yapu Hillary Fernanda** | **A. Bebidas y personalización** | `Bebida`, `Espresso`, `Latte`, `Cappuccino`, `Americano`, `Frappe`, `TipoBebida`, `Calculable`, `Personalizable`, `Tamano`, `Temperatura`, `Grano`, `Agregado`, `Jarabe`, `Leche`, `Topping`, `Catalogo` (`coffeecraft.modelo`) | Clases y objetos, constructores sobrecargados y `this`, `toString`, herencia y polimorfismo, clases abstractas e interfaces, `HashMap` |
| **Gomez Huaman Melany Yaritza** | **B. Pedidos, clientes y reportes** | `Usuario`, `Pedido`, `EstadoPedido`, `Descuento`, `SinDescuento`, `DescuentoFrecuente` (`coffeecraft.modelo`); `Reportes`, `DatosIniciales` (`coffeecraft.servicio`) | `ArrayList`, composición y agregación, arreglos 1D y 2D, ordenamiento (burbuja, selección), búsqueda (lineal, binaria) |
| **Paredes Gonzales Melanie Grace** | **C. Persistencia y archivos** | `DatosApp`, `RepositorioBinario`, `RepositorioTexto` (`coffeecraft.persistencia`); `ConexionBD`, `ClienteDAO`, `PedidoDAO`, `SincronizadorBD` (`coffeecraft.persistencia`) | Archivos de texto (CSV, ticket), archivos binarios y de objetos (`Serializable`), bases de datos relacionales, SQL y JDBC |

`coffeecraft/Main.java` es la demostración por consola que une las tres partes.

## Cómo ejecutar

Desde esta carpeta:

```
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out coffeecraft.Main
```

En NetBeans/Eclipse: importar la carpeta `src` como proyecto Java y ejecutar `coffeecraft.Main`.
Todo el código es compatible con Java 8 o superior.

## Reglas de negocio usadas (valores de ejemplo, se pueden cambiar)

- Precio base S/ 8.00 × factor de tamaño (pequeño 1.0, mediano 1.25, grande 1.5) + extra del grano + agregados + extra propio de cada bebida.
- Tiempo base 2 min + minutos por tamaño + tiempo del grano + tiempo de agregados + tiempo propio de cada bebida.
- Cliente frecuente: desde su 4.º pedido recibe 10 % de descuento.
- Un pedido solo admite bebidas mientras está `PENDIENTE`, y su estado solo avanza (Pendiente → En preparación → Listo → Entregado).

## Pendiente para el siguiente avance

- Interfaz gráfica Swing  con el estilo del dashboard de referencia: Dashboard, Menú, Diseñar bebida con precio y tiempo en vivo, Clientes y Pedidos.
- Probar la parte JDBC con un driver real (por ejemplo `sqlite-jdbc`): no pudo probarse en el entorno donde se escribió. Hay que agregar el `.jar` del driver al classpath del proyecto.
- Diagrama UML actualizado (se agregaron `TipoBebida`, `Catalogo`, `Reportes`, y los atributos `telefono` y `direccion` en `Usuario`).
