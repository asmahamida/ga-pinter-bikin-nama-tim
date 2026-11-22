/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pojat.view;

import pojat.dao.PesananDAO;
import pojat.model.Pesanan;
import pojat.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class RiwayatPesananFrame extends JFrame {

    private User user;
    private PesananDAO pesananDAO;
    private JTable tabel;
    private DefaultTableModel model;

    public RiwayatPesananFrame(User user) {
        this.user = user;
        this.pesananDAO = new PesananDAO();

        setTitle("POJAT - Riwayat Pesanan");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        tampilkanRiwayat();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel judul = new JLabel("RIWAYAT PESANAN SAYA", JLabel.CENTER);
        judul.setFont(new Font("Arial", Font.BOLD, 22));
        add(judul, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new Object[]{
                "ID Pesanan", "Kode Transaksi", "Batch",
                "Tanggal", "Total", "Pengambilan", "Status"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabel = new JTable(model);
        tabel.setRowHeight(25);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        JPanel panelButton = new JPanel(new FlowLayout());

        JButton btnRefresh = new JButton("Refresh");
        JButton btnDetail = new JButton("Lihat Detail");
        JButton btnKembali = new JButton("Kembali");

        panelButton.add(btnRefresh);
        panelButton.add(btnDetail);
        panelButton.add(btnKembali);

        add(panelButton, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> tampilkanRiwayat());
        btnDetail.addActionListener(e -> lihatDetail());

        btnKembali.addActionListener(e -> {
            new DashboardPembeli(user).setVisible(true);
            dispose();
        });
    }

    private void tampilkanRiwayat() {
        model.setRowCount(0);

        try {
            List<Pesanan> daftar =
                    pesananDAO.getPesananPembeli(user.getIdUser());

            for (Pesanan p : daftar) {
                model.addRow(new Object[]{
                    p.getIdPesanan(),
                    p.getKodeTransaksi(),
                    p.getIdBatch(),
                    p.getTglPesanan(),
                    p.getTotalHarga(),
                    p.getMetodePengambilan(),
                    p.getStatusPesanan()
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this, "Gagal memuat riwayat: " + e.getMessage()
            );
        }
    }

    private void lihatDetail() {
        int baris = tabel.getSelectedRow();

        if (baris == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan terlebih dahulu!");
            return;
        }

        int idPesanan = (int) model.getValueAt(baris, 0);

        try {
            List<?> detail = pesananDAO.getDetailPesananNamaMenu(idPesanan);

            if (detail.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Detail pesanan tidak ditemukan.");
                return;
            }

            JOptionPane.showMessageDialog(
                this,
                "ID Pesanan: " + idPesanan
                    + "\nJumlah item detail: " + detail.size(),
                "Detail Pesanan",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this, "Gagal mengambil detail: " + e.getMessage()
            );
        }
    }
}
