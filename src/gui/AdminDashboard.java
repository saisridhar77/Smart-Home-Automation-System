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
    private JScrollPane tableScrollPane;

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
        assignDevicesButton.addActionListener(e -> {
            try {
                assignDevicesToUser();
            } catch (Exception ex) {
                showError("Error assigning devices: " + ex.getMessage());
            }
        });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            try {
                loadDevices();
            } catch (Exception ex) {
                showError("Error refreshing devices: " + ex.getMessage());
            }
        });

        JButton createUserButton = new JButton("Create New User");
        createUserButton.addActionListener(e -> {
            try {
                createNewUser();
            } catch (Exception ex) {
                showError("Error creating new user: " + ex.getMessage());
            }
        });

        topPanel.add(logoutButton, BorderLayout.EAST);
        topPanel.add(assignDevicesButton, BorderLayout.CENTER);
        topPanel.add(refreshButton, BorderLayout.AFTER_LAST_LINE);
        topPanel.add(createUserButton, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);
        loadDevices();
    }

    private void assignDevicesToUser() {
        try {
            String[] usernames = allUsers.stream()
                    .filter(u -> !(u instanceof Admin))
                    .map(User::getUsername)
                    .toArray(String[]::new);

            if (usernames.length == 0) {
                showError("No regular users available!");
                return;
            }

            String selectedUser = (String) JOptionPane.showInputDialog(this, "Select user:", "Assign Devices",
                    JOptionPane.PLAIN_MESSAGE, null, usernames, usernames[0]);

            if (selectedUser == null) return;

            User user = allUsers.stream()
                    .filter(u -> u.getUsername().equals(selectedUser))
                    .findFirst()
                    .orElseThrow(() -> new Exception("Selected user not found."));

            List<Device> selectedDevices = selectDevicesPopup();
            if (!selectedDevices.isEmpty()) {
                user.addDevices(selectedDevices.toArray(new Device[0]));
                JOptionPane.showMessageDialog(this, "Devices assigned successfully!");
            }
        } catch (Exception e) {
            showError("Failed to assign devices: " + e.getMessage());
        }
    }

    private List<Device> selectDevicesPopup() {
        List<Device> selectedDevices = new ArrayList<>();
        try {
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
                    throw new IllegalArgumentException("Device ID and Name cannot be empty!");
                }

                Device newDevice;
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
                        throw new IllegalArgumentException("Unknown device type!");
                }

                selectedDevices.add(newDevice);
            }
        } catch (Exception e) {
            showError("Failed to add device: " + e.getMessage());
        }

        return selectedDevices;
    }

    private void createNewUser() {
        try {
            JTextField usernameField = new JTextField();
            JTextField passwordField = new JTextField();
            Object[] fields = {"Username:", usernameField, "Password:", passwordField};

            int option = JOptionPane.showConfirmDialog(this, fields, "Create New User", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String username = usernameField.getText().trim();
                String password = passwordField.getText().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    throw new IllegalArgumentException("Username and password cannot be empty.");
                }

                if (allUsers.stream().anyMatch(u -> u.getUsername().equals(username))) {
                    throw new IllegalArgumentException("Username already exists!");
                }
                allUsers.add(new RegularUser(username, password));

                JOptionPane.showMessageDialog(this, "User created successfully!");
            }
        } catch (Exception e) {
            showError("Failed to create user: " + e.getMessage());
        }
    }

    private Object[][] getDeviceData() {
        List<Object[]> deviceRows = new ArrayList<>();

        for (User user : allUsers) {
            for (Device device : user.getDevices()) {
                Object[] row = new Object[6];
                row[0] = user.getUsername();
                row[1] = device.getName();
                row[2] = device.getClass().getSimpleName();
                row[3] = device.getStatus().toString();

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

        Object[][] data = new Object[deviceRows.size()][6];
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
        try {
            Object[][] deviceData = getDeviceData();
            String[] columnNames = {"Owner", "Device Name", "Device Type", "Status", "Parameter", "User Editable"};

            DefaultTableModel tableModel = new DefaultTableModel(deviceData, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 3 || column == 4 || column == 5;
                }
            };

            JTable deviceTable = new JTable(tableModel);

            TableColumn statusColumn = deviceTable.getColumnModel().getColumn(3);
            JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"ON", "OFF"});
            statusColumn.setCellEditor(new DefaultCellEditor(statusComboBox));

            TableColumn editableColumn = deviceTable.getColumnModel().getColumn(5);
            JComboBox<String> editableComboBox = new JComboBox<>(new String[]{"Yes", "No"});
            editableColumn.setCellEditor(new DefaultCellEditor(editableComboBox));

            tableModel.addTableModelListener(e -> {
                int row = e.getFirstRow();
                int column = e.getColumn();
                try {
                    String deviceName = (String) tableModel.getValueAt(row, 1);
                    String ownerName = (String) tableModel.getValueAt(row, 0);

                    User owner = allUsers.stream()
                            .filter(u -> u.getUsername().equals(ownerName))
                            .findFirst()
                            .orElseThrow(() -> new Exception("User not found"));

                    Device device = owner.getDevices().stream()
                            .filter(d -> d.getName().equals(deviceName))
                            .findFirst()
                            .orElseThrow(() -> new Exception("Device not found"));

                    if (column == 3) {
                        String newStatus = (String) tableModel.getValueAt(row, column);
                        device.setStatus(DeviceStatus.valueOf(newStatus));
                    } else if (column == 4) {
                        int newParam = Integer.parseInt(tableModel.getValueAt(row, column).toString());
                        if (device instanceof AirConditioner) {
                            ((AirConditioner) device).adjustSetting(newParam);
                        } else if (device instanceof Fan) {
                            ((Fan) device).adjustSetting(Math.min(Math.max(newParam, 1), 5));
                        } else if (device instanceof Light) {
                            ((Light) device).adjustSetting(Math.min(Math.max(newParam, 0), 100));
                        }
                        tableModel.setValueAt(newParam, row, column);
                    } else if (column == 5) {
                        String editable = (String) tableModel.getValueAt(row, column);
                        device.setUserEditable(UserEditable.valueOf(editable));
                    }

                } catch (Exception ex) {
                    showError("Failed to update device: " + ex.getMessage());
                }
            });

            if (tableScrollPane != null) {
                remove(tableScrollPane);
            }

            if (deviceTable.isEditing()) {
                deviceTable.getCellEditor().stopCellEditing();
            }

            tableScrollPane = new JScrollPane(deviceTable);
            add(tableScrollPane, BorderLayout.CENTER);

            revalidate();
            repaint();
        } catch (Exception e) {
            showError("Failed to load devices: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
