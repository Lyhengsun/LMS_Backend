package com.norton.lms_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.cache.annotation.EnableCaching;

@OpenAPIDefinition(info = @Info(title = "LMS SYSTEM API", version = "v1", description = "This is description"), servers = {
        @Server(url = "/", description = "Default Server URL") })
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", in = SecuritySchemeIn.HEADER)
@SpringBootApplication
@EnableCaching
public class LmsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmsBackendApplication.class, args);
	}

}
