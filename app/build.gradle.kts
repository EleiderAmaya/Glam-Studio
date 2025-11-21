plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp") version "2.0.21-1.0.26"
}

android {
    namespace = "com.glamstudio"
    compileSdk =
        34

    defaultConfig {
        applicationId = "com.glamstudio"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    // --- INICIO DE AJUSTES PARA COMPOSE ---
    buildFeatures {
        // Habilita Jetpack Compose
        compose = true
    }
    composeOptions {
        // Define la versión de la extensión del compilador de Kotlin.
        // Asegúrate de que tu versión de Kotlin sea compatible.
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    // --- FIN DE AJUSTES PARA COMPOSE ---
}

dependencies {
    // Dependencias originales
    implementation(libs.androidx.core.ktx)
    // Se comentan appcompat y material regular, ya que Compose usa su propio sistema de UI
    // implementation(libs.androidx.appcompat)
    // implementation(libs.material)

    // --- INICIO DE DEPENDENCIAS DE COMPOSE ---

    // BOM de Compose (Bill of Materials) para gestionar versiones
    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Dependencia para `setContent` y la integración con Activity
    implementation("androidx.activity:activity-compose:1.9.0")

    // Dependencias fundamentales de la UI de Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3") // Se usa Material3 en lugar de `libs.material`
    // Iconos Material (Outlined/Filled)
    implementation("androidx.compose.material:material-icons-extended")

    // Dependencias de ciclo de vida (comunes en apps de Compose)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")

    // Navegación con Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // (Opcional) Google Fonts en tiempo de ejecución para tipografías como Epilogue
    // implementation("androidx.compose.ui:ui-text-google-fonts")

    // --- FIN DE DEPENDENCIAS DE COMPOSE ---

    // Dependencias de prueba
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Dependencias de prueba para Compose
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // --- INICIO: Persistencia y arquitectura ---
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    // ViewModel para Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    // --- FIN: Persistencia y arquitectura ---
}