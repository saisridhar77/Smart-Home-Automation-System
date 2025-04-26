package gui;

import devices.*;
import enums.DeviceStatus;
import enums.UserEditable;
import users.Admin;
import users.RegularUser;
import users.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends JFrame {
    private Admin adminUser;
    public List<User> allUsers;
    private boolean isEditingEnabled = false;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private JScrollPane tableScrollPane; // Declare a class-level variable for the scroll pane

    public AdminDashboard(Admin adminUser, List<User> allUsers) {
        this.adminUser = adminUser;
        this.allUsers = allUsers;

        setTitle("Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setBackground(new Color(200, 50, 50));
        logoutButton.setForeground(Color.WHITE);

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginUI(allUsers).setVisible(true);
        });

        JButton assignDevicesButton = new JButton("Assign Devices");
        assignDevicesButton.addActionListener(e -> assignDevicesToUser());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadDevices()); // Correctly attach loadDevices() to the Refresh button

        JButton createUserButton = new JButton("Create New User");
        createUserButton.addActionListener(e -> createNewUser()); // Attach createNewUser() to the Create New User button

        topPanel.add(logoutButton, BorderLayout.EAST);
        topPanel.add(assignDevicesButton, BorderLayout.CENTER);
        topPanel.add(refreshButton, BorderLayout.AFTER_LAST_LINE);
        topPanel.add(createUserButton, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);
        loadDevices();
        
    }

    private void assignDevicesToUser() {
        String[] usernames = allUsers.stream()
                .filter(u -> !(u instanceof Admin))
                .map(User::getUsername)
                .toArray(String[]::new);

        String selectedUser = (String) JOptionPane.showInputDialog(this, "Select user:", "Assign Devices",
                JOptionPane.PLAIN_MESSAGE, null, usernames, usernames[0]);

        if (selectedUser == null) return;

        User user = allUsers.stream()
                .filter(u -> u.getUsername().equals(selectedUser))
                .findFirst()
                .orElse(null);

        if (user != null) {
            List<Device> selectedDevices = selectDevicesPopup();
            if (!selectedDevices.isEmpty()) {
                user.addDevices(selectedDevices.toArray(new Device[0]));
                JOptionPane.showMessageDialog(this, "Devices assigned successfully!");
            }
        }
    }

    private List<Device> selectDevicesPopup() {
        List<Device> selectedDevices = new ArrayList<>();

        String[] availableDevices = {"Light", "Fan", "AirConditioner", "MotionSensor", "DoorLock"};

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));

        JComboBox<String> deviceTypeCombo = new JComboBox<>(availableDevices);
        JTextField deviceIdField = new JTextField(15);
        JTextField deviceNameField = new JTextField(15);

        panel.add(new JLabel("Select Device Type:"));
        panel.add(deviceTypeCombo);
        panel.add(new JLabel("Enter Device ID:"));
        panel.add(deviceIdField);
        panel.add(new JLabel("Enter Device Name:"));
        panel.add(deviceNameField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Add Device", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String selectedType = (String) deviceTypeCombo.getSelectedItem();
            String deviceId = deviceIdField.getText().trim();
            String deviceName = deviceNameField.getText().trim();

            if (deviceId.isEmpty() || deviceName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Device ID and Name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return selectedDevices;
            }

            Device newDevice = null;
            switch (selectedType) {
                case "Light":
                    newDevice = new Light(deviceId, deviceName);
                    break;
                case "Fan":
                    newDevice = new Fan(deviceId, deviceName);
                    break;
                case "AirConditioner":
                    newDevice = new AirConditioner(deviceId, deviceName);
                    break;
                case "MotionSensor":
                    newDevice = new MotionSensor(deviceId, deviceName);
                    break;
                case "DoorLock":
                    newDevice = new DoorLock(deviceId, deviceName);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Unknown device type!", "Error", JOptionPane.ERROR_MESSAGE);
                    return selectedDevices;
            }

            selectedDevices.add(newDevice);
            
        }

        return selectedDevices;
    }

    private void createNewUser() {
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        Object[] fields = {"Username:", usernameField, "Password:", passwordField};

        int option = JOptionPane.showConfirmDialog(this, fields, "Create New User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty.");
                return;
            }

            if (allUsers.stream().anyMatch(u -> u.getUsername().equals(username))) {
                JOptionPane.showMessageDialog(this, "Username already exists!");
                return;
            }
            allUsers.add(new RegularUser(username, password));

            JOptionPane.showMessageDialog(this, "User created successfully!");
        }
    }

    private Object[][] getDeviceData() {
        List<Object[]> deviceRows = new ArrayList<>();

        for (User user : allUsers) {
            for (Device device : user.getDevices()) {
                Object[] row = new Object[6];
                row[0] = user.getUsername(); // Owner first
                row[1] = device.getName();    // Device Name
                row[2] = device.getClass().getSimpleName(); // Device Type
                row[3] = device.getStatus().toString();     // Status

                // Setting value based on device type
                if (device instanceof AirConditioner) {
                    row[4] = ((AirConditioner) device).getTemperature();
                } else if (device instanceof Fan) {
                    row[4] = ((Fan) device).getSpeed();
                } else if (device instanceof Light) {
                    row[4] = ((Light) device).getBrightness();
                } else {
                    row[4] = "N/A";
                }

                row[5] = device.getUserEditable() == UserEditable.Yes ? "Yes" : "No";

                deviceRows.add(row);
            }
        }

        Object[][] data = new Object[deviceRows.size()][7];
        for (int i = 0; i < deviceRows.size(); i++) {
            data[i] = deviceRows.get(i);
        }
        return data;
    }

    public boolean isEditingEnabled() {
        return isEditingEnabled;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    private void loadDevices() {
        Object[][] deviceData = getDeviceData(); // Fetch updated device data
        String[] columnNames = {"Owner", "Device Name", "Device Type", "Status", "Parameter", "User Editable"};

        DefaultTableModel tableModel = new DefaultTableModel(deviceData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4 || column==5; // Allow editing for "Status" and "Parameter"
            }
        };

        JTable deviceTable = new JTable(tableModel);

        // Configure the "Status" column (column 3) with a combo box editor
        TableColumn statusColumn = deviceTable.getColumnModel().getColumn(3);
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"ON", "OFF"});
        statusColumn.setCellEditor(new DefaultCellEditor(statusComboBox));

        TableColumn statusColumn2 = deviceTable.getColumnModel().getColumn(5);
        JComboBox<String> statusComboBox2 = new JComboBox<>(new String[]{"Yes", "No"});
        statusColumn2.setCellEditor(new DefaultCellEditor(statusComboBox2));

        // Add a listener to handle table model changes
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (column == 3) { // Status column
                String newStatus = (String) tableModel.getValueAt(row, column);

                // Find the correct device based on the row
                String deviceName = (String) tableModel.getValueAt(row, 1); // Get the device name from the table
                User owner = allUsers.stream()
                        .filter(user -> user.getUsername().equals(tableModel.getValueAt(row, 0))) // Match the owner
                        .findFirst()
                        .orElse(null);

                if (owner != null) {
                    Device device = owner.getDevices().stream()
                            .filter(d -> d.getName().equals(deviceName)) // Match the device name
                            .findFirst()
                            .orElse(null);

                    if (device != null) {
                        device.setStatus(DeviceStatus.valueOf(newStatus)); // Update the actual device status
                    }
                }
            } else if (column == 4) { // Parameter column
                try {
                    int newParameter = Integer.parseInt(tableModel.getValueAt(row, column).toString());

                    // Find the correct device based on the row
                    String deviceName = (String) tableModel.getValueAt(row, 1); // Get the device name from the table
                    User owner = allUsers.stream()
                            .filter(user -> user.getUsername().equals(tableModel.getValueAt(row, 0))) // Match the owner
                            .findFirst()
                            .orElse(null);

                    if (owner != null) {
                        Device device = owner.getDevices().stream()
                                .filter(d -> d.getName().equals(deviceName)) // Match the device name
                                .findFirst()
                                .orElse(null);

                        if (device != null) {
                            // Update the parameter based on the device type
                            if (device instanceof AirConditioner) {
                                ((AirConditioner) device).adjustSetting(newParameter);
                            } else if (device instanceof Fan) {
                                if (newParameter < 1) newParameter = 1; // Minimum speed
                                if (newParameter > 5) newParameter = 5; // Maximum speed
                                ((Fan) device).adjustSetting(newParameter);
                            } else if (device instanceof Light) {
                                if (newParameter < 0) newParameter = 0; // Minimum brightness
                                if (newParameter > 100) newParameter = 100; // Maximum brightness
                                ((Light) device).adjustSetting(newParameter);
                            }

                            // Update the table with the adjusted value
                            tableModel.setValueAt(newParameter, row, column);
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid parameter value!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            else if (column == 5){
                String newPa = (String) tableModel.getValueAt(row, column);

                // Find the correct device based on the row
                String deviceName = (String) tableModel.getValueAt(row, 1); // Get the device name from the table
                User owner = allUsers.stream()
                        .filter(user -> user.getUsername().equals(tableModel.getValueAt(row, 0))) // Match the owner
                        .findFirst()
                        .orElse(null);

                if (owner != null) {
                    Device device = owner.getDevices().stream()
                            .filter(d -> d.getName().equals(deviceName)) // Match the device name
                            .findFirst()
                            .orElse(null);

                    if (device != null) {
                        device.setUserEditable(UserEditable.valueOf(newPa)); // Update the actual device status
                    }
                }
            }
        });

        // Remove the old table if it exists
        if (tableScrollPane != null) {
            remove(tableScrollPane);
        }

        // Add the new table
        if (deviceTable.isEditing()) {
            deviceTable.getCellEditor().stopCellEditing();
        }
        

        tableScrollPane = new JScrollPane(deviceTable);
        add(tableScrollPane, BorderLayout.CENTER);

        

        // Refresh the UI
        revalidate();
        repaint();
    }

}
