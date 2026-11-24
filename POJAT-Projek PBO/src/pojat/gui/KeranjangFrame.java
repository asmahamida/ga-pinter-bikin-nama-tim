/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pojat.gui;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import pojat.dao.BatchPODAO;
import pojat.dao.CheckoutDAO;
import pojat.dao.UMKMDAO;
import pojat.model.BatchPO;
import pojat.model.UMKM;
import pojat.model.User;
import pojat.util.KeranjangPOJAT;
import pojat.util.KeranjangPOJAT.ItemKeranjang;

/**
 *
 * @author Jasmine
 */
public class KeranjangFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(KeranjangFrame.class.getName());
    private User user;
    private DefaultTableModel model;
    private final List<BatchPO> batchAktif = new ArrayList<>();
    private final UMKMDAO umkmDAO = new UMKMDAO();
    private final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    /**
     * Creates new form KeranjangFrame
     */
    public KeranjangFrame() {
        initComponents();
        setLocationRelativeTo(null);
        pojat.util.UIHelper.styleTable(tabel);
        pojat.util.UIHelper.applyHandCursor(btnUbah, btnHapus, btnRefresh, btnTutup, btnBrowse, btnCheckout);
        inisialisasiTabel();
        tampilkanKeranjang();
        muatBatchAktif();
    }

    public KeranjangFrame(User user) {
        this.user = user;
        initComponents();
        setLocationRelativeTo(null);
        pojat.util.UIHelper.styleTable(tabel);
        pojat.util.UIHelper.applyHandCursor(btnUbah, btnHapus, btnRefresh, btnTutup, btnBrowse, btnCheckout);
        inisialisasiTabel();
        tampilkanKeranjang();
        muatBatchAktif();
    }

    private void inisialisasiTabel() {
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model = (DefaultTableModel) tabel.getModel();
        tabel.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(170);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(60);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(160);
        tabel.getColumnModel().getColumn(5).setPreferredWidth(110);
    }

    private void tampilkanKeranjang() {
        model.setRowCount(0);

        List<ItemKeranjang> items = KeranjangPOJAT.getItems();

        for (ItemKeranjang item : items) {
            String namaTampil = (item.namaToko != null && !item.namaToko.isEmpty())
                    ? item.namaMenu + " [" + item.namaToko + "]"
                    : item.namaMenu;
            model.addRow(new Object[]{
                item.idMenu,
                namaTampil,
                rupiahFormat.format(item.harga),
                item.jumlah,
                item.catatanKhusus == null || item.catatanKhusus.isEmpty() ? "-" : item.catatanKhusus,
                rupiahFormat.format(item.getSubtotal())
            });
        }

        lblTotal.setText("Total: " + rupiahFormat.format(KeranjangPOJAT.getTotal()));
    }

    private void ubahItem() {
        int baris = tabel.getSelectedRow();
        List<ItemKeranjang> items = KeranjangPOJAT.getItems();
        if (baris < 0 || baris >= items.size()) {
            JOptionPane.showMessageDialog(this, "Pilih item di tabel yang ingin diubah terlebih dahulu!");
            return;
        }
        ItemKeranjang item = items.get(baris);
        javax.swing.JSpinner spnJumlah = new javax.swing.JSpinner(
                new javax.swing.SpinnerNumberModel(item.jumlah, 1, Math.max(1, item.stok), 1));
        javax.swing.JTextField txtCatatan = new javax.swing.JTextField(
                item.catatanKhusus == null ? "" : item.catatanKhusus);

        Object[] form = {
            "Menu: " + item.namaMenu + " (Stok maks: " + item.stok + ")",
            "Jumlah:", spnJumlah,
            "Catatan (opsional):", txtCatatan
        };
        int res = JOptionPane.showConfirmDialog(
            this, form, "Ubah Item Keranjang", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (res != JOptionPane.OK_OPTION) {
            return;
        }
        int jumlahBaru = ((Number) spnJumlah.getValue()).intValue();
        if (KeranjangPOJAT.updateItem(baris, jumlahBaru, txtCatatan.getText())) {
            tampilkanKeranjang();
        } else {
            JOptionPane.showMessageDialog(this, "Jumlah harus antara 1 sampai " + item.stok);
        }
    }

    private void muatBatchAktif() {
        batchAktif.clear();
        cmbBatch.removeAllItems();

        try {
            BatchPODAO batchDAO = new BatchPODAO();
            List<BatchPO> daftarBatch = batchDAO.getAllBatch();

            for (BatchPO batch : daftarBatch) {
                if ("DIBUKA".equalsIgnoreCase(batch.getStatus())
                        || "OPEN".equalsIgnoreCase(batch.getStatus())) {
                    batchAktif.add(batch);
                    cmbBatch.addItem(batch.getIdBatch() + " - " + batch.getNamaBatch()
                            + " (Kirim: " + batch.getTglPengiriman() + ")");
                }
            }

            if (batchAktif.isEmpty()) {
                cmbBatch.addItem("Belum ada batch PO dibuka");
                lblRekening.setText("Tujuan Transfer: Belum ada batch PO yang dibuka saat ini.");
            } else {
                updateInfoRekening();
            }
        } catch (SQLException e) {
            cmbBatch.addItem("Gagal memuat batch PO");
            lblRekening.setText("Tujuan Transfer: Tidak dapat terhubung ke database.");
        }
    }

    private void updateInfoRekening() {
        int idx = cmbBatch.getSelectedIndex();
        if (idx < 0 || idx >= batchAktif.size()) {
            return;
        }
        BatchPO batch = batchAktif.get(idx);
        try {
            UMKM umkm = umkmDAO.getUMKMById(batch.getIdUMKM());
            if (umkm != null) {
                String rek = (umkm.getNoRekening() == null || umkm.getNoRekening().isEmpty())
                        ? "-" : umkm.getNoRekening();
                String bank = (umkm.getBank() == null || umkm.getBank().isEmpty())
                        ? "-" : umkm.getBank();
                lblRekening.setText("Transfer ke " + umkm.getNamaToko() + ": " + bank + " " + rek
                        + "  |  Lokasi Jemput: " + umkm.getAlamatPenjemputan());
            } else {
                lblRekening.setText("Tujuan Transfer: UMKM #" + batch.getIdUMKM());
            }
        } catch (SQLException e) {
            lblRekening.setText("Tujuan Transfer: UMKM #" + batch.getIdUMKM());
        }
    }

    private void hapusItem() {
        int baris = tabel.getSelectedRow();

        if (baris == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih item di tabel yang ingin dihapus!"
            );
            return;
        }

        KeranjangPOJAT.hapus(baris);
        tampilkanKeranjang();
    }

    private void browseBukti() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih File Bukti Pembayaran");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtBukti.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void checkout() {
        if (user == null) {
            JOptionPane.showMessageDialog(
                this,
                "Silakan login terlebih dahulu untuk checkout!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (KeranjangPOJAT.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Keranjang masih kosong! Tambahkan menu dari Katalog terlebih dahulu."
            );
            return;
        }

        int indexBatch = cmbBatch.getSelectedIndex();
        if (indexBatch < 0 || indexBatch >= batchAktif.size()) {
            JOptionPane.showMessageDialog(
                this,
                "Belum ada Batch PO aktif yang tersedia!"
            );
            return;
        }

        BatchPO batchDipilih = batchAktif.get(indexBatch);
        for (ItemKeranjang item : KeranjangPOJAT.getItems()) {
            if (item.idUmkm > 0 && item.idUmkm != batchDipilih.getIdUMKM()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Menu '" + item.namaMenu + "' berasal dari " + item.namaToko
                    + ", bukan toko pemilik Batch PO yang dipilih!\n"
                    + "Pilih Batch PO toko yang sesuai atau hapus item dari toko lain terlebih dahulu.",
                    "Batch PO Tidak Sesuai Toko",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        }
        String metodeDipilih = String.valueOf(cmbMetode.getSelectedItem());
        String bukti = txtBukti.getText().trim();
        if (bukti.isEmpty()) {
            bukti = null;
        } else if (bukti.length() > 255) {
            JOptionPane.showMessageDialog(
                this,
                "Referensi / path bukti pembayaran maksimal 255 karakter!"
            );
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Total pembayaran : " + rupiahFormat.format(KeranjangPOJAT.getTotal())
            + "\nBatch PO         : " + batchDipilih.getNamaBatch()
            + "\nMetode           : " + metodeDipilih
            + "\nBukti Bayar      : " + (bukti == null ? "Belum dilampirkan" : bukti)
            + "\n\nBuat pesanan sekarang?",
            "Konfirmasi Checkout",
            JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            CheckoutDAO checkoutDAO = new CheckoutDAO();

            boolean berhasil = checkoutDAO.checkout(
                user.getIdUser(),
                batchDipilih.getIdBatch(),
                metodeDipilih,
                bukti,
                KeranjangPOJAT.getItems()
            );

            if (berhasil) {
                KeranjangPOJAT.kosongkan();
                txtBukti.setText("");
                tampilkanKeranjang();

                JOptionPane.showMessageDialog(
                    this,
                    "Pesanan berhasil dibuat!\n"
                    + (bukti == null
                        ? "Silakan unggah bukti pembayaran melalui menu Riwayat Pesanan."
                        : "Bukti pembayaran tersimpan dan menunggu validasi penjual.")
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
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnTutup = new javax.swing.JButton();
        pnlContent = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        scrollKeranjang = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        pnlCheckoutCard = new javax.swing.JPanel();
        lblBatch = new javax.swing.JLabel();
        cmbBatch = new javax.swing.JComboBox<>();
        lblMetode = new javax.swing.JLabel();
        cmbMetode = new javax.swing.JComboBox<>();
        lblRekening = new javax.swing.JLabel();
        lblBukti = new javax.swing.JLabel();
        txtBukti = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        lblTotal = new javax.swing.JLabel();
        btnCheckout = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("POJAT - Keranjang & Checkout");
        setResizable(false);

        splitMain.setDividerLocation(200);
        splitMain.setDividerSize(0);

        pnlBrand.setBackground(new java.awt.Color(255, 198, 0));
        pnlBrand.setPreferredSize(new java.awt.Dimension(200, 620));

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pojat/resources/images/pojat-it.png"))); // NOI18N

        lblRoleBadge.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        lblRoleBadge.setForeground(new java.awt.Color(0, 83, 38));
        lblRoleBadge.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoleBadge.setText("CHECKOUT");

        btnUbah.setBackground(new java.awt.Color(255, 249, 236));
        btnUbah.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnUbah.setForeground(new java.awt.Color(0, 83, 38));
        btnUbah.setText("Ubah Jumlah / Catatan");
        btnUbah.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 83, 38)));
        btnUbah.addActionListener(this::btnUbahActionPerformed);

        btnHapus.setBackground(new java.awt.Color(255, 82, 0));
        btnHapus.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setText("Hapus Item Dipilih");
        btnHapus.setBorder(null);
        btnHapus.addActionListener(this::btnHapusActionPerformed);

        btnRefresh.setBackground(new java.awt.Color(145, 190, 0));
        btnRefresh.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnRefresh.setForeground(new java.awt.Color(0, 83, 38));
        btnRefresh.setText("Refresh");
        btnRefresh.setBorder(null);
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);

        btnTutup.setBackground(new java.awt.Color(0, 83, 38));
        btnTutup.setFont(new java.awt.Font("Circe Slab C Bold", 0, 14)); // NOI18N
        btnTutup.setForeground(new java.awt.Color(247, 231, 198));
        btnTutup.setText("Tutup");
        btnTutup.setBorder(null);
        btnTutup.addActionListener(this::btnTutupActionPerformed);

        javax.swing.GroupLayout pnlBrandLayout = new javax.swing.GroupLayout(pnlBrand);
        pnlBrand.setLayout(pnlBrandLayout);
        pnlBrandLayout.setHorizontalGroup(
            pnlBrandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBrandLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBrandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                    .addComponent(lblRoleBadge, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUbah, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnHapus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTutup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlBrandLayout.setVerticalGroup(
            pnlBrandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBrandLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblRoleBadge)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 242, Short.MAX_VALUE)
                .addComponent(btnUbah, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnTutup, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        splitMain.setLeftComponent(pnlBrand);

        pnlContent.setBackground(new java.awt.Color(247, 231, 198));

        lblTitle.setFont(new java.awt.Font("Circe Slab C Bold", 0, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(0, 83, 38));
        lblTitle.setText("Keranjang & Checkout");

        lblSubtitle.setFont(new java.awt.Font("Circe Slab C", 0, 14)); // NOI18N
        lblSubtitle.setText("Periksa daftar pesanan, pilih Batch PO, dan lengkapi pembayaran");

        tabel.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        tabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nama Menu", "Harga", "Jumlah", "Catatan", "Subtotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel.setRowHeight(28);
        tabel.setSelectionBackground(new java.awt.Color(145, 190, 0));
        tabel.setSelectionForeground(new java.awt.Color(0, 83, 38));
        scrollKeranjang.setViewportView(tabel);

        pnlCheckoutCard.setBackground(new java.awt.Color(255, 249, 236));
        pnlCheckoutCard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 83, 38)));

        lblBatch.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        lblBatch.setForeground(new java.awt.Color(0, 83, 38));
        lblBatch.setText("Pilih Batch PO Aktif:");

        cmbBatch.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        cmbBatch.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Memuat batch PO..." }));
        cmbBatch.addActionListener(this::cmbBatchActionPerformed);

        lblMetode.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        lblMetode.setForeground(new java.awt.Color(0, 83, 38));
        lblMetode.setText("Metode Pengambilan:");

        cmbMetode.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        cmbMetode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ambil Sendiri", "Pengantaran" }));

        lblRekening.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        lblRekening.setForeground(new java.awt.Color(255, 82, 0));
        lblRekening.setText("Tujuan Transfer: Pilih Batch PO untuk melihat rekening toko");

        lblBukti.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        lblBukti.setText("Bukti Pembayaran / Referensi Transfer (opsional, bisa diunggah nanti di Riwayat):");

        txtBukti.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N

        btnBrowse.setBackground(new java.awt.Color(255, 198, 0));
        btnBrowse.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnBrowse.setForeground(new java.awt.Color(0, 83, 38));
        btnBrowse.setText("Browse...");
        btnBrowse.setBorder(null);
        btnBrowse.addActionListener(this::btnBrowseActionPerformed);

        lblTotal.setFont(new java.awt.Font("Circe Slab C Bold", 0, 22)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(0, 83, 38));
        lblTotal.setText("Total: Rp0");

        btnCheckout.setBackground(new java.awt.Color(145, 190, 0));
        btnCheckout.setFont(new java.awt.Font("Circe Slab C Bold", 0, 15)); // NOI18N
        btnCheckout.setForeground(new java.awt.Color(0, 83, 38));
        btnCheckout.setText("Buat Pesanan Sekarang");
        btnCheckout.setBorder(null);
        btnCheckout.addActionListener(this::btnCheckoutActionPerformed);

        javax.swing.GroupLayout pnlCheckoutCardLayout = new javax.swing.GroupLayout(pnlCheckoutCard);
        pnlCheckoutCard.setLayout(pnlCheckoutCardLayout);
        pnlCheckoutCardLayout.setHorizontalGroup(
            pnlCheckoutCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCheckoutCardLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlCheckoutCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRekening, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlCheckoutCardLayout.createSequentialGroup()
                        .addGroup(pnlCheckoutCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblBatch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbBatch, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlCheckoutCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMetode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbMetode, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pnlCheckoutCardLayout.createSequentialGroup()
                        .addComponent(lblBukti)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlCheckoutCardLayout.createSequentialGroup()
                        .addComponent(txtBukti)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlCheckoutCardLayout.createSequentialGroup()
                        .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20))
        );
        pnlCheckoutCardLayout.setVerticalGroup(
            pnlCheckoutCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCheckoutCardLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlCheckoutCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBatch)
                    .addComponent(lblMetode))
                .addGap(4, 4, 4)
                .addGroup(pnlCheckoutCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbBatch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbMetode, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblRekening)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblBukti)
                .addGap(4, 4, 4)
                .addGroup(pnlCheckoutCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBukti, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlCheckoutCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal)
                    .addComponent(btnCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        javax.swing.GroupLayout pnlContentLayout = new javax.swing.GroupLayout(pnlContent);
        pnlContent.setLayout(pnlContentLayout);
        pnlContentLayout.setHorizontalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblSubtitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollKeranjang, javax.swing.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
                    .addComponent(pnlCheckoutCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        pnlContentLayout.setVerticalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubtitle)
                .addGap(18, 18, 18)
                .addComponent(scrollKeranjang, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlCheckoutCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        splitMain.setRightComponent(pnlContent);

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE)
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
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

    private void btnTutupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTutupActionPerformed
        dispose();
    }//GEN-LAST:event_btnTutupActionPerformed

    private void btnCheckoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckoutActionPerformed
        checkout();
    }//GEN-LAST:event_btnCheckoutActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        hapusItem();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        ubahItem();
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        tampilkanKeranjang();
        muatBatchAktif();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void cmbBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBatchActionPerformed
        updateInfoRekening();
    }//GEN-LAST:event_cmbBatchActionPerformed

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        browseBukti();
    }//GEN-LAST:event_btnBrowseActionPerformed

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

        java.awt.EventQueue.invokeLater(() -> new KeranjangFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnCheckout;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnTutup;
    private javax.swing.JButton btnUbah;
    private javax.swing.JComboBox<String> cmbBatch;
    private javax.swing.JComboBox<String> cmbMetode;
    private javax.swing.JLabel lblBatch;
    private javax.swing.JLabel lblBukti;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblMetode;
    private javax.swing.JLabel lblRekening;
    private javax.swing.JLabel lblRoleBadge;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnlBrand;
    private javax.swing.JPanel pnlCheckoutCard;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JScrollPane scrollKeranjang;
    private javax.swing.JSplitPane splitMain;
    private javax.swing.JTable tabel;
    private javax.swing.JTextField txtBukti;
    // End of variables declaration//GEN-END:variables
}
