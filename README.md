# ControlDePresencia2025 - Android
Hecho por Bruno Di Sabatino

## Útil

### Error de AGP incompatible
1. Abrir [/gradle/libs.version.toml](/gradle/libs.versions.toml).
2. Ubicar la línea ``agp = "X.X.X"``, debajo de ``[versions]``.
3. Cambiar la versión pedida por la consola. Ejemplo: ``agp = 8.6.0``.
4. Abrir la pestaña ``File`` y clicar en ``Sync Project with Gradle Files (Ctrl+Mayús+O)``.
