package gabriel.tiziano.trade_platform;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatePasswordTest {
    @ParameterizedTest(name = "senha válida: {0}")
    @ValueSource(strings = {
            "Teste123",
            "teste123T",
            "123testeT",
            "Teste@123",
            "SenhaMuitoLonga2026"
    })
    void deveValidarUmaSenhaValida(String password) {
        assertThat(ValidatePassword.validate(password)).isTrue();
    }

    @ParameterizedTest(name = "senha inválida: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {
            "Teste12",
            "teste123",
            "TESTE123",
            "TesteTeste"
    })
    void naoDeveValidarUmaSenhaInvalida(String password) {
        assertThat(ValidatePassword.validate(password)).isFalse();
    }
}
