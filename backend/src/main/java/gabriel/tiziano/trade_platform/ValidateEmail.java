package gabriel.tiziano.trade_platform;

public final class ValidateEmail {

    private static final String EMAIL_PATTERN = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.(com|com\\.br)$";

    private ValidateEmail() {

    }

    public static boolean validate(String email) {
        return email != null && email.matches(EMAIL_PATTERN);
    }
}
