package devices;

import enums.DeviceStatus;

public class Light extends Device {
    private int brightness;

    public Light(String id, String name) {
        super(id, name);
        this.brightness = 0;
    }

    @Override
    public void turnOn() {
        setStatus(DeviceStatus.ON);
        brightness = 50; // Default brightness
    }

    @Override
    public void turnOff() {
        setStatus(DeviceStatus.OFF);
        brightness = 0;
    }

    @Override
    public void adjustSetting(double value) {
        this.brightness = (int) value;
    }

    public int getBrightness() { return brightness; }
}