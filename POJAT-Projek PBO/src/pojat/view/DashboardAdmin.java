/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.view;

import pojat.model.User;

import javax.swing.*;
import java.awt.*;

public class DashboardAdmin extends JFrame {
        private User user;

    public DashboardAdmin(User user) {

        this.user = user;

        setTitle("POJAT - Dashboard Super Admin");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel utama
        JPanel panelUtama = new JPanel(new BorderLayout());
        panelUtama.setBackground(new Color(240, 245, 250));

        // Header
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(35, 80, 130));
        panelHeader.setBorder(
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel lblJudul = new JLabel("POJAT | SUPER ADMIN");
        lblJudul.setFont(new Font("Arial", Font.BOLD, 24));
        lblJudul.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");

        panelHeader.add(lblJudul, BorderLayout.WEST);
        panelHeader.add(btnLogout, BorderLayout.EAST);

        // Konten dashboard
        JPanel panelKonten = new JPanel();
        panelKonten.setLayout(new GridLayout(3, 2, 20, 20));
        panelKonten.setBackground(new Color(240, 245, 250));
        panelKonten.setBorder(
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        JLabel lblSelamatDatang = new JLabel(
            "Selamat datang, " + user.getNamaLengkap()
        );
        lblSelamatDatang.setFont(
            new Font("Arial", Font.BOLD, 22)
        );

        JLabel lblUsername = new JLabel(
            "Username: " + user.getUsername()
        );

        JLabel lblRole = new JLabel(
            "Role: " + user.getRole()
        );

        JLabel lblMenu = new JLabel("MENU PENGELOLAAN");
        lblMenu.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnKelolaUMKM = new JButton("Kelola Data UMKM");
        JButton btnKelolaMenu = new JButton("Kelola Menu Produk");
        JButton btnKelolaPesanan = new JButton("Kelola Pesanan");
        JButton btnKelolaPengguna = new JButton("Kelola Pengguna");
        JButton btnKelolaBatch = new JButton("Kelola Batch PO");
        JButton btnStatistik = new JButton("Dashboard Statistik");

        panelKonten.add(lblSelamatDatang);
        panelKonten.add(lblUsername);
        panelKonten.add(lblRole);
        panelKonten.add(lblMenu);
        panelKonten.add(btnKelolaUMKM);
        panelKonten.add(btnKelolaMenu);

        // Panel menu tambahan
        JPanel panelBawah = new JPanel(new GridLayout(1, 3, 20, 0));
        panelBawah.setBackground(new Color(240, 245, 250));
        panelBawah.setBorder(
            BorderFactory.createEmptyBorder(0, 30, 30, 30)
        );

        panelBawah.add(btnKelolaPesanan);
        panelBawah.add(btnKelolaPengguna);
        panelBawah.add(btnKelolaBatch);
        panelBawah.add(btnStatistik);

        JPanel panelTengah = new JPanel(new BorderLayout());
        panelTengah.setBackground(new Color(240, 245, 250));
        panelTengah.add(panelKonten, BorderLayout.CENTER);
        panelTengah.add(panelBawah, BorderLayout.SOUTH);

        panelUtama.add(panelHeader, BorderLayout.NORTH);
        panelUtama.add(panelTengah, BorderLayout.CENTER);

        add(panelUtama);

        // Tombol logout
        btnKelolaBatch.addActionListener(e -> {
            new DataBatchFrame(user).setVisible(true);
            dispose();
        });
        
        btnKelolaMenu.addActionListener(e -> {
            new DataMenuFrame(user).setVisible(true);
            dispose();
        });
        
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        
        btnStatistik.addActionListener(e -> {
            new DashboardStatistikFrame(user).setVisible(true);
            dispose();
        });
        
        btnKelolaPesanan.addActionListener(e -> {
            new DataPesananFrame(user).setVisible(true);
            dispose();
        });
        
        btnKelolaUMKM.addActionListener(e -> {
        new DataUMKMFrame(user).setVisible(true);
        dispose();
});
    }
}
