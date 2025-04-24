import gui.LoginUI;
import utils.DeviceInitializer;
import devices.Device;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Initialize the shared list of devices
        List<Device> devices = DeviceInitializer.initializeDevices();

        // Pass the devices list to the LoginUI
        LoginUI loginUI = new LoginUI(devices);
        loginUI.setVisible(true);
    }
}