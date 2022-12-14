## Оглавление

Данный репозиторий - это результат выполнения задания по теме микросервисов их взаимодействию.  

Во время выполнения задания использовались следующие технологии:
- spring-boot
- spring-cloud
- netflix-eureka
- netflix-zuul
- servo, servo-graphite
- zipkin

## Информация по запуску

Для запуска приложения необходимо выполнить команду `docker-compose up` из главной директории проекта

## Информация по сервисам

| Имя сервиса |      Порт      | Стартовая страница                                           | Дополнительная информация                               |
|:------------|:--------------:|:-------------------------------------------------------------|:--------------------------------------------------------|
| common      |      8081      |                                                              | Хранит конфигурации                                     |
| one         |      8082      | http://localhost:8082 <br/>http://localhost:8765/one-service | Выполняет математическую операцию вычисления факториала |
| two         |      8083      |                                                              | Переводи число в научный формат                         |
| two-api     |      8084      |                                                              | Выполняет запросы к сервису `one`                       |
| discovery   |      8761      | http://localhost:8761/eureka                                 | Сервис обнаружения микросервисов                        |
| api-gateway |      8765      |                                                              | Прокси                                                  |
| graphite    | 2003 <br/>8080 | http://localhost:8080                                        | Сервис метрик                                           |
| zipkin      |      9411      | http://localhost:9411                                        | Сервис логирования                                      |