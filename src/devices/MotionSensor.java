package devices;

public class MotionSensor extends Device {
    // TODO : Implement the MotionSensor class
    public MotionSensor(String id, String name) {
        super(id, name);
    }

    @Override
    public void turnOn() {
        throw new UnsupportedOperationException("Unimplemented method 'turnOn'");
    }

    @Override
    public void turnOff() {
        throw new UnsupportedOperationException("Unimplemented method 'turnOff'");
    }

    @Override
    public void adjustSetting(double value) {
        throw new UnsupportedOperationException("Unimplemented method 'adjustSetting'");
    }
}
