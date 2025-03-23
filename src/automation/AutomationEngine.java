package automation;

import devices.Device;

public class AutomationEngine {
    public static void triggerEvent(Device device, String event) {
        if (event.equals("motion_detected")) {
            device.turnOn();
        }
    }
}