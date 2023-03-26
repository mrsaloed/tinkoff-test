package com.example.tinkofftest.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "yandex")
public class YandexProperties {
    private static final String API_KEY_PREFIX = "Api-Key ";
    private String token;
    private String apiPath;

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public void setToken(String token) {
        this.token = API_KEY_PREFIX + token;
    }

    public String getToken() {
        return token;
    }
}
