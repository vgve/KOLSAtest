# Fitness App for Kolsanovafit

## Project Overview
A clean architecture Android application showcasing workout videos with seamless playback experience. This implementation demonstrates modern Android development best practices and modular app architecture.

## Tech Stack
- Language: Kotlin
- Architecture: Clean Architecture + MVI, Single Activity
- DI: Dagger Hilt
- Async: Kotlin Coroutines & Flow 
- Views: XML, Fragments, Navigation Component
- Binding: ViewBinding, ViewBindingPropertyDelegate
- Lists: RecyclerView, AdapterDelegates, DiffUtil
- HTTP: REST API, Retrofit + OkHttp
- Serialization: Gson
- Video: Media 3 ExoPlayer

## Project structure
```
KOLSAtest/
├── app/                              # Android Application Module
│   ├── src/main/
│   │   ├── java/com/vgve/kolsatest/
│   │   │   ├── App.kt                # Application Class
│   │   │   └── MainActivity.kt   
│   │   ├── res/                      # Resources
│   │   │   ├── drawable/             # Vector Assets
│   │   │   ├── values/               # Colors, Strings, Styles
│   │   │   └── ...
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts              # Module Dependencies
├── core/                             # Core Module
│   ├── src/main/
│   │   ├── java/com/vgve/core/
│   │   │   ├── di/                   # Dependency Injection (Dagger Hilt)
│   │   │   │   ├── CoreModule.kt
│   │   │   │   └── NetworkModule.kt
│   │   │   └── utils/extensions/
│   │   │       └── NetworkExtensions.kt
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts              # Module Dependencies
├── player/                           # Player Module
│   ├── src/main/
│   │   ├── java/com/vgve/player/
│   │   │   ├── di/                   # Dependency Injection (Dagger Hilt)
│   │   │   │   └── VideoPlayerModule.kt
│   │   │   ├── data/
│   │   │   │   └── VideoPlayerServiceImpl.kt
│   │   │   ├── domain/
│   │   │   │   └── VideoPlayerService.kt
│   │   │   └── presentation/ui/
│   │   │       └── CustomPlayerView.kt
│   │   ├── res/                      # Resources
│   │   │   ├── menu/                 # Popup menu
│   │   │   └── ...
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts              # Module Dependencies
├── workouts/                         # Feaure Module
│   ├── src/main/
│   │   ├── java/com/vgve/workout/
│   │   │   ├── di/                   # Dependency Injection (Dagger Hilt)
│   │   │   │   └── WorkoutsModule.kt
│   │   │   ├── data/
│   │   │   │   ├── models/
│   │   │   │   ├── repository/
│   │   │   │   └── WorkoutsService.kt
│   │   │   ├── domain/
│   │   │   │   ├── models/
│   │   │   │   └── repository/
│   │   │   └── presentation/
│   │   │       ├── workouts/
│   │   │       │   ├── adapters/     # AdapterDelegates, DiffUtil
│   │   │       │   ├── WorkoutsFragment.kt
│   │   │       │   └── WorkoutsViewModel.kt
│   │   │       └── workoutcard/      
│   │   │           ├── WorkoutCardFragment.kt
│   │   │           └── WorkoutCardViewModel.kt
│   │   ├── res/                      # Resources
│   │   │   ├── drawable/             # Vector Assets
│   │   │   ├── layouts/              # View layouts
│   │   │   └── ...
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts                  # Project-level Config
├── settings.gradle.kts               # Project Settings
├── gradle.properties                 # Gradle Properties
└── README.md                         # Project Documentation
```

## Screenshots
<div style="display: flex; gap: 10px;"> 
  <img src="https://raw.github.com/vgve/KOLSAtest/master/screenshots/mockup_light.png" alt="Light Theme Screens" border="10" height="800px"> 
</div>

## Modular Architecture

The project follows Clean Architecture principles with three distinct modules:
### Core Module
The foundation module containing shared infrastructure:
 - Network layer:
 - OkHttp client configuration 
 - Retrofit instance creation
 - Utilities: Shared extensions

### Player Module
Dedicated media handling module:
- ExoPlayer wrapper: Singleton implementation
- Centralized playback control
- Lifecycle-aware player management

### Workouts Feature Module
The main feature module containing:
- Workout list screen:
  - Fetching and displaying workout collection 
  - Smooth scrolling performance
- Workout detail screen:
  - Comprehensive workout information
  - Integrated video player (utilizing Player module)
  - State preservation during configuration changes
