package cl.duoc.cloudnative.consumer.service;

import cl.duoc.cloudnative.consumer.shared.CursoCreadoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CursoListener {

    private static final Logger log =
            LoggerFactory.getLogger(CursoListener.class);

    private final CursoConsumerService guiaConsumerService;

    public CursoListener(CursoConsumerService guiaConsumerService) {
        this.guiaConsumerService = guiaConsumerService;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void recibirCurso(CursoCreadoEvent event) {

        if ("ERROR".equalsIgnoreCase(event.codigo())) {
            throw new RuntimeException("Error simulado para probar la DLQ.");
        }

        log.info(
                "Curso recibido. Código {} - {}",
                event.codigo(),
                event.nombre()
        );

        guiaConsumerService.guardarCurso(event);

        log.info(
                "Curso {} almacenado correctamente.",
                event.codigo()
        );
    }
}