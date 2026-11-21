package gabriel.tiziano.trade_platform.repository;

import gabriel.tiziano.trade_platform.data.Account;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository {
    void save(Account account);

    Optional<Account> getById(String accountId);
}