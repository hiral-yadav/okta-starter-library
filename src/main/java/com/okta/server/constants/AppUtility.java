package com.okta.server.constants;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ssm.model.Parameter;

import java.text.Format;
import java.util.Formatter;
import java.util.List;

public class AppUtility {

    private static final Logger logger = LoggerFactory.getLogger(AppUtility.class);

    public static String getParameter(String clientName, String paramName, List<Parameter> parameterList) {
        String _paramName = String.join(".", clientName, paramName);
        String parameterValue = parameterList.stream().filter(parameter -> _paramName.equalsIgnoreCase(parameter.name()))
                .map(Parameter::value).findFirst().orElse(Strings.EMPTY);
        logger.info("Value for parameter {} is {}", paramName, parameterValue);
        return parameterValue;
    }

    public static String getSSWSToken(String clientName, List<Parameter> parameterList) {
        return getParameter(clientName, OktaClientConfiguration.ssws.name(), parameterList);
    }

    public static String getEndpoint(String clientName, String endpoint, List<Parameter> parameterList) {
        String domainName = getParameter(clientName, OktaClientConfiguration.domain.name(), parameterList);
        String _endpoint = getParameter(clientName, endpoint, parameterList);
        logger.info("Value for domainName {}", domainName);
        logger.info("Value for endpoint {}", _endpoint);
        return domainName.concat(_endpoint);
    }

    public static String assignAppEndpoint(String clientName, String endpoint, List<Parameter> parameterList) {
        String _appId = getParameter(clientName, OktaClientConfiguration.appId.name(), parameterList);
        String _endpoint = getParameter(clientName, endpoint, parameterList);
        String appId_endpoint = String.format(_endpoint, _appId);
        String domainName = getParameter(clientName, OktaClientConfiguration.domain.name(), parameterList);
        logger.info("Value for domainName {}", domainName);
        logger.info("Value for endpoint {}", appId_endpoint);
        return domainName.concat(appId_endpoint);
    }

}
