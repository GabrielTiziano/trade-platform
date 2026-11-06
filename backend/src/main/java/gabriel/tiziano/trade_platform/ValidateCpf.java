package gabriel.tiziano.trade_platform;

public final class ValidateCpf {

    private static final String CPF_PATTERN = "\\d{11}";
    private static final int BASE_DIGITS = 9;

    private ValidateCpf() {
    }

    public static boolean validate(String cpf) {
        if (cpf == null) {
            return false;
        }
        String digits = cpf.replaceAll("[.\\- ]", "");
        if (!digits.matches(CPF_PATTERN) || hasAllDigitsEqual(digits)) {
            return false;
        }
        String expectedCheckDigits = "" + checkDigit(digits, BASE_DIGITS) + checkDigit(digits, BASE_DIGITS + 1);
        return digits.endsWith(expectedCheckDigits);
    }

    private static boolean hasAllDigitsEqual(String digits) {
        return digits.chars().allMatch(c -> c == digits.charAt(0));
    }

    private static int checkDigit(String digits, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += (digits.charAt(i) - '0') * (length + 1 - i);
        }
        int rest = sum % 11;
        return rest < 2 ? 0 : 11 - rest;
    }
}
