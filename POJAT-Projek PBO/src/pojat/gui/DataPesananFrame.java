/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pojat.gui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import pojat.dao.PesananDAO;
import pojat.model.UMKM;
import pojat.model.User;

/**
 *
 * @author Jasmine
 */
public class DataPesananFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DataPesananFrame.class.getName());
    private User user;
    private UMKM umkm;
    private PesananDAO pesananDAO;
    private DefaultTableModel model;
    private List<Object[]> semuaPesanan = new ArrayList<>();
    private List<Object[]> filteredPesanan = new ArrayList<>();
    private final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    /**
     * Creates new form DataPesananFrame
     */
    public DataPesananFrame() {
        this.pesananDAO = new PesananDAO();
        initComponents();
        setLocationRelativeTo(null);
        pojat.util.UIHelper.styleTable(tabelPesanan);
        pojat.util.UIHelper.applyHandCursor(
            btnRekap, btnRefresh, btnKembali, btnValidasi, btnSelesai, btnStatus, btnBatalkan
        );
        inisialisasiTabel();
    }

    public DataPesananFrame(User user) {
        this(user, null);
    }

    public DataPesananFrame(User user, UMKM umkm) {
        this.user = user;
        this.umkm = umkm;
        this.pesananDAO = new PesananDAO();
        initComponents();
        setLocationRelativeTo(null);
        pojat.util.UIHelper.styleTable(tabelPesanan);
        pojat.util.UIHelper.applyHandCursor(
            btnRekap, btnRefresh, btnKembali, btnValidasi, btnSelesai, btnStatus, btnBatalkan
        );
        if (umkm != null) {
            lblSubtitle.setText("Pesanan masuk untuk toko " + umkm.getNamaToko()
                    + " — klik baris untuk melihat rincian & bukti bayar");
        }
        inisialisasiTabel();
        tampilkanData(false);
    }

    private void inisialisasiTabel() {
        tabelPesanan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model = (DefaultTableModel) tabelPesanan.getModel();
        tabelPesanan.getColumnModel().getColumn(0).setPreferredWidth(35);
        tabelPesanan.getColumnModel().getColumn(1).setPreferredWidth(120);
        tabelPesanan.getColumnModel().getColumn(2).setPreferredWidth(115);
        tabelPesanan.getColumnModel().getColumn(3).setPreferredWidth(145);
        tabelPesanan.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabelPesanan.getColumnModel().getColumn(5).setPreferredWidth(85);
        tabelPesanan.getColumnModel().getColumn(6).setPreferredWidth(105);
        tabelPesanan.getColumnModel().getColumn(7).setPreferredWidth(100);
        tabelPesanan.getColumnModel().getColumn(8).setPreferredWidth(85);

        tabelPesanan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                tampilkanDetailTerpilih();
            }
        });
    }

    private void tampilkanData() {
        tampilkanData(true);
    }

    private void tampilkanData(boolean showErrorDialog) {
        try {
            semuaPesanan = (umkm != null)
                    ? pesananDAO.getPesananUMKMLengkap(umkm.getIdUmkm())
                    : pesananDAO.getAllPesananLengkap();
            terapkanFilter();
        } catch (SQLException e) {
            if (showErrorDialog) {
                JOptionPane.showMessageDialog(
                    this,
                    "Gagal mengambil data pesanan: " + e.getMessage()
                );
            } else {
                txtDetailArea.setText("Database belum terhubung. Klik Refresh Data setelah MySQL aktif.");
            }
        }
    }

    private void terapkanFilter() {
        model.setRowCount(0);
        filteredPesanan.clear();
        String filter = String.valueOf(cmbFilter.getSelectedItem());

        for (Object[] row : semuaPesanan) {
            String status = row[9] == null ? "-" : String.valueOf(row[9]);
            if (!"SEMUA".equalsIgnoreCase(filter) && !filter.equalsIgnoreCase(status)) {
                continue;
            }
            filteredPesanan.add(row);
            String pembeli = String.valueOf(row[2]);
            Timestamp tgl = (Timestamp) row[5];
            BigDecimal total = (BigDecimal) row[6];
            String bukti = row[8] == null ? "" : String.valueOf(row[8]).trim();

            model.addRow(new Object[]{
                row[0],
                row[1],
                pembeli,
                row[4],
                tgl != null ? dateFormat.format(tgl) : "-",
                total != null ? rupiahFormat.format(total) : "Rp0",
                row[7],
                bukti.isEmpty() ? "Belum Ada" : "Ada Bukti",
                status
            });
        }

        if (filteredPesanan.isEmpty()) {
            lblDetailHeader.setText("Detail Pesanan & Bukti Pembayaran");
            txtDetailArea.setText("Tidak ada pesanan untuk filter '" + filter + "'.");
        } else {
            tabelPesanan.setRowSelectionInterval(0, 0);
        }
    }

    private void tampilkanDetailTerpilih() {
        int baris = tabelPesanan.getSelectedRow();
        if (baris < 0 || baris >= filteredPesanan.size()) {
            lblDetailHeader.setText("Detail Pesanan & Bukti Pembayaran (pilih pesanan di atas)");
            txtDetailArea.setText("Belum ada pesanan yang dipilih.");
            return;
        }

        Object[] row = filteredPesanan.get(baris);
        int idPesanan = (int) row[0];
        String kode = String.valueOf(row[1]);
        String pembeli = String.valueOf(row[2]);
        String noHp = row[3] == null || String.valueOf(row[3]).isEmpty() ? "-" : String.valueOf(row[3]);
        String tokoBatch = String.valueOf(row[4]);
        String bukti = row[8] == null || String.valueOf(row[8]).trim().isEmpty()
                ? "Pembeli belum mengunggah bukti pembayaran"
                : String.valueOf(row[8]);
        String status = String.valueOf(row[9]);

        lblDetailHeader.setText("Pesanan #" + idPesanan + " — " + kode + " | Status: " + status);

        try {
            List<String[]> daftarDetail = pesananDAO.getDetailPesananNamaMenu(idPesanan);

            StringBuilder isi = new StringBuilder();
            isi.append("Pembeli      : ").append(pembeli).append(" (HP: ").append(noHp).append(")\n");
            isi.append("Toko & Batch : ").append(tokoBatch).append("  |  Pengambilan: ").append(row[7]).append("\n");
            isi.append("Bukti Bayar  : ").append(bukti).append("\n");
            isi.append("----------------------------------------------------------------------\n");

            if (daftarDetail.isEmpty()) {
                isi.append("Detail item pesanan tidak ditemukan.");
            } else {
                for (String[] detail : daftarDetail) {
                    double sub = Double.parseDouble(detail[2]);
                    String catatan = (detail[3] == null || detail[3].isEmpty()) ? "-" : detail[3];
                    isi.append("- ").append(detail[0])
                       .append("  x").append(detail[1])
                       .append("  (").append(rupiahFormat.format(sub)).append(")")
                       .append("  | Catatan: ").append(catatan).append("\n");
                }
            }

            txtDetailArea.setText(isi.toString());
            txtDetailArea.setCaretPosition(0);
        } catch (SQLException e) {
            txtDetailArea.setText("Gagal mengambil detail pesanan: " + e.getMessage());
        }
    }

    private void ubahStatusCepat(String statusBaru) {
        int baris = tabelPesanan.getSelectedRow();
        if (baris == -1 || baris >= filteredPesanan.size()) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan terlebih dahulu!");
            return;
        }

        int idPesanan = (int) filteredPesanan.get(baris)[0];
        String kode = String.valueOf(filteredPesanan.get(baris)[1]);

        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Ubah status pesanan " + kode + " menjadi " + statusBaru + "?",
            "Konfirmasi Perubahan Status",
            JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (pesananDAO.updateStatusPesanan(idPesanan, statusBaru)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Status pesanan " + kode + " diperbarui menjadi " + statusBaru + "!"
                );
                tampilkanData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal mengubah status: " + e.getMessage()
            );
        }
    }

    private void ubahStatus() {
        int baris = tabelPesanan.getSelectedRow();

        if (baris == -1 || baris >= filteredPesanan.size()) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih pesanan yang ingin diubah!"
            );
            return;
        }

        int idPesanan = (int) filteredPesanan.get(baris)[0];
        String statusSaatIni = String.valueOf(filteredPesanan.get(baris)[9]);

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
            statusSaatIni
        );

        if (statusBaru == null) {
            return;
        }

        ubahStatusCepat(statusBaru);
    }

    private void validasiPembayaran() {
        int baris = tabelPesanan.getSelectedRow();

        if (baris == -1 || baris >= filteredPesanan.size()) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih pesanan terlebih dahulu!"
            );
            return;
        }

        int idPesanan = (int) filteredPesanan.get(baris)[0];

        try {
            String bukti = pesananDAO.getBuktiPembayaran(idPesanan);

            if (bukti == null || bukti.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Pesanan ini belum mengunggah bukti pembayaran."
                );
                return;
            }

            Object kontenDialog;
            File fileBukti = new File(bukti.trim());
            String lower = bukti.trim().toLowerCase();
            if (fileBukti.exists() && fileBukti.isFile()
                    && (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg"))) {
                ImageIcon icon = new ImageIcon(fileBukti.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(380, -1, Image.SCALE_SMOOTH);
                JLabel lblImg = new JLabel(new ImageIcon(scaled));
                JScrollPane spImg = new JScrollPane(lblImg);
                spImg.setPreferredSize(new Dimension(420, 320));
                kontenDialog = spImg;
            } else {
                JTextArea areaBukti = new JTextArea(bukti);
                areaBukti.setEditable(false);
                areaBukti.setLineWrap(true);
                areaBukti.setWrapStyleWord(true);
                areaBukti.setMargin(new Insets(10, 10, 10, 10));
                JScrollPane scroll = new JScrollPane(areaBukti);
                scroll.setPreferredSize(new Dimension(450, 150));
                kontenDialog = scroll;
            }

            String[] opsi = {"Terima & Proses", "Tolak (Kembalikan ke MENUNGGU)", "Tutup"};
            int pilihan = JOptionPane.showOptionDialog(
                this,
                kontenDialog,
                "Validasi Bukti Pembayaran Pesanan #" + idPesanan,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opsi,
                opsi[0]
            );

            if (pilihan == 0) {
                if (pesananDAO.validasiPembayaran(idPesanan, "DIPROSES")) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Pembayaran diterima. Status pesanan menjadi DIPROSES."
                    );
                    tampilkanData();
                }
            } else if (pilihan == 1) {
                if (pesananDAO.validasiPembayaran(idPesanan, "MENUNGGU")) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Pembayaran ditolak. Status dikembalikan ke MENUNGGU."
                    );
                    tampilkanData();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal memvalidasi pembayaran: " + e.getMessage()
            );
        }
    }

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
        btnRekap = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnKembali = new javax.swing.JButton();
        pnlContent = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        lblFilter = new javax.swing.JLabel();
        cmbFilter = new javax.swing.JComboBox<>();
        scrollPesanan = new javax.swing.JScrollPane();
        tabelPesanan = new javax.swing.JTable();
        pnlDetailCard = new javax.swing.JPanel();
        lblDetailHeader = new javax.swing.JLabel();
        scrollDetail = new javax.swing.JScrollPane();
        txtDetailArea = new javax.swing.JTextArea();
        pnlActions = new javax.swing.JPanel();
        btnValidasi = new javax.swing.JButton();
        btnSelesai = new javax.swing.JButton();
        btnStatus = new javax.swing.JButton();
        btnBatalkan = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("POJAT - Kelola Pesanan");
        setResizable(false);

        splitMain.setDividerLocation(210);
        splitMain.setDividerSize(0);

        pnlBrand.setBackground(new java.awt.Color(255, 198, 0));
        pnlBrand.setPreferredSize(new java.awt.Dimension(210, 660));

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pojat/resources/images/pojat-it.png"))); // NOI18N

        lblRoleBadge.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        lblRoleBadge.setForeground(new java.awt.Color(0, 83, 38));
        lblRoleBadge.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoleBadge.setText("KELOLA PESANAN");

        btnRekap.setBackground(new java.awt.Color(145, 190, 0));
        btnRekap.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnRekap.setForeground(new java.awt.Color(0, 83, 38));
        btnRekap.setText("Rekap Status");
        btnRekap.setBorder(null);
        btnRekap.addActionListener(this::btnRekapActionPerformed);

        btnRefresh.setBackground(new java.awt.Color(145, 190, 0));
        btnRefresh.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnRefresh.setForeground(new java.awt.Color(0, 83, 38));
        btnRefresh.setText("Refresh Data");
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
                    .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .addComponent(lblRoleBadge, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRekap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 330, Short.MAX_VALUE)
                .addComponent(btnRekap, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        lblTitle.setText("Kelola Pesanan");

        lblSubtitle.setFont(new java.awt.Font("Circe Slab C", 0, 14)); // NOI18N
        lblSubtitle.setText("Klik pesanan pada tabel untuk melihat item, pembeli, dan bukti pembayaran");

        lblFilter.setFont(new java.awt.Font("Circe Slab C Bold", 0, 12)); // NOI18N
        lblFilter.setForeground(new java.awt.Color(0, 83, 38));
        lblFilter.setText("Filter Status:");

        cmbFilter.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        cmbFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SEMUA", "MENUNGGU_PEMBAYARAN", "MENUNGGU_VALIDASI", "DIPROSES", "SELESAI", "DIBATALKAN" }));
        cmbFilter.addActionListener(this::cmbFilterActionPerformed);

        tabelPesanan.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        tabelPesanan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Kode Transaksi", "Pembeli", "Toko & Batch", "Tanggal", "Total", "Pengambilan", "Bukti Bayar", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelPesanan.setRowHeight(28);
        tabelPesanan.setSelectionBackground(new java.awt.Color(145, 190, 0));
        tabelPesanan.setSelectionForeground(new java.awt.Color(0, 83, 38));
        scrollPesanan.setViewportView(tabelPesanan);

        pnlDetailCard.setBackground(new java.awt.Color(255, 249, 236));
        pnlDetailCard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 83, 38)));

        lblDetailHeader.setFont(new java.awt.Font("Circe Slab C Bold", 0, 15)); // NOI18N
        lblDetailHeader.setForeground(new java.awt.Color(0, 83, 38));
        lblDetailHeader.setText("Detail Pesanan & Bukti Pembayaran (pilih pesanan di atas)");

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
                .addComponent(scrollDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        pnlActions.setBackground(new java.awt.Color(247, 231, 198));
        pnlActions.setLayout(new java.awt.GridLayout(1, 4, 12, 0));

        btnValidasi.setBackground(new java.awt.Color(145, 190, 0));
        btnValidasi.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnValidasi.setForeground(new java.awt.Color(0, 83, 38));
        btnValidasi.setText("Validasi Pembayaran");
        btnValidasi.setBorder(null);
        btnValidasi.addActionListener(this::btnValidasiActionPerformed);
        pnlActions.add(btnValidasi);

        btnSelesai.setBackground(new java.awt.Color(0, 83, 38));
        btnSelesai.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnSelesai.setForeground(new java.awt.Color(247, 231, 198));
        btnSelesai.setText("Tandai Selesai");
        btnSelesai.setBorder(null);
        btnSelesai.addActionListener(this::btnSelesaiActionPerformed);
        pnlActions.add(btnSelesai);

        btnStatus.setBackground(new java.awt.Color(255, 198, 0));
        btnStatus.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnStatus.setForeground(new java.awt.Color(0, 83, 38));
        btnStatus.setText("Ubah Status Lain");
        btnStatus.setBorder(null);
        btnStatus.addActionListener(this::btnStatusActionPerformed);
        pnlActions.add(btnStatus);

        btnBatalkan.setBackground(new java.awt.Color(255, 82, 0));
        btnBatalkan.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnBatalkan.setForeground(new java.awt.Color(255, 255, 255));
        btnBatalkan.setText("Batalkan Pesanan");
        btnBatalkan.setBorder(null);
        btnBatalkan.addActionListener(this::btnBatalkanActionPerformed);
        pnlActions.add(btnBatalkan);

        javax.swing.GroupLayout pnlContentLayout = new javax.swing.GroupLayout(pnlContent);
        pnlContent.setLayout(pnlContentLayout);
        pnlContentLayout.setHorizontalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlContentLayout.createSequentialGroup()
                        .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSubtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbFilter, 0, 234, Short.MAX_VALUE)))
                    .addComponent(scrollPesanan)
                    .addComponent(pnlDetailCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(scrollPesanan, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlDetailCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        splitMain.setRightComponent(pnlContent);

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, 1160, Short.MAX_VALUE)
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
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
        if (umkm != null || (user != null && "UMKM".equalsIgnoreCase(user.getRole()))) {
            new DashboardUMKM(user).setVisible(true);
        } else {
            new DashboardAdmin(user).setVisible(true);
        }
        dispose();
    }//GEN-LAST:event_btnKembaliActionPerformed

    private void btnStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStatusActionPerformed
        ubahStatus();
    }//GEN-LAST:event_btnStatusActionPerformed

    private void btnValidasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidasiActionPerformed
        validasiPembayaran();
    }//GEN-LAST:event_btnValidasiActionPerformed

    private void btnRekapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRekapActionPerformed
        tampilkanRekapStatus();
    }//GEN-LAST:event_btnRekapActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        tampilkanData();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void cmbFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFilterActionPerformed
        terapkanFilter();
    }//GEN-LAST:event_cmbFilterActionPerformed

    private void btnSelesaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelesaiActionPerformed
        ubahStatusCepat("SELESAI");
    }//GEN-LAST:event_btnSelesaiActionPerformed

    private void btnBatalkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalkanActionPerformed
        ubahStatusCepat("DIBATALKAN");
    }//GEN-LAST:event_btnBatalkanActionPerformed

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

        java.awt.EventQueue.invokeLater(() -> new DataPesananFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBatalkan;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRekap;
    private javax.swing.JButton btnSelesai;
    private javax.swing.JButton btnStatus;
    private javax.swing.JButton btnValidasi;
    private javax.swing.JComboBox<String> cmbFilter;
    private javax.swing.JLabel lblDetailHeader;
    private javax.swing.JLabel lblFilter;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblRoleBadge;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlBrand;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlDetailCard;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JScrollPane scrollDetail;
    private javax.swing.JScrollPane scrollPesanan;
    private javax.swing.JSplitPane splitMain;
    private javax.swing.JTable tabelPesanan;
    private javax.swing.JTextArea txtDetailArea;
    // End of variables declaration//GEN-END:variables
}
