package cl.duoc.cloudnative.consumer.controller;

import cl.duoc.cloudnative.consumer.dto.request.ActualizarCursoRequest;
import cl.duoc.cloudnative.consumer.model.Curso;
import cl.duoc.cloudnative.consumer.service.CursoConsumerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoConsumerService cursoConsumerService;

    public CursoController(CursoConsumerService cursoConsumerService) {
        this.cursoConsumerService = cursoConsumerService;
    }

    @GetMapping
    public List<Curso> obtenerTodos() {
        return cursoConsumerService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Curso obtenerPorId(@PathVariable Long id) {
        return cursoConsumerService.obtenerPorId(id);
    }

    @GetMapping("/instructor/{instructor}")
    public List<Curso> obtenerPorInstructor(
            @PathVariable String instructor) {

        return cursoConsumerService.obtenerPorInstructor(instructor);
    }

    @GetMapping("/fecha/{fechaInicio}")
    public List<Curso> obtenerPorFecha(
            @PathVariable LocalDate fechaInicio) {

        return cursoConsumerService.obtenerPorFecha(fechaInicio);
    }

    @GetMapping("/buscar")
    public List<Curso> buscar(

            @RequestParam String instructor,

            @RequestParam LocalDate fechaInicio

    ) {

        return cursoConsumerService.obtenerPorInstructorYFecha(
                instructor,
                fechaInicio
        );
    }

    @PutMapping("/{id}")
    public Curso actualizarCurso(

            @PathVariable Long id,

            @Valid @RequestBody ActualizarCursoRequest request

    ) {

        return cursoConsumerService.actualizarCurso(id, request);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCurso(

            @PathVariable Long id

    ) {

        cursoConsumerService.eliminarCurso(id);

        return ResponseEntity.noContent().build();

    }

    @PostMapping("/{id}/material")
    public Curso subirMaterial(
            @PathVariable Long id,
            @RequestParam("archivo") MultipartFile archivo
    ) throws IOException {

        return cursoConsumerService.subirMaterial(id, archivo);

    }

    @GetMapping("/{id}/material")
    public ResponseEntity<String> obtenerMaterial(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                cursoConsumerService.obtenerUrlMaterial(id)
        );

    }
}