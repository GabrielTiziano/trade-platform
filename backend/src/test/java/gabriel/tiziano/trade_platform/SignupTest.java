package gabriel.tiziano.trade_platform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SignupTest {
    private final RestClient client = RestClient.create("http://localhost:8080");

    @Test
    void deveCriarUmaConta() {
        Map<String, String> input = Map.of(
                "name", "Gabriel Tiziano",
                "email", "gabrieltiziano@gmail.com",
                "document", "12345678909",
                "password", "Teste123"
        );

        Map<?, ?> output = client.post()
                .uri("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .body(input)
                .retrieve()
                .body(Map.class);

        assertThat(output.get("accountId")).isNotNull();
    }
}
