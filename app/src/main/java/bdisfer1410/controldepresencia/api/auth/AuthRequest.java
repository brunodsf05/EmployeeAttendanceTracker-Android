package bdisfer1410.controldepresencia.api.auth;

public class AuthRequest {
    private final String username;
    private final String password;

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isUsernameValid() {
        return username != null && !username.isBlank();
    }

    public boolean isPasswordValid() {
        return password != null && !password.isBlank();
    }
}
