import gui.LoginUI;
import users.Admin;
import users.RegularUser;
import users.User;


import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Initialize the shared list of devices
        // List<Device> devices = DeviceInitializer.initializeDevices();
        List<User> users = new ArrayList<>();

        users.add(new Admin("admin","admin123"));
        users.add(new RegularUser("user1", "password1"));
        users.add(new RegularUser("user2", "password2"));
        
        // Pass the devices list to the LoginUI
        LoginUI loginUI = new LoginUI(users);
        loginUI.setVisible(true);
    }
}
