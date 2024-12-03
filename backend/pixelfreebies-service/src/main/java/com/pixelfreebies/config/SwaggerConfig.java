package com.pixelfreebies.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                description = "Free PNG Image downloader platform for downloading images seamlessly.",
                license = @License(
                        name = "Some_Licence",
                        url = "some_url"
                ),
                title = "Free PNG Downloader",
                version = "0.1",
                termsOfService = "You are free to download and use all our free Images!"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "some_prod",
                        url = "some_prod_url"
                )
        },
        security = {
                @SecurityRequirement(name = "BearerToken")
        }
)
@SecurityScheme(
        name = "BearerToken",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
}
