package gabriel.tiziano.trade_platform;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ValidateEmailTest {

    @ParameterizedTest(name = "email válido: {0}")
    @ValueSource(strings = {
            "gabrieltiziano@gmail.com",
            "gabriel.tiziano@empresa.com.br",
            "gabriel+teste@gmail.com",
            "gabriel_tiziano@gmail.com",
            "gabriel@mail.empresa.com.br"
    })
    void deveValidarUmEmailValido(String email) {
        assertThat(ValidateEmail.validate(email)).isTrue();
    }

    @ParameterizedTest(name = "email inválido: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {
            "gabrieltiziano.gmail.com",
            "@gmail.com",
            "gabrieltiziano@",
            "gabrieltiziano@.com",
            "gabrieltiziano@gmail",
            "gabrieltiziano@gmail.org",
            "gabrieltiziano@gmail.com.ar",
            "gabriel tiziano@gmail.com",
            "gabriel@tiziano@gmail.com"
    })
    void naoDeveValidarUmEmailInvalido(String email) {
        assertThat(ValidateEmail.validate(email)).isFalse();
    }
}