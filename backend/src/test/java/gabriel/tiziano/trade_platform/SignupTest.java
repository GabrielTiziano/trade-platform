package gabriel.tiziano.trade_platform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SignupTest {

    private final RestClient client = RestClient.create("http://localhost:8080");

    @Test
    void deveCriarUmaConta() {
        Map<String, String> input = Map.of(
                "name", "Gabriel Tiziano",
                "email", emailUnico(),
                "document", "12345678909",
                "password", "Teste123"
        );

        Map<?, ?> outputSignup = signup(input);
        assertThat(outputSignup.get("accountId")).isNotNull();

        Map<?, ?> outputGetAccount = client.get()
                .uri("/accounts/{accountId}", outputSignup.get("accountId"))
                .retrieve()
                .body(Map.class);

        assertThat(outputGetAccount.get("accountId")).isEqualTo(outputSignup.get("accountId"));
        assertThat(outputGetAccount.get("name")).isEqualTo(input.get("name"));
        assertThat(outputGetAccount.get("email")).isEqualTo(input.get("email"));
        assertThat(outputGetAccount.get("document")).isEqualTo(input.get("document"));
        assertThat(outputGetAccount.get("password")).isEqualTo(input.get("password"));
    }

    @Test
    void naoDeveCriarUmaContaComNomeInvalido() {
        Map<String, String> input = Map.of(
                "name", "Gabriel", // sem sobrenome
                "email", emailUnico(),
                "document", "12345678909",
                "password", "Teste123"
        );

        assertThatThrownBy(() -> signup(input))
                .isInstanceOfSatisfying(HttpClientErrorException.class, e -> {
                    assertThat(e.getStatusCode().value()).isEqualTo(422);
                    assertThat(e.getResponseBodyAsString()).contains("Invalid name");
                });
    }

    @ParameterizedTest(name = "email inválido: {0}")
    @EmptySource
    @ValueSource(strings = {
            "gabrieltiziano.gmail.com",     // sem @
            "@gmail.com",                   // sem usuário
            "teste123@gmail",               // sem terminação
            "teste+teste@gmail.org",        // terminação não permitida
            "gabrieltiziano@gmail.com.ar",  // termina com .ar
            "gabriel ghiaroni@gmail.com"    // espaço no usuário
    })
    void naoDeveCriarUmaContaComEmailInvalido(String email) {
        Map<String, String> input = Map.of(
                "name", "Gabriel Tiziano",
                "email", email,
                "document", "12345678909",
                "password", "Teste123"
        );

        assertThatThrownBy(() -> signup(input))
                .isInstanceOfSatisfying(HttpClientErrorException.class, e -> {
                    assertThat(e.getStatusCode().value()).isEqualTo(422);
                    assertThat(e.getResponseBodyAsString()).contains("Invalid email");
                });
    }

    @ParameterizedTest(name = "email válido: {0}")
    @MethodSource("emailsValidos")
    void deveCriarUmaContaComEmailValido(String email) {
        Map<String, String> input = Map.of(
                "name", "Gabriel Tiziano",
                "email", email,
                "document", "12345678909",
                "password", "Teste123"
        );

        Map<?, ?> output = signup(input);

        assertThat(output.get("accountId")).isNotNull();
    }

    @ParameterizedTest(name = "documento inválido: {0}")
    @EmptySource
    @ValueSource(strings = {
            "11111111111",   // todos os dígitos iguais
            "97456321559",   // dígito verificador errado
            "123"            // tamanho inválido
    })
    void naoDeveCriarUmaContaComDocumentoInvalido(String document) {
        Map<String, String> input = Map.of(
                "name", "Gabriel Tiziano",
                "email", emailUnico(),
                "document", document,
                "password", "Teste123"
        );

        assertThatThrownBy(() -> signup(input))
                .isInstanceOfSatisfying(HttpClientErrorException.class, e -> {
                    assertThat(e.getStatusCode().value()).isEqualTo(422);
                    assertThat(e.getResponseBodyAsString()).contains("Invalid document");
                });
    }

    @Test
    void deveCriarUmaContaComDocumentoFormatado() {
        Map<String, String> input = Map.of(
                "name", "Gabriel Tiziano",
                "email", emailUnico(),
                "document", "974.563.215-58",
                "password", "Teste123"
        );

        Map<?, ?> output = signup(input);

        assertThat(output.get("accountId")).isNotNull();
    }

    @ParameterizedTest(name = "senha inválida: {0}")
    @EmptySource
    @ValueSource(strings = {
            "Teste12",      // 7 caracteres: abaixo do mínimo
            "teste123",     // sem letra maiúscula
            "TESTE123",     // sem letra minúscula
            "TesteTeste"    // sem número
    })
    void naoDeveCriarUmaContaComSenhaInvalida(String password) {
        Map<String, String> input = Map.of(
                "name", "Gabriel Tiziano",
                "email", emailUnico(),
                "document", "12345678909",
                "password", password
        );

        assertThatThrownBy(() -> signup(input))
                .isInstanceOfSatisfying(HttpClientErrorException.class, e -> {
                    assertThat(e.getStatusCode().value()).isEqualTo(422);
                    assertThat(e.getResponseBodyAsString()).contains("Invalid password");
                });
    }

    @ParameterizedTest(name = "senha válida: {0}")
    @ValueSource(strings = {
            "Teste123",          // exatamente 8 caracteres (limite)
            "teste123T",         // maiúscula no fim
            "123testeT",         // começa com número
            "Teste@123",         // com caractere especial
            "SenhaMuitoLonga2026" // bem acima do mínimo
    })
    void deveCriarUmaContaComSenhaValida(String password) {
        Map<String, String> input = Map.of(
                "name", "Gabriel Tiziano",
                "email", emailUnico(),
                "document", "12345678909",
                "password", password
        );

        Map<?, ?> output = signup(input);

        assertThat(output.get("accountId")).isNotNull();
    }


    static Stream<String> emailsValidos() {
        String id = UUID.randomUUID().toString().substring(0, 8);
        return Stream.of(
                "gabriel" + id + "@gmail.com",
                "gabriel.tiziano" + id + "@empresa.com.br",
                "gabriel+" + id + "@gmail.com"
        );
    }

    private static String emailUnico() {
        return "gabriel." + UUID.randomUUID() + "@gmail.com";
    }

    private Map<?, ?> signup(Map<String, String> input) {
        return client.post()
                .uri("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .body(input)
                .retrieve()
                .body(Map.class);
    }
}