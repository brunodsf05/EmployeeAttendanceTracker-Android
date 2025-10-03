package brunodsf05.employee.attendance.tracker.api.auth;

public class AuthRequest {
    private final String username;
    private final String password;
    private final String server;

    public AuthRequest(String username, String password, String server) {
        this.username = username;
        this.password = password;
        this.server = server;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getServer() {
        return server;
    }

    public boolean isUsernameValid() {
        return username != null && !username.isBlank();
    }

    public boolean isPasswordValid() {
        return password != null && !password.isBlank();
    }

    public boolean isServerValid() {
        if (server == null || server.isBlank())
            return false;

        String lower = server.toLowerCase();
        return lower.startsWith("http://") || lower.startsWith("https://");
    }
}
