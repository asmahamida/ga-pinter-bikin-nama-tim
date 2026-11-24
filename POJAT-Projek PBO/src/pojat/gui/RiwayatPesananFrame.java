/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pojat.gui;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import pojat.dao.PesananDAO;
import pojat.model.User;

/**
 *
 * @author Jasmine
 */
public class RiwayatPesananFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RiwayatPesananFrame.class.getName());
    private User user;
    private PesananDAO pesananDAO;
    private DefaultTableModel model;
    private List<Object[]> semuaRiwayat = new ArrayList<>();
    private List<Object[]> filteredRiwayat = new ArrayList<>();
    private final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    /**
     * Creates new form RiwayatPesananFrame
     */
    public RiwayatPesananFrame() {
        this.pesananDAO = new PesananDAO();
        initComponents();
        setLocationRelativeTo(null);
        pojat.util.UIHelper.styleTable(tabel);
        pojat.util.UIHelper.applyHandCursor(btnBukti, btnBatal, btnRefresh, btnKembali);
        inisialisasiTabel();
    }

    public RiwayatPesananFrame(User user) {
        this.user = user;
        this.pesananDAO = new PesananDAO();
        initComponents();
        setLocationRelativeTo(null);
        pojat.util.UIHelper.styleTable(tabel);
        pojat.util.UIHelper.applyHandCursor(btnBukti, btnBatal, btnRefresh, btnKembali);
        inisialisasiTabel();
        tampilkanRiwayat(false);
    }

    private void inisialisasiTabel() {
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model = (DefaultTableModel) tabel.getModel();
        tabel.getColumnModel().getColumn(0).setPreferredWidth(35);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(130);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(165);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(105);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(90);
        tabel.getColumnModel().getColumn(5).setPreferredWidth(110);
        tabel.getColumnModel().getColumn(6).setPreferredWidth(100);
        tabel.getColumnModel().getColumn(7).setPreferredWidth(85);

        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                tampilkanDetailTerpilih();
            }
        });
    }

    private void tampilkanRiwayat() {
        tampilkanRiwayat(true);
    }

    private void tampilkanRiwayat(boolean showErrorDialog) {
        if (user == null) {
            return;
        }

        try {
            semuaRiwayat = pesananDAO.getRiwayatPembeliLengkap(user.getIdUser());
            terapkanFilter();
        } catch (SQLException e) {
            if (showErrorDialog) {
                JOptionPane.showMessageDialog(
                    this,
                    "Gagal memuat riwayat: " + e.getMessage()
                );
            } else {
                txtDetailArea.setText("Database belum terhubung. Klik Refresh setelah MySQL aktif.");
            }
        }
    }

    private void terapkanFilter() {
        model.setRowCount(0);
        filteredRiwayat.clear();
        String filter = String.valueOf(cmbFilter.getSelectedItem());

        for (Object[] row : semuaRiwayat) {
            String status = row[7] == null ? "-" : String.valueOf(row[7]);
            if (!"SEMUA".equalsIgnoreCase(filter) && !filter.equalsIgnoreCase(status)) {
                continue;
            }
            filteredRiwayat.add(row);
            Timestamp tgl = (Timestamp) row[3];
            BigDecimal total = (BigDecimal) row[4];
            String bukti = row[6] == null ? "" : String.valueOf(row[6]).trim();
            model.addRow(new Object[]{
                row[0],
                row[1],
                row[2],
                tgl != null ? dateFormat.format(tgl) : "-",
                total != null ? rupiahFormat.format(total) : "Rp0",
                row[5],
                bukti.isEmpty() ? "Belum Ada" : "Sudah Diunggah",
                status
            });
        }

        if (filteredRiwayat.isEmpty()) {
            lblDetailHeader.setText("Rincian Item Pesanan");
            txtDetailArea.setText("Tidak ada pesanan untuk filter '" + filter + "'.");
        } else {
            tabel.setRowSelectionInterval(0, 0);
        }
    }

    private void tampilkanDetailTerpilih() {
        int baris = tabel.getSelectedRow();
        if (baris < 0 || baris >= filteredRiwayat.size()) {
            lblDetailHeader.setText("Rincian Item Pesanan (pilih pesanan pada tabel di atas)");
            txtDetailArea.setText("Belum ada pesanan yang dipilih.");
            return;
        }

        Object[] row = filteredRiwayat.get(baris);
        int idPesanan = (int) row[0];
        String kode = String.valueOf(row[1]);
        String tokoBatch = String.valueOf(row[2]);
        String bukti = row[6] == null || String.valueOf(row[6]).trim().isEmpty()
                ? "Belum ada (klik 'Upload Bukti Bayar' di sidebar kiri)"
                : String.valueOf(row[6]);
        String status = String.valueOf(row[7]);

        lblDetailHeader.setText("Rincian Pesanan #" + idPesanan + " — " + kode + " (" + status + ")");

        try {
            List<String[]> daftarDetail = pesananDAO.getDetailPesananNamaMenu(idPesanan);

            StringBuilder isi = new StringBuilder();
            isi.append("Toko & Batch : ").append(tokoBatch).append("\n");
            isi.append("Bukti Bayar  : ").append(bukti).append("\n");
            isi.append("------------------------------------------------------------\n");

            if (daftarDetail.isEmpty()) {
                isi.append("Detail item pesanan belum tercatat.");
            } else {
                for (String[] d : daftarDetail) {
                    double sub = Double.parseDouble(d[2]);
                    String catatan = (d[3] == null || d[3].isEmpty()) ? "-" : d[3];
                    isi.append("- ").append(d[0])
                       .append("  x").append(d[1])
                       .append("  (").append(rupiahFormat.format(sub)).append(")")
                       .append("  | Catatan: ").append(catatan).append("\n");
                }
            }

            txtDetailArea.setText(isi.toString());
            txtDetailArea.setCaretPosition(0);
        } catch (SQLException e) {
            txtDetailArea.setText("Gagal memuat rincian pesanan: " + e.getMessage());
        }
    }

    private void uploadBuktiBayar() {
        int baris = tabel.getSelectedRow();

        if (baris == -1 || baris >= filteredRiwayat.size()) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih pesanan yang ingin diunggah bukti pembayarannya!"
            );
            return;
        }

        Object[] row = filteredRiwayat.get(baris);
        int idPesanan = (int) row[0];
        String kodeTransaksi = String.valueOf(row[1]);

        String[] opsi = {"Pilih File Bukti", "Ketik Referensi Transfer", "Batal"};
        int pilih = JOptionPane.showOptionDialog(
            this,
            "Unggah bukti pembayaran untuk pesanan " + kodeTransaksi + ":",
            "Upload Bukti Pembayaran",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opsi,
            opsi[0]
        );

        String bukti = null;
        if (pilih == 0) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Pilih File Bukti Pembayaran");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                bukti = chooser.getSelectedFile().getAbsolutePath();
            }
        } else if (pilih == 1) {
            bukti = JOptionPane.showInputDialog(
                this,
                "Masukkan keterangan / referensi transfer:"
            );
        } else {
            return;
        }

        if (bukti == null || bukti.trim().isEmpty()) {
            return;
        }
        bukti = bukti.trim();
        if (bukti.length() > 255) {
            JOptionPane.showMessageDialog(
                this,
                "Path / referensi bukti pembayaran maksimal 255 karakter!"
            );
            return;
        }

        try {
            if (pesananDAO.uploadBuktiPembayaran(idPesanan, bukti)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Bukti pembayaran berhasil dikirim dan menunggu validasi!"
                );
                tampilkanRiwayat();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal mengunggah bukti pembayaran: " + e.getMessage(),
                "Kesalahan Database",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void batalkanPesanan() {
        int baris = tabel.getSelectedRow();
        if (baris < 0 || baris >= filteredRiwayat.size()) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan yang ingin dibatalkan terlebih dahulu!");
            return;
        }
        int idPesanan = (int) filteredRiwayat.get(baris)[0];
        String kode = String.valueOf(filteredRiwayat.get(baris)[1]);
        String status = filteredRiwayat.get(baris)[7] == null ? "" : String.valueOf(filteredRiwayat.get(baris)[7]);

        if ("DIPROSES".equalsIgnoreCase(status)
                || "SELESAI".equalsIgnoreCase(status)
                || "DIBATALKAN".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(
                this,
                "Pesanan dengan status '" + status + "' tidak dapat dibatalkan sendiri.\nHubungi penjual jika perlu bantuan."
            );
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin membatalkan pesanan " + kode + "?",
            "Konfirmasi Pembatalan",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (pesananDAO.updateStatusPesanan(idPesanan, "DIBATALKAN")) {
                JOptionPane.showMessageDialog(this, "Pesanan " + kode + " berhasil dibatalkan.");
                tampilkanRiwayat();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal membatalkan pesanan: " + e.getMessage(),
                "Kesalahan Database",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        splitMain = new javax.swing.JSplitPane();
        pnlBrand = new javax.swing.JPanel();
        lblLogo = new javax.swing.JLabel();
        lblRoleBadge = new javax.swing.JLabel();
        btnBukti = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnKembali = new javax.swing.JButton();
        pnlContent = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        lblFilter = new javax.swing.JLabel();
        cmbFilter = new javax.swing.JComboBox<>();
        scrollRiwayat = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        pnlDetailCard = new javax.swing.JPanel();
        lblDetailHeader = new javax.swing.JLabel();
        scrollDetail = new javax.swing.JScrollPane();
        txtDetailArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("POJAT - Riwayat Pesanan");
        setResizable(false);

        splitMain.setDividerLocation(200);
        splitMain.setDividerSize(0);

        pnlBrand.setBackground(new java.awt.Color(255, 198, 0));
        pnlBrand.setPreferredSize(new java.awt.Dimension(200, 630));

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pojat/resources/images/pojat-it.png"))); // NOI18N

        lblRoleBadge.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        lblRoleBadge.setForeground(new java.awt.Color(0, 83, 38));
        lblRoleBadge.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoleBadge.setText("RIWAYAT PESANAN");

        btnBukti.setBackground(new java.awt.Color(255, 82, 0));
        btnBukti.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnBukti.setForeground(new java.awt.Color(255, 255, 255));
        btnBukti.setText("Upload Bukti Bayar");
        btnBukti.setBorder(null);
        btnBukti.addActionListener(this::btnBuktiActionPerformed);

        btnBatal.setBackground(new java.awt.Color(255, 249, 236));
        btnBatal.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnBatal.setForeground(new java.awt.Color(0, 83, 38));
        btnBatal.setText("Batalkan Pesanan");
        btnBatal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 83, 38)));
        btnBatal.addActionListener(this::btnBatalActionPerformed);

        btnRefresh.setBackground(new java.awt.Color(145, 190, 0));
        btnRefresh.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnRefresh.setForeground(new java.awt.Color(0, 83, 38));
        btnRefresh.setText("Refresh");
        btnRefresh.setBorder(null);
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);

        btnKembali.setBackground(new java.awt.Color(0, 83, 38));
        btnKembali.setFont(new java.awt.Font("Circe Slab C Bold", 0, 14)); // NOI18N
        btnKembali.setForeground(new java.awt.Color(247, 231, 198));
        btnKembali.setText("Kembali");
        btnKembali.setBorder(null);
        btnKembali.addActionListener(this::btnKembaliActionPerformed);

        javax.swing.GroupLayout pnlBrandLayout = new javax.swing.GroupLayout(pnlBrand);
        pnlBrand.setLayout(pnlBrandLayout);
        pnlBrandLayout.setHorizontalGroup(
            pnlBrandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBrandLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBrandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                    .addComponent(lblRoleBadge, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBukti, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBatal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnKembali, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlBrandLayout.setVerticalGroup(
            pnlBrandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBrandLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblRoleBadge)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 252, Short.MAX_VALUE)
                .addComponent(btnBukti, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBatal, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        splitMain.setLeftComponent(pnlBrand);

        pnlContent.setBackground(new java.awt.Color(247, 231, 198));

        lblTitle.setFont(new java.awt.Font("Circe Slab C Bold", 0, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(0, 83, 38));
        lblTitle.setText("Riwayat Pesanan Saya");

        lblSubtitle.setFont(new java.awt.Font("Circe Slab C", 0, 14)); // NOI18N
        lblSubtitle.setText("Klik salah satu pesanan untuk melihat rincian menu dan status pembayaran");

        lblFilter.setFont(new java.awt.Font("Circe Slab C Bold", 0, 12)); // NOI18N
        lblFilter.setForeground(new java.awt.Color(0, 83, 38));
        lblFilter.setText("Filter Status:");

        cmbFilter.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        cmbFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SEMUA", "MENUNGGU_PEMBAYARAN", "MENUNGGU_VALIDASI", "DIPROSES", "SELESAI", "DIBATALKAN" }));
        cmbFilter.addActionListener(this::cmbFilterActionPerformed);

        tabel.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        tabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Kode Transaksi", "Toko & Batch PO", "Tanggal", "Total", "Pengambilan", "Bukti Bayar", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel.setRowHeight(28);
        tabel.setSelectionBackground(new java.awt.Color(145, 190, 0));
        tabel.setSelectionForeground(new java.awt.Color(0, 83, 38));
        scrollRiwayat.setViewportView(tabel);

        pnlDetailCard.setBackground(new java.awt.Color(255, 249, 236));
        pnlDetailCard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 83, 38)));

        lblDetailHeader.setFont(new java.awt.Font("Circe Slab C Bold", 0, 15)); // NOI18N
        lblDetailHeader.setForeground(new java.awt.Color(0, 83, 38));
        lblDetailHeader.setText("Rincian Item Pesanan (pilih pesanan pada tabel di atas)");

        txtDetailArea.setEditable(false);
        txtDetailArea.setBackground(new java.awt.Color(255, 255, 255));
        txtDetailArea.setColumns(20);
        txtDetailArea.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        txtDetailArea.setRows(5);
        txtDetailArea.setText("Belum ada pesanan yang dipilih.");
        scrollDetail.setViewportView(txtDetailArea);

        javax.swing.GroupLayout pnlDetailCardLayout = new javax.swing.GroupLayout(pnlDetailCard);
        pnlDetailCard.setLayout(pnlDetailCardLayout);
        pnlDetailCardLayout.setHorizontalGroup(
            pnlDetailCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetailCardLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlDetailCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDetailHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollDetail))
                .addGap(16, 16, 16))
        );
        pnlDetailCardLayout.setVerticalGroup(
            pnlDetailCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetailCardLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblDetailHeader)
                .addGap(8, 8, 8)
                .addComponent(scrollDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout pnlContentLayout = new javax.swing.GroupLayout(pnlContent);
        pnlContent.setLayout(pnlContentLayout);
        pnlContentLayout.setHorizontalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlContentLayout.createSequentialGroup()
                        .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSubtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbFilter, 0, 224, Short.MAX_VALUE)))
                    .addComponent(scrollRiwayat)
                    .addComponent(pnlDetailCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        pnlContentLayout.setVerticalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlContentLayout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblSubtitle))
                    .addGroup(pnlContentLayout.createSequentialGroup()
                        .addComponent(lblFilter)
                        .addGap(4, 4, 4)
                        .addComponent(cmbFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(scrollRiwayat, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlDetailCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        splitMain.setRightComponent(pnlContent);

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, 1040, Short.MAX_VALUE)
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembaliActionPerformed
        new DashboardPembeli(user).setVisible(true);
        dispose();
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void btnBuktiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuktiActionPerformed
        uploadBuktiBayar();
    }//GEN-LAST:event_btnBuktiActionPerformed

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        batalkanPesanan();
    }//GEN-LAST:event_btnBatalActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        tampilkanRiwayat();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void cmbFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFilterActionPerformed
        terapkanFilter();
    }//GEN-LAST:event_cmbFilterActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new RiwayatPesananFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnBukti;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JComboBox<String> cmbFilter;
    private javax.swing.JLabel lblDetailHeader;
    private javax.swing.JLabel lblFilter;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblRoleBadge;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlBrand;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlDetailCard;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JScrollPane scrollDetail;
    private javax.swing.JScrollPane scrollRiwayat;
    private javax.swing.JSplitPane splitMain;
    private javax.swing.JTable tabel;
    private javax.swing.JTextArea txtDetailArea;
    // End of variables declaration//GEN-END:variables
}
