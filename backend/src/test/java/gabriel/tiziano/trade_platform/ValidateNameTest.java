package gabriel.tiziano.trade_platform;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ValidateNameTest {

    @ParameterizedTest(name = "nome válido: {0}")
    @ValueSource(strings = {
            "Gabriel Tiziano",
            "Guilherme Almeida Gomes",
            "José da Silva",
            "Conceição Souza"
    })
    void deveValidarUmNomeValido(String name) {
        assertThat(ValidateName.validate(name)).isTrue();
    }

    @ParameterizedTest(name = "nome inválido: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {
            "Gabriel",
            "Gabriel 123",
            "   ",
            "Gabriel@ Tiziano",
            "Gabriel  Tiziano",
            " Gabriel Tiziano"
    })
    void naoDeveValidarUmNomeInvalido(String name) {
        assertThat(ValidateName.validate(name)).isFalse();
    }
}