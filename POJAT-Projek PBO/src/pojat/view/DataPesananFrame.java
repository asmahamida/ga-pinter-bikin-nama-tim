/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pojat.view;

import java.util.Map;

import pojat.model.DetailPesanan;

import pojat.dao.PesananDAO;
import pojat.model.Pesanan;
import pojat.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DataPesananFrame extends JFrame {

    private JTable tabelPesanan;
    private DefaultTableModel model;
    private PesananDAO pesananDAO;
    private User user;

    public DataPesananFrame(User user) {
        this.user = user;
        pesananDAO = new PesananDAO();

        setTitle("POJAT - Kelola Pesanan");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        tampilkanData();
    }

    private void initComponents() {

        setLayout(new BorderLayout(10, 10));

        // HEADER
        JLabel lblJudul = new JLabel(
                "KELOLA PESANAN", JLabel.CENTER);

        lblJudul.setFont(new Font("Arial", Font.BOLD, 24));
        lblJudul.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 10, 10));

        add(lblJudul, BorderLayout.NORTH);

        // TABEL
        String[] kolom = {
            "ID Pesanan",
            "Kode Transaksi",
            "ID Batch",
            "ID Pembeli",
            "Tanggal Pesanan",
            "Total Harga",
            "Metode Pengambilan",
            "Status Pesanan"
        };

        model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelPesanan = new JTable(model);
        tabelPesanan.setRowHeight(25);
        tabelPesanan.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tabelPesanan);
        add(scrollPane, BorderLayout.CENTER);

        // BUTTON
        JPanel panelButton = new JPanel(new FlowLayout());

        JButton btnRefresh = new JButton("Refresh");
        JButton btnKembali = new JButton("Kembali");
        JButton btnDetail = new JButton("Lihat Detail");
        JButton btnStatus = new JButton("Ubah Status");
        JButton btnValidasi = new JButton("Validasi Pembayaran");
        JButton btnRekap = new JButton("Rekap Status");

        panelButton.add(btnRekap);
        panelButton.add(btnRefresh);
        panelButton.add(btnKembali);
        panelButton.add(btnDetail);
        panelButton.add(btnStatus);
        panelButton.add(btnValidasi);

        add(panelButton, BorderLayout.SOUTH);

        // ACTION
        btnRefresh.addActionListener(e -> tampilkanData());
        btnDetail.addActionListener(e -> lihatDetail());
        btnStatus.addActionListener(e -> ubahStatus());
        btnValidasi.addActionListener(e -> validasiPembayaran());
        btnRekap.addActionListener(e -> tampilkanRekapStatus());

        btnKembali.addActionListener(e -> {
            new DashboardAdmin(user).setVisible(true);
            dispose();
        });
    }

    // ==========================================
    // MENAMPILKAN DATA PESANAN
    // ==========================================
    private void tampilkanData() {

        model.setRowCount(0);

        try {
            List<Pesanan> daftarPesanan =
                    pesananDAO.getAllPesanan();

            for (Pesanan pesanan : daftarPesanan) {

                model.addRow(new Object[]{
                    pesanan.getIdPesanan(),
                    pesanan.getKodeTransaksi(),
                    pesanan.getIdBatch(),
                    pesanan.getIdPembeli(),
                    pesanan.getTglPesanan(),
                    pesanan.getTotalHarga(),
                    pesanan.getMetodePengambilan(),
                    pesanan.getStatusPesanan()
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil data pesanan: "
                    + e.getMessage());
        }
    }
    // ==========================================
    // MELIHAT DETAIL PESANAN
    // ==========================================
    // ==========================================
    // MELIHAT DETAIL PESANAN
    // ==========================================
    private void lihatDetail() {

        int baris = tabelPesanan.getSelectedRow();

        if (baris == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih pesanan terlebih dahulu!");
            return;
        }

        int idPesanan = (int) model.getValueAt(baris, 0);
        String kodeTransaksi = String.valueOf(
                model.getValueAt(baris, 1));

        try {

            List<String[]> daftarDetail =
                    pesananDAO.getDetailPesananNamaMenu(idPesanan);

            if (daftarDetail.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Detail pesanan tidak ditemukan.");
                return;
            }

            StringBuilder isi = new StringBuilder();

            isi.append("KODE TRANSAKSI: ")
                    .append(kodeTransaksi)
                    .append("\n");

            isi.append("ID PESANAN: ")
                    .append(idPesanan)
                    .append("\n");

            isi.append("========================================\n\n");

            for (String[] detail : daftarDetail) {

                isi.append("Nama Menu  : ")
                        .append(detail[0]).append("\n");

                isi.append("Jumlah     : ")
                        .append(detail[1]).append("\n");

                isi.append("Subtotal   : Rp ")
                        .append(detail[2]).append("\n");

                isi.append("Catatan    : ")
                        .append(detail[3] == null
                                || detail[3].isEmpty()
                                ? "-"
                                : detail[3])
                        .append("\n");

                isi.append("----------------------------------------\n");
            }

            JTextArea textArea = new JTextArea(isi.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            textArea.setMargin(new Insets(10, 10, 10, 10));

            JScrollPane scroll = new JScrollPane(textArea);
            scroll.setPreferredSize(new Dimension(500, 400));

            JOptionPane.showMessageDialog(this,
                    scroll,
                    "Detail Pesanan - " + kodeTransaksi,
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(this,
                    "Gagal mengambil detail pesanan: "
                    + e.getMessage());
        }
    }
    // ==========================================
    // MENGUBAH STATUS PESANAN
    // ==========================================
    private void ubahStatus() {

        int baris = tabelPesanan.getSelectedRow();

        if (baris == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih pesanan yang ingin diubah!");
            return;
        }

        int idPesanan = (int) model.getValueAt(baris, 0);

        String[] pilihanStatus = {
            "MENUNGGU",
            "DIPROSES",
            "SELESAI",
            "DIBATALKAN"
        };

        String statusBaru = (String) JOptionPane.showInputDialog(
                this,
                "Pilih status pesanan baru:",
                "Ubah Status Pesanan",
                JOptionPane.QUESTION_MESSAGE,
                null,
                pilihanStatus,
                pilihanStatus[0]
        );

        if (statusBaru == null) {
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Ubah status pesanan ID " + idPesanan
                + " menjadi " + statusBaru + "?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean berhasil = pesananDAO.updateStatusPesanan(
                    idPesanan, statusBaru);

            if (berhasil) {
                JOptionPane.showMessageDialog(this,
                        "Status pesanan berhasil diperbarui!");

                tampilkanData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Pesanan tidak ditemukan.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengubah status: " + e.getMessage());
        }
    }
    // ==========================================
    // VALIDASI PEMBAYARAN
    // ==========================================
    private void validasiPembayaran() {

        int baris = tabelPesanan.getSelectedRow();

        if (baris == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih pesanan terlebih dahulu!");
            return;
        }

        int idPesanan = (int) model.getValueAt(baris, 0);

        try {

            String bukti = pesananDAO.getBuktiPembayaran(idPesanan);

            if (bukti == null || bukti.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Pesanan ini belum mengunggah bukti pembayaran.");
                return;
            }

            JTextArea areaBukti = new JTextArea(bukti);
            areaBukti.setEditable(false);
            areaBukti.setLineWrap(true);
            areaBukti.setWrapStyleWord(true);
            areaBukti.setMargin(new Insets(10, 10, 10, 10));

            JScrollPane scroll = new JScrollPane(areaBukti);
            scroll.setPreferredSize(new Dimension(450, 150));

            int pilihan = JOptionPane.showConfirmDialog(
                    this,
                    scroll,
                    "Bukti Pembayaran Pesanan #" + idPesanan
                    + "\n\nApakah pembayaran sudah sesuai?",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (pilihan == JOptionPane.YES_OPTION) {

                boolean berhasil = pesananDAO.validasiPembayaran(
                        idPesanan, "DIPROSES");

                if (berhasil) {
                    JOptionPane.showMessageDialog(this,
                            "Pembayaran diterima. Status pesanan menjadi DIPROSES.");
                    tampilkanData();
                }

            } else if (pilihan == JOptionPane.NO_OPTION) {

                boolean berhasil = pesananDAO.validasiPembayaran(
                        idPesanan, "MENUNGGU");

                if (berhasil) {
                    JOptionPane.showMessageDialog(this,
                            "Pembayaran belum diterima. Status dikembalikan ke MENUNGGU.");
                    tampilkanData();
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memvalidasi pembayaran: " + e.getMessage());
        }
    }
    // ==========================================
    // REKAP STATUS PESANAN
    // ==========================================
    private void tampilkanRekapStatus() {
        try {
            Map<String, Integer> rekap = pesananDAO.getRekapStatusPesanan();

            StringBuilder sb = new StringBuilder();
            sb.append("REKAP STATUS PESANAN\n");
            sb.append("============================\n\n");

            if (rekap.isEmpty()) {
                sb.append("Belum ada pesanan.");
            } else {
                int total = 0;

                for (Map.Entry<String, Integer> entry : rekap.entrySet()) {
                    sb.append(entry.getKey())
                      .append(" : ")
                      .append(entry.getValue())
                      .append(" pesanan\n");

                    total += entry.getValue();
                }

                sb.append("\n----------------------------\n");
                sb.append("TOTAL PESANAN : ").append(total);
            }

            JOptionPane.showMessageDialog(
                this,
                sb.toString(),
                "Rekap Status Pesanan",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal menampilkan rekap: " + e.getMessage()
            );
        }
    }
}
