package gabriel.tiziano.trade_platform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SignupIT {

    private final RestClient client = RestClient.create("http://localhost:8080");

    @Test
    void deveCriarUmaConta() {
        Map<String, String> input = inputValido();

        Map<?, ?> outputSignup = signup(input);
        assertThat(outputSignup.get("accountId")).isNotNull();

        Map<?, ?> outputGetAccount = getAccount(outputSignup.get("accountId"));

        assertThat(outputGetAccount.get("accountId")).isEqualTo(outputSignup.get("accountId"));
        assertThat(outputGetAccount.get("name")).isEqualTo(input.get("name"));
        assertThat(outputGetAccount.get("email")).isEqualTo(input.get("email"));
        assertThat(outputGetAccount.get("document")).isEqualTo(input.get("document"));
        assertThat(outputGetAccount.get("password")).isEqualTo(input.get("password"));
    }

    @ParameterizedTest(name = "{0} inválido: \"{1}\" → {2}")
    @CsvSource({
            "name,     Gabriel,                  Invalid name",
            "email,    gabrieltiziano.gmail.com, Invalid email",
            "document, 11111111111,              Invalid document",
            "password, teste123,                 Invalid password"
    })
    void naoDeveCriarUmaContaComCampoInvalido(String campo, String valor, String mensagemEsperada) {
        Map<String, String> input = inputValido();
        input.put(campo, valor);

        assertThatThrownBy(() -> signup(input))
                .isInstanceOfSatisfying(HttpClientErrorException.class, e -> {
                    assertThat(e.getStatusCode().value()).isEqualTo(422);
                    assertThat(e.getResponseBodyAsString()).contains(mensagemEsperada);
                });
    }

    @Test
    void deveRetornar404ParaContaInexistente() {
        assertThatThrownBy(() -> getAccount(UUID.randomUUID()))
                .isInstanceOfSatisfying(HttpClientErrorException.class, e ->
                        assertThat(e.getStatusCode().value()).isEqualTo(404));
    }

    private Map<String, String> inputValido() {
        Map<String, String> input = new HashMap<>();
        input.put("name", "Gabriel Tiziano");
        input.put("email", "gabriel." + UUID.randomUUID() + "@gmail.com");
        input.put("document", "12345678909");
        input.put("password", "Teste123");
        return input;
    }

    private Map<?, ?> signup(Map<String, String> input) {
        return client.post()
                .uri("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .body(input)
                .retrieve()
                .body(Map.class);
    }

    private Map<?, ?> getAccount(Object accountId) {
        return client.get()
                .uri("/accounts/{accountId}", accountId)
                .retrieve()
                .body(Map.class);
    }
}