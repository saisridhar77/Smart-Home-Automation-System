// AdminDashboard.java
package gui;

import java.awt.*;
import javax.swing.*;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome, Admin", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Add more components for device control and monitoring
    }
}