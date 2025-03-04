# ControlDePresencia2025 - Android
Hecho por Bruno Di Sabatino

![apphero](/apphero.png)

## Útil

### Navegar por el código

Las clases no son cortas, por lo que recomiendo utilizar combinaciones de tecla para "plegar" el
código, es decir; Solo ver los nombres de los métodos más no su lógica.

#### Combinaciones de tecla

``CTRL+SHIFT+MENOS`` Plegar código.
``CTRL+SHIFT+MÁS`` Desplegar código.

### Error de AGP incompatible
1. Abrir [/gradle/libs.version.toml](/gradle/libs.versions.toml).
2. Ubicar la línea ``agp = "X.X.X"``, debajo de ``[versions]``.
3. Cambiar la versión pedida por la consola. Ejemplo: ``agp = 8.6.0``.
4. Abrir la pestaña ``File`` y clicar en ``Sync Project with Gradle Files (Ctrl+Mayús+O)``.
