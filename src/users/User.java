// User.java (Abstract Class)
package users;

import java.util.ArrayList;
import java.util.List;

import devices.Device;
import enums.UserRole;

public abstract class User {
    private String username;
    private String password;
    private UserRole role;
    private List<Device> devices = new ArrayList<>();


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

    public void addDevices(Device... devices) {
        for (Device device : devices) {
            this.devices.add(device);
        }
    }
    
    public List<Device> getDevices() {
        return devices;
    }
    
    public void removeDevice(Device device) {
        devices.remove(device);
    }
    
    
}
