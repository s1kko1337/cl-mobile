# Клиент-серверное приложение - учет товаров в интернет магазине

Основной стек: .NET 9 и мобильный клиент на Android (Kotlin + Jetpack Compose).

## Репозитории проекта

Проект разделен на два независимых репозитория:

- **Backend (API):** https://github.com/s1kko1337/cl-backend
- **Mobile App (Android):** https://github.com/s1kko1337/cl-mobile

## Оглавление

- [Быстрый старт](#быстрый-старт)
- [Описание проекта](#описание-проекта)
- [Архитектура](#архитектура)
- [Требования](#требования)
- [Установка и запуск Backend](#установка-и-запуск-backend)
- [Установка и запуск Mobile App](#установка-и-запуск-mobile-app)
- [Конфигурация](#конфигурация)
- [API Documentation](#api-documentation)
- [Тестовые учетные данные](#тестовые-учетные-данные)
- [Устранение неполадок](#устранение-неполадок)

---

## Быстрый старт

### Backend

```bash
# 1. Клонируйте репозиторий
git clone https://github.com/s1kko1337/cl-backend.git
cd cl-backend

# 2. Создайте .env файл
cp .env.example .env

# 3. Запустите через Docker
docker compose up -d

# 4. Проверьте работу
# Откройте в браузере: http://localhost:5000/swagger
```

### Mobile App

```bash
# 1. Клонируйте репозиторий
git clone https://github.com/s1kko1337/cl-mobile.git
cd cl-mobile

# 2. Создайте local.properties
cp local.properties.example local.properties

# 3. Отредактируйте local.properties:
# - Получите Yandex MapKit API Key: https://developer.tech.yandex.ru/
# - Укажите MAPKIT_API_KEY=ваш-ключ
# - Для эмулятора: BASE_URL=http://10.0.2.2:5000
# - Для реального устройства: BASE_URL=http://ваш-IP:5000

# 4. Откройте проект в Android Studio и запустите
```

**Тестовые учетные данные:**
- Admin: `admin@admin.admin` / `admin@admin.admin`
- User: `test@test.test` / `test@test.test`

---

## Описание проекта

Проект состоит из двух основных компонентов, размещенных в отдельных репозиториях:

1. **Backend (.NET 9)** - RESTful API с PostgreSQL базой данных ([репозиторий](https://github.com/s1kko1337/cl-backend))
2. **Mobile App (Android)** - Нативное Android приложение с интеграцией Yandex Maps ([репозиторий](https://github.com/s1kko1337/cl-mobile))

### Основные возможности

- Аутентификация пользователей (JWT)
- Управление каталогом товаров
- Корзина покупок и оформление заказов
- Интеграция с Яндекс.Картами для выбора адреса доставки
- Экспорт данных в различные форматы
- Управление отзывами и оценками товаров

---

## Архитектура

### Backend Stack

- **Framework:** ASP.NET Core 9.0
- **Database:** PostgreSQL
- **ORM:** Entity Framework Core
- **Authentication:** JWT Bearer
- **API Documentation:** Swagger/OpenAPI
- **Containerization:** Docker + Docker Compose

### Mobile App Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** MVVM + Clean Architecture
- **DI:** Hilt
- **Network:** Retrofit + OkHttp
- **Maps:** Yandex MapKit
- **Local Storage:** Room + DataStore

---

## Требования

### Backend

- **Docker** и **Docker Compose** (рекомендуется)
  - Docker Desktop 20.10+
  - Docker Compose 2.0+

**ИЛИ** для локального запуска без Docker:

- **.NET SDK 9.0+**
- **PostgreSQL 15+**
- **Make** (опционально, для удобства)

### Mobile App

- **Android Studio** Hedgehog (2023.1.1) или новее
- **JDK 17+**
- **Android SDK:**
  - Min SDK: 26 (Android 8.0)
  - Target SDK: 36 (Android 14+)
  - Build Tools: 35.0.0
- **Gradle 8.0+** (включен в wrapper)
- **Yandex Maps API Key** (см. раздел [Получение API ключа Яндекс.Карт](#получение-api-ключа-яндекскарт))

---

## Установка и запуск Backend

### Шаг 0: Клонирование репозитория

Сначала склонируйте репозиторий backend:

```bash
# Клонируйте репозиторий
git clone https://github.com/s1kko1337/cl-backend.git
cd cl-backend
```

Backend можно запустить тремя способами: через Docker (рекомендуется), через Makefile или локально без контейнеризации.

### Способ 1: Запуск через Docker Compose (Рекомендуется)

Это самый простой способ, который автоматически настроит базу данных и API.

#### Шаг 1: Настройка переменных окружения

```bash
# Создайте .env файл из примера
cp .env.example .env

# Отредактируйте .env файл (опционально)
nano .env  # или любой другой редактор
```

Содержимое `.env` файла:

```env
# Database Configuration
POSTGRES_DB=cl_backend_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=55451241
POSTGRES_PORT=54321

# API Configuration
API_PORT=5000

# Application Configuration
ASPNETCORE_ENVIRONMENT=Production
```

#### Шаг 2: Запуск проекта

```bash
# Сборка и запуск всех контейнеров
docker compose up -d

# Проверка статуса
docker compose ps

# Просмотр логов
docker compose logs -f
```

#### Шаг 3: Проверка работоспособности

После запуска API будет доступен по адресу:

- **API:** http://localhost:5000
- **Swagger UI:** http://localhost:5000/swagger

Дождитесь сообщения в логах о том, что миграции применены и пользователи созданы.

#### Управление контейнерами

```bash
# Остановка контейнеров
docker compose down

# Остановка с удалением volumes (БД будет очищена)
docker compose down -v

# Перезапуск
docker compose restart

# Просмотр логов API
docker compose logs -f api

# Просмотр логов БД
docker compose logs -f postgres
```

### Способ 2: Запуск через Makefile (Linux/macOS)

Если вы работаете на Linux или macOS, можно использовать удобные команды Makefile.

```bash
# Убедитесь, что вы в корне репозитория cl-backend
# Показать все доступные команды
make help

# Полное развертывание (build + up + health check)
make deploy

# Другие полезные команды:
make build          # Собрать Docker образы
make up             # Запустить контейнеры
make down           # Остановить контейнеры
make logs           # Показать логи
make logs-api       # Логи только API
make logs-db        # Логи только БД
make restart        # Перезапустить контейнеры
make health         # Проверить готовность API
make init-db        # Пересоздать БД с нуля
make clean          # Удалить все (контейнеры, образы, volumes)
```

### Способ 3: Локальный запуск без Docker

Если вы предпочитаете запускать приложение локально без Docker.

#### Шаг 1: Установка PostgreSQL

Установите PostgreSQL и создайте базу данных:

```bash
# Создайте БД
createdb -U postgres cl_backend_db
```

#### Шаг 2: Настройка подключения к БД

Отредактируйте строку подключения в файле `cl-backend/DbContexts/ApplicationContext.cs`:

```csharp
protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
{
    // Измените на ваши учетные данные PostgreSQL
    optionsBuilder.UseNpgsql("Host=localhost;Port=5432;Database=cl_backend_db;Username=postgres;Password=your_password");
}
```

#### Шаг 3: Запуск проекта

```bash
# Перейдите в папку с проектом
cd cl-backend

# Применить миграции
dotnet ef database update

# Запустить проект
dotnet run
```

API будет доступен по адресу: http://localhost:5000

---

## Установка и запуск Mobile App

### Шаг 0: Клонирование репозитория

Сначала склонируйте репозиторий мобильного приложения:

```bash
# Клонируйте репозиторий
git clone https://github.com/s1kko1337/cl-mobile.git
cd cl-mobile
```

### Предварительная подготовка

#### Получение API ключа Яндекс.Карт

Мобильное приложение использует Яндекс.Карты для выбора адреса доставки. Для работы необходим API ключ:

1. Перейдите на портал разработчика Яндекс: https://developer.tech.yandex.ru/
2. Войдите в аккаунт Яндекс или зарегистрируйтесь
3. Перейдите в раздел **"Сервисы"** → **"MapKit"**
4. Нажмите **"Подключить API"** или **"Создать ключ"**
5. Заполните информацию о приложении:
   - Название приложения
   - Описание
   - Тип приложения: **Android**
6. Получите **API ключ** (формат: `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`)
7. Сохраните ключ - он понадобится для конфигурации

**Примечание:** Яндекс.Карты предоставляют бесплатный лимит запросов. Для коммерческого использования ознакомьтесь с тарифами.

### Настройка проекта

#### Шаг 1: Открытие проекта в Android Studio

```bash
# Откройте Android Studio
# File → Open → выберите папку cl-mobile
```

#### Шаг 2: Конфигурация local.properties

Создайте файл `local.properties` на основе шаблона:

```bash
# В корне cl-mobile
cp local.properties.example local.properties
```

Отредактируйте `local.properties`:

```properties
# Путь к Android SDK (обычно автоматически заполняется Android Studio)
sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk

# URL Backend API
# Для эмулятора: используйте 10.0.2.2 вместо localhost
# Для реального устройства: укажите IP-адрес компьютера в локальной сети
BASE_URL=http://10.0.2.2:5000

# API ключ Яндекс.Карт (полученный на предыдущем шаге)
MAPKIT_API_KEY=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

**Важные замечания по BASE_URL:**

- **Для Android эмулятора:** `10.0.2.2` - это специальный адрес, указывающий на localhost хост-машины
- **Для реального устройства:** используйте IP-адрес вашего компьютера (например, `http://192.168.1.100:5000`)
- **Для удаленного сервера:** используйте полный URL (например, `http://94.142.138.106:5000`)

#### Шаг 3: Синхронизация проекта

1. В Android Studio нажмите **"Sync Project with Gradle Files"** (иконка слона в toolbar)
2. Дождитесь завершения синхронизации и загрузки всех зависимостей
3. Убедитесь, что нет ошибок в Gradle Build

### Запуск приложения

#### Вариант 1: Через Android Studio (GUI)

1. Убедитесь, что backend запущен и доступен
2. Выберите устройство/эмулятор в Android Studio
3. Нажмите кнопку **"Run"** (зеленый треугольник) или `Shift + F10`

#### Вариант 2: Через командную строку

```bash
# В корне cl-mobile

# Для Windows
gradlew.bat assembleDebug
gradlew.bat installDebug

# Для Linux/macOS
./gradlew assembleDebug
./gradlew installDebug
```

### Сборка APK для установки

#### Debug версия (для тестирования)

```bash
# Windows
gradlew.bat assembleDebug

# Linux/macOS
./gradlew assembleDebug
```

APK файл будет создан в: `app/build/outputs/apk/debug/app-debug.apk`

#### Release версия (для продакшена)

```bash
# Windows
gradlew.bat assembleRelease

# Linux/macOS
./gradlew assembleRelease
```

**Примечание:** Для release сборки необходимо настроить signing config с вашим keystore.

---

## Конфигурация

### Backend Configuration

#### Переменные окружения (.env)


| Переменная     | Описание                        | Значение по умолчанию |
| ------------------------ | --------------------------------------- | ---------------------------------------- |
| `POSTGRES_DB`            | Имя базы данных            | `cl_backend_db`                          |
| `POSTGRES_USER`          | Пользователь PostgreSQL     | `postgres`                               |
| `POSTGRES_PASSWORD`      | Пароль PostgreSQL                 | `55451241`                               |
| `POSTGRES_PORT`          | Порт PostgreSQL (внешний)    | `54321`                                  |
| `API_PORT`               | Порт API (внешний)           | `5000`                                   |
| `ASPNETCORE_ENVIRONMENT` | Окружение приложения | `Production`                             |

#### JWT Configuration

JWT настройки находятся в файле `AuthOptions.cs`:

```csharp
public const string ISSUER = "MyAuthServer";
public const string AUDIENCE = "MyAuthClient";
const string KEY = "mysupersecret_secretkey!123";
```

**ВАЖНО:** Для production измените ключ на более стойкий!

### Mobile App Configuration

#### Переменные в local.properties


| Переменная | Описание                   | Пример           |
| -------------------- | ---------------------------------- | ---------------------- |
| `sdk.dir`            | Путь к Android SDK            | `E:\\androidSDK`       |
| `BASE_URL`           | URL Backend API                    | `http://10.0.2.2:5000` |
| `MAPKIT_API_KEY`     | API ключ Яндекс.Карт | `fa39cb0c-891d-...`    |

#### Network Security Config

Для разработки приложение разрешает HTTP соединения. В файле `app/src/main/res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <!-- Разрешить HTTP для разработки -->
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

**ВАЖНО:** В production используйте HTTPS и установите `cleartextTrafficPermitted="false"`.

---

## API Documentation

После запуска backend, полная документация API доступна через Swagger UI:

**URL:** http://localhost:5000/swagger

### Основные эндпоинты

#### Authentication

- `POST /api/account/login` - Вход пользователя
- `POST /api/account/register` - Регистрация нового пользователя

#### Products

- `GET /api/products` - Получить список товаров
- `GET /api/products/{id}` - Получить детали товара
- `POST /api/products` - Создать товар (требуется авторизация Admin)
- `PUT /api/products/{id}` - Обновить товар (требуется авторизация Admin)
- `DELETE /api/products/{id}` - Удалить товар (требуется авторизация Admin)

#### Categories

- `GET /api/categories` - Получить список категорий
- `POST /api/categories` - Создать категорию (требуется авторизация Admin)

#### Orders

- `GET /api/orders` - Получить заказы пользователя
- `POST /api/orders` - Создать новый заказ
- `GET /api/orders/{id}` - Получить детали заказа

#### Reviews

- `GET /api/products/{productId}/reviews` - Получить отзывы о товаре
- `POST /api/products/{productId}/reviews` - Добавить отзыв

### Авторизация в Swagger

1. Войдите через эндпоинт `/api/account/login`
2. Скопируйте полученный JWT токен
3. Нажмите кнопку **"Authorize"** в Swagger UI
4. Введите: `Bearer <ваш_токен>`
5. Нажмите **"Authorize"**

---

## Тестовые учетные данные

Backend автоматически создает тестовых пользователей при первом запуске:

### Администратор

- **Email:** `admin@admin.admin`
- **Пароль:** `admin@admin.admin`
- **Роль:** Admin

### Обычный пользователь

- **Email:** `test@test.test`
- **Пароль:** `test@test.test`
- **Роль:** User

### Моковые данные

Backend автоматически заполняет базу тестовыми данными:

- Категории товаров
- Товары с изображениями
- Отзывы и оценки

---

## Устранение неполадок

### Backend Issues

#### Проблема: Контейнеры не запускаются

**Решение:**

```bash
# Проверьте логи
docker compose logs

# Убедитесь, что порты не заняты
# Windows
netstat -ano | findstr :5000
netstat -ano | findstr :54321

# Linux/macOS
lsof -i :5000
lsof -i :54321

# Остановите конфликтующие процессы или измените порты в .env
```

#### Проблема: Ошибки миграций базы данных

**Решение:**

```bash
# Пересоздайте БД с нуля
docker compose down -v
docker compose up -d

# Проверьте логи
docker compose logs -f api
```

#### Проблема: API недоступен

**Решение:**

```bash
# Проверьте статус контейнеров
docker compose ps

# Проверьте health check
curl http://localhost:5000/swagger/index.html

# Перезапустите API
docker compose restart api
```

### Mobile App Issues

#### Проблема: "Unresolved reference: BuildConfig"

**Решение:**

1. Убедитесь, что файл `local.properties` существует и содержит все переменные
2. Синхронизируйте проект: **File → Sync Project with Gradle Files**
3. Пересоберите проект: **Build → Rebuild Project**
4. Проверьте, что в `app/build.gradle.kts` есть: `buildFeatures { buildConfig = true }`

#### Проблема: "Yandex Maps API key is missing"

**Решение:**

1. Проверьте наличие `MAPKIT_API_KEY` в `local.properties`
2. Убедитесь, что ключ без кавычек: `MAPKIT_API_KEY=your-key`, НЕ `MAPKIT_API_KEY="your-key"`
3. Синхронизируйте проект и пересоберите
4. Проверьте логи: **Logcat → фильтр "ECommerceApp"**

#### Проблема: Не удается подключиться к Backend

**Решение:**

**Для эмулятора:**

```properties
# В local.properties используйте специальный адрес эмулятора
BASE_URL=http://10.0.2.2:5000
```

**Для реального устройства:**

1. Убедитесь, что устройство и компьютер в одной Wi-Fi сети
2. Узнайте IP адрес вашего компьютера:
   ```bash
   # Windows
   ipconfig

   # Linux/macOS
   ifconfig
   ```
3. Используйте этот IP в `local.properties`:
   ```properties
   BASE_URL=http://192.168.1.XXX:5000
   ```

#### Проблема: Network Security Exception

**Решение:**

1. Убедитесь, что в `AndroidManifest.xml` указан:
   ```xml
   android:networkSecurityConfig="@xml/network_security_config"
   ```
2. Проверьте файл `app/src/main/res/xml/network_security_config.xml`
3. Для development должно быть: `cleartextTrafficPermitted="true"`

#### Проблема: Gradle Sync Failed

**Решение:**

```bash
# Очистите кэш Gradle
# В Android Studio: File → Invalidate Caches → Invalidate and Restart

# Или через командную строку
cd cl-mobile
./gradlew clean
./gradlew build --refresh-dependencies
```

---

## Дополнительная информация

### Структура проекта

Проект разделен на два репозитория:

#### Backend (cl-backend)
```
cl-backend/
├── cl-backend/              # Исходный код API
│   ├── Controllers/         # API контроллеры
│   ├── Models/              # Модели данных
│   ├── DbContexts/          # Контекст БД
│   ├── Services/            # Бизнес-логика
│   ├── DTO/                 # Data Transfer Objects
│   ├── Extensions/          # Расширения
│   ├── Utils/               # Утилиты
│   ├── Migrations/          # Миграции БД
│   └── Program.cs           # Точка входа
├── docker-compose.yml       # Docker конфигурация
├── Dockerfile               # Образ API
├── Makefile                 # Команды для управления
├── .env.example             # Пример переменных окружения
└── cl-backend.sln           # Solution файл
```

#### Mobile App (cl-mobile)
```
cl-mobile/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/ecommerceapp/
│   │   │   ├── data/        # Репозитории, API, БД
│   │   │   ├── domain/      # Use Cases, модели
│   │   │   ├── ui/          # UI компоненты (Compose)
│   │   │   └── di/          # Dependency Injection
│   │   ├── res/             # Ресурсы
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts     # Конфигурация приложения
├── docs/                    # Документация
├── build.gradle.kts         # Корневой Gradle
├── local.properties.example # Пример конфигурации
└── settings.gradle.kts
```

### Полезные ссылки

#### Репозитории проекта
- [Backend Repository (cl-backend)](https://github.com/s1kko1337/cl-backend)
- [Mobile App Repository (cl-mobile)](https://github.com/s1kko1337/cl-mobile)

#### Документация технологий
- [ASP.NET Core Documentation](https://docs.microsoft.com/en-us/aspnet/core/)
- [Entity Framework Core](https://docs.microsoft.com/en-us/ef/core/)
- [Docker Documentation](https://docs.docker.com/)
- [Android Developers](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Yandex MapKit SDK](https://yandex.ru/dev/maps/mapkit/)

---

## Поддержка и разработка

### Команды для разработки Backend

```bash
# Запуск в режиме разработки с логами
make dev

# Пересборка после изменений
make rebuild

# Подключение к контейнеру API
make shell-api

# Подключение к PostgreSQL
make db-shell

# Создание резервной копии БД
make db-backup

# Восстановление БД
make db-restore FILE=backup.sql
```

### Команды для разработки Mobile App

```bash
# Очистка проекта
./gradlew clean

# Запуск тестов
./gradlew test

# Проверка кода (lint)
./gradlew lint

# Сборка всех вариантов
./gradlew assemble
```

---

## Контакты

При возникновении вопросов или проблем, пожалуйста, создайте Issue в соответствующем репозитории:

- **Backend Issues:** https://github.com/s1kko1337/cl-backend/issues
- **Mobile App Issues:** https://github.com/s1kko1337/cl-mobile/issues
