# Тестовое задание от kolsanovafit

## Стэк
- Kotlin
- XML, Fragments, ViewBinding
- RecyclerView, AdapterDelegates
- Retrofit, OkHttp, Gson
- Hilt
- ExoPlayer

## Скриншоты
![image](https://raw.github.com/vgve/KOLSAtest/master/screenshots/first_screen.jpg)
![image](https://raw.github.com/vgve/KOLSAtest/master/screenshots/second_screen.jpg)

## Модульная структура проекта

Проект разделен на три модуля с использованием Clean Architecture.
### 🧠 core модуль
- Сетевой слой:
    - Инициализация Retrofit
    - Базовые функции для работы с API
    - Общие модели данных и interceptors

### ▶️ player модуль
- Медиа:
    - Singleton-обертка над ExoPlayer
    - Централизованное управление состоянием плеера

### 💪 workouts модуль (feature-модуль)
- Фича тренировок:
    - Экран списка тренировок
    - Экран детализации тренировки:
        - Информация
        - Видеоплеер (использует player модуль)
