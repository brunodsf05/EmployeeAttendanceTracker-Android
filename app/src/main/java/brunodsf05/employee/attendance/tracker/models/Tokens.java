package brunodsf05.employee.attendance.tracker.models;

import android.content.Intent;

public class Tokens {
    public static class Token {
        private static final String HEADER_TEMPLATE = "Bearer %s";
        private static final String DEBUG_TEMPLATE = "%s: %.50s...";

        private final String name;
        private String value = "";

        public Token(String value, String name) {
            this.name = name;
            this.value = value;
        }

        public String get() {
            return this.value;
        }

        public String getHeader() {
            String token = (isValid()) ? value : "";

            return String.format(HEADER_TEMPLATE, token);
        }

        public String getDebug() {
            return String.format(DEBUG_TEMPLATE, name, (isValid()) ? value : "Inválido");
        }

        public void set(String newToken) {
            this.value = newToken;
        }

        public boolean isValid() {
            return value != null && !value.isEmpty();
        }
    }

    //region Configuración
    public static final String INTENTKEY_ACCESS = "ACCESS_TOKEN";
    public static final String INTENTKEY_REFRESH = "REFRESH_TOKEN";
    //endregion

    //region Tokens
    public final Token access = new Token("", "Acceso");
    public final Token refresh = new Token("", "Refresco");
    //endregion

    //region Constructores
    public Tokens() {
        super();
    }

    public Tokens(String access, String refresh) {
        this.access.set(access);
        this.refresh.set(refresh);
    }

    public Tokens(Intent intent) {
        this.access.set(intent.getStringExtra(INTENTKEY_ACCESS));
        this.refresh.set(intent.getStringExtra(INTENTKEY_REFRESH));
    }
    //endregion

    public boolean intoIntent(Intent intent) {
        intent.putExtra(INTENTKEY_ACCESS, access.get());
        intent.putExtra(INTENTKEY_REFRESH, refresh.get());

        return true;
    }
}
