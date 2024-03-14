![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

## In Touch - Корпоративный мессенджер

Серверное приложение, предоставляющее REST API для мобильного приложения и клиента под Windows

- [Oписание API (swagger)](http://195.133.196.67:8080/swagger-ui/index.html)
- [Docker hub](https://hub.docker.com/repository/docker/zzzzeonnnnn/chat_backend/general)

Ссылки на связанные репозитрии:
* [Windows desktop client](https://github.com/Zeonx9/chat-client-demo)
* [Android mobile client](https://github.com/Zeonx9/in-touch-mobile-app)


## Использованные библиотеки

Проект написан на Java. C использованием Spring boot 
* Spring boot - внедрение зависимостей, построение MVC архитектуры.
* Spring data JPA - маппинг сущностей базы данных Postgres в объекты Java
* Flyway - миграции базы данных
* Jackson - для сериализации и десериализации объектов передаваемых по сети в формате JSON
* Spring Websocket + Spring Messaging - Websocket Подключение и обмен сообщениям по протоколу STOMP


## Сборка и развертывание

Приложение собирается с помощью _maven_ внутри docker-контейнера. С помощью docker-compose запускается контейнер
с базой данных Postgres и контейнер с серверным приложением. 

Добавлен пайплайн github-actions для автоматического развертывания тестовой версии `dev-workflow.yml` и основной версии
`master-workflow.yml`
