package com.okta.server.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("spring")
public record ApplicationConfigurationProperties(
        String appName,
        @JsonProperty("main.allow-bean-definition-overriding")
        @DefaultValue("TRUE")
        Boolean allowBeanDefinitionOverride
) {
}
