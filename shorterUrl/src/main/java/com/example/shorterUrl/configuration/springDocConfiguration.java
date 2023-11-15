package com.example.shorterUrl.configuration;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class springDocConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("Shorter Url API")
                        .description("API Rest da aplicação shorter url, contendo as funcionalidades de CRUD de um Shorter Url")
                        .contact(new Contact()
                                .name("Time Backend")
                                .email("haroldomorais92@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://shorterUrl/api/licenca")));
    }


//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("SHORTER URL API REST")
//                        .description("API Rest da aplicação shortUrl, contendo as funcionalidades de CRUD")
//                        .contact(new Contact()
//                                .name("Time Backend")
//                                .email("backend@email.com"))
//                        .license(new License()
//                                .name("Apache 2.0")
//                                .url("http://projectShortenedUrlJava/api/licenca")));
//    }
}
