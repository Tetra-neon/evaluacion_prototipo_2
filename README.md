
# 🐾 SOS Wild - Sistema de Reporte de Fauna Salvaje

Aplicación Android para reportar avistamientos de fauna salvaje en peligro de extinción en zonas urbanas


## Badges
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white)
![License](https://img.shields.io/badge/License-Academic-blue?style=for-the-badge)


![Logo](https://cdn0.ecologiaverde.com/es/posts/4/8/6/animales_en_peligro_de_extincion_en_chile_1684_600.webp)


## 📑 Tabla de Contenidos

- [Descripción](#-descripción-del-proyecto)
- [Características](#-características-principales)
- [Intents](#-intents-implementados)

![Image](https://github.com/user-attachments/assets/4a59d9c2-6286-4ef4-be46-1b1aaa17c086)

## 📋 Descripción del Proyecto


SOS Wild es una aplicación móvil diseñada para facilitar el reporte rápido y eficiente de avistamientos de fauna salvaje protegida o en peligro de extinción que aparece en zonas urbanas.

 La app permite a los ciudadanos alertar a centros de rescate para que puedan intervenir, evaluar el estado de salud del animal y proceder con su liberación en su hábitat natural.

## 🎯 Objetivo

Crear un puente de comunicación entre la comunidad y los centros de rescate de fauna silvestre, permitiendo:

- ✅ Reportes georreferenciados de avistamientos
- ✅ Comunicación directa con centros de rescate
- ✅ Captura fotográfica de evidencia
- ✅ Acceso rápido a información de contacto de emergencia

## ✨ Características Principales

### 🏠 Pantalla Principal (HomeActivity)
- Diseño moderno con Material Design 3
- CoordinatorLayout con CollapsingToolbarLayout
- Navegación intuitiva por secciones
- Interfaz adaptable (smartphones y tablets, máx. 600dp)

### 📍 Geolocalización
- Ubicación GPS en tiempo real
- Integración con Google Maps
- Visualización de coordenadas exactas
- Compartir ubicación con centros de rescate

### 📸 Captura de Evidencia
- Cámara integrada con la app
- Guardado automático en galería
- Captura continua de múltiples fotos
- Preview inmediato de fotos tomadas

### 📞 Comunicación Rápida
- ☎️ Llamadas directas a centros de rescate
- 📧 Email con plantillas SOS predefinidas
- 💬 SMS con mensajes de reporte
- 📤 Compartir alertas en redes sociales

### 🔦 Herramientas de Campo
- Control de linterna LED (flash de la cámara)
- Sistema de ayuda con FAQ
- Configuración de notificaciones
- Guías de actuación ante avistamientos

### 👤 Gestión de Perfil
- Registro de datos personales (Nombre, RUT, Email)
- Validación de formularios
- Edición de información de usuario
- Sistema de autenticación


## 🔗 Intents Implementados

### 📤 Intents Implícitos (7)

| Intent | Descripción | 
|--------|-------------|
| ACTION_VIEW (Web) | Abre sitio web de SOS Pets Rescue |
| ACTION_SENDTO (Email) | Envía correo SOS con datos|
| ACTION_SEND (Compartir) | Comparte la app en redes sociales | 
| ACTION_DIAL (Llamar) | Marca número de emergencia | 
| ACTION_SENDTO (SMS) | Envía SMS con plantilla de reporte |
| ACTION_VIEW (Maps) | Abre Google Maps con ubicación GPS |
| TakePicture (Photo) | Captura foto y guarda en galería |

### 🔄 Intents Explícitos (6)

| Intent | Descripción | 
|--------|-------------|
| ConfigActivity (ajustes) | Pantalla de ajustes interna |
| AyudaActivity (FAQ o tutorial) |  Guía de uso|
| Abrir Activity con animación | overridePendingTransition | 
| HomeActivity (Home) | Interfaz de funcionalidades |
| PerfilActivity (Perfil) | UI que llama el correo  | 
| EditarPerfilActivity (EditarPerfil) | UI que permite editar los datos | 

### 🔬 Pasos de Prueba

### 🌐 ACTION_VIEW - Navegación Web
  1. Abrir la aplicación e iniciar sesión
  2. En Home, ir a sección "Contacto"
  3. Presionar botón "Nuestro sitio web"
  4. **Resultado esperado**: Se abre el navegador con la URL

### 📫 ACTION_SENDTO - Envío de Email
  1. En Home, presionar "Enviar correo SOS"
  2. Seleccionar app de correo (Gmail, Outlook, etc.)
  3. **Resultado esperado**: Email con asunto y mensaje prellenados

### 📨 ACTION_SEND - Compartir Contenido
  1. Presionar "Ayúdanos a difundir"
  2. Elegir app (WhatsApp, Facebook, etc.)
  3. **Resultado esperado**: Mensaje predefinido listo para compartir

![Image](https://github.com/user-attachments/assets/69326a7c-6f93-482d-b6aa-a38709733066)
### 📞 ACTION_DIAL - Realizar Llamada
  1. Presionar botón "Llamar"
  2. **Resultado esperado**: Abre marcador con +56966820967
  3. Usuario debe presionar botón de llamada

 ### 💬 ACTION_SENDTO - Envío de SMS
  1. Presionar "Enviar SMS"
  2. **Resultado esperado**: App de mensajes con número y texto prellenados


 ### 📍 ACTION_VIEW - Geolocalización
  1. Presionar "Ver mi ubicación"
  2. **Resultado esperado**: App de GoogleMaps con ubicación en tiempo real

### 📸 TakePicture - fotográfica
  1. Presionar "Activar Cámara"
  2. **Resultado esperado**: App de cámara se abre
  3. Permite tomar fotográfica
  4. Intentar de nuevo / Guardar en galería
     
![Image](https://github.com/user-attachments/assets/38dccb65-dd4c-4171-b730-1d89b28aaa21)
### 👤 LOGIN → HomeActivity
  1. Ingresar email: `t.baron@alumnos.santotomas.cl`
  2. Ingresar password: `123456`
  3. Presionar "Iniciar Sesión"
  4. **Resultado esperado**: Navega a HomeActivity con email visible

![Image](https://github.com/user-attachments/assets/bb24751e-051b-4def-8633-23c1f81e3a0c)
 ### 💁🏼‍♂️ HOME → PerfilActivity
  1. En Home, sección "Perfil"
  2. Presionar "Ver mi Perfil"
  3. **Resultado esperado**: Se abre perfil con email del usuario

![Image](https://github.com/user-attachments/assets/1ad2b28f-0ed5-4fca-966b-f08dd4776c4a)
 ### 💁🏼‍♂️ HOME → EditarPerfilActivity
  1. En Perfil, seleccionar "Editar Perfil"
  2. **Resultado esperado**: Se abre formulario para rellenar el perfil con nombre, rut y email del usuario
  3. Guardar cambios, toast con mensaje de cambios guardados con éxito.

![Image](https://github.com/user-attachments/assets/05383ec1-ed02-4970-912c-202e4513319b)
 ### 🆘 HOME → AyudaActivity
  1. En Home, sección "Cuenta"
  2. Presionar "Ayuda y FAQ"
  3. **Resultado esperado**: Pantalla con preguntas frecuentes y formulario

![Image](https://github.com/user-attachments/assets/cef313c3-7889-4fc2-9e74-82eac9d024c4)
 ### 🆘 HOME → ConfiguracionActivity
  1. En Home, sección "Cuenta"
  2. Presionar "Ajustes de la Cuenta"
  3. **Resultado esperado**: Pantalla con la configuración básicas

## 👨‍💻 Autor

**Tiffany Baron**  
Estudiante de Ingeniería en Informática - 2do Año

- 🐱 GitHub: [@Tetra-neon](https://github.com/Tetra-neon)
- 📧 Email: t.baron@alumnos.santotomas.cl
- 📂 Repo: [evaluacion_prototipo_2](https://github.com/Tetra-neon/evaluacion_prototipo_2)

## 🛠️ Tecnologías Utilizadas

### Entorno de Desarrollo
- **Android Studio**: Hedgehog | 2023.1.1 o superior
- **Gradle Version**: 8.13
- **Android Gradle Plugin (AGP)**: 8.7.3
- **JDK**: 17
- **Build Tools**: 34.0.0

### SDK y APIs
- **Compile SDK**: 34 (Android 14)
- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 34 (Android 14)

## 🎓 Contexto Académico

Proyecto desarrollado como parte de la **Evaluación de Prototipo 2**  
**Ingeniería en Informática (2do año)** - Santo Tomás Chile

### Objetivos Cumplidos
- ✅ 5 Intents Implícitos implementados
- ✅ 3+ Intents Explícitos implementados
- ✅ README completo con capturas
- ✅ APK Debug funcional
- ✅ Commits atómicos en Git
- ✅ Material Design 3

### Criterios de Evaluación

| Criterio | Estado |
|----------|--------|
| Intents Implícitos (+5) | ✅ Completo |
| Intents Explícitos (+3) | ✅ Completo |
| README.md | ✅ Completo |
| APK Debug | ✅ Funcional |
| Diseño Material | ✅ MD3 |
