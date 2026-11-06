package gabriel.tiziano.trade_platform;

public final class ValidateName {

    private static final String NAME_PATTERN = "\\p{L}+(['’-]\\p{L}+)*( \\p{L}+(['’-]\\p{L}+)*)+";

    private ValidateName() {
    }

    public static boolean validate(String name) {
        return name != null && name.matches(NAME_PATTERN);
    }
}