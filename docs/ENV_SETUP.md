# Настройка окружения для разработки

## Конфигурация переменных окружения

Этот проект использует конфиденциальные данные (URL бэкенда и API ключи), которые хранятся в файле `local.properties` и **не должны** попадать в систему контроля версий.

### Шаги настройки

1. **Скопируйте файл-шаблон:**

   ```bash
   cp local.properties.example local.properties
   ```
2. **Отредактируйте `local.properties`:**

   Откройте файл `local.properties` и замените значения-заполнители на реальные:

   ```properties
   # Android SDK путь (обычно автоматически устанавливается Android Studio)
   sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk

   # URL бэкенд API
   BASE_URL=http://94.142.138.106:5000

   # API ключ Яндекс.Карт
   MAPKIT_API_KEY=ваш-ключ-здесь
   ```
3. **Получение API ключа Яндекс.Карт:**

   - Перейдите на https://developer.tech.yandex.ru/
   - Зарегистрируйтесь или войдите в аккаунт
   - Создайте новое приложение
   - Получите API ключ для MapKit
   - Вставьте ключ в `local.properties` как значение `MAPKIT_API_KEY`
4. **Синхронизация проекта:**

   После настройки `local.properties`:

   - Откройте проект в Android Studio
   - Нажмите **File → Sync Project with Gradle Files**
   - Дождитесь завершения синхронизации

## Важные замечания

### Безопасность

- ⚠️ **НИКОГДА** не коммитьте файл `local.properties` в Git
- Файл `local.properties` уже добавлен в `.gitignore`
- Используйте `local.properties.example` для документирования необходимых переменных

### Для продакшена

- **HTTPS:** В продакшене обязательно используйте HTTPS для бэкенд API
- **Network Security Config:** Удалите или обновите `app/src/main/res/xml/network_security_config.xml`
  - Текущая конфигурация разрешает HTTP (cleartext) для разработки
  - В продакшене это должно быть отключено!

### Переменные окружения

Проект использует следующие переменные:


| Переменная | Описание                   | Пример                   |
| -------------------- | ---------------------------------- | ------------------------------ |
| `BASE_URL`           | URL бэкенд API               | `http://94.142.138.106:5000`   |
| `MAPKIT_API_KEY`     | API ключ Яндекс.Карт | `fa39cb0c-891d-...`            |
| `sdk.dir`            | Путь к Android SDK            | `C:\\Users\\...\\Android\\Sdk` |

### Где используются эти переменные

- **BASE_URL:**

  - `BuildConfig.BASE_URL` в коде приложения
  - Используется в `AppModule.kt` для настройки Retrofit
  - Используется в `ProductDetailViewModel.kt` для построения URL изображений
- **MAPKIT_API_KEY:**

  - Автоматически вставляется в `AndroidManifest.xml` как `meta-data`
  - Используется `ECommerceApplication.kt` для инициализации MapKit

## Устранение неполадок

### Ошибка: "Unresolved reference: BuildConfig"

Убедитесь, что:

1. Файл `local.properties` существует и содержит все необходимые переменные
2. Выполнена синхронизация проекта с Gradle
3. В `build.gradle.kts` включена опция `buildFeatures { buildConfig = true }`

### Ошибка: "Yandex Maps API key is missing"

Проверьте:

1. Наличие `MAPKIT_API_KEY` в `local.properties`
2. Правильность синтаксиса (без кавычек, пробелов)
3. Логи в Logcat с тегом "ECommerceApp"

### Проблемы с сетевым подключением

Если бэкенд недоступен:

1. Проверьте, что `BASE_URL` указывает на правильный адрес
2. Для эмулятора: используйте `10.0.2.2` вместо `localhost`
3. Для реального устройства: убедитесь, что устройство и сервер в одной сети
