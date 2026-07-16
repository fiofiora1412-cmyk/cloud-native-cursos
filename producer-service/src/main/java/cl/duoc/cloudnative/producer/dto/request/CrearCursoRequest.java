package cl.duoc.cloudnative.producer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CrearCursoRequest(

        @NotBlank(message = "El código es obligatorio")
        @Size(max = 20)
        String codigo,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150)
        String nombre,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 500)
        String descripcion,

        @NotBlank(message = "El instructor es obligatorio")
        @Size(max = 100)
        String instructor,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate fechaFin

) {
}