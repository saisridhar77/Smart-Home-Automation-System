package gui;

import devices.*;
import enums.DeviceStatus;
import utils.DeviceInitializer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class UserDashboard extends JFrame {
    private List<Device> devices;
    private DefaultTableModel tableModel;
    private JTable deviceTable;
    private AdminDashboard adminDashboard; // Reference to AdminDashboard

    public UserDashboard(List<Device> devices, AdminDashboard adminDashboard) {
        this.devices = devices; // Use the shared devices list
        this.adminDashboard = adminDashboard; // Reference to AdminDashboard

        setTitle("User Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create a panel for the top bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add a Logout Button to the top-right corner
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setBackground(new Color(200, 50, 50));
        logoutButton.setForeground(Color.WHITE);

        // Add Action Listener for Logout
        logoutButton.addActionListener(e -> {
            dispose(); // Close the dashboard
            new LoginUI(devices).setVisible(true); // Pass devices back to LoginUI
        });

        // Add the Logout Button to the top-right corner
        topPanel.add(logoutButton, BorderLayout.EAST);

        // Add the top panel to the frame
        add(topPanel, BorderLayout.NORTH);

        // Add a table to display devices and their statuses
        String[] columnNames = {"Device Name", "Device Type", "Status", "Temperature"};
        Object[][] deviceData = getDeviceData();

        tableModel = new DefaultTableModel(deviceData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing only for Status and Temperature columns if editing is enabled by the admin
                return adminDashboard.isEditingEnabled() && (column == 2 || column == 3);
            }
        };

        deviceTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(deviceTable);

        // Add the table to the center of the frame
        add(scrollPane, BorderLayout.CENTER);

        // Listen for changes to the "isEditingEnabled" property
        adminDashboard.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("isEditingEnabled".equals(evt.getPropertyName())) {
                    // Refresh the table model to update editability
                    refreshTableModel();
                }
            }
        });
    }

    // Method to fetch device data for the table
    private Object[][] getDeviceData() {
        Object[][] data = new Object[devices.size()][4];
        for (int i = 0; i < devices.size(); i++) {
            Device device = devices.get(i);
            data[i][0] = device.getName();
            data[i][1] = device.getClass().getSimpleName();
            data[i][2] = device.getStatus().toString();
            if (device instanceof AirConditioner) {
                data[i][3] = ((AirConditioner) device).getTemperature();
            } else {
                data[i][3] = "N/A"; // For devices without temperature
            }
        }
        return data;
    }

    // Method to refresh the table model
    private void refreshTableModel() {
        tableModel = new DefaultTableModel(getDeviceData(), new String[]{"Device Name", "Device Type", "Status", "Temperature"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing only for Status and Temperature columns if editing is enabled by the admin
                return adminDashboard.isEditingEnabled() && (column == 2 || column == 3);
            }
        };
        deviceTable.setModel(tableModel); // Update the table with the new model
    }

    public static void main(String[] args) {
        new UserDashboard(DeviceInitializer.initializeDevices(), new AdminDashboard(DeviceInitializer.initializeDevices())).setVisible(true);
    }
}