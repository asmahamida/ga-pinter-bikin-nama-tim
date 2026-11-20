/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pojat.view;

import pojat.dao.CheckoutDAO;
import pojat.dao.BatchPODAO;
import pojat.model.BatchPO;
import java.util.List;

import pojat.model.User;
import pojat.util.KeranjangPOJAT;
import pojat.util.KeranjangPOJAT.ItemKeranjang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class KeranjangFrame extends JFrame {

    private User user;
    private JTable tabel;
    private DefaultTableModel model;
    private JLabel lblTotal;

    public KeranjangFrame(User user) {
        this.user = user;

        setTitle("Keranjang Belanja");
        setSize(750, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        tampilkanKeranjang();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel judul = new JLabel("KERANJANG PEMBELI", JLabel.CENTER);
        judul.setFont(new Font("Arial", Font.BOLD, 22));
        add(judul, BorderLayout.NORTH);

        model = new DefaultTableModel(
            new Object[]{"ID Menu", "Nama Menu", "Harga", "Jumlah", "Subtotal"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabel = new JTable(model);
        tabel.setRowHeight(25);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new BorderLayout());

        lblTotal = new JLabel("Total: Rp0");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel panelButton = new JPanel(new FlowLayout());

        JButton btnHapus = new JButton("Hapus Item");
        JButton btnCheckout = new JButton("Checkout");
        JButton btnRefresh = new JButton("Refresh");

        panelButton.add(btnRefresh);
        panelButton.add(btnHapus);
        panelButton.add(btnCheckout);

        panelBawah.add(lblTotal, BorderLayout.WEST);
        panelBawah.add(panelButton, BorderLayout.EAST);

        add(panelBawah, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> tampilkanKeranjang());
        btnHapus.addActionListener(e -> hapusItem());
        btnCheckout.addActionListener(e -> checkout());
    }

    private void tampilkanKeranjang() {
        model.setRowCount(0);

        List<ItemKeranjang> items = KeranjangPOJAT.getItems();

        for (ItemKeranjang item : items) {
            model.addRow(new Object[]{
                item.idMenu,
                item.namaMenu,
                item.harga,
                item.jumlah,
                item.getSubtotal()
            });
        }

        lblTotal.setText(
            "Total: Rp" + String.format("%,.0f", KeranjangPOJAT.getTotal())
        );
    }

    private void hapusItem() {
        int baris = tabel.getSelectedRow();

        if (baris == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item yang ingin dihapus!");
            return;
        }

        KeranjangPOJAT.hapus(baris);
        tampilkanKeranjang();
    }


private void checkout() {
        if (KeranjangPOJAT.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong!");
            return;
        }

        try {
            BatchPODAO batchDAO = new BatchPODAO();
            List<BatchPO> daftarBatch = batchDAO.getAllBatch();

            if (daftarBatch.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, "Belum ada batch PO yang tersedia!"
                );
                return;
            }

            List<BatchPO> batchAktif = new java.util.ArrayList<>();

            for (BatchPO batch : daftarBatch) {
                if ("DIBUKA".equalsIgnoreCase(batch.getStatus())
                        || "OPEN".equalsIgnoreCase(batch.getStatus())) {
                    batchAktif.add(batch);
                }
            }

            if (batchAktif.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, "Saat ini belum ada batch PO yang dibuka."
                );
                return;
            }

            String[] pilihanBatch = new String[batchAktif.size()];

            for (int i = 0; i < batchAktif.size(); i++) {
                BatchPO batch = batchAktif.get(i);

                pilihanBatch[i] = batch.getIdBatch()
                        + " - " + batch.getNamaBatch();
            }

            String batchDipilih = (String) JOptionPane.showInputDialog(
                this,
                "Pilih Batch PO:",
                "Checkout",
                JOptionPane.QUESTION_MESSAGE,
                null,
                pilihanBatch,
                pilihanBatch[0]
            );

            if (batchDipilih == null) return;

            int indexBatch = -1;

            for (int i = 0; i < pilihanBatch.length; i++) {
                if (pilihanBatch[i].equals(batchDipilih)) {
                    indexBatch = i;
                    break;
                }
            }

            if (indexBatch == -1) return;

            int idBatch = batchAktif.get(indexBatch).getIdBatch();

            String[] metode = {
                "Ambil Sendiri",
                "Pengantaran"
            };

            String metodeDipilih = (String) JOptionPane.showInputDialog(
                this,
                "Pilih metode pengambilan:",
                "Metode Pengambilan",
                JOptionPane.QUESTION_MESSAGE,
                null,
                metode,
                metode[0]
            );

            if (metodeDipilih == null) return;

            int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Total pembayaran: Rp"
                + String.format("%,.0f", KeranjangPOJAT.getTotal())
                + "\nBatch: " + batchDipilih
                + "\nMetode: " + metodeDipilih
                + "\n\nBuat pesanan sekarang?",
                "Konfirmasi Checkout",
                JOptionPane.YES_NO_OPTION
            );

            if (konfirmasi != JOptionPane.YES_OPTION) return;

            CheckoutDAO checkoutDAO = new CheckoutDAO();

            boolean berhasil = checkoutDAO.checkout(
                user.getIdUser(),
                idBatch,
                metodeDipilih,
                KeranjangPOJAT.getItems()
            );

            if (berhasil) {
                KeranjangPOJAT.kosongkan();
                tampilkanKeranjang();

                JOptionPane.showMessageDialog(
                    this,
                    "Pesanan berhasil dibuat!\n"
                    + "Silakan lanjutkan proses pembayaran."
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Checkout gagal: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

}
