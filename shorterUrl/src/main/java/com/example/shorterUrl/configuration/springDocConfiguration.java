package com.example.shorterUrl.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class springDocConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SHORTER URL API REST")
                        .description("API Rest da aplicação shortUrl, contendo as funcionalidades de CRUD")
                        .contact(new Contact()
                                .name("Time Backend")
                                .email("backend@email.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://projectShortenedUrlJava/api/licenca")));
    }
}
