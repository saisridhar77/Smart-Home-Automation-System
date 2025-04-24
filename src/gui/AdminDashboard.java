package gui;

import devices.*;
import enums.DeviceStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class AdminDashboard extends JFrame {
    private List<Device> devices;
    private boolean isEditingEnabled = false; // Shared flag to control user editing
    private final PropertyChangeSupport support = new PropertyChangeSupport(this); // For notifying changes

    public AdminDashboard(List<Device> devices) {
        this.devices = devices; // Use the shared devices list

        setTitle("Admin Dashboard");
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

        // Add an "Enable Editing" toggle button
        

        // Add the top panel to the frame
        add(topPanel, BorderLayout.NORTH);

        // Add a table to display devices and their statuses
        String[] columnNames = {"Device Name", "Device Type", "Status", "Temperature"};
        Object[][] deviceData = getDeviceData();

        DefaultTableModel tableModel = new DefaultTableModel(deviceData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing only for Status and Temperature columns
                return column == 2 || column == 3;
            }
        };

        //hello

        JTable deviceTable = new JTable(tableModel);

        // Add a drop-down for the Status column
        TableColumn statusColumn = deviceTable.getColumnModel().getColumn(2);
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"ON", "OFF"});
        statusColumn.setCellEditor(new DefaultCellEditor(statusComboBox));

        // Add a listener to update the device data when edited
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (column == 2) { // Status column
                String newStatus = (String) tableModel.getValueAt(row, column);
                devices.get(row).setStatus(DeviceStatus.valueOf(newStatus));
            } else if (column == 3) { // Temperature column
                try {
                    int newTemperature = Integer.parseInt(tableModel.getValueAt(row, column).toString());
                    if (devices.get(row) instanceof AirConditioner) {
                        // Ensure temperature is within the range 16-30
                        if (newTemperature < 16) {
                            newTemperature = 16;
                        } else if (newTemperature > 30) {
                            newTemperature = 30;
                        }
                        ((AirConditioner) devices.get(row)).adjustSetting(newTemperature);
                        tableModel.setValueAt(newTemperature, row, column); // Update table with corrected value
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid temperature value!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(deviceTable);

        // Add the table to the center of the frame
        add(scrollPane, BorderLayout.CENTER);
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

    public boolean isEditingEnabled() {
        return isEditingEnabled;
    }

    // Add a method to allow listeners to subscribe to property changes
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}