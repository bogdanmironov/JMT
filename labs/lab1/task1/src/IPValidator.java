public class IPValidator {
    public static boolean validateIPv4Address(String str) {
        String[] tokens = str.split("\\.");

        if (tokens.length != 4) return false;

        for (String token: tokens) {
            if (!isTokenValid(token)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isTokenValid(String token) {
        if ("0".equals(token)) return true;

        if (token.length() > 3 || token.length() == 0) {
            return false;
        } else {
            var tokenChArr = token.toCharArray();

            for (var currToken: tokenChArr) {
                if (!isDigit(currToken)) return false;
            }

            return Integer.parseInt(token) >= 1 && Integer.parseInt(token) <= 255;
        }
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <='9';
    }

    public static void main(String[] args) {
        System.out.println(validateIPv4Address("192.168.1.1"));
        System.out.println(validateIPv4Address("192.168.1.0"));
        System.out.println(validateIPv4Address("192.168.1.00"));
        System.out.println(validateIPv4Address("192.168@1.1"));
    }

}