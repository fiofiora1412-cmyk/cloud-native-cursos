package cl.duoc.cloudnative.producer.service;

import cl.duoc.cloudnative.producer.shared.CursoCreadoEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CursoProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    public CursoProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarCursoCreado(CursoCreadoEvent event) {

        rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                event
        );

    }

}