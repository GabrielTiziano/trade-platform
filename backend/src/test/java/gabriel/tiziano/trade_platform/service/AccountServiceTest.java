package gabriel.tiziano.trade_platform.service;

import gabriel.tiziano.trade_platform.repository.AccountRepositoryMemory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(new AccountRepositoryMemory());
    }

    @Test
    void deveCriarEConsultarUmaConta() {
        SignupInput input = new SignupInput("Gabriel Tiziano", "gabriel@gmail.com", "12345678909", "Teste123");

        SignupOutput output = accountService.signup(input);

        assertThat(output.accountId()).isNotNull();
        assertThat(accountService.getAccount(output.accountId())).hasValueSatisfying(account -> {
            assertThat(account.accountId()).isEqualTo(output.accountId());
            assertThat(account.name()).isEqualTo(input.name());
            assertThat(account.email()).isEqualTo(input.email());
            assertThat(account.document()).isEqualTo(input.document());
            assertThat(account.password()).isEqualTo(input.password());
        });
    }

    @ParameterizedTest(name = "{4}")
    @CsvSource({
            "Gabriel,         gabriel@gmail.com, 12345678909, Teste123, Invalid name",
            "Gabriel Tiziano, gabriel.gmail.com, 12345678909, Teste123, Invalid email",
            "Gabriel Tiziano, gabriel@gmail.com, 11111111111, Teste123, Invalid document",
            "Gabriel Tiziano, gabriel@gmail.com, 12345678909, teste123, Invalid password"
    })
    void naoDeveCriarUmaContaComCampoInvalido(String name, String email, String document, String password, String mensagemEsperada) {
        SignupInput input = new SignupInput(name, email, document, password);

        assertThatThrownBy(() -> accountService.signup(input))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage(mensagemEsperada);
    }

    @Test
    void deveRetornarVazioParaContaInexistente() {
        assertThat(accountService.getAccount("conta-inexistente")).isEmpty();
    }
}