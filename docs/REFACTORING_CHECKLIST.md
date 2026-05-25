# Чеклист рефакторинга HabbitApp (2–3 недели)

Цель: довести pet-проект до уровня **уверенного Junior** — тестируемый data layer, безопасный Compose, стабильная БД.

---

## Неделя 1 — Критичные баги и фундамент

### День 1–2: Data layer (начато в этом PR)
- [x] `model/repository/TaskRepository.kt` — интерфейс
- [x] `model/repository/TaskRepositoryImpl.kt` — реализация через DAO
- [x] `TaskViewModel` — работает через Repository, не через DAO напрямую
- [ ] `AimRepository` + `ReminderRepository` по тому же шаблону
- [ ] `AimViewModel`, `ReminderViewModel` — перевести на репозитории

**Файлы:** `TaskViewModel.kt`, `AimViewModel.kt`, `ReminderViewModel.kt`

### День 3: Unit-тесты (начато)
- [x] `StreakManagerTest` — базовые сценарии streak
- [ ] `ConvertersTest` — сериализация `List<Boolean>`, `Map`
- [ ] `TaskRepositoryTest` — in-memory Room (androidTest или `room-testing`)

**Файлы:** `app/src/test/...`, `Converters.kt`

### День 4–5: Coroutines в Compose
- [ ] `levelProductivityPage.kt` — убрать `scope.launch` из тела Composable → `LaunchedEffect(date)`
- [ ] `TaskCard.kt` — убрать `LaunchedEffect` + `updateTask` из карточки → один вызов в `MainPage` / ViewModel при открытии экрана
- [ ] Проверить `UpdateTask.kt`, `AddTaskPage.kt` — save только по клику, не при recomposition

**Файлы:** `levelProductivityPage.kt`, `TaskCard.kt`, `MainActivity.kt`

### День 6–7: Room
- [ ] `Database.kt` — убрать `fallbackToDestructiveMigration()`
- [ ] Включить `exportSchema = true`, папка `app/schemas/`
- [ ] Добавить `Migration(11, 12)` если менялась схема

**Файлы:** `Database.kt`, `app/build.gradle.kts`

---

## Неделя 2 — Архитектура и DRY

### День 8–9: Общий UI-каркас
- [ ] Создать `view/ui/scaffold/AppDrawerScaffold.kt` — drawer + top bar
- [ ] Заменить дубликаты в:
  - `MainActivity.kt` (`MainPage`)
  - `AimsAndObjectivesPage.kt`
  - `levelProductivityPage.kt`
  - `SettingsPage.kt`

### День 10: Model ≠ UI
- [ ] `task.kt` — удалить `import Color`, поле `actualColor`; в UI: `Color(task.backgroundColor)`
- [ ] Magic numbers `repeat` → `enum class RepeatType { DAILY, WEEKLY, MONTHLY }`
- [ ] Обновить `StreakManager`, фильтры в `MainActivity`, `AddTaskPage`

**Файлы:** `task.kt`, `StreakManager.kt`, `MainActivity.kt`, `AddTaskPage.kt`, `UpdateTask.kt`

### День 11–12: Application / Worker
- [ ] `MyApplication.kt` — не создавать `AimViewModel()`; вызвать `AimsRepository.migrateUnfinishedTasks(date)`
- [ ] `NextDayTaskManagerTransfer.kt` / `TaskMigrationWorker` — инжектить Repository или DAO, не ViewModel
- [ ] Решить Hilt: **либо** полный setup (plugin + `@Module` + `@HiltViewModel`), **либо** убрать `@HiltAndroidApp` и оставить manual DI

**Файлы:** `MyApplication.kt`, `NextDayTaskManagerTransfer.kt`, `app/build.gradle.kts`

### День 13–14: Навигация
- [ ] `AppNavHost.kt` — возврат через `popBackStack()` где уместно
- [ ] Удалить неиспользуемый `Destination.Second`
- [ ] Исправить опечатку `AimsAndObjectibesPage` → `AimsAndObjectivesPage` (по желанию, затронет много импортов)

**Файлы:** `AppNavHost.kt`, `Destination.kt`

---

## Неделя 3 — Качество и polish

### День 15–16: Состояние UI
- [ ] Ввести `UiState<T>` (Loading / Success / Error) хотя бы для списков задач
- [ ] `ReminderViewModel` — вынести `requestPermission` из ViewModel в UI/Activity

### День 17–18: Тема и настройки
- [ ] Связать `DataStore` (тёмная тема) с `HabbitAppTheme` — не только `isSystemInDarkTheme()`
- [ ] `SettingsPage` — дочитать логику переключателей

**Файлы:** `Theme.kt`, `SettingsPage.kt`, `DataStore.kt`

### День 19–20: Gradle и зависимости
- [ ] Убрать дубликат `appcompat`
- [ ] Выровнять версии lifecycle / coroutines через `libs.versions.toml`
- [ ] Удалить `libs.transport.runtime` если не используется
- [ ] Обновить KSP под Kotlin 2.0.21

### День 21: Финал
- [ ] Прогнать `./gradlew test debugUnitTest`
- [ ] Ручной smoke-test: добавить привычку, streak, напоминание, смена языка, aims
- [ ] README: стек, структура пакетов, как запустить тесты

---

## Порядок файлов (шпаргалка)

| Приоритет | Файл | Действие |
|-----------|------|----------|
| P0 | `levelProductivityPage.kt` | LaunchedEffect вместо launch в composition |
| P0 | `TaskCard.kt` | Убрать side effect из карточки |
| P0 | `Database.kt` | Миграции вместо destructive |
| P1 | `TaskViewModel.kt` | ✅ Repository |
| P1 | `TaskRepository*.kt` | ✅ Создать |
| P1 | `StreakManagerTest.kt` | ✅ Тесты |
| P1 | `MyApplication.kt` | Не создавать ViewModel |
| P2 | `MainActivity.kt` | Вынести MainPage, AppDrawerScaffold |
| P2 | `task.kt` | Убрать Compose из entity |
| P2 | `AppNavHost.kt` | popBackStack |
| P3 | `build.gradle.kts` | Зависимости, KSP |

---

## Критерий «готово к Junior в резюме»

- [ ] ≥ 10 unit-тестов (StreakManager, Converters, Repository)
- [ ] ViewModel не импортирует `ItemDatabase` / `MyApplication`
- [ ] Нет `scope.launch` в теле `@Composable` без `LaunchedEffect`
- [ ] Room не удаляет данные при обновлении приложения
- [ ] Один общий drawer/scaffold, не 4 копии

---

## Полезные команды

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:compileDebugKotlin
```
