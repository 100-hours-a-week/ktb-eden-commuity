package com.example.restapi_subject.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "KTB-3-eden-community",
                version = "v1",
                description = "KTB 3rd eden's community REST DOCS."
        ),
        security = @SecurityRequirement(name = "Authorization")
)

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME_ACCESS = "Authorization";
    private static final String SECURITY_SCHEME_NAME_REFRESH = "refresh";


    @Bean
    public OpenAPI swaggerApi() {
        Components components = new Components()
                .addSecuritySchemes(SwaggerConfig.SECURITY_SCHEME_NAME_ACCESS, new SecurityScheme()
                        .name(SwaggerConfig.SECURITY_SCHEME_NAME_ACCESS)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .description("JWT 토큰 정보"))
                .addSecuritySchemes(SECURITY_SCHEME_NAME_REFRESH, new SecurityScheme()
                        .name(SECURITY_SCHEME_NAME_REFRESH)
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .description("refresh 토큰 정보"));

        return new OpenAPI().components(components);
    }
    @Bean
    public GroupedOpenApi authGroup() {
        return GroupedOpenApi.builder()
                .group("Auth")     // 그룹 이름
                .pathsToMatch("/api/v1/auth/**")  // 그룹에 속하는 경로
                .build();
    }
    @Bean
    public GroupedOpenApi userGroup() {
        return GroupedOpenApi.builder()
                .group("Users")     // 그룹 이름
                .pathsToMatch("/api/v1/users/**")  // 그룹에 속하는 경로
                .build();
    }
    @Bean
    public GroupedOpenApi boardGroup() {
        return GroupedOpenApi.builder()
                .group("Boards")     // 그룹 이름
                .pathsToMatch("/api/v1/boards/**")  // 그룹에 속하는 경로
                .build();
    }
    // Json SNAKE_CASE로 매핑
    @Bean
    public ModelResolver modelResolver(ObjectMapperProvider objectMapperProvider) {
        ObjectMapper swaggerMapper = objectMapperProvider.jsonMapper().copy();
        swaggerMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return new ModelResolver(swaggerMapper);
    }
}