/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.view;

import pojat.model.User;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private User user;

    public DashboardFrame(User user) {

        this.user = user;

        setTitle("POJAT - Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        // Bagian atas
        JLabel lblTitle = new JLabel(
            "DASHBOARD POJAT",
            SwingConstants.CENTER
        );

        lblTitle.setFont(
            new Font("Arial", Font.BOLD, 25)
        );

        lblTitle.setForeground(
            new Color(35, 80, 130)
        );

        panel.add(lblTitle, BorderLayout.NORTH);

        // Informasi pengguna
        JPanel infoPanel = new JPanel(
            new GridLayout(4, 1, 5, 5)
        );

        infoPanel.setBackground(Color.WHITE);

        infoPanel.add(
            new JLabel("Selamat datang, " +
                user.getNamaLengkap())
        );

        infoPanel.add(
            new JLabel("Username: " +
                user.getUsername())
        );

        infoPanel.add(
            new JLabel("Role: " +
                user.getRole())
        );

        infoPanel.add(
            new JLabel("ID Pengguna: " +
                user.getIdUser())
        );

        panel.add(infoPanel, BorderLayout.CENTER);

        // Tombol logout
        JButton btnLogout = new JButton("LOGOUT");

        btnLogout.addActionListener(e -> {

            new LoginFrame().setVisible(true);

            dispose();

        });

        panel.add(btnLogout, BorderLayout.SOUTH);

        add(panel);
    }
}
