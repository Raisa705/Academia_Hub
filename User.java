public class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean checkLogin(String inputUser, String inputPass) {
        return username.equals(inputUser) && password.equals(inputPass);
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
