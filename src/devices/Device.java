package devices;

import enums.DeviceStatus;
import enums.UserEditable;

public class Device {
    private String id;
    private String name;
    private DeviceStatus status;
    private UserEditable userEditable;

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

    public  void turnOn(){

    };
    public  void turnOff(){

    };
    public  void adjustSetting(double value){

    };
    public  void adjustSetting(DeviceStatus value){

    };

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public DeviceStatus getStatus() { return status; }
    public void setStatus(DeviceStatus status) { this.status = status; }
    public UserEditable getUserEditable () {return userEditable;}
    public void setUserEditable(UserEditable userEditable) {this.userEditable = userEditable;}
}