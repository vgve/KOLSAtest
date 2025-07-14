# Тестовое задание от kolsanovafit

## Стэк
- Kotlin, ViewModel, Coroutines
- XML, Fragments, ViewBinding
- RecyclerView, AdapterDelegates
- Retrofit, OkHttp, Gson
- Hilt
- ExoPlayer

## Скриншоты
<img src="https://raw.github.com/vgve/KOLSAtest/master/screenshots/first_screen.jpg" alt="Screenshot-20220921-133949" border="10" height="500px">
<img src="https://raw.github.com/vgve/KOLSAtest/master/screenshots/second_screen.jpg" alt="Screenshot-20220921-133949" border="10" height="500px">

## Модульная структура проекта

Проект разделен на три модуля с использованием Clean Architecture.
### 🧠 core модуль
- Сетевой слой:
    - Инициализация OkHttp и Retrofit
    - Общие утилиты

### ▶️ player модуль
- Медиа:
    - Singleton-обертка над ExoPlayer
    - Централизованное управление плеером

### 💪 workouts модуль (feature-модуль)
- Фича тренировок:
    - Экран списка тренировок
    - Экран детализации тренировки:
        - Информация
        - Видеоплеер (использует player модуль)
