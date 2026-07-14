package com.examly.springapp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI digitalLockerOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Digital Locker System")
                .description("Digital Locker System API")
                .version("1.0.0")
                .contact(new io.swagger.v3.oas.models.info.Contact()
                    .name("Soorya S")
                    .email("soorya@gmail.com"))
                .license(new io.swagger.v3.oas.models.info.License()
                    .name("Apache 2.0")
                    .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}