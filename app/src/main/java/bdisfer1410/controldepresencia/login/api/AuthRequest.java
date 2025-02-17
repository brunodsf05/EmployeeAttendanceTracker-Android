package bdisfer1410.controldepresencia.login.api;

public class AuthRequest {
    private String username;
    private String password;

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isUsernameValid() {
        return username != null && !username.isBlank();
    }

    public boolean isPasswordValid() {
        return password != null && !password.isBlank();
    }
}
