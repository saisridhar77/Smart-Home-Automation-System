package devices;

import enums.DeviceStatus;

public abstract class Device {
    private String id;
    private String name;
    private DeviceStatus status;

    // Constructor
    public Device(String id, String name) {
        this.id = id;
        this.name = name;
        this.status = DeviceStatus.OFF;
    }

    // Overloaded constructor
    public Device(String id) {
        this(id, "Unnamed Device");
    }

    // Abstract methods
    public abstract void turnOn();
    public abstract void turnOff();
    public abstract void adjustSetting(double value);

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public DeviceStatus getStatus() { return status; }
    protected void setStatus(DeviceStatus status) { this.status = status; }
}