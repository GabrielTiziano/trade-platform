package gabriel.tiziano.trade_platform;

public final class ValidatePassword {

    private static final String PASSWORD_PATTERN = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}";

    private ValidatePassword() {

    }

    public static boolean validate(String password) {
        return password != null && password.matches(PASSWORD_PATTERN);
    }
}
