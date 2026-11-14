/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.view;

import pojat.model.User;
import javax.swing.*;
import java.awt.*;

public class DashboardUMKM extends JFrame {
        public DashboardUMKM(User user) {

        setTitle("POJAT - Dashboard UMKM");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel title = new JLabel(
            "DASHBOARD ADMIN UMKM",
            SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel welcome = new JLabel(
            "Selamat datang, " + user.getNamaLengkap(),
            SwingConstants.CENTER
        );

        JButton btnLogout = new JButton("Logout");

        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        setLayout(new GridLayout(3, 1, 10, 10));

        add(title);
        add(welcome);
        add(btnLogout);
    }
}
