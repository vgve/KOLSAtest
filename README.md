# Fitness App for Kolsanovafit

## 📱 Project Overview
A clean architecture Android application showcasing workout videos with seamless playback experience. This implementation demonstrates modern Android development best practices and modular app architecture.

## 🛠 Tech Stack
### Core Technologies
- Language: Kotlin
- Architecture: Clean Architecture + MVI, Single Activity
- DI: Dagger Hilt
- Async: Kotlin Coroutines & Flow 
### UI Layer
- Views: XML, Fragments, Navigation Component
- Binding: ViewBinding, ViewBindingPropertyDelegate
- Lists: RecyclerView, AdapterDelegates, DiffUtil
### Network & Media
- HTTP: REST API, Retrofit + OkHttp
- Serialization: Gson
- Video: Media 3 ExoPlayer (custom wrapper)

## 📸 Screenshots
<div style="display: flex; gap: 10px;"> 
  <img src="https://raw.github.com/vgve/KOLSAtest/master/screenshots/mockup_light.png" alt="Light Theme Screens" border="10" height="800px"> 
</div>

## 🏗 Modular Architecture

The project follows Clean Architecture principles with three distinct modules:
### 🧠 Core Module
The foundation module containing shared infrastructure:
 - Network layer:
 - OkHttp client configuration 
 - Retrofit instance creation
 - Utilities: Shared extensions

### ▶️ Player Module
Dedicated media handling module:
- ExoPlayer wrapper: Singleton implementation
- Centralized playback control
- Lifecycle-aware player management

### 💪 Workouts Feature Module
The main feature module containing:
- Workout list screen:
  - Fetching and displaying workout collection 
  - Smooth scrolling performance
- Workout detail screen:
  - Comprehensive workout information
  - Integrated video player (utilizing Player module)
  - State preservation during configuration changes
