package gui;

import devices.*;
import enums.DeviceStatus;
import enums.UserEditable;
import users.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UserDashboard extends JFrame {
    private User loggedInUser;
    private AdminDashboard adminDashboard;
    private JTable deviceTable;
    private JScrollPane scrollPane;

    public UserDashboard(User user, AdminDashboard adminDashboard) {
        this.loggedInUser = user;
        this.adminDashboard = adminDashboard;

        setTitle("User Dashboard - " + user.getUsername());
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
            new LoginUI(adminDashboard.allUsers).setVisible(true);
        });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadDevices());

        topPanel.add(logoutButton, BorderLayout.EAST);
        topPanel.add(refreshButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        loadDevices(); // Load table on startup
    }

    private void loadDevices() {
        Object[][] deviceData = getDeviceData();
        String[] columnNames = {"Owner", "Device Name", "Device Type", "Status", "Parameter", "User Editable"};

        DefaultTableModel tableModel = new DefaultTableModel(deviceData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only allow editing Status (3) or Parameter (4) if userEditable == "Yes"
                String userEditableFlag = (String) getValueAt(row, 5);
                if (!"Yes".equals(userEditableFlag)) {
                    return false;
                }
                return column == 3 || column == 4;
            }
        };

        deviceTable = new JTable(tableModel);

        // Set combo box editor for Status column
        TableColumn statusColumn = deviceTable.getColumnModel().getColumn(3);
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"ON", "OFF"});
        statusColumn.setCellEditor(new DefaultCellEditor(statusComboBox));

        // Add listener to handle model updates
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (row >= 0 && column >= 0) {
                updateDeviceFromTable(row, column, tableModel);
            }
        });

        // Remove old scrollpane if any
        if (scrollPane != null) {
            remove(scrollPane);
        }

        scrollPane = new JScrollPane(deviceTable);
        add(scrollPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private Object[][] getDeviceData() {
        List<Object[]> deviceRows = new ArrayList<>();

        for (Device device : loggedInUser.getDevices()) {
            Object[] row = new Object[6];
            row[0] = loggedInUser.getUsername(); // Owner
            row[1] = device.getName();           // Device Name
            row[2] = device.getClass().getSimpleName(); // Device Type
            row[3] = device.getStatus().toString();     // Status

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

        Object[][] data = new Object[deviceRows.size()][6];
        for (int i = 0; i < deviceRows.size(); i++) {
            data[i] = deviceRows.get(i);
        }
        return data;
    }

    private void updateDeviceFromTable(int row, int column, DefaultTableModel tableModel) {
        String deviceName = (String) tableModel.getValueAt(row, 1);
        Device device = loggedInUser.getDevices().stream()
                .filter(d -> d.getName().equals(deviceName))
                .findFirst()
                .orElse(null);

        if (device == null) return;

        try {
            if (column == 3) { // Status
                String newStatus = (String) tableModel.getValueAt(row, column);
                device.setStatus(DeviceStatus.valueOf(newStatus));
            } else if (column == 4) { // Parameter
                int newParam = Integer.parseInt(tableModel.getValueAt(row, column).toString());

                if (device instanceof AirConditioner) {
                    ((AirConditioner) device).adjustSetting(newParam);
                } else if (device instanceof Fan) {
                    ((Fan) device).adjustSetting(Math.max(1, Math.min(5, newParam)));
                } else if (device instanceof Light) {
                    ((Light) device).adjustSetting(Math.max(0, Math.min(100, newParam)));
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
