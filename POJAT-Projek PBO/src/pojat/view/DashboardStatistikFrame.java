/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.view;

import pojat.dao.StatistikDAO;
import pojat.model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class DashboardStatistikFrame extends JFrame {

    private User user;
    private StatistikDAO statistikDAO;

    private JLabel lblUMKM;
    private JLabel lblMenu;
    private JLabel lblPesanan;
    private JLabel lblBatch;
    private JLabel lblOmzet;

    public DashboardStatistikFrame(User user) {

        this.user = user;
        statistikDAO = new StatistikDAO();

        setTitle("POJAT - Statistik Admin");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        tampilkanStatistik();
    }

    private void initComponents() {

        JPanel panelUtama = new JPanel(new BorderLayout(15, 15));
        panelUtama.setBackground(new Color(240, 245, 250));
        panelUtama.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel judul = new JLabel(
                "STATISTIK POJAT", JLabel.CENTER);

        judul.setFont(new Font("Arial", Font.BOLD, 26));

        panelUtama.add(judul, BorderLayout.NORTH);

        JPanel panelStatistik = new JPanel(
                new GridLayout(2, 2, 20, 20));

        panelStatistik.setBackground(new Color(240, 245, 250));

        lblUMKM = buatLabelStatistik();
        lblMenu = buatLabelStatistik();
        lblPesanan = buatLabelStatistik();
        lblBatch = buatLabelStatistik();
        lblOmzet = buatLabelStatistik();

        panelStatistik.add(buatCard("TOTAL UMKM", lblUMKM));
        panelStatistik.add(buatCard("TOTAL MENU", lblMenu));
        panelStatistik.add(buatCard("TOTAL PESANAN", lblPesanan));
        panelStatistik.add(buatCard("TOTAL BATCH PO", lblBatch));

        JPanel panelTengah = new JPanel(new BorderLayout(15, 15));
        panelTengah.setBackground(new Color(240, 245, 250));

        panelTengah.add(panelStatistik, BorderLayout.CENTER);

        JPanel panelOmzet = buatCard("TOTAL OMZET PESANAN", lblOmzet);
        panelOmzet.setPreferredSize(new Dimension(0, 120));

        panelTengah.add(panelOmzet, BorderLayout.SOUTH);

        panelUtama.add(panelTengah, BorderLayout.CENTER);

        JPanel panelButton = new JPanel(new FlowLayout());
        panelButton.setBackground(new Color(240, 245, 250));

        JButton btnRefresh = new JButton("Refresh Statistik");
        JButton btnKembali = new JButton("Kembali");

        panelButton.add(btnRefresh);
        panelButton.add(btnKembali);

        panelUtama.add(panelButton, BorderLayout.PAGE_END);

        btnRefresh.addActionListener(e -> tampilkanStatistik());

        btnKembali.addActionListener(e -> {
            new DashboardAdmin(user).setVisible(true);
            dispose();
        });

        add(panelUtama);
    }

    private JLabel buatLabelStatistik() {

        JLabel label = new JLabel("-", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 30));
        label.setForeground(new Color(35, 80, 130));

        return label;
    }

    private JPanel buatCard(String judul, JLabel nilai) {

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(210, 220, 230)),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        JLabel labelJudul = new JLabel(judul, JLabel.CENTER);
        labelJudul.setFont(new Font("Arial", Font.BOLD, 16));

        card.add(labelJudul, BorderLayout.NORTH);
        card.add(nilai, BorderLayout.CENTER);

        return card;
    }

    // ==========================================
    // MEMUAT STATISTIK DATABASE
    // ==========================================
    private void tampilkanStatistik() {

        try {

            lblUMKM.setText(
                    String.valueOf(statistikDAO.getTotalUMKM()));

            lblMenu.setText(
                    String.valueOf(statistikDAO.getTotalMenu()));

            lblPesanan.setText(
                    String.valueOf(statistikDAO.getTotalPesanan()));

            lblBatch.setText(
                    String.valueOf(statistikDAO.getTotalBatch()));

            double omzet = statistikDAO.getTotalOmzet();

            NumberFormat formatRupiah =
                    NumberFormat.getCurrencyInstance(
                            new Locale("id", "ID"));

            lblOmzet.setText(formatRupiah.format(omzet));

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(this,
                    "Gagal memuat statistik: " + e.getMessage());
        }
    }
}
