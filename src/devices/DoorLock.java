package devices;

import enums.DeviceStatus;
public class DoorLock extends Device {
    @Override
    public void adjustSetting(DeviceStatus status) {
        if (status == DeviceStatus.ON) {
            turnOn();
        } else {
            turnOff();
        }
    }
    public DoorLock(String id, String name) {
        super(id, name);
    }
    @Override
    public void turnOn() {
        setStatus(DeviceStatus.ON);
    }

    @Override
    public void turnOff() {
        setStatus(DeviceStatus.OFF);
    }    
}
