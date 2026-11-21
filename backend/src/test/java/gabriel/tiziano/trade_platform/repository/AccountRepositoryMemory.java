package gabriel.tiziano.trade_platform.repository;

import gabriel.tiziano.trade_platform.data.Account;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepositoryMemory implements AccountRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public void save(Account account) {
        accounts.put(account.accountId(), account);
    }

    @Override
    public Optional<Account> getById(String accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }
}