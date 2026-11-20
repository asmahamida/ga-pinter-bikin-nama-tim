/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pojat.view;

import pojat.dao.KatalogMenuDAO;
import pojat.model.MenuItem;
import pojat.model.User;
import pojat.util.KeranjangPOJAT;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class KatalogMenuFrame extends JFrame {

    private User user;
    private KatalogMenuDAO katalogDAO;
    private JTable tabelMenu;
    private DefaultTableModel model;

    public KatalogMenuFrame(User user) {
        this.user = user;
        this.katalogDAO = new KatalogMenuDAO();

        setTitle("Katalog Menu POJAT");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        tampilkanMenu();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel judul = new JLabel("KATALOG MENU UMKM", JLabel.CENTER);
        judul.setFont(new Font("Arial", Font.BOLD, 22));
        add(judul, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new Object[]{"ID Menu", "Nama Menu", "Deskripsi", "Harga", "Stok"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelMenu = new JTable(model);
        tabelMenu.setRowHeight(25);
        add(new JScrollPane(tabelMenu), BorderLayout.CENTER);

        JPanel panelButton = new JPanel(new FlowLayout());

        JButton btnRefresh = new JButton("Refresh");
        JButton btnTambah = new JButton("Tambah ke Keranjang");
        JButton btnKeranjang = new JButton("Keranjang");
        JButton btnKembali = new JButton("Kembali");

        panelButton.add(btnRefresh);
        panelButton.add(btnTambah);
        panelButton.add(btnKeranjang);
        panelButton.add(btnKembali);

        add(panelButton, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> tampilkanMenu());
        btnTambah.addActionListener(e -> tambahKeranjang());
        btnKeranjang.addActionListener(e -> tampilkanKeranjang());

        btnKembali.addActionListener(e -> {
            new DashboardPembeli(user).setVisible(true);
            dispose();
        });
    }

    private void tampilkanMenu() {
        model.setRowCount(0);

        try {
            List<MenuItem> daftar = katalogDAO.getSemuaMenu();

            for (MenuItem menu : daftar) {
                model.addRow(new Object[]{
                    menu.getIdMenu(),
                    menu.getNamaMenu(),
                    menu.getDeskripsi(),
                    menu.getHarga(),
                    menu.getStokPerBatch()
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this, "Gagal menampilkan menu: " + e.getMessage()
            );
        }
    }

    private void tambahKeranjang() {
        int baris = tabelMenu.getSelectedRow();

        if (baris == -1) {
            JOptionPane.showMessageDialog(this, "Pilih menu terlebih dahulu!");
            return;
        }

        int idMenu = (int) model.getValueAt(baris, 0);
        String namaMenu = model.getValueAt(baris, 1).toString();
        double harga = Double.parseDouble(model.getValueAt(baris, 3).toString());
        int stok = Integer.parseInt(model.getValueAt(baris, 4).toString());

        if (stok <= 0) {
            JOptionPane.showMessageDialog(this, "Stok menu habis!");
            return;
        }

        String input = JOptionPane.showInputDialog(
            this, "Jumlah " + namaMenu + " yang ingin dibeli:"
        );

        if (input == null) return;

        try {
            int jumlah = Integer.parseInt(input);

            if (jumlah <= 0 || jumlah > stok) {
                JOptionPane.showMessageDialog(
                    this, "Jumlah harus antara 1 sampai " + stok
                );
                return;
            }

            KeranjangPOJAT.tambah(idMenu, namaMenu, harga, jumlah, stok);

            JOptionPane.showMessageDialog(
                this, namaMenu + " berhasil ditambahkan ke keranjang!"
            );

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka!");
        }
    }

    private void tampilkanKeranjang() {
        new KeranjangFrame(user).setVisible(true);
    }
}
