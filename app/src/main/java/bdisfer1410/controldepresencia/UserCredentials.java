package bdisfer1410.controldepresencia;

public class UserCredentials {
    private String name;
    private String password;

    public UserCredentials(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isNameValid() {
        return name != null && !name.isBlank();
    }

    public boolean isPasswordValid() {
        return password != null && !password.isBlank();
    }
}
