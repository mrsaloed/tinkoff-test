# Тестовое задание "Переводчик"

Сборка Docker image
```
docker build --build-arg DB_PASSWORD=<your_password> --build-arg DB_USERNAME=<your_username> --build-arg YANDEX_FOLDER_ID=<your_folder_id> --build-arg YANDEX_TOKEN=<your_token> -t tinkoff/test .
```
Запуск Docker image
```
docker run -p 8080:8080 tinkoff/test
```