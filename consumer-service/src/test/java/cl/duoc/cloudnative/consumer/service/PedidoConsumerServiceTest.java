/*package cl.duoc.cloudnative.consumer.service;

import cl.duoc.cloudnative.shared.events.GuiaCreadaEvent;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PedidoConsumerServiceTest {

    private final PedidoConsumerService service = new PedidoConsumerService();

    @Test
    void shouldNotFailForErrorProductWhenSimulationIsDisabled() {
        ReflectionTestUtils.setField(service, "simulateErrorForProduct", false);

        assertThatCode(() -> service.procesarPedido(errorProductEvent())).doesNotThrowAnyException();
    }

    @Test
    void shouldFailForErrorProductWhenSimulationIsEnabled() {
        ReflectionTestUtils.setField(service, "simulateErrorForProduct", true);

        assertThatThrownBy(() -> service.procesarPedido(errorProductEvent()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Error simulado para probar reintentos y Dead Letter Queue");
    }

    private GuiaCreadaEvent errorProductEvent() {
        return new GuiaCreadaEvent(
                "evt-1",
                "ped-1",
                "ERROR",
                1,
                "user-1",
                "user@example.com",
                List.of("ROLE_USER"),
                "issuer",
                "2026-01-01T00:00:00Z"
        );
    }
}
*/