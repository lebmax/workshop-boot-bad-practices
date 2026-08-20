# Проект получения информации по имени человека

Краткое описание проекта:

* REST-сервис с UI-страницей, доступной на [localhost:8080](http://127.0.0.1:8080)
* Задача — предоставить информацию по имени (предположительный пол и возраст)
* При формировании ответа обращается к другим сервисам
* Нефункциональные требования — количество запросов с одного IP должно быть ограничено (rate-limit)

# Структура
Проект состоит из трёх модулей:

* [workshop-app](workshop-app) — само приложение с REST-сервисом и UI-страницей
* [rate-limiter-starter](rate-limiter-starter) — наш стартер, который используется в приложении и каких-то других наших проектах
* [bad-practices-examples](bad-practices-examples) — изолированные запускаемые примеры ошибок в Spring Boot

## workshop-app (Практика, часть 1)

Модуль самого приложения с эндпойнтом для предоставления информации о запрошенном имени.  
Для тестирования можно воспользоваться графическим интерфейсом [localhost:8080](http://127.0.0.1:8080) либо вызывать `api/names/{name}/info` самостоятельно.  
Попробуйте найти максимальное количество отклонений от Best Practices в данном модуле.  
Используемый в проекте [rate-limiter-starter](rate-limiter-starter) пока не рассматривать  
Для удобства найденные отклонения можно перечислить прямо здесь:
1. ...
2. ...

## bad-practices-examples (Дополнительные демонстрации)

Отдельное Spring Boot-приложение с H2 и Actuator. Запускается на порту `8081`
и не влияет на зависимости и конфигурацию `workshop-app`.

### Долгий сетевой запрос внутри транзакции

В `TransactionalNetworkService` метод открывает
транзакцию, записывает событие в H2, а затем ждёт ответ по HTTP. Всё время ожидания
JDBC-соединение остаётся занято транзакцией.

Запустите приложение:

```bash
./gradlew :bad-practices-examples:bootRun
```

Затем одновременно выполните пять запросов:

```bash
for i in {1..5}; do
  curl "http://127.0.0.1:8081/api/transaction-demo/bad?delayMillis=3000" &
done
wait
```

Пул HikariCP ограничен четырьмя соединениями, а ожидание соединения —
одной секундой. Первые четыре запроса удерживают все соединения около трёх секунд,
поэтому пятый завершается ошибкой получения соединения.

Более корректно: сначала выполнить сетевой запрос без транзакции, затем передать
результат в отдельный короткий `@Transactional`-метод, который работает только с БД.

### Self-invocation обходит `@Transactional`

```bash
curl -X POST \
  "http://127.0.0.1:8081/api/bad-practices/transactions/self-invocation?marker=lesson-1"
```

`SelfInvocationTransactionService` вызывает аннотированный метод через `this`.
Spring proxy не перехватывает такой вызов, поэтому `INSERT` выполняется в режиме
auto-commit и остаётся в БД после `RuntimeException`. Поле `persisted` в ответе
равно `true`.

Более корректно: вынести транзакционный метод в отдельный Spring bean и вызывать
его через внедрённую зависимость.

### Перехваченное исключение приводит к commit

```bash
curl -X POST \
  "http://127.0.0.1:8081/api/bad-practices/transactions/swallowed-exception?marker=lesson-2"
```

`SwallowedExceptionTransactionService` ловит `RuntimeException` внутри
`@Transactional`-метода и нормально возвращает результат. Для transaction
interceptor метод завершился успешно, поэтому строка сохраняется.

Более корректно: позволить исключению выйти за границу транзакционного метода
либо явно вызвать `setRollbackOnly()`, если исключение действительно нужно поймать.

### Потерянное обновление

```bash
curl -X POST \
  "http://127.0.0.1:8081/api/bad-practices/concurrency/lost-update?parallelRequests=4"
```

Четыре транзакции одновременно читают значение `0`, затем каждая записывает `1`.
Ожидаемый результат — `4`, фактический — `1`, три обновления потеряны.

Более корректно: атомарный SQL `counter_value = counter_value + 1`,
пессимистическая блокировка или optimistic locking с повтором операции.

### Ошибка в `@Async void` не возвращается клиенту

```bash
curl -X POST \
  "http://127.0.0.1:8081/api/bad-practices/async/fire-and-forget?marker=lesson-4"

curl \
  "http://127.0.0.1:8081/api/bad-practices/async/fire-and-forget/lesson-4"
```

Первый запрос получает `202 Accepted`, после чего фоновый метод падает. Исключение
попадает только в лог, а повторный GET показывает статус `FAILED`.

Более корректно: возвращать `CompletableFuture`, хранить состояние фоновой
задачи либо настроить `AsyncUncaughtExceptionHandler` для `void`-методов.

### Небезопасная публикация Actuator

Профиль выключен по умолчанию. Запустите его явно:

```bash
SPRING_PROFILES_ACTIVE=insecure-actuator \
  ./gradlew :bad-practices-examples:bootRun

curl \
  "http://127.0.0.1:8081/actuator/env/workshop.insecure-actuator-demo.visible-value"
```

Профиль публикует все Actuator endpoints без аутентификации и отключает сокрытие
значений в `env` и `configprops`. В ответе видно только значение `DEMO_ONLY_NOT_A_REAL_SECRET`.

Более корректно: публиковать только необходимые endpoints и защищать их
сетевыми ограничениями и аутентификацией.


## rate-limiter-starter (Практика, часть 2)

Наш собственный **стартер** для ограничения числа запросов (rate-limit).  
Подключается в модуль приложения [workshop-app](workshop-app).  
При подключении стартера, можно помечать методы аннотацией [@RateLimiter](rate-limiter-starter/src/main/java/ru/yandex/workshop/ratelimiter/annotation/RateLimited.java).
Тем самым создадутся прокси для реализации АОП, вокруг которых будет срабатывать [RateLimiter](rate-limiter-starter/src/main/java/ru/yandex/workshop/ratelimiter/core/RateLimiterInMemoryImpl.java)  
Попробуйте сделать несколько запросов подряд и убедиться что рейт-лимитер не срабатывает.  
Необходимо разобраться в причинах (подсказка — конфигурации стартера и пропертей) и поправить стартер так, чтобы он работал
