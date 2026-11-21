package gabriel.tiziano.trade_platform.service;

import gabriel.tiziano.trade_platform.ValidateCpf;
import gabriel.tiziano.trade_platform.ValidateEmail;
import gabriel.tiziano.trade_platform.ValidateName;
import gabriel.tiziano.trade_platform.ValidatePassword;
import gabriel.tiziano.trade_platform.data.Account;
import gabriel.tiziano.trade_platform.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public SignupOutput signup(SignupInput input) {
        if (!ValidateName.validate(input.name())) throw new InvalidInputException("Invalid name");
        if (!ValidateEmail.validate(input.email())) throw new InvalidInputException("Invalid email");
        if (!ValidateCpf.validate(input.document())) throw new InvalidInputException("Invalid document");
        if (!ValidatePassword.validate(input.password())) throw new InvalidInputException("Invalid password");
        Account account = new Account(UUID.randomUUID().toString(), input.name(), input.email(), input.document(), input.password());
        accountRepository.save(account);
        return new SignupOutput(account.accountId());
    }

    public Optional<GetAccountOutput> getAccount(String accountId) {
        return accountRepository.getById(accountId)
                .map(a -> new GetAccountOutput(a.accountId(), a.name(), a.email(), a.document(), a.password()));
    }
}