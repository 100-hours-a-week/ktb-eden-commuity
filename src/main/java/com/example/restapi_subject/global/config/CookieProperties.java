package com.example.restapi_subject.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "custom.cookie")
public class CookieProperties {
    private boolean secure;
    private String sameSite;
    private String path;
    private int maxAgeDays;
}