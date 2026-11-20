/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pojat.view;

import pojat.model.User;
import javax.swing.*;
import java.awt.*;

public class DashboardPembeli extends JFrame {

    public DashboardPembeli(User user) {

        setTitle("POJAT - Dashboard Pembeli");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(15, 15));

        JLabel title = new JLabel(
            "DASHBOARD PEMBELI",
            SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel welcome = new JLabel(
            "Selamat datang, " + user.getNamaLengkap(),
            SwingConstants.CENTER
        );
        welcome.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton btnKatalog = new JButton("Katalog Menu");
        JButton btnLogout = new JButton("Logout");
        JButton btnRiwayat = new JButton("Riwayat Pesanan");

        JPanel panelMenu = new JPanel(new GridLayout(2, 1, 15, 15));
        panelMenu.setBorder(BorderFactory.createEmptyBorder(40, 150, 40, 150));

        panelMenu.add(btnKatalog);
        panelMenu.add(btnLogout);
        panelMenu.add(btnRiwayat);

        btnKatalog.addActionListener(e -> {
            new KatalogMenuFrame(user).setVisible(true);
            dispose();
        });
        
        btnRiwayat.addActionListener(e -> {
            new RiwayatPesananFrame(user).setVisible(true);
            dispose();
        });

        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        add(title, BorderLayout.NORTH);
        add(welcome, BorderLayout.CENTER);
        add(panelMenu, BorderLayout.SOUTH);
    }
}

