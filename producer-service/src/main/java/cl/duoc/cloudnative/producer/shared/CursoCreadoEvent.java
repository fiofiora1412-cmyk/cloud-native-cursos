package cl.duoc.cloudnative.producer.shared;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CursoCreadoEvent(

        UUID eventId,

        String codigo,

        String nombre,

        String descripcion,

        String instructor,

        LocalDate fechaInicio,

        LocalDate fechaFin,

        Instant fechaEvento

) {
}