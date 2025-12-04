# JunaMaps - Locales JUNAEB en Curacaví 🗺️

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![Open-Meteo](https://img.shields.io/badge/API-Open--Meteo-orange.svg)](https://open-meteo.com/)
[![License](https://img.shields.io/badge/License-Educational-orange.svg)](LICENSE)

## 📱 Descripción del Proyecto
**JunaMaps** es una aplicación móvil Android desarrollada en **Kotlin** con **Jetpack Compose**, diseñada para ayudar a los estudiantes beneficiarios de la beca JUNAEB a localizar comercios adheridos en la comuna de Curacaví. La aplicación integra geolocalización, persistencia de datos local y consumo de APIs externas para mejorar la experiencia del usuario.

---

## ✨ Funcionalidades y Requisitos Cumplidos

### 🎨 Diseño de Interfaz (UI/UX)
*   **Tecnología:** Jetpack Compose + Material Design 3.
*   **Pantallas:** Login, Registro, Home (Mapa).
*   **Navegación:** Implementada con `NavigationCompose`, asegurando un flujo sin errores entre formularios y mapa.
*   **Validaciones:** Feedback visual inmediato en campos de texto (email, contraseña).

### 🔄 Integración y Persistencia (CRUD)
*   **Arquitectura MVVM:** Separación clara entre interfaz (`ui`), lógica (`viewmodel`) y datos (`repository`).
*   **Persistencia Local (Room):** Base de datos SQLite para gestión de usuarios (Registro/Login) con operaciones CRUD completas.
*   **Datos de Locales:** Gestión eficiente de datos estáticos mediante `JSON` local (`places.json`) con repositorio dedicado, garantizando funcionamiento offline.

### ☁️ Consumo de API Externa
*   **API:** Open-Meteo (Clima).
*   **Integración:** Implementada con **Retrofit**.
*   **Funcionalidad:** Muestra el clima actual basado en la ubicación GPS del dispositivo, integrándose visualmente en la tarjeta de información sin interferir con el mapa.

### 🧪 Pruebas Unitarias (QA)
*   **Cobertura:** >80% de la lógica de negocio crítica.
*   **Herramientas:** `JUnit` para el framework y `MockK` para simulación de componentes.
*   **Módulos probados:**
    *   `AuthViewModel`: Lógica de sesión, validaciones de regex y estados de UI.
    *   `PlaceRepository`: Parseo de datos y manejo de errores de lectura.

### 📦 Generación de APK Firmado
*   **Keystore:** Archivo `.jks` generado y configurado.
*   **Configuración:** Signing Configs implementados en `build.gradle.kts` para la variante `release`.
*   **Seguridad:** ProGuard/R8 habilitado para ofuscación y optimización.

---

## 🔗 Endpoints y Datos Utilizados

### 1. API Externa (Clima)
Utilizamos la API gratuita de Open-Meteo para obtener datos meteorológicos.
*   **Base URL:** `https://api.open-meteo.com/`
*   **Endpoint:** `v1/forecast`
*   **Parámetros:** `latitude`, `longitude`, `current_weather=true`

### 2. Datos Locales (Locales JUNAEB)
*   **Fuente:** Archivo `assets/places.json`
*   **Estructura:** Array de objetos JSON con ID, nombre, latitud, longitud y dirección.

---

## 🛠️ Pasos para Ejecutar el Proyecto

1.  **Clonar el repositorio:**
    ```bash
    git clone [URL_DEL_REPOSITORIO]
    ```
2.  **Abrir en Android Studio:** (Versión Iguana o superior recomendada).
3.  **Sincronizar Gradle:** Esperar a que se descarguen las dependencias.
4.  **Configurar API Key de Google Maps:**
    *   Añadir en `local.properties`: `MAPS_API_KEY=TU_API_KEY`
    *   O verificar `AndroidManifest.xml`.
5.  **Ejecutar:** Presionar `Run` ▶️ seleccionando un emulador o dispositivo físico.

---

## 🔐 Generación de APK Firmado y .jks

El proyecto incluye la configuración para generar el APK firmado automáticamente.

### Pasos para generar el APK:
1.  Abrir terminal en Android Studio.
2.  Ejecutar el comando:
    ```bash
    ./gradlew assembleRelease
    ```
3.  **Ubicación del APK:** `app/build/outputs/apk/release/app-release.apk`

---

## 🤝 Herramientas de Colaboración

### 🐙 GitHub (Control de Versiones)
El repositorio evidencia el trabajo colaborativo mediante:
*   **Commits:** Distribuidos entre los integrantes.
*   **Ramas:** Uso de ramas para características (features).

### 📋 Trello (Planificación)
Se utilizó metodología Kanban para la distribución de tareas.

---

## 📂 Estructura del Código (Arquitectura)

El proyecto sigue el patrón **MVVM** para cumplir con los estándares de mantenibilidad:

```
cl.duoc.basico
├── model/           # Entidades (Usuario, Place) y DAOs (Room)
├── repository/      # Fuente de verdad (PlaceRepository, WeatherRepository)
├── viewmodel/       # Lógica de negocio y gestión de estado (AuthViewModel)
├── ui/              # Pantallas Jetpack Compose (Home, Login, Maps)
└── service/         # Interfaces Retrofit (WeatherService)
```

---

## 📊 Métricas de Calidad

*   **Líneas de Código:** ~2,500
*   **Tests Unitarios:** Implementados y pasando (✅).
*   **Validación UI:** Feedback inmediato al usuario.
*   **Estabilidad:** Manejo de errores en llamadas de red y lectura de archivos.

---
