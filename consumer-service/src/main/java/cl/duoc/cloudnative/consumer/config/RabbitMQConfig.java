package cl.duoc.cloudnative.consumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${app.rabbitmq.dlx-exchange}")
    private String dlxExchangeName;

    @Value("${app.rabbitmq.dlq}")
    private String dlqName;

    @Value("${app.rabbitmq.dlq-routing-key}")
    private String dlqRoutingKey;

    @Bean
    Queue pedidosQueue() {
        log.info("Creando Queue {}", queueName);

        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }

    @Bean
    DirectExchange pedidosExchange() {
        log.info("Creando Exchange {}", exchangeName);
        return new DirectExchange(exchangeName);
    }

    @Bean
    Binding pedidosBinding() {
        log.info("Creando Binding para Queue {}", queueName);
        return BindingBuilder.bind(pedidosQueue()).to(pedidosExchange()).with(routingKey);
    }

    @Bean
    Queue deadLetterQueue() {
        log.info("Creando Queue de Letras Muertas {}", dlqName);
        return QueueBuilder.durable(dlqName).build();
    }

    @Bean
    DirectExchange deadLetterExchange() {
        log.info("Creando Exchange de Letras Muertas {}", dlxExchangeName);
        return new DirectExchange(dlxExchangeName);
    }

    @Bean
    Binding deadLetterBinding() {
        log.info("Creando Binding para Queue de Letras Muertas {}", dlqName);
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(dlqRoutingKey);
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter("cl.duoc.cloudnative.consumer.shared");
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            final SimpleRabbitListenerContainerFactoryConfigurer configurer,
            final ConnectionFactory connectionFactory,
            final MessageConverter jsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}
