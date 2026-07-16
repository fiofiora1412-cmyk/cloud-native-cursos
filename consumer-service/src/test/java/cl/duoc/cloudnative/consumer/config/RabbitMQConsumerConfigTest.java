/*package cl.duoc.cloudnative.consumer.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = RabbitMQConfig.class,
        properties = {
                "spring.rabbitmq.listener.simple.auto-startup=false",

                "spring.rabbitmq.host=localhost",
                "spring.rabbitmq.port=5672",
                "spring.rabbitmq.username=guest",
                "spring.rabbitmq.password=guest"
        }
)
@Import(RabbitMQConfig.class)
@TestPropertySource(properties = {
        "app.rabbitmq.exchange=test.exchange",
        "app.rabbitmq.queue=test.queue",
        "app.rabbitmq.routing-key=test.key",
        "app.rabbitmq.dlx-exchange=test.dlx",
        "app.rabbitmq.dlq=test.dlq",
        "app.rabbitmq.dlq-routing-key=test.dlq"
})
class RabbitMQConsumerConfigTest {

    @Autowired
    private SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory;

    @Test
    void shouldDisableRequeue() {

        Object defaultRequeueRejected =
                ReflectionTestUtils.getField(
                        rabbitListenerContainerFactory,
                        "defaultRequeueRejected"
                );

        assertThat(defaultRequeueRejected)
                .isEqualTo(Boolean.FALSE);
    }

    @Test
    void shouldConfigureRetryInterceptor() {

        Object adviceChain =
                ReflectionTestUtils.getField(
                        rabbitListenerContainerFactory,
                        "adviceChain"
                );

        assertThat(adviceChain).isNotNull();
        assertThat(adviceChain.getClass().isArray()).isTrue();
        assertThat((Object[]) adviceChain).isNotEmpty();
    }
}*/