// Main.java


import enums.UserRole;
import gui.AdminDashboard;
import gui.UserDashboard;
import users.Admin;
import users.User;

public class Main {
    public static void main(String[] args) {
        User admin = new Admin("admin", "admin123");
        // User user = new RegularUser("user", "user123");

        if (admin.getRole() == UserRole.ADMIN) {
            AdminDashboard adminDashboard = new AdminDashboard();
            adminDashboard.setVisible(true);
        } else {
            UserDashboard userDashboard = new UserDashboard();
            userDashboard.setVisible(true);
        }
    }
}