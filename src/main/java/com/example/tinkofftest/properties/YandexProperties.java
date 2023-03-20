package com.example.tinkofftest.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "yandex")
public class YandexProperties {
    private String folderId;
    private String token;
    private String apiPath;

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFolderId() {
        return folderId;
    }

    public String getToken() {
        return token;
    }
}
