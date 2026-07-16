package cl.duoc.cloudnative.consumer.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.rabbitmq.listener.simple.auto-startup=false")
class RabbitMQConsumerConfigTest {

    @Autowired
    private SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory;

    @Test
    void shouldDisableRequeue() {
        Object defaultRequeueRejected = ReflectionTestUtils.getField(
                rabbitListenerContainerFactory,
                "defaultRequeueRejected"
        );

        assertThat(defaultRequeueRejected).isEqualTo(Boolean.FALSE);
    }

    @Test
    void shouldConfigureRetryInterceptor() {
        Object adviceChain = ReflectionTestUtils.getField(rabbitListenerContainerFactory, "adviceChain");

        assertThat(adviceChain).isNotNull();
        assertThat(adviceChain.getClass().isArray()).isTrue();
        assertThat((Object[]) adviceChain).isNotEmpty();
    }
}
