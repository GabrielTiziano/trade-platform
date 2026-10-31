package gabriel.tiziano.trade_platform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ValidateCpfTest {
    @ParameterizedTest(name = "CPF válido: {0}")
    @ValueSource(strings = {
            "97456321558",    // dois dígitos verificadores diferentes de zero
            "71428793860",    // segundo dígito verificador igual a zero
            "87748248800",    // dois dígitos verificadores iguais a zero
            "12345678909",    // primeiro dígito verificador igual a zero
            "974.563.215-58"  // com formatação
    })
    void deveValidarUmCpfValido(String cpf) {
        boolean isValid = ValidateCpf.validate(cpf);
        assertThat(isValid).isTrue();
    }

    @ParameterizedTest(name = "CPF inválido: {0}")
    @NullAndEmptySource
    @ValueSource(strings = {
            "11111111111",      // todos os dígitos iguais
            "123",              // menor que o tamanho mínimo
            "714287938601234",  // maior que o tamanho máximo
            "97456321559"       // dígito verificador errado
    })
    void naoDeveValidarUmCpfInvalido(String cpf) {
        boolean isValid = ValidateCpf.validate(cpf);
        assertThat(isValid).isFalse();
    }
}