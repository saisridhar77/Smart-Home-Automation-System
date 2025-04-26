package gui;

import devices.*;
import enums.DeviceStatus;
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

        JButton createUserButton = new JButton("Create New User");
        createUserButton.addActionListener(e -> createNewUser());

        topPanel.add(logoutButton, BorderLayout.EAST);
        topPanel.add(assignDevicesButton, BorderLayout.CENTER);
        topPanel.add(createUserButton, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        Object[][] deviceData = getDeviceData();
        String[] columnNames = {"Owner","Device Name", "Device Type", "Status", "Temperature"};

        DefaultTableModel tableModel = new DefaultTableModel(deviceData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }
        };

        JTable deviceTable = new JTable(tableModel);

        TableColumn statusColumn = deviceTable.getColumnModel().getColumn(2);
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"ON", "OFF"});
        statusColumn.setCellEditor(new DefaultCellEditor(statusComboBox));

        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (column == 3) {
                String newStatus = (String) tableModel.getValueAt(row, column);
                adminUser.getDevices().get(row).setStatus(DeviceStatus.valueOf(newStatus));
            } else if (column == 4) {
                try {
                    int newTemperature = Integer.parseInt(tableModel.getValueAt(row, column).toString());
                    if (adminUser.getDevices().get(row) instanceof AirConditioner) {
                        if (newTemperature < 16) newTemperature = 16;
                        if (newTemperature > 30) newTemperature = 30;
                        ((AirConditioner) adminUser.getDevices().get(row)).adjustSetting(newTemperature);
                        tableModel.setValueAt(newTemperature, row, column);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid temperature value!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(deviceTable);
        add(scrollPane, BorderLayout.CENTER);
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
                Object[] row = new Object[5];
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

                deviceRows.add(row);
            }
        }

        Object[][] data = new Object[deviceRows.size()][5];
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
}
