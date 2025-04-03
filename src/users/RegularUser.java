// RegularUser.java (Concrete Class)
package users;

import enums.UserRole;

public class RegularUser extends User {
    public RegularUser(String username, String password) {
        super(username, password, UserRole.REGULAR);
    }

    @Override
    public void login() {
        System.out.println("User logged in.");
    }

    @Override
    public void logout() {
        System.out.println("User logged out.");
    }
}