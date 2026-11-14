/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.view;

import pojat.dao.UMKMDAO;
import pojat.model.UMKM;
import pojat.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DataUMKMFrame extends JFrame {

    private User user;
    private UMKMDAO umkmDAO;

    private JTable tabelUMKM;
    private DefaultTableModel modelTabel;

    private JButton btnTambah;
    private JButton btnEdit;
    private JButton btnHapus;
    private JButton btnRefresh;
    private JButton btnKembali;

    public DataUMKMFrame(User user) {

        this.user = user;
        this.umkmDAO = new UMKMDAO();

        setTitle("POJAT - Data UMKM");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // =================================
        // PANEL UTAMA
        // =================================
        JPanel panelUtama = new JPanel(new BorderLayout());
        panelUtama.setBackground(new Color(240, 245, 250));

        // =================================
        // HEADER
        // =================================
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(35, 80, 130));
        panelHeader.setBorder(
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel lblJudul = new JLabel("PENGELOLAAN DATA UMKM");
        lblJudul.setFont(new Font("Arial", Font.BOLD, 24));
        lblJudul.setForeground(Color.WHITE);

        JLabel lblAdmin = new JLabel(
            "Administrator: " + user.getNamaLengkap()
        );
        lblAdmin.setForeground(Color.WHITE);

        panelHeader.add(lblJudul, BorderLayout.WEST);
        panelHeader.add(lblAdmin, BorderLayout.EAST);

        // =================================
        // TABEL UMKM
        // =================================
        String[] kolom = {
            "ID UMKM",
            "ID Owner",
            "Nama Toko",
            "Deskripsi",
            "Alamat Penjemputan",
            "No Rekening",
            "Bank"
        };

        modelTabel = new DefaultTableModel(kolom, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelUMKM = new JTable(modelTabel);
        tabelUMKM.setRowHeight(28);
        tabelUMKM.setFont(new Font("Arial", Font.PLAIN, 12));
        tabelUMKM.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 12)
        );

        JScrollPane scrollPane = new JScrollPane(tabelUMKM);
        scrollPane.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // =================================
        // PANEL TOMBOL
        // =================================
        JPanel panelTombol = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 15, 15)
        );

        panelTombol.setBackground(
            new Color(240, 245, 250)
        );

        btnTambah = new JButton("Tambah UMKM");
        btnEdit = new JButton("Edit UMKM");
        btnHapus = new JButton("Hapus UMKM");
        btnRefresh = new JButton("Refresh");
        btnKembali = new JButton("Kembali");

        panelTombol.add(btnTambah);
        panelTombol.add(btnEdit);
        panelTombol.add(btnHapus);
        panelTombol.add(btnRefresh);
        panelTombol.add(btnKembali);

        // =================================
        // MENYUSUN PANEL
        // =================================
        panelUtama.add(panelHeader, BorderLayout.NORTH);
        panelUtama.add(scrollPane, BorderLayout.CENTER);
        panelUtama.add(panelTombol, BorderLayout.SOUTH);

        add(panelUtama);

        // =================================
        // EVENT TOMBOL
        // =================================

        btnTambah.addActionListener(e -> tambahData());

        btnEdit.addActionListener(e -> editData());

        btnHapus.addActionListener(e -> hapusData());

        btnRefresh.addActionListener(e -> tampilkanData());

        btnKembali.addActionListener(e -> {
            new DashboardAdmin(user).setVisible(true);
            dispose();
        });

        // Tampilkan data saat halaman dibuka
        tampilkanData();
    }


    // =====================================
    // READ: MENAMPILKAN DATA UMKM
    // =====================================
    private void tampilkanData() {

        modelTabel.setRowCount(0);

        try {

            List<UMKM> daftarUMKM = umkmDAO.getAllUMKM();

            for (UMKM umkm : daftarUMKM) {

                Object[] baris = {
                    umkm.getIdUmkm(),
                    umkm.getIdOwner(),
                    umkm.getNamaToko(),
                    umkm.getDeskripsi(),
                    umkm.getAlamatPenjemputan(),
                    umkm.getNoRekening(),
                    umkm.getBank()
                };

                modelTabel.addRow(baris);
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                this,
                "Gagal mengambil data UMKM!\n" + e.getMessage(),
                "Kesalahan Database",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================
    // FORM INPUT DATA UMKM
    // =====================================
    private UMKM tampilkanForm(UMKM dataLama) {

        JTextField txtIdOwner = new JTextField();
        JTextField txtNamaToko = new JTextField();
        JTextArea txtDeskripsi = new JTextArea(3, 20);
        JTextArea txtAlamat = new JTextArea(3, 20);
        JTextField txtNoRekening = new JTextField();
        JTextField txtBank = new JTextField();

        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);

        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);

        if (dataLama != null) {

            txtIdOwner.setText(
                String.valueOf(dataLama.getIdOwner())
            );

            txtNamaToko.setText(dataLama.getNamaToko());
            txtDeskripsi.setText(dataLama.getDeskripsi());
            txtAlamat.setText(dataLama.getAlamatPenjemputan());
            txtNoRekening.setText(dataLama.getNoRekening());
            txtBank.setText(dataLama.getBank());
        }

        JPanel panelForm = new JPanel(new GridLayout(6, 2, 8, 8));

        panelForm.add(new JLabel("ID Owner:"));
        panelForm.add(txtIdOwner);

        panelForm.add(new JLabel("Nama Toko:"));
        panelForm.add(txtNamaToko);

        panelForm.add(new JLabel("Deskripsi:"));
        panelForm.add(new JScrollPane(txtDeskripsi));

        panelForm.add(new JLabel("Alamat Penjemputan:"));
        panelForm.add(new JScrollPane(txtAlamat));

        panelForm.add(new JLabel("No Rekening:"));
        panelForm.add(txtNoRekening);

        panelForm.add(new JLabel("Bank:"));
        panelForm.add(txtBank);

        int hasil = JOptionPane.showConfirmDialog(
            this,
            panelForm,
            dataLama == null ? "Tambah Data UMKM" : "Edit Data UMKM",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (hasil != JOptionPane.OK_OPTION) {
            return null;
        }

        // Validasi input
        String idOwnerText = txtIdOwner.getText().trim();
        String namaToko = txtNamaToko.getText().trim();
        String deskripsi = txtDeskripsi.getText().trim();
        String alamat = txtAlamat.getText().trim();
        String noRekening = txtNoRekening.getText().trim();
        String bank = txtBank.getText().trim();

        if (idOwnerText.isEmpty()
                || namaToko.isEmpty()
                || alamat.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "ID Owner, Nama Toko, dan Alamat wajib diisi!"
            );

            return null;
        }

        int idOwner;

        try {

            idOwner = Integer.parseInt(idOwnerText);

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                this,
                "ID Owner harus berupa angka!"
            );

            return null;
        }

        int idUmkm = dataLama == null
                ? 0
                : dataLama.getIdUmkm();

        return new UMKM(
            idUmkm,
            idOwner,
            namaToko,
            deskripsi,
            alamat,
            noRekening,
            bank
        );
    }


    // =====================================
    // CREATE: TAMBAH DATA UMKM
    // =====================================
    private void tambahData() {

        UMKM umkm = tampilkanForm(null);

        if (umkm == null) {
            return;
        }

        try {

            boolean berhasil = umkmDAO.tambahUMKM(umkm);

            if (berhasil) {

                JOptionPane.showMessageDialog(
                    this,
                    "Data UMKM berhasil ditambahkan!"
                );

                tampilkanData();
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                this,
                "Gagal menambahkan data UMKM!\n" + e.getMessage(),
                "Kesalahan Database",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================
    // UPDATE: EDIT DATA UMKM
    // =====================================
    private void editData() {

        int baris = tabelUMKM.getSelectedRow();

        if (baris == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Pilih data UMKM yang ingin diedit!"
            );

            return;
        }

        int barisModel = tabelUMKM.convertRowIndexToModel(baris);

        int idUmkm = Integer.parseInt(
            modelTabel.getValueAt(barisModel, 0).toString()
        );

        try {

            List<UMKM> daftarUMKM = umkmDAO.getAllUMKM();

            UMKM dataLama = null;

            for (UMKM umkm : daftarUMKM) {

                if (umkm.getIdUmkm() == idUmkm) {
                    dataLama = umkm;
                    break;
                }
            }

            if (dataLama == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Data UMKM tidak ditemukan!"
                );
                tampilkanData();
                return;
            }

            UMKM dataBaru = tampilkanForm(dataLama);

            if (dataBaru == null) {
                return;
            }

            boolean berhasil = umkmDAO.updateUMKM(dataBaru);

            if (berhasil) {

                JOptionPane.showMessageDialog(
                    this,
                    "Data UMKM berhasil diperbarui!"
                );

                tampilkanData();
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                this,
                "Gagal mengedit data UMKM!\n" + e.getMessage(),
                "Kesalahan Database",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================
    // DELETE: HAPUS DATA UMKM
    // =====================================
    private void hapusData() {

        int baris = tabelUMKM.getSelectedRow();

        if (baris == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Pilih data UMKM yang ingin dihapus!"
            );

            return;
        }

        int barisModel = tabelUMKM.convertRowIndexToModel(baris);

        int idUmkm = Integer.parseInt(
            modelTabel.getValueAt(barisModel, 0).toString()
        );

        String namaToko = modelTabel.getValueAt(
            barisModel, 2
        ).toString();

        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin menghapus UMKM " + namaToko + "?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean berhasil = umkmDAO.hapusUMKM(idUmkm);

            if (berhasil) {

                JOptionPane.showMessageDialog(
                    this,
                    "Data UMKM berhasil dihapus!"
                );

                tampilkanData();

            } else {

                JOptionPane.showMessageDialog(
                    this,
                    "Data UMKM tidak berhasil dihapus!"
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                this,
                "Gagal menghapus data UMKM!\n" + e.getMessage(),
                "Kesalahan Database",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }}