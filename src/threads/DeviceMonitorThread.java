package threads;

import devices.Device;

public class DeviceMonitorThread extends Thread {
    private Device device;

    public DeviceMonitorThread(Device device) {
        this.device = device;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Monitoring " + device.getName() + ": " + device.getStatus());
            try {
                Thread.sleep(5000); // Check every 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}