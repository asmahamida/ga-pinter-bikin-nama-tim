/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pojat.view;

import pojat.dao.MenuItemDAO;
import pojat.model.MenuItem;
import pojat.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DataMenuFrame extends JFrame {

    private JTable tabelMenu;
    private DefaultTableModel model;
    private MenuItemDAO menuDAO;
    private User user;

    public DataMenuFrame(User user) {
        this.user = user;
        menuDAO = new MenuItemDAO();

        setTitle("POJAT - Kelola Menu");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        tampilkanData();
    }

    private void initComponents() {

        setLayout(new BorderLayout(10, 10));

        // HEADER
        JLabel lblJudul = new JLabel("KELOLA MENU UMKM", JLabel.CENTER);
        lblJudul.setFont(new Font("Arial", Font.BOLD, 24));
        lblJudul.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        add(lblJudul, BorderLayout.NORTH);

        // TABEL
        String[] kolom = {
            "ID Menu", "ID UMKM", "Nama Menu",
            "Deskripsi", "Harga", "Stok / Batch"
        };

        model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelMenu = new JTable(model);
        tabelMenu.setRowHeight(25);
        tabelMenu.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane = new JScrollPane(tabelMenu);
        add(scrollPane, BorderLayout.CENTER);

        // BUTTON
        JPanel panelButton = new JPanel(new FlowLayout());

        JButton btnTambah = new JButton("Tambah Menu");
        JButton btnEdit = new JButton("Edit Menu");
        JButton btnHapus = new JButton("Hapus Menu");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnKembali = new JButton("Kembali");

        panelButton.add(btnTambah);
        panelButton.add(btnEdit);
        panelButton.add(btnHapus);
        panelButton.add(btnRefresh);
        panelButton.add(btnKembali);

        add(panelButton, BorderLayout.SOUTH);

        // ACTION LISTENER
        btnTambah.addActionListener(e -> tambahData());
        btnEdit.addActionListener(e -> editData());
        btnHapus.addActionListener(e -> hapusData());
        btnRefresh.addActionListener(e -> tampilkanData());

        btnKembali.addActionListener(e -> {
            new DashboardAdmin(user).setVisible(true);
            dispose();
        });
    }

    // ==========================================
    // MENAMPILKAN DATA MENU
    // ==========================================
    private void tampilkanData() {

        model.setRowCount(0);

        try {
            List<MenuItem> daftarMenu = menuDAO.getAllMenu();

            for (MenuItem menu : daftarMenu) {
                model.addRow(new Object[]{
                    menu.getIdMenu(),
                    menu.getIdUmkm(),
                    menu.getNamaMenu(),
                    menu.getDeskripsi(),
                    menu.getHarga(),
                    menu.getStokPerBatch()
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal mengambil data menu: " + e.getMessage());
        }
    }

    // ==========================================
    // TAMBAH MENU
    // ==========================================
    private void tambahData() {

        JTextField txtIdUmkm = new JTextField();
        JTextField txtNama = new JTextField();
        JTextField txtDeskripsi = new JTextField();
        JTextField txtHarga = new JTextField();
        JTextField txtStok = new JTextField();

        Object[] form = {
            "ID UMKM:", txtIdUmkm,
            "Nama Menu:", txtNama,
            "Deskripsi:", txtDeskripsi,
            "Harga:", txtHarga,
            "Stok per Batch:", txtStok
        };

        int pilihan = JOptionPane.showConfirmDialog(
            this, form, "Tambah Menu",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (pilihan != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int idUmkm = Integer.parseInt(txtIdUmkm.getText().trim());
            String nama = txtNama.getText().trim();
            String deskripsi = txtDeskripsi.getText().trim();
            double harga = Double.parseDouble(txtHarga.getText().trim());
            int stok = Integer.parseInt(txtStok.getText().trim());

            if (nama.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Nama menu tidak boleh kosong!");
                return;
            }

            if (harga < 0 || stok < 0 || idUmkm <= 0) {
                JOptionPane.showMessageDialog(this,
                    "ID UMKM, harga, dan stok harus valid!");
                return;
            }

            MenuItem menu = new MenuItem(
                0, idUmkm, nama, deskripsi, harga, stok
            );

            if (menuDAO.tambahMenu(menu)) {
                JOptionPane.showMessageDialog(this,
                    "Menu berhasil ditambahkan!");
                tampilkanData();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "ID UMKM, harga, dan stok harus berupa angka!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal menambahkan menu: " + e.getMessage());
        }
    }

    // ==========================================
    // EDIT MENU
    // ==========================================
    private void editData() {

        int baris = tabelMenu.getSelectedRow();

        if (baris == -1) {
            JOptionPane.showMessageDialog(this,
                "Pilih menu yang ingin diedit terlebih dahulu!");
            return;
        }

        int idMenu = (int) model.getValueAt(baris, 0);
        int idUmkm = (int) model.getValueAt(baris, 1);
        String nama = model.getValueAt(baris, 2).toString();
        String deskripsi = model.getValueAt(baris, 3).toString();
        String harga = model.getValueAt(baris, 4).toString();
        String stok = model.getValueAt(baris, 5).toString();

        JTextField txtIdUmkm = new JTextField(String.valueOf(idUmkm));
        JTextField txtNama = new JTextField(nama);
        JTextField txtDeskripsi = new JTextField(deskripsi);
        JTextField txtHarga = new JTextField(harga);
        JTextField txtStok = new JTextField(stok);

        Object[] form = {
            "ID UMKM:", txtIdUmkm,
            "Nama Menu:", txtNama,
            "Deskripsi:", txtDeskripsi,
            "Harga:", txtHarga,
            "Stok per Batch:", txtStok
        };

        int pilihan = JOptionPane.showConfirmDialog(
            this, form, "Edit Menu",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (pilihan != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int owner = Integer.parseInt(txtIdUmkm.getText().trim());
            String namaBaru = txtNama.getText().trim();
            String deskripsiBaru = txtDeskripsi.getText().trim();
            double hargaBaru = Double.parseDouble(txtHarga.getText().trim());
            int stokBaru = Integer.parseInt(txtStok.getText().trim());

            if (namaBaru.isEmpty() || owner <= 0
                    || hargaBaru < 0 || stokBaru < 0) {
                JOptionPane.showMessageDialog(this,
                    "Data menu tidak valid!");
                return;
            }

            MenuItem menu = new MenuItem(
                idMenu, owner, namaBaru,
                deskripsiBaru, hargaBaru, stokBaru
            );

            if (menuDAO.updateMenu(menu)) {
                JOptionPane.showMessageDialog(this,
                    "Menu berhasil diperbarui!");
                tampilkanData();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "ID UMKM, harga, dan stok harus berupa angka!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal mengedit menu: " + e.getMessage());
        }
    }

    // ==========================================
    // HAPUS MENU
    // ==========================================
    private void hapusData() {

        int baris = tabelMenu.getSelectedRow();

        if (baris == -1) {
            JOptionPane.showMessageDialog(this,
                "Pilih menu yang ingin dihapus terlebih dahulu!");
            return;
        }

        int idMenu = (int) model.getValueAt(baris, 0);
        String namaMenu = model.getValueAt(baris, 2).toString();

        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin menghapus menu " + namaMenu + "?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (menuDAO.hapusMenu(idMenu)) {
                JOptionPane.showMessageDialog(this,
                    "Menu berhasil dihapus!");
                tampilkanData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Gagal menghapus menu: " + e.getMessage());
        }
    }
}

