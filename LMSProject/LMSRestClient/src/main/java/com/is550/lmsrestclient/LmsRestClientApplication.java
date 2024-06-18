package com.is550.lmsrestclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.is550.lmsrestclient.client.LMSClient;
import com.is550.lmsrestclient.exceptions.LMSClientExceptionHandler;
import com.is550.lmsrestclient.variables.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Scanner;


@SpringBootApplication
public class LmsRestClientApplication {

	private static final LMSClient lmsClient = new LMSClient();

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(LmsRestClientApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new Jackson2HalModule());

		MappingJackson2HttpMessageConverter halMessageConverter = new MappingJackson2HttpMessageConverter();
		halMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
		halMessageConverter.setObjectMapper(objectMapper);

		return builder.messageConverters(halMessageConverter).build();
	}


	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) {
		return args -> {
			restTemplate.setErrorHandler(new LMSClientExceptionHandler());
			Scanner scanner = new Scanner(System.in);

			System.out.println("\nWelcome LMS System.\nPlease first login the system.\n");

			System.out.println("Enter email:");
			String email = scanner.nextLine();
			System.out.println("Enter password:");
			String password = scanner.nextLine();

			LoginRequestRest loginRequest = new LoginRequestRest();
			loginRequest.setEmail(email);
			loginRequest.setPassword(password);

			while(true)
			{
				try{
					lmsClient.login(restTemplate, loginRequest);
				}catch (Exception e){
					System.out.println("Enter email:");
					email = scanner.nextLine();
					System.out.println("Enter password:");
					password = scanner.nextLine();

					loginRequest = new LoginRequestRest();
					loginRequest.setEmail(email);
					loginRequest.setPassword(password);
				}
			}
		};
	}
}
