# Курсовой проект по модулю «Автоматизация тестирования» для профессии «Инженер по тестированию»

В рамках курсового проекта требовалось автоматизировать тестирование комплексного сервиса покупки тура,
взаимодействующего с СУБД и API Банка.

[Ссылка на задание](https://github.com/netology-code/aqa-qamid-diplom.git).

Сервис обрабатывает только специальные номера карт, которые даны для тестирования:

* APPROVED карта — `1111 2222 3333 4444`;
* DECLINED карта — `5555 6666 7777 8888`.

## Тестовая документация:

1. [План автоматизации тестирования](documentation/Plan.md);
1. [Отчёт по итогам тестирования](documentation/Report.md);
1. [Отчет по итогам автоматизации](documentation/Summary.md)

## Шаги для воспроизведения:

### Подготовительный этап

1. Установить и запустить IntelliJ IDEA;
2. Установить и запустить Docker Desktop;
3. Склонировать репозиторий с Github командой через терминал:
```
git clone git@github.com:OAOblat/course_project.git
```
4. Открыть проект в IntelliJ IDEA.
   
### Запуск тестового приложения 

1. Запустить в контейнерах две базы данных — Mysql и Postgres командой в терминале
```
docker-compose up
```
| Mysql                                                                         | Postgresql                                                                                                                                                               |
|-------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 2. В новой вкладке терминала запустить тестируемое приложение:                | 2. В новой вкладке терминала запустить тестируемое приложение:                                                                                                           |
| ``` java -jar artifacts/aqa-shop.jar ```                                      | ```java -Dspring.datasource.url=jdbc:postgresql://localhost:5432/app -Dspring.datasource.username=app -Dspring.datasource.password=pass -jar ./artifacts/aqa-shop.jar``` |
| 3. Убедиться в готовности системы. Приложение должно быть доступно по адресу: | 3. Убедиться в готовности системы. Приложение должно быть доступно по адресу:                                                                                            |
| ``` http://localhost:8080/ ```                                                | ``` http://localhost:8080/ ```                                                                                                                                           |
### Запуск тестов

| Mysql                                                                         | Postgresql                                                                                                                                                               |
|-------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 4. В новой вкладке терминала запустить тесты:                                 | 4. В новой вкладке терминала запустить тесты:                                                                                                                            |
| ``` ./gradlew clean test ```                                                  | ``` ./gradlew test -Dspring.datasource.url=jdbc:postgresql://localhost:5432/app -Dspring.datasource.username=app -Dspring.datasource.password=pass ```                   |      
### Перезапуск тестов и приложения

Для остановки приложения в окне терминала нужно ввести команду `Ctrl+С` и повторить необходимые действия из предыдущих
разделов.

## Формирование отчёта о тестировании

Предусмотрено формирование отчётности через Allure. Для этого в новой вкладке терминала вводим команду

```
./gradlew allureServe
```
