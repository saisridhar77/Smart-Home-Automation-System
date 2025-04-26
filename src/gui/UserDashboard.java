package gui;

import devices.*;
import users.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class UserDashboard extends JFrame {
    private User loggedInUser;
    private AdminDashboard adminDashboard;
    private DefaultTableModel tableModel;
    private JTable deviceTable;

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

        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Device Name", "Device Type", "Status", "Temperature"};
        tableModel = new DefaultTableModel(getDeviceData(), columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return adminDashboard.isEditingEnabled() && (column == 2 || column == 3);
            }
        };

        deviceTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(deviceTable);
        add(scrollPane, BorderLayout.CENTER);

        adminDashboard.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("isEditingEnabled".equals(evt.getPropertyName())) {
                    refreshTableModel();
                }
            }
        });
    }

    private Object[][] getDeviceData() {
        List<Device> devices = loggedInUser.getDevices();
        Object[][] data = new Object[devices.size()][4];
        for (int i = 0; i < devices.size(); i++) {
            Device device = devices.get(i);
            data[i][0] = device.getName();
            data[i][1] = device.getClass().getSimpleName();
            data[i][2] = device.getStatus().toString();
            data[i][3] = (device instanceof AirConditioner) ? ((AirConditioner) device).getTemperature() : "N/A";
        }
        return data;
    }

    private void refreshTableModel() {
        tableModel.setDataVector(getDeviceData(), new String[]{"Device Name", "Device Type", "Status", "Temperature"});
    }
}
