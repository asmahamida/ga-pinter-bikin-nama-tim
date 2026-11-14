/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.view;

import pojat.controller.LoginController;
import pojat.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    private LoginController loginController;

    public LoginFrame() {

        loginController = new LoginController();

        setTitle("POJAT - Login");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel utama
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 245, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Judul
        JLabel lblTitle = new JLabel("POJAT");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 30));
        lblTitle.setForeground(new Color(35, 80, 130));
        lblTitle.setHorizontalAlignment(JLabel.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblTitle, gbc);

        // Subjudul
        JLabel lblSubtitle = new JLabel(
            "Platform Pemesanan UMKM"
        );
        lblSubtitle.setHorizontalAlignment(JLabel.CENTER);

        gbc.gridy = 1;
        panel.add(lblSubtitle, gbc);

        // Label username
        JLabel lblUsername = new JLabel("Username");
        gbc.gridy = 2;
        panel.add(lblUsername, gbc);

        // Input username
        txtUsername = new JTextField(20);
        gbc.gridy = 3;
        panel.add(txtUsername, gbc);

        // Label password
        JLabel lblPassword = new JLabel("Password");
        gbc.gridy = 4;
        panel.add(lblPassword, gbc);

        // Input password
        txtPassword = new JPasswordField(20);
        gbc.gridy = 5;
        panel.add(txtPassword, gbc);

        // Tombol login
        btnLogin = new JButton("LOGIN");
        btnLogin.setBackground(new Color(35, 80, 130));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);

        gbc.gridy = 6;
        panel.add(btnLogin, gbc);

        // Event tombol login
        btnLogin.addActionListener(e -> prosesLogin());

        // Tekan Enter untuk login
        txtPassword.addActionListener(e -> prosesLogin());

        add(panel);

    }

    private void prosesLogin() {

        String username = txtUsername.getText().trim();

        char[] passwordChars = txtPassword.getPassword();
        String password = new String(passwordChars);
        java.util.Arrays.fill(passwordChars, '\0');

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Username dan password harus diisi!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        User user = loginController.login(
            username,
            password
        );

        if (user != null) {

                JOptionPane.showMessageDialog(
        this,
        "Selamat datang, " + user.getNamaLengkap()
    );

    switch (user.getRole().toUpperCase()) {

        case "SUPERADMIN":
            new DashboardAdmin(user).setVisible(true);
            break;

        case "UMKM":
            new DashboardUMKM(user).setVisible(true);
            break;

        case "PEMBELI":
            new DashboardPembeli(user).setVisible(true);
            break;

        default:
            JOptionPane.showMessageDialog(
                this,
                "Role pengguna tidak dikenali!"
            );
            return;
    }

    dispose();

               
    
        } else {

            JOptionPane.showMessageDialog(
                this,
                "Username atau password salah!",
                "Login Gagal",
                JOptionPane.ERROR_MESSAGE
            );

            txtPassword.setText("");
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });

    }
}
