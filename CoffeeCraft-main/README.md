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

# CoffeeCraft – Avance 2

Aplicación de escritorio en **Java puro** (solo `javax.swing`, `java.awt` y el JDK) para personalizar café.
Permite diseñar la bebida (tipo, grano, tamaño, temperatura y agregados) y ver al instante el precio y el tiempo de entrega. También gestiona clientes y pedidos, genera tickets y reportes, y guarda los datos automáticamente.

**Estado del proyecto:** Avance 2 (interfaz gráfica Swing). El Avance 1 (modelo, persistencia y demo por consola) sigue incluido.

## Funcionalidades

- **Dashboard:** indicadores (clientes, pedidos, ingresos, ticket promedio), gráficos de barras dibujados con Java2D, ranking de mejores clientes y tabla de ingresos por bebida y tamaño.
- **Menú:** tarjetas con las bebidas base; cada una lleva al diseñador con el tipo ya elegido.
- **Diseñar bebida:** listas desplegables, botones de opción, casillas y control deslizante. Cada cambio recalcula precio y tiempo. Incluye carrito y confirmación del pedido.
- **Clientes:** tabla con búsqueda, filtro (Todos / Frecuentes / Regulares), ordenamiento por columna, formulario con validación, edición, eliminación e importar/exportar CSV.
- **Pedidos:** tabla con filtros por cliente y estado, avance de estado, ticket en pantalla, guardado del ticket en `.txt` y exportación a CSV.
- **Guardado automático:** los datos se guardan en `coffeecraft.dat` (archivo binario) tras cada cambio y al cerrar la ventana.

## Reparto del trabajo

| Integrante | Avance 1 | Avance 2 |
|---|---|---|
| **Morales Yapu, Hillary Fernanda** | Bebidas y personalización | Menú y diseñador de bebidas (`IconoTaza`, `PanelMenu`, `PanelDisenador`) |
| **Gomez Huaman, Melany Yaritza** | Pedidos, clientes y reportes | Clientes y pedidos (`DialogoCliente`, `PanelClientes`, `DialogoTicket`, `PanelPedidos`) |
| **Paredes Gonzales, Melanie Grace** | Persistencia y archivos | Base de la interfaz, dashboard y archivos (`Estilo`, `Contexto`, `Dialogos`, `GraficoBarras`, `PanelDashboard`, `VentanaPrincipal`, `MainGUI`, `RepositorioTexto`) |

## Estructura del proyecto

```
src/coffeecraft/
├── Main.java              Demostración por consola (Avance 1)
├── MainGUI.java           Arranque de la ventana (Avance 2)
├── modelo/                Bebidas, agregados, granos, usuarios, pedidos, descuentos, catálogo
├── servicio/              Reportes y datos iniciales
├── persistencia/          Archivo binario, texto/CSV, JDBC (DAO y sincronizador)
└── gui/                   Pantallas y componentes Swing
    ├── Actualizable, Navegador, Personalizador   (interfaces)
    ├── Estilo, Contexto, Dialogos, GraficoBarras
    ├── VentanaPrincipal, PanelDashboard
    ├── IconoTaza, PanelMenu, PanelDisenador
    └── DialogoCliente, PanelClientes, DialogoTicket, PanelPedidos
```

## Cómo ejecutar

Requiere **Java 8 o superior**. Desde la carpeta del proyecto:

```
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out coffeecraft.MainGUI
```

En Windows (PowerShell o CMD), si `find` no está disponible:

```
dir /s /b src\*.java > fuentes.txt
javac -encoding UTF-8 -d out @fuentes.txt
java -cp out coffeecraft.MainGUI
```

En VS Code, NetBeans o Eclipse: abrir `MainGUI.java` y pulsar **Run**.

- La primera vez se crean datos de ejemplo y el archivo `coffeecraft.dat`.
- Para volver a los datos de ejemplo, borra `coffeecraft.dat`.
- La demostración por consola del Avance 1 se ejecuta con `java -cp out coffeecraft.Main`.

## Reglas de negocio (valores de ejemplo, se pueden cambiar)

- Precio base S/ 8.00 × factor de tamaño (pequeño 1.0, mediano 1.25, grande 1.5) + extra del grano + agregados + extra propio de cada bebida.
- Tiempo base 2 min + minutos por tamaño + tiempo del grano + tiempo de agregados + tiempo propio de cada bebida.
- Cliente frecuente: desde su 4.º pedido recibe 10 % de descuento.
- Un pedido solo admite bebidas mientras está `PENDIENTE`, y su estado solo avanza (Pendiente → En preparación → Listo → Entregado).
- El frappé solo se sirve frío.

## Temas del curso aplicados

Clases y objetos, herencia y polimorfismo, clases abstractas e interfaces, `ArrayList` y `HashMap`, arreglos 1D y 2D, ordenamiento y búsqueda, eventos y layouts de Swing (`CardLayout`, `GridBagLayout`), `JTable` con modelo propio, Java2D, archivos de texto (CSV y tickets), archivos binarios (`Serializable`), Look and Feel y JDBC.

## Base de datos (JDBC)

El botón **Sincronizar con base de datos** del Dashboard usa JDBC. Para probarlo hay que agregar el `.jar` de **sqlite-jdbc** al classpath:

```
java -cp "out:sqlite-jdbc.jar" coffeecraft.MainGUI
```

(En Windows, usar `;` en lugar de `:`). Sin el driver, la aplicación muestra un mensaje de error y sigue funcionando.

## Pendiente para el siguiente avance

- Probar la sincronización con la base de datos usando `sqlite-jdbc` y cargar los datos desde ella.
- Pantalla para administrar el catálogo (agregar y editar granos y agregados).
- Reportes con filtro por fechas y exportación.
- Pruebas finales de los cuadros de selección de archivo (importar/exportar CSV y guardar ticket) en todos los equipos.

## Integrantes

Morales Yapu, Hillary Fernanda · Gomez Huaman, Melany Yaritza · Paredes Gonzales, Melanie Grace
EPIS – UNSA 2026
