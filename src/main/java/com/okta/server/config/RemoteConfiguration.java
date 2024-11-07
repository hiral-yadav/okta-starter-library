package com.okta.server.config;

import com.okta.server.api.client.OktaClient;
import com.okta.server.api.service.AwsParameterStoreClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

@AutoConfiguration
@EnableConfigurationProperties({OktaConfigurationProperties.class,
        SMTPConfigurationProperties.class,
        ApplicationConfigurationProperties.class})
public class RemoteConfiguration {

    private final OktaConfigurationProperties oktaConfigurationProperties;
    private final SMTPConfigurationProperties smtpConfigurationProperties;
    private final ApplicationConfigurationProperties applicationConfigurationProperties;

    private final Logger logger = LoggerFactory.getLogger(RemoteConfiguration.class);

    @Value("${spring.appName}")
    private String clientName;

    public RemoteConfiguration(OktaConfigurationProperties oktaConfigurationProperties,
                               SMTPConfigurationProperties smtpConfigurationProperties,
                               ApplicationConfigurationProperties applicationConfigurationProperties) {
        this.oktaConfigurationProperties = oktaConfigurationProperties;
        this.smtpConfigurationProperties = smtpConfigurationProperties;
        this.applicationConfigurationProperties = applicationConfigurationProperties;
    }

    @Bean
    public AwsParameterStoreClient getAwsParameterStoreClient() {
        return new AwsParameterStoreClient();
    }

    @Bean
    public OktaClient getOktaClient() {
        logger.info("CLient is {}", clientName);
        return new OktaClient(clientName, getAwsParameterStoreClient());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    //@Bean
    public AccessDeniedHandler getAccessDeniedHandler() {
        return (HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("dfert45dfgbdf345345 fdhfghtfrhyryfgh");
        };
    }

}
