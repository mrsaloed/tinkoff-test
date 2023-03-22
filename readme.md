# Тестовое задание "Переводчик"

Сборка Docker image
```
docker build --build-arg DB_PASSWORD=<your_password> --build-arg DB_USERNAME=<your_username> --build-arg YANDEX_FOLDER_ID=<your_folder_id> --build-arg YANDEX_TOKEN=<your_API_token> -t tinkoff/test .
```
Для генерации API-token воспользуйтесь данной [документацией](https://cloud.yandex.ru/docs/translate/operations/translate), используя _"Сервисный аккаунт"_

Запуск Docker image
```
docker run -p 8080:8080 tinkoff/test
```

Формат входного сообщения:
```
{
    "parameters": "ru-en",
    "message": "Привет Тинькофф!"
}
```

Формат выходного сообщения:
```
{
    "message": "Hi Tinkoff!"
}
```
или, если фозникает ошиька:
```
{
    "statusCode": 400,
    "error": "400 unsupported target_language_code: en1"
}
```