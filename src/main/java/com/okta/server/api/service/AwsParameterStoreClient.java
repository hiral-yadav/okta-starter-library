package com.okta.server.api.service;

import com.okta.server.constants.OktaClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParametersRequest;
import software.amazon.awssdk.services.ssm.model.GetParametersResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AwsParameterStoreClient {

    private final SsmClient ssmClient = SsmClient.builder()
            .region(Region.US_EAST_2) // Change to your desired region
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    private final Logger logger = LoggerFactory.getLogger(AwsParameterStoreClient.class);

    public List<Parameter> getParameters(String client) {
        logger.info("Getting parameters from parameter store for client {}", client);
        try {
            List<String> parameterList = Arrays
                    .stream(OktaClientConfiguration.values())
                    .map(param -> String.join(".", client, param.name()))
                    .collect(Collectors.toList());

            GetParametersRequest parametersRequest = GetParametersRequest.builder()
                    .names(parameterList)
                    .withDecryption(false) // Set to true if parameters are encrypted
                    .build();

            GetParametersResponse parametersResponse = ssmClient.getParameters(parametersRequest);
            logger.info("Got Parameters {} for client {} ", parametersResponse, client);

            return parametersResponse.parameters();
        } catch (Exception e) {
            logger.error("Exception {} occurred in fetching parameters for client {} ", e.getMessage(), client);
        }
        return Collections.emptyList();
    }
}
