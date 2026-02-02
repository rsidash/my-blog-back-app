# My Blog Backend Application

REST API для блог-платформы на Spring Framework с поддержкой постов, комментариев, лайков и изображений.

## Технологии

- Java 21
- Spring Framework 6.2.1 (WebMVC, JDBC)
- PostgreSQL / H2
- Gradle
- Lombok
- Jakarta Validation

## Структура проекта

```
kz.rsidash/
├── config/          # Конфигурация Spring
├── controller/      # REST контроллеры
├── service/         # Бизнес-логика
├── repository/      # Работа с БД
├── model/           # Сущности
├── dto/             # DTO объекты
├── mapper/          # Маппинг между entity и DTO
└── exception/       # Обработка исключений
```

## API Endpoints

### Health Check
- `GET /api/health` - проверка работоспособности

### Посты
- `GET /api/posts?search={query}&pageNumber={page}&pageSize={size}` - список постов с поиском
- `POST /api/posts/{postId}` - получить пост
- `POST /api/posts` - создать пост
- `PUT /api/posts/{postId}` - обновить пост
- `DELETE /api/posts/{postId}` - удалить пост

### Лайки
- `POST /api/posts/{postId}/likes` - поставить лайк

### Изображения
- `PUT /api/posts/{postId}/image` - загрузить изображение (multipart/form-data)
- `GET /api/posts/{postId}/image` - получить изображение

### Комментарии
- `GET /api/posts/{postId}/comments` - список комментариев
- `GET /api/posts/{postId}/comments/{commentId}` - получить комментарий
- `POST /api/posts/{postId}/comments` - добавить комментарий
- `PUT /api/posts/{postId}/comments/{commentId}` - обновить комментарий
- `DELETE /api/posts/{postId}/comments/{commentId}` - удалить комментарий

## Запуск

1. Настроить БД в `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/blog
spring.datasource.username=user
spring.datasource.password=password
```

2. Собрать проект:
```bash
./gradlew build
```

3. Развернуть WAR файл на сервере приложений

## Модели данных

### Post
- id, title, text, tags, likesCount, commentsCount, imagePath

### Comment
- id, text, postId
