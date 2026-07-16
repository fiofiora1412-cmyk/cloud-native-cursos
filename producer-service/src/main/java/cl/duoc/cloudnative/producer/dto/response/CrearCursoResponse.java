package cl.duoc.cloudnative.producer.dto.response;

import java.util.UUID;

public record CrearCursoResponse(

        String mensaje,

        String codigo,

        UUID eventId

) {
}