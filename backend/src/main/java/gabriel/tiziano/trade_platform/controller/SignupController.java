package gabriel.tiziano.trade_platform.controller;

import gabriel.tiziano.trade_platform.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class SignupController {

    private final AccountService accountService;

    public SignupController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/signup")
    public SignupOutput signup(@RequestBody SignupInput input) {
        return accountService.signup(input);
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<GetAccountOutput> getAccount(@PathVariable("accountId") String accountId) {
        return accountService.getAccount(accountId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Map<String, String>> handleInvalidInput(InvalidInputException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(Map.of("message", e.getMessage()));
    }
}