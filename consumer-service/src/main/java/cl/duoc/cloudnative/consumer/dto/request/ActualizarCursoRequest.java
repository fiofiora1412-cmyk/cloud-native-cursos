package cl.duoc.cloudnative.consumer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ActualizarCursoRequest(

        @NotBlank
        @Size(max = 20)
        String codigo,

        @NotBlank
        @Size(max = 150)
        String nombre,

        @NotBlank
        @Size(max = 500)
        String descripcion,

        @NotBlank
        @Size(max = 100)
        String instructor,

        @NotNull
        LocalDate fechaInicio,

        @NotNull
        LocalDate fechaFin

) {
}