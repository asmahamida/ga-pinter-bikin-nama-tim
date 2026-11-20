/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.view;

import pojat.dao.BatchPODAO;
import pojat.model.BatchPO;
import pojat.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DataBatchFrame extends JFrame {

    private JTable tabelBatch;
    private DefaultTableModel model;
    private BatchPODAO batchDAO;
    private User user;

    public DataBatchFrame(User user) {
        this.user = user;
        batchDAO = new BatchPODAO();

        setTitle("POJAT - Kelola Batch PO");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        tampilkanData();
    }

    private void initComponents() {

        setLayout(new BorderLayout(10, 10));

        JLabel lblJudul = new JLabel("KELOLA BATCH PO", JLabel.CENTER);
        lblJudul.setFont(new Font("Arial", Font.BOLD, 24));
        lblJudul.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 10, 10));

        add(lblJudul, BorderLayout.NORTH);

        String[] kolom = {
            "ID Batch",
            "ID UMKM",
            "Nama Batch",
            "Tanggal Buka",
            "Tanggal Tutup",
            "Pengiriman",
            "Kuota Maks",
            "Status"
        };

        model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelBatch = new JTable(model);
        tabelBatch.setRowHeight(25);
        tabelBatch.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(tabelBatch), BorderLayout.CENTER);

        JPanel panelButton = new JPanel(new FlowLayout());

        JButton btnRefresh = new JButton("Refresh");
        JButton btnStatus = new JButton("Ubah Status");
        JButton btnKembali = new JButton("Kembali");

        panelButton.add(btnRefresh);
        panelButton.add(btnStatus);
        panelButton.add(btnKembali);

        add(panelButton, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> tampilkanData());
        btnStatus.addActionListener(e -> ubahStatus());

        btnKembali.addActionListener(e -> {
            new DashboardAdmin(user).setVisible(true);
            dispose();
        });
    }

    // ==========================================
    // MENAMPILKAN DATA BATCH
    // ==========================================
    private void tampilkanData() {

        model.setRowCount(0);

        try {
            List<BatchPO> daftar = batchDAO.getAllBatch();

            for (BatchPO batch : daftar) {
                model.addRow(new Object[]{
                    batch.getIdBatch(),
                    batch.getIdUMKM(),
                    batch.getNamaBatch(),
                    batch.getTglBuka(),
                    batch.getTglTutup(),
                    batch.getTglPengiriman(),
                    batch.getKuotaMax(),
                    batch.getStatus()
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil data batch: " + e.getMessage());
        }
    }

    // ==========================================
    // MENGUBAH STATUS BATCH
    // ==========================================
    private void ubahStatus() {

        int baris = tabelBatch.getSelectedRow();

        if (baris == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih batch terlebih dahulu!");
            return;
        }

        int idBatch = (int) model.getValueAt(baris, 0);

        String[] pilihan = {
            "DRAFT",
            "OPEN",
            "CLOSED",
            "SELESAI"
        };

        String status = (String) JOptionPane.showInputDialog(
                this,
                "Pilih status batch:",
                "Ubah Status Batch",
                JOptionPane.QUESTION_MESSAGE,
                null,
                pilihan,
                model.getValueAt(baris, 7)
        );

        if (status == null) {
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Ubah status batch ID " + idBatch
                + " menjadi " + status + "?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean berhasil = batchDAO.updateStatusBatch(idBatch, status);

            if (berhasil) {
                JOptionPane.showMessageDialog(this,
                        "Status batch berhasil diperbarui!");
                tampilkanData();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengubah status batch: " + e.getMessage());
        }
    }
}
