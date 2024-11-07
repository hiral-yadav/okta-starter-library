package com.okta.server.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.server.api.service.AwsParameterStoreClient;
import com.okta.server.constants.AppUtility;
import com.okta.server.constants.OktaClientConfiguration;
import com.okta.server.model.User;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ssm.model.Parameter;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class OktaClient {

    private final Logger logger = LoggerFactory.getLogger(OktaClient.class);
    private final String client;
    private final AwsParameterStoreClient awsParameterStoreClient;
    private final ObjectMapper objectMapper;

    public OktaClient(
            String client,
            AwsParameterStoreClient awsParameterStoreClient) {
        this.client = client;
        this.awsParameterStoreClient = awsParameterStoreClient;
        this.objectMapper = new ObjectMapper();
    }

    private Object get(String endpoint) throws Exception {
        try {
            logger.info("Get Call for endpoint {}", endpoint);

            var request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(endpoint))
                    .header(HttpHeaderNames.AUTHORIZATION.toString(), AppUtility.getSSWSToken(client, getParameters()))
                    .build();

            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return response;

        } catch (Exception e) {
            logger.error("Exception {} occurred in get ", e.getMessage());
            throw new RuntimeException("Exception occurred in getting users from server : " + e);
        }
    }

    private Object post(String endpoint, Object data) throws Exception {
        try {
            logger.info("Post Call for endpoint {}", endpoint);

            var request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(data)))
                    .uri(URI.create(endpoint))
                    .header(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString())
                    .header(HttpHeaderNames.AUTHORIZATION.toString(), AppUtility.getSSWSToken(client, getParameters()))
                    .build();

            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (Exception e) {
            logger.error("Exception {} occurred in post ", e.getMessage());
            throw new RuntimeException("Exception occurred in getting users from server : " + e);
        }
    }

    public Object getUsers() throws Exception {
        try {
            List<Parameter> parameterList = getParameters();
            String endpoint = AppUtility.getEndpoint(client, OktaClientConfiguration.users.name(), parameterList);
            logger.info("GetUsers Call for endpoint {}", endpoint);
            return get(endpoint);
        } catch (Exception e) {
            logger.error("Exception {} occurred in getUsers ", e.getMessage());
            throw new RuntimeException("Exception occurred in retrieving users : " + e);
        }
    }

    public List<Parameter> getParameters() {
        return this.awsParameterStoreClient.getParameters(client);
    }

    public Object addUser(Object user) throws Exception {
        try {
            List<Parameter> parameterList = getParameters();
            String endpoint = AppUtility.getEndpoint(client, OktaClientConfiguration.add.name(), parameterList);
            logger.info("Add User Call for endpoint {}", endpoint);

            HashMap<String, String> params = new HashMap<>();
            params.put("activate", "true");
            params.put("provider", "false");

            var query = params.keySet().stream()
                    .map(key -> key + "=" + URLEncoder.encode(params.get(key), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));

            return post(endpoint + '?' + query, user);
        } catch (Exception e) {
            logger.error("Exception {} occurred in addUser ", e.getMessage());
            throw new RuntimeException("Exception occurred in creating new User : {}" +
                    this.objectMapper.writeValueAsString(user) + " : " + e.getMessage());
        }
    }

    public Object assignUserApplication(User userIdJson) throws Exception {
        String endpoint = AppUtility.assignAppEndpoint(client, OktaClientConfiguration.assign.name(), getParameters());
        logger.info("AssignUserToApplication Call for endpoint {}", endpoint);
        return post(endpoint, userIdJson);
    }

    public List<Parameter> testParams() {
        try {
            List<Parameter> parameterList = getParameters();
            System.out.println("Parameters => " + parameterList);

            System.out.println("Parameter Values > ");
            for (var param : parameterList) {
                System.out.println(param.name());
                System.out.println(param.value());
            }

            return parameterList;

        } catch (Exception e) {
            System.out.print("Exception occureed......" + e.getMessage());
        }

        return Collections.emptyList();
    }
}
