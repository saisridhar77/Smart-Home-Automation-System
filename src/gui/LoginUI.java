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
    private List<User> users; // List of users (Admin and RegularUsers)

    public LoginUI(List<User> users) {
        this.users = users;

        setTitle("Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
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

        loginButton.addActionListener((ActionEvent e) -> performLogin());
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User loggedInUser = null;

        for (User user : users) {
            if (user.getUsername().equals(username) && user.checkPassword(password)) {
                loggedInUser = user;
                break;
            }
        }

        if (loggedInUser != null) {
            dispose();
            launchDashboard(loggedInUser);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void launchDashboard(User user) {
        if (user instanceof Admin) {
            AdminDashboard adminDashboard = new AdminDashboard((Admin) user, users);
            adminDashboard.setVisible(true);
        } else {
            AdminDashboard adminDashboard = new AdminDashboard(getAdminUser(), users);
            UserDashboard userDashboard = new UserDashboard(user, adminDashboard);
            userDashboard.setVisible(true);
        }
    }

    private Admin getAdminUser() {
        for (User user : users) {
            if (user instanceof Admin) {
                return (Admin) user;
            }
        }
        return null; // Should not happen if admin user exists
    }
}
