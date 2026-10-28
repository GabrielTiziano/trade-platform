package gabriel.tiziano.trade_platform;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidateCpfTest {

    @Test
    void deveValidarUmCpfValido() {
        assertThat(ValidateCpf.validate("974.563.215-58")).isTrue();
    }
}