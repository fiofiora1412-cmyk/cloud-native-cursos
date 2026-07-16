package cl.duoc.cloudnative.consumer.service;

import cl.duoc.cloudnative.consumer.dto.request.ActualizarCursoRequest;
import cl.duoc.cloudnative.consumer.model.Curso;
import cl.duoc.cloudnative.consumer.model.EstadoCurso;
import cl.duoc.cloudnative.consumer.repository.CursoRepository;
import cl.duoc.cloudnative.consumer.shared.CursoCreadoEvent;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;


import org.springframework.stereotype.Service;

@Service
public class CursoConsumerService {

    private final CursoRepository cursoRepository;
    private final CursoStorageService cursoStorageService;

    public CursoConsumerService(
            CursoRepository cursoRepository,
            CursoStorageService cursoStorageService
    ) {
        this.cursoRepository = cursoRepository;
        this.cursoStorageService = cursoStorageService;
    }

    public void guardarCurso(CursoCreadoEvent event) {

        Curso curso = new Curso();

        curso.setCodigo(event.codigo());
        curso.setNombre(event.nombre());
        curso.setDescripcion(event.descripcion());
        curso.setInstructor(event.instructor());
        curso.setFechaInicio(event.fechaInicio());
        curso.setFechaFin(event.fechaFin());

        curso.setEstado(EstadoCurso.CREADO);

        cursoRepository.save(curso);
    }

    public List<Curso> obtenerTodos() {
        return cursoRepository.findAll();
    }

    public Curso obtenerPorId(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Curso no encontrado."));
    }

    public List<Curso> obtenerPorInstructor(String instructor) {
        return cursoRepository.findByInstructor(instructor);
    }

    public List<Curso> obtenerPorFecha(LocalDate fechaInicio) {
        return cursoRepository.findByFechaInicio(fechaInicio);
    }

    public List<Curso> obtenerPorInstructorYFecha(
            String instructor,
            LocalDate fechaInicio
    ) {
        return cursoRepository.findByInstructorAndFechaInicio(
                instructor,
                fechaInicio
        );
    }

    public Curso actualizarCurso(
            Long id,
            ActualizarCursoRequest request
    ) {

        Curso curso = obtenerPorId(id);

        curso.setCodigo(request.codigo());
        curso.setNombre(request.nombre());
        curso.setDescripcion(request.descripcion());
        curso.setInstructor(request.instructor());
        curso.setFechaInicio(request.fechaInicio());
        curso.setFechaFin(request.fechaFin());

        return cursoRepository.save(curso);
    }

    public void eliminarCurso(Long id) {

        Curso curso = obtenerPorId(id);

        cursoRepository.delete(curso);

    }

    public Curso subirMaterial(
            Long id,
            MultipartFile archivo
    ) throws IOException {

        Curso curso = obtenerPorId(id);

        Path temporal = Files.createTempFile(
                "curso-",
                archivo.getOriginalFilename()
        );

        Files.copy(
                archivo.getInputStream(),
                temporal,
                StandardCopyOption.REPLACE_EXISTING
        );

        String url = cursoStorageService.subirArchivo(
                temporal,
                archivo.getOriginalFilename()
        );

        curso.setUrlMaterial(url);

        return cursoRepository.save(curso);
    }

    public String obtenerUrlMaterial(Long id) {

        Curso curso = obtenerPorId(id);

        return curso.getUrlMaterial();

    }
    
}