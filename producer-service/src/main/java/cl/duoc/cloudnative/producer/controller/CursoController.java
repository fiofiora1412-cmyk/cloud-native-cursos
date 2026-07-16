package cl.duoc.cloudnative.producer.controller;

import cl.duoc.cloudnative.producer.dto.request.CrearCursoRequest;
import cl.duoc.cloudnative.producer.dto.response.CrearCursoResponse;
import cl.duoc.cloudnative.producer.service.CursoProducerService;
import cl.duoc.cloudnative.producer.shared.CursoCreadoEvent;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoProducerService cursoProducerService;

    public CursoController(CursoProducerService cursoProducerService) {
        this.cursoProducerService = cursoProducerService;
    }

    @PostMapping
    public ResponseEntity<CrearCursoResponse> crearCurso(

            @Valid @RequestBody CrearCursoRequest request,

            JwtAuthenticationToken authentication

    ) {

        Jwt jwt = authentication.getToken();

        UUID eventId = UUID.randomUUID();

        CursoCreadoEvent event = new CursoCreadoEvent(

        eventId,

        request.codigo(),

        request.nombre(),

        request.descripcion(),

        request.instructor(),

        request.fechaInicio(),

        request.fechaFin(),

        Instant.now()

    );

        cursoProducerService.publicarCursoCreado(event);

        return ResponseEntity.accepted().body(

            new CrearCursoResponse(

                    "Curso enviado correctamente a RabbitMQ.",

                    request.codigo(),

                    eventId

            )

        );

    }

}