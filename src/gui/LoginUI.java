package gui;

import enums.UserRole;
import devices.Device;
import users.Admin;
import users.RegularUser;
import users.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private User loggedInUser;
    private List<Device> devices; // Shared list of devices

    public LoginUI(List<Device> devices) {
        this.devices = devices; // Store the shared devices list

        setTitle("Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Smart Home Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(50, 150, 250));
        loginButton.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        add(panel, BorderLayout.CENTER);

        loginButton.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if ("admin".equals(username) && "admin123".equals(password)) {
                loggedInUser = new Admin(username, password);
            } else if ("user".equals(username) && "user123".equals(password)) {
                loggedInUser = new RegularUser(username, password);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dispose();
            launchDashboard();
        });
    }

    private void launchDashboard() {
        if (loggedInUser.getRole() == UserRole.ADMIN) {
            AdminDashboard adminDashboard = new AdminDashboard(devices); // Create AdminDashboard
            adminDashboard.setVisible(true);
        } else {
            AdminDashboard adminDashboard = new AdminDashboard(devices); // Create AdminDashboard
            UserDashboard userDashboard = new UserDashboard(devices, adminDashboard); // Pass AdminDashboard to UserDashboard
            userDashboard.setVisible(true);
        }
    }
}