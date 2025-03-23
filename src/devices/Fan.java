package devices;

import enums.DeviceStatus;

public class Fan extends Device {
    private int speed;

    public Fan(String id, String name) {
        super(id, name);
        this.speed = 0;
    }

    @Override
    public void turnOn() {
        setStatus(DeviceStatus.ON);
        speed = 3; // Default speed
    }

    @Override
    public void turnOff() {
        setStatus(DeviceStatus.OFF);
        speed = 0;
    }

    @Override
    public void adjustSetting(double value) {
        this.speed = (int) value;
    }

    public int getSpeed() { return speed; }
}
