package cl.duoc.cloudnative.consumer.repository;

import cl.duoc.cloudnative.consumer.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findByInstructor(String instructor);

    List<Curso> findByFechaInicio(LocalDate fechaInicio);

    List<Curso> findByInstructorAndFechaInicio(
            String instructor,
            LocalDate fechaInicio
    );

}