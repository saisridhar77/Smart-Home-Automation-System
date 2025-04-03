// User.java (Abstract Class)
package users;

import enums.UserRole;

public abstract class User {
    private String username;
    private String password;
    private UserRole role;

    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Overloaded constructor
    public User(String username, String password) {
        this(username, password, UserRole.REGULAR);
    }

    public abstract void login();
    public abstract void logout();

    // Getters
    public String getUsername() { return username; }
    public UserRole getRole() { return role; }
    public boolean checkPassword(String password) { return this.password.equals(password); }
}
