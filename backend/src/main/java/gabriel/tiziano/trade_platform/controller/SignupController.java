package gabriel.tiziano.trade_platform.controller;

import gabriel.tiziano.trade_platform.ValidateCpf;
import gabriel.tiziano.trade_platform.ValidateEmail;
import gabriel.tiziano.trade_platform.ValidateName;
import gabriel.tiziano.trade_platform.ValidatePassword;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SignupController {

    private final Map<String, Map<String, String>> accounts = new ConcurrentHashMap<>();

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody Map<String, String> input) {
        String name = input.get("name");
        if (!ValidateName.validate(name)) {
            return ResponseEntity.status(422).body(Map.of("message", "Invalid name"));
        }

        String email = input.get("email");
        if (!ValidateEmail.validate(email)) {
            return ResponseEntity.status(422).body(Map.of("message", "Invalid email"));
        }

        String document = input.get("document");
        if (!ValidateCpf.validate(document)) {
            return ResponseEntity.status(422).body(Map.of("message", "Invalid document"));
        }

        String password = input.get("password");
        if (!ValidatePassword.validate(password)) {
            return ResponseEntity.status(422).body(Map.of("message", "Invalid password"));
        }

        String accountId = UUID.randomUUID().toString();
        Map<String, String> account = new HashMap<>(input);
        account.put("accountId", accountId);
        accounts.put(accountId, account);
        return ResponseEntity.ok(Map.of("accountId", accountId));
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<Map<String, String>> getAccount(@PathVariable("accountId") String accountId) {
        Map<String, String> account = accounts.get(accountId);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }
}
