package com.glamstudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.glamstudio.ui.theme.GlamStudioTheme
import com.glamstudio.navigation.AppRoot

/**
 * Punto de entrada Android (nivel Activity).
 *
 * Responsabilidades:
 * - Montar la UI de Compose mediante `setContent { ... }`.
 * - Aplicar el tema de la app (`GlamStudioTheme`).
 * - Delegar la navegación y pantallas a `AppRoot()`.
 *
 * Cómo extender:
 * - Si necesitas inyectar dependencias, inicialízalas aquí y pásalas a `AppRoot()` o a un `CompositionLocal`.
 * - Para theming global, modifica `GlamStudioTheme` (colores/tipografías) y todos los composables lo heredarán.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GlamStudioTheme {
                AppRoot()
            }
        }
    }
}
