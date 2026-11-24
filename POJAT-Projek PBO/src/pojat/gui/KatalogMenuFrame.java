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
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import pojat.dao.KatalogMenuDAO;
import pojat.model.User;
import pojat.util.KeranjangPOJAT;

/**
 *
 * @author Jasmine
 */
public class KatalogMenuFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(KatalogMenuFrame.class.getName());
    private User user;
    private KatalogMenuDAO katalogDAO;
    private DefaultTableModel model;
    private List<Object[]> semuaMenu = new ArrayList<>();
    private List<Object[]> filteredMenu = new ArrayList<>();
    private final NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    /**
     * Creates new form KatalogMenuFrame
     */
    public KatalogMenuFrame() {
        this.katalogDAO = new KatalogMenuDAO();
        initComponents();
        setLocationRelativeTo(null);
        pojat.util.UIHelper.styleTable(tabelMenu);
        pojat.util.UIHelper.applyHandCursor(btnCari, btnRefresh, btnTambah, btnKeranjang, btnKembali);
        inisialisasiTabel();
        tampilkanMenu(false);
        updateBadgeKeranjang();
    }

    public KatalogMenuFrame(User user) {
        this.user = user;
        this.katalogDAO = new KatalogMenuDAO();
        initComponents();
        setLocationRelativeTo(null);
        pojat.util.UIHelper.styleTable(tabelMenu);
        pojat.util.UIHelper.applyHandCursor(btnCari, btnRefresh, btnTambah, btnKeranjang, btnKembali);
        inisialisasiTabel();
        tampilkanMenu(false);
        updateBadgeKeranjang();
    }

    private void inisialisasiTabel() {
        tabelMenu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model = (DefaultTableModel) tabelMenu.getModel();
        tabelMenu.getColumnModel().getColumn(0).setPreferredWidth(45);
        tabelMenu.getColumnModel().getColumn(1).setPreferredWidth(140);
        tabelMenu.getColumnModel().getColumn(2).setPreferredWidth(140);
        tabelMenu.getColumnModel().getColumn(3).setPreferredWidth(210);
        tabelMenu.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabelMenu.getColumnModel().getColumn(5).setPreferredWidth(55);

        tabelMenu.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                tampilkanMenuTerpilih();
            }
        });
    }

    private void updateBadgeKeranjang() {
        int jumlahJenis = KeranjangPOJAT.getItems().size();
        btnKeranjang.setText("Keranjang (" + jumlahJenis + " item)");
    }

    private void tampilkanMenuTerpilih() {
        int baris = tabelMenu.getSelectedRow();
        if (baris < 0 || baris >= filteredMenu.size()) {
            lblMenuDipilih.setText("Menu dipilih: Belum ada (klik baris pada tabel)");
            return;
        }
        Object[] row = filteredMenu.get(baris);
        String toko = String.valueOf(row[1]);
        String nama = String.valueOf(row[2]);
        double harga = Double.parseDouble(String.valueOf(row[4]));
        int stok = Integer.parseInt(String.valueOf(row[5]));

        lblMenuDipilih.setText("Menu dipilih: " + nama + " (" + toko + ") — "
                + rupiahFormat.format(harga) + "  |  Stok tersedia: " + stok);
        int max = Math.max(1, stok);
        spnJumlah.setModel(new SpinnerNumberModel(1, 1, max, 1));
    }

    private void tampilkanMenu() {
        tampilkanMenu(true);
    }

    private void tampilkanMenu(boolean showErrorDialog) {
        try {
            semuaMenu = katalogDAO.getSemuaMenuDenganToko();
            filterMenu(txtCari.getText().trim());
        } catch (SQLException e) {
            if (showErrorDialog) {
                JOptionPane.showMessageDialog(
                    this,
                    "Gagal menampilkan menu: " + e.getMessage()
                );
            } else {
                lblMenuDipilih.setText("Database belum terhubung (klik Refresh setelah MySQL aktif)");
            }
        }
    }

    private void filterMenu(String keyword) {
        model.setRowCount(0);
        filteredMenu.clear();
        String q = keyword == null ? "" : keyword.toLowerCase();

        for (Object[] row : semuaMenu) {
            String toko = String.valueOf(row[1]).toLowerCase();
            String namaMenu = String.valueOf(row[2]).toLowerCase();
            String deskripsi = row[3] == null ? "" : String.valueOf(row[3]).toLowerCase();

            if (q.isEmpty() || toko.contains(q) || namaMenu.contains(q) || deskripsi.contains(q)) {
                filteredMenu.add(row);
                double harga = Double.parseDouble(String.valueOf(row[4]));
                model.addRow(new Object[]{
                    row[0],
                    row[1],
                    row[2],
                    row[3],
                    rupiahFormat.format(harga),
                    row[5]
                });
            }
        }
        tampilkanMenuTerpilih();
    }

    private void tambahKeranjang() {
        int baris = tabelMenu.getSelectedRow();

        if (baris == -1 || baris >= filteredMenu.size()) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih menu pada tabel terlebih dahulu!"
            );
            return;
        }

        Object[] row = filteredMenu.get(baris);
        int idMenu = Integer.parseInt(String.valueOf(row[0]));
        String namaToko = String.valueOf(row[1]);
        String namaMenu = String.valueOf(row[2]);
        double harga = Double.parseDouble(String.valueOf(row[4]));
        int stok = Integer.parseInt(String.valueOf(row[5]));
        int idUmkm = row.length > 6 ? Integer.parseInt(String.valueOf(row[6])) : 0;

        if (stok <= 0) {
            JOptionPane.showMessageDialog(this, "Stok menu habis!");
            return;
        }

        int jumlah = ((Number) spnJumlah.getValue()).intValue();
        if (jumlah <= 0 || jumlah > stok) {
            JOptionPane.showMessageDialog(
                this,
                "Jumlah harus antara 1 sampai " + stok
            );
            return;
        }

        String catatan = txtCatatan.getText().trim();
        if (catatan.isEmpty()) {
            catatan = null;
        }

        KeranjangPOJAT.tambah(idMenu, idUmkm, namaToko, namaMenu, harga, jumlah, stok, catatan);
        updateBadgeKeranjang();
        txtCatatan.setText("");

        JOptionPane.showMessageDialog(
            this,
            jumlah + "x " + namaMenu + " ditambahkan ke keranjang!"
        );
    }

    private void tampilkanKeranjang() {
        KeranjangFrame kf = new KeranjangFrame(user);
        kf.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                updateBadgeKeranjang();
                tampilkanMenu();
            }
        });
        kf.setVisible(true);
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
        btnKeranjang = new javax.swing.JButton();
        btnKembali = new javax.swing.JButton();
        pnlContent = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        pnlSearch = new javax.swing.JPanel();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        scrollMenu = new javax.swing.JScrollPane();
        tabelMenu = new javax.swing.JTable();
        pnlOrderCard = new javax.swing.JPanel();
        lblMenuDipilih = new javax.swing.JLabel();
        lblJumlah = new javax.swing.JLabel();
        spnJumlah = new javax.swing.JSpinner();
        lblCatatan = new javax.swing.JLabel();
        txtCatatan = new javax.swing.JTextField();
        btnTambah = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("POJAT - Katalog Menu");
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
        lblRoleBadge.setText("KATALOG MENU");

        btnKeranjang.setBackground(new java.awt.Color(255, 82, 0));
        btnKeranjang.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnKeranjang.setForeground(new java.awt.Color(255, 255, 255));
        btnKeranjang.setText("Keranjang (0 item)");
        btnKeranjang.setBorder(null);
        btnKeranjang.addActionListener(this::btnKeranjangActionPerformed);

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
                    .addComponent(btnKeranjang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 348, Short.MAX_VALUE)
                .addComponent(btnKeranjang, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        splitMain.setLeftComponent(pnlBrand);

        pnlContent.setBackground(new java.awt.Color(247, 231, 198));

        lblTitle.setFont(new java.awt.Font("Circe Slab C Bold", 0, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(0, 83, 38));
        lblTitle.setText("Katalog Menu UMKM");

        lblSubtitle.setFont(new java.awt.Font("Circe Slab C", 0, 14)); // NOI18N
        lblSubtitle.setText("1. Klik menu di tabel   2. Atur jumlah & catatan di bawah   3. Buka Keranjang untuk checkout");

        pnlSearch.setBackground(new java.awt.Color(247, 231, 198));

        txtCari.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        txtCari.addActionListener(this::txtCariActionPerformed);

        btnCari.setBackground(new java.awt.Color(0, 83, 38));
        btnCari.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnCari.setForeground(new java.awt.Color(247, 231, 198));
        btnCari.setText("Cari Menu");
        btnCari.setBorder(null);
        btnCari.addActionListener(this::btnCariActionPerformed);

        btnRefresh.setBackground(new java.awt.Color(255, 198, 0));
        btnRefresh.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnRefresh.setForeground(new java.awt.Color(0, 83, 38));
        btnRefresh.setText("Refresh");
        btnRefresh.setBorder(null);
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);

        javax.swing.GroupLayout pnlSearchLayout = new javax.swing.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addComponent(txtCari)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabelMenu.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        tabelMenu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Toko UMKM", "Nama Menu", "Deskripsi", "Harga", "Stok"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelMenu.setRowHeight(28);
        tabelMenu.setSelectionBackground(new java.awt.Color(145, 190, 0));
        tabelMenu.setSelectionForeground(new java.awt.Color(0, 83, 38));
        scrollMenu.setViewportView(tabelMenu);

        pnlOrderCard.setBackground(new java.awt.Color(255, 249, 236));
        pnlOrderCard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 83, 38)));

        lblMenuDipilih.setFont(new java.awt.Font("Circe Slab C Bold", 0, 15)); // NOI18N
        lblMenuDipilih.setForeground(new java.awt.Color(0, 83, 38));
        lblMenuDipilih.setText("Menu dipilih: Belum ada (klik baris pada tabel)");

        lblJumlah.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        lblJumlah.setText("Jumlah:");

        spnJumlah.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        spnJumlah.setModel(new javax.swing.SpinnerNumberModel(1, 1, 999, 1));

        lblCatatan.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        lblCatatan.setText("Catatan (opsional):");

        txtCatatan.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N

        btnTambah.setBackground(new java.awt.Color(145, 190, 0));
        btnTambah.setFont(new java.awt.Font("Circe Slab C Bold", 0, 14)); // NOI18N
        btnTambah.setForeground(new java.awt.Color(0, 83, 38));
        btnTambah.setText("+ Tambah ke Keranjang");
        btnTambah.setBorder(null);
        btnTambah.addActionListener(this::btnTambahActionPerformed);

        javax.swing.GroupLayout pnlOrderCardLayout = new javax.swing.GroupLayout(pnlOrderCard);
        pnlOrderCard.setLayout(pnlOrderCardLayout);
        pnlOrderCardLayout.setHorizontalGroup(
            pnlOrderCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOrderCardLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlOrderCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMenuDipilih, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlOrderCardLayout.createSequentialGroup()
                        .addComponent(lblJumlah)
                        .addGap(8, 8, 8)
                        .addComponent(spnJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblCatatan)
                        .addGap(8, 8, 8)
                        .addComponent(txtCatatan)
                        .addGap(14, 14, 14)
                        .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18))
        );
        pnlOrderCardLayout.setVerticalGroup(
            pnlOrderCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOrderCardLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblMenuDipilih)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlOrderCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblJumlah)
                    .addComponent(spnJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCatatan)
                    .addComponent(txtCatatan, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTambah, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(pnlSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 742, Short.MAX_VALUE)
                    .addComponent(pnlOrderCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        pnlContentLayout.setVerticalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubtitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlOrderCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        splitMain.setRightComponent(pnlContent);

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
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

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        tambahKeranjang();
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnKeranjangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeranjangActionPerformed
        tampilkanKeranjang();
    }//GEN-LAST:event_btnKeranjangActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        txtCari.setText("");
        tampilkanMenu();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        filterMenu(txtCari.getText().trim());
    }//GEN-LAST:event_btnCariActionPerformed

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariActionPerformed
        filterMenu(txtCari.getText().trim());
    }//GEN-LAST:event_txtCariActionPerformed

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

        java.awt.EventQueue.invokeLater(() -> new KatalogMenuFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnKeranjang;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnTambah;
    private javax.swing.JLabel lblCatatan;
    private javax.swing.JLabel lblJumlah;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblMenuDipilih;
    private javax.swing.JLabel lblRoleBadge;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlBrand;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlOrderCard;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JScrollPane scrollMenu;
    private javax.swing.JSplitPane splitMain;
    private javax.swing.JSpinner spnJumlah;
    private javax.swing.JTable tabelMenu;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtCatatan;
    // End of variables declaration//GEN-END:variables
}
