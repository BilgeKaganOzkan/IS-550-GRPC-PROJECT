package com.is550.lmsrestclient.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class LMSClientExceptionHandler extends DefaultResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();;
        return (
                statusCode.is4xxClientError() ||
                        statusCode.is5xxServerError()
        );
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        String responseBody = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        if (statusCode.is4xxClientError()) {
            System.out.println("\nError: " + statusCode + " Response Body: " + responseBody);
        } else if (statusCode.is5xxServerError()) {
            System.out.println("\nError: " + statusCode + " Response Body: " + responseBody);
        }
    }
}