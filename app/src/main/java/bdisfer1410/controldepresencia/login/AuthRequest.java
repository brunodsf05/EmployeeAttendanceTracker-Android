package bdisfer1410.controldepresencia.login;

public class AuthRequest {
    private String user;
    private String password;

    public AuthRequest(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isNameValid() {
        return user != null && !user.isBlank();
    }

    public boolean isPasswordValid() {
        return password != null && !password.isBlank();
    }
}
