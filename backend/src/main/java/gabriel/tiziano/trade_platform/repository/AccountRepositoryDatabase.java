package gabriel.tiziano.trade_platform.repository;

import gabriel.tiziano.trade_platform.data.Account;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountRepositoryDatabase implements AccountRepository {
    private final JdbcClient jdbcClient;

    public AccountRepositoryDatabase(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void save(Account account) {
        jdbcClient.sql("insert into app.account (account_id, name, email, document, password) values (?, ?, ?, ?, ?)")
                .params(UUID.fromString(account.accountId()), account.name(), account.email(), account.document(), account.password())
                .update();
    }

    @Override
    public Optional<Account> getById(String accountId) {
        return jdbcClient.sql("select account_id, name, email, document, password from app.account where account_id = ?")
                .param(UUID.fromString(accountId))
                .query((rs, rowNum) -> new Account(
                        rs.getString("account_id"), rs.getString("name"), rs.getString("email"),
                        rs.getString("document"), rs.getString("password")))
                .optional();
    }
}
