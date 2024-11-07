package com.okta.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("okta.oauth2")
public record OktaConfigurationProperties(
        String issuer
) {}

