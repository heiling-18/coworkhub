package com.coworkhub.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/** Módulo 06: información de la API, botón Authorize (JWT), grupos y respuestas de error. */
@Configuration
public class ConfiguracionOpenApi {

    private static final String ESQUEMA_JWT = "bearerAuth";

    @Bean
    public OpenAPI coworkhubOpenApi() {
        return new OpenAPI()
                .info(new Info().title("CoWorkHub API").version("1.0.0")
                        .description("API REST para gestionar una red de coworking: sedes, salas, miembros y reservas. "
                                + "Para probar: 1) POST /auth/login  2) copie el token  3) pulse Authorize y péguelo."))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_JWT))
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_JWT, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))
                        .addSchemas("ErrorRespuesta", new ObjectSchema()
                                .addProperty("error", new StringSchema().example("La sala ya está reservada en ese horario"))
                                .addProperty("codigo", new StringSchema().example("RN-01"))
                                .addProperty("timestamp", new StringSchema().example("2030-06-03T13:00:00Z"))));
    }

    /** Agrega a todas las operaciones las respuestas de error con el cuerpo único. */
    @Bean
    public GlobalOpenApiCustomizer respuestasDeError() {
        Map<String, String> errores = new LinkedHashMap<>();
        errores.put("400", "Datos inválidos");
        errores.put("401", "Sin token, o token alterado o vencido");
        errores.put("403", "Sin permiso para esta acción");
        errores.put("404", "El recurso no existe");
        errores.put("409", "Viola una regla de negocio (RN-xx) o es un duplicado");
        return openApi -> openApi.getPaths().values().forEach(path -> path.readOperations().forEach(op ->
                errores.forEach((codigo, descripcion) -> {
                    if (op.getResponses() != null && !op.getResponses().containsKey(codigo)) {
                        Schema<?> ref = new Schema<>().$ref("#/components/schemas/ErrorRespuesta");
                        op.getResponses().addApiResponse(codigo, new ApiResponse().description(descripcion)
                                .content(new Content().addMediaType("application/json", new MediaType().schema(ref))));
                    }
                })));
    }

    @Bean
    public GroupedOpenApi grupoTodo() {
        return GroupedOpenApi.builder().group("0-todo").pathsToMatch("/**").build();
    }

    @Bean
    public GroupedOpenApi grupoAutenticacion() {
        return GroupedOpenApi.builder().group("1-autenticacion").pathsToMatch("/auth/**").build();
    }

    @Bean
    public GroupedOpenApi grupoCatalogos() {
        return GroupedOpenApi.builder().group("2-catalogos").pathsToMatch("/api/sedes/**", "/api/salas/**",
                "/api/equipamientos/**", "/api/servicios-adicionales/**", "/api/planes/**").build();
    }

    @Bean
    public GroupedOpenApi grupoMiembros() {
        return GroupedOpenApi.builder().group("3-miembros").pathsToMatch("/api/miembros/**").build();
    }

    @Bean
    public GroupedOpenApi grupoReservas() {
        return GroupedOpenApi.builder().group("4-reservas").pathsToMatch("/api/reservas/**", "/api/disponibilidad/**").build();
    }
}
