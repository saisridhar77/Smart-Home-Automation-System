package devices;

import enums.DeviceStatus;
public class AirConditioner extends Device {
    private int temperature;

    public AirConditioner(String id, String name) {
        super(id, name);
        this.temperature = 0;
    }
    @Override
    public void turnOn() {
        setStatus(DeviceStatus.ON);
        temperature = 23; 
    }

    @Override
    public void turnOff() {
        setStatus(DeviceStatus.OFF);
        temperature = 0;
    }

    @Override
    public void adjustSetting(double value) {
        if (value < 16 ) {
            this.temperature = 16;
        }else if (value > 30) {
            this.temperature = 30;
        }else {
            this.temperature = (int) value;
        }
    }
    public int getTemperature() { return temperature; }
    
}
