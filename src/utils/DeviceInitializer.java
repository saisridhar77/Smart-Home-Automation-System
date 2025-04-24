package utils;

import devices.*;
import java.util.ArrayList;
import java.util.List;

public class DeviceInitializer {
    public static List<Device> initializeDevices() {
        List<Device> devices = new ArrayList<>();
        AirConditioner ac = new AirConditioner("1", "Living Room AC");
        ac.turnOn(); // Set initial status and temperature
        devices.add(ac);

        // Add other devices here (e.g., Light, Fan, etc.)
        return devices;
    }
}