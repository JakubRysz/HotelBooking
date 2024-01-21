package com.project.hotelBooking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.stereotype.Component;

@Component
public class OpenApiConfig implements OpenApiCustomiser {
    @Override
    public void customise(OpenAPI openApi) {
        var securitySchemeName = "bearerAuth";
        openApi.getComponents().addSecuritySchemes(securitySchemeName, new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"));
        openApi.addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}