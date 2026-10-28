package gabriel.tiziano.trade_platform;

public class ValidateCpf {

    public static boolean validate(String str) {
        if (str != null) {
            if (str.length() >= 11 && str.length() <= 14) {
                // cleaning cpf
                str = str
                        .replace(".", "")
                        .replace("-", "")
                        .replace(" ", "");
                // tudo igual
                final String cpf = str;
                if (!cpf.chars().allMatch(c -> c == cpf.charAt(0))) {
                    try {
                        int d1, d2;
                        int dg1, dg2, rest;
                        int digito;
                        String nDigResult;
                        d1 = d2 = 0;
                        dg1 = dg2 = rest = 0;

                        for (int nCount = 1; nCount < cpf.length() - 1; nCount++) {
                            digito = Integer.parseInt(cpf.substring(nCount - 1, nCount));
                            d1 = d1 + (11 - nCount) * digito;
                            d2 = d2 + (12 - nCount) * digito;
                        }

                        rest = (d1 % 11);
                        dg1 = (rest < 2) ? 0 : 11 - rest;
                        d2 += 2 * dg1;
                        rest = (d2 % 11);
                        if (rest < 2)
                            dg2 = 0;
                        else
                            dg2 = 11 - rest;

                        String nDigVerific = cpf.substring(cpf.length() - 2);
                        nDigResult = "" + dg1 + dg2;
                        return nDigVerific.equals(nDigResult);
                    } catch (Exception e) {
                        System.err.println("Erro !" + e);
                        return false;
                    }
                } else return false;
            } else return false;
        } else return false;
    }
}