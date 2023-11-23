package com.ade.chat.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "In Touch API",
                description = "Корпоративный мессенджер In Touch", version = "1.0.0",
                contact = @Contact(
                        name = "Муштуков Артём",
                        email = "avmushtukov@edu.hse.ru"
                )
        )
)
public class OpenApiConfig {
}
