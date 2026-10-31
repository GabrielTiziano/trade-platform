package gabriel.tiziano.trade_platform.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
public class SignupController {

    @PostMapping("signup")
    public Map<String, String> signup() {
        String accountId = UUID.randomUUID().toString();
        return Map.of("accountId", accountId);
    }

}
