/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pojat.gui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import pojat.dao.MenuItemDAO;
import pojat.dao.UMKMDAO;
import pojat.model.MenuItem;
import pojat.model.UMKM;
import pojat.model.User;
import pojat.util.UIHelper;

/**
 *
 * @author Jasmine
 */
public class DataMenuFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DataMenuFrame.class.getName());
    private User user;
    private UMKM umkm;
    private MenuItemDAO menuDAO;
    private UMKMDAO umkmDAO;
    private DefaultTableModel model;
    private List<MenuItem> semuaMenu = new ArrayList<>();
    private List<MenuItem> filteredMenu = new ArrayList<>();
    private List<UMKM> daftarUmkmCache = new ArrayList<>();
    private Map<Integer, String> namaUmkmMap = new HashMap<>();

    /**
     * Creates new form DataMenuFrame
     */
    public DataMenuFrame() {
        this.menuDAO = new MenuItemDAO();
        this.umkmDAO = new UMKMDAO();
        initComponents();
        setLocationRelativeTo(null);
        UIHelper.styleTable(tabelMenu);
        UIHelper.applyHandCursor(btnCari, btnTambah, btnEdit, btnHapus, btnRefresh, btnKembali);
        tabelMenu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model = (DefaultTableModel) tabelMenu.getModel();
        tampilkanData(false);
    }

    public DataMenuFrame(User user) {
        this(user, null);
    }

    public DataMenuFrame(User user, UMKM umkm) {
        this.user = user;
        this.umkm = umkm;
        this.menuDAO = new MenuItemDAO();
        this.umkmDAO = new UMKMDAO();
        initComponents();
        setLocationRelativeTo(null);
        UIHelper.styleTable(tabelMenu);
        UIHelper.applyHandCursor(btnCari, btnTambah, btnEdit, btnHapus, btnRefresh, btnKembali);
        if (umkm != null) {
            lblSubtitle.setText("Kelola menu produk untuk toko " + umkm.getNamaToko());
        }
        tabelMenu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model = (DefaultTableModel) tabelMenu.getModel();
        tampilkanData(false);
    }

    private void tampilkanData() {
        tampilkanData(true);
    }

    private void tampilkanData(boolean showErrorDialog) {
        try {
            namaUmkmMap.clear();
            daftarUmkmCache = umkmDAO.getAllUMKM();
            for (UMKM u : daftarUmkmCache) {
                namaUmkmMap.put(u.getIdUmkm(), u.getNamaToko());
            }
            if (umkm != null) {
                namaUmkmMap.put(umkm.getIdUmkm(), umkm.getNamaToko());
            }
            semuaMenu = (umkm != null)
                    ? menuDAO.getMenuByUMKM(umkm.getIdUmkm())
                    : menuDAO.getAllMenu();
            terapkanFilter();
        } catch (SQLException e) {
            if (showErrorDialog) {
                JOptionPane.showMessageDialog(
                    this,
                    "Gagal mengambil data menu: " + e.getMessage()
                );
            } else {
                lblSubtitle.setText("Database belum terhubung (klik Refresh setelah MySQL aktif)");
            }
        }
    }

    private void terapkanFilter() {
        model.setRowCount(0);
        filteredMenu.clear();
        String q = txtCari.getText().trim().toLowerCase();

        for (MenuItem menu : semuaMenu) {
            String namaToko = namaUmkmMap.getOrDefault(menu.getIdUmkm(), "UMKM #" + menu.getIdUmkm());
            String nama = menu.getNamaMenu() == null ? "" : menu.getNamaMenu();
            String desc = menu.getDeskripsi() == null ? "" : menu.getDeskripsi();
            if (!q.isEmpty()
                    && !nama.toLowerCase().contains(q)
                    && !desc.toLowerCase().contains(q)
                    && !namaToko.toLowerCase().contains(q)) {
                continue;
            }
            filteredMenu.add(menu);
            model.addRow(new Object[]{
                menu.getIdMenu(),
                namaToko,
                menu.getNamaMenu(),
                menu.getDeskripsi(),
                UIHelper.formatRupiah(menu.getHarga()),
                menu.getStokPerBatch()
            });
        }
    }

    private MenuItem tampilkanFormMenu(MenuItem lama) {
        JComboBox<String> cmbUmkm = new JComboBox<>();
        List<UMKM> pilihanUmkm = new ArrayList<>();
        if (umkm != null) {
            pilihanUmkm.add(umkm);
            cmbUmkm.addItem(umkm.getIdUmkm() + " - " + umkm.getNamaToko());
            cmbUmkm.setEnabled(false);
        } else {
            int selectedIdx = 0;
            for (int i = 0; i < daftarUmkmCache.size(); i++) {
                UMKM u = daftarUmkmCache.get(i);
                pilihanUmkm.add(u);
                cmbUmkm.addItem(u.getIdUmkm() + " - " + u.getNamaToko());
                if (lama != null && u.getIdUmkm() == lama.getIdUmkm()) {
                    selectedIdx = i;
                }
            }
            if (pilihanUmkm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Belum ada data UMKM terdaftar!");
                return null;
            }
            cmbUmkm.setSelectedIndex(selectedIdx);
        }

        JTextField txtNama = new JTextField(lama != null ? lama.getNamaMenu() : "");
        JTextField txtDeskripsi = new JTextField(lama != null && lama.getDeskripsi() != null ? lama.getDeskripsi() : "");
        JTextField txtHarga = new JTextField(lama != null ? String.valueOf((long) lama.getHarga()) : "");
        JTextField txtStok = new JTextField(lama != null ? String.valueOf(lama.getStokPerBatch()) : "10");

        Object[] form = {
            "Toko UMKM:", cmbUmkm,
            "Nama Menu:", txtNama,
            "Deskripsi:", txtDeskripsi,
            "Harga (Rp):", txtHarga,
            "Stok per Batch:", txtStok
        };

        int pilihan = JOptionPane.showConfirmDialog(
            this, form, lama == null ? "Tambah Menu" : "Edit Menu",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (pilihan != JOptionPane.OK_OPTION) {
            return null;
        }

        try {
            int idUmkm = pilihanUmkm.get(cmbUmkm.getSelectedIndex()).getIdUmkm();
            String nama = txtNama.getText().trim();
            String deskripsi = txtDeskripsi.getText().trim();
            double harga = Double.parseDouble(txtHarga.getText().trim());
            int stok = Integer.parseInt(txtStok.getText().trim());

            if (nama.isEmpty() || harga < 0 || stok < 0) {
                JOptionPane.showMessageDialog(
                    this,
                    "Nama menu, harga (>= 0), dan stok (>= 0) harus valid!"
                );
                return null;
            }

            return new MenuItem(lama == null ? 0 : lama.getIdMenu(), idUmkm, nama, deskripsi, harga, stok);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this,
                "Harga dan stok harus berupa angka!"
            );
            return null;
        }
    }

    private void tambahData() {
        MenuItem menu = tampilkanFormMenu(null);
        if (menu == null) {
            return;
        }

        try {
            if (menuDAO.tambahMenu(menu)) {
                JOptionPane.showMessageDialog(this, "Menu berhasil ditambahkan!");
                tampilkanData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan menu: " + e.getMessage());
        }
    }

    private void editData() {
        int baris = tabelMenu.getSelectedRow();
        if (baris < 0 || baris >= filteredMenu.size()) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih menu yang ingin diedit terlebih dahulu!"
            );
            return;
        }

        MenuItem menuBaru = tampilkanFormMenu(filteredMenu.get(baris));
        if (menuBaru == null) {
            return;
        }

        try {
            if (menuDAO.updateMenu(menuBaru)) {
                JOptionPane.showMessageDialog(this, "Menu berhasil diperbarui!");
                tampilkanData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengedit menu: " + e.getMessage());
        }
    }

    private void hapusData() {
        int baris = tabelMenu.getSelectedRow();
        if (baris < 0 || baris >= filteredMenu.size()) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih menu yang ingin dihapus terlebih dahulu!"
            );
            return;
        }

        MenuItem target = filteredMenu.get(baris);
        int idMenu = target.getIdMenu();
        String namaMenu = target.getNamaMenu();

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
                JOptionPane.showMessageDialog(
                    this,
                    "Menu berhasil dihapus!"
                );
                tampilkanData();
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(
                this,
                "Menu tidak dapat dihapus karena masih digunakan dalam detail pesanan!",
                "Relasi Data",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal menghapus menu: " + e.getMessage()
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
        btnKembali = new javax.swing.JButton();
        pnlContent = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        pnlSearch = new javax.swing.JPanel();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        scrollMenu = new javax.swing.JScrollPane();
        tabelMenu = new javax.swing.JTable();
        pnlActions = new javax.swing.JPanel();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("POJAT - Kelola Menu");
        setResizable(false);

        splitMain.setDividerLocation(210);
        splitMain.setDividerSize(0);

        pnlBrand.setBackground(new java.awt.Color(255, 198, 0));
        pnlBrand.setPreferredSize(new java.awt.Dimension(210, 560));

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pojat/resources/images/pojat-it.png"))); // NOI18N

        lblRoleBadge.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        lblRoleBadge.setForeground(new java.awt.Color(0, 83, 38));
        lblRoleBadge.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoleBadge.setText("DATA MENU");

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
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        splitMain.setLeftComponent(pnlBrand);

        pnlContent.setBackground(new java.awt.Color(247, 231, 198));

        lblTitle.setFont(new java.awt.Font("Circe Slab C Bold", 0, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(0, 83, 38));
        lblTitle.setText("Kelola Menu UMKM");

        lblSubtitle.setFont(new java.awt.Font("Circe Slab C", 0, 14)); // NOI18N
        lblSubtitle.setText("Tambah, ubah, dan hapus menu produk UMKM");

        pnlSearch.setBackground(new java.awt.Color(247, 231, 198));

        txtCari.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        txtCari.addActionListener(this::txtCariActionPerformed);

        btnCari.setBackground(new java.awt.Color(0, 83, 38));
        btnCari.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnCari.setForeground(new java.awt.Color(247, 231, 198));
        btnCari.setText("Cari Menu");
        btnCari.setBorder(null);
        btnCari.addActionListener(this::btnCariActionPerformed);

        javax.swing.GroupLayout pnlSearchLayout = new javax.swing.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addComponent(txtCari)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabelMenu.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        tabelMenu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Menu", "Toko UMKM", "Nama Menu", "Deskripsi", "Harga", "Stok / Batch"
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

        pnlActions.setBackground(new java.awt.Color(247, 231, 198));
        pnlActions.setLayout(new java.awt.GridLayout(1, 4, 12, 0));

        btnTambah.setBackground(new java.awt.Color(145, 190, 0));
        btnTambah.setFont(new java.awt.Font("Circe Slab C Bold", 0, 14)); // NOI18N
        btnTambah.setForeground(new java.awt.Color(0, 83, 38));
        btnTambah.setText("Tambah Menu");
        btnTambah.setBorder(null);
        btnTambah.addActionListener(this::btnTambahActionPerformed);
        pnlActions.add(btnTambah);

        btnEdit.setBackground(new java.awt.Color(255, 198, 0));
        btnEdit.setFont(new java.awt.Font("Circe Slab C Bold", 0, 14)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(0, 83, 38));
        btnEdit.setText("Edit Menu");
        btnEdit.setBorder(null);
        btnEdit.addActionListener(this::btnEditActionPerformed);
        pnlActions.add(btnEdit);

        btnHapus.setBackground(new java.awt.Color(255, 82, 0));
        btnHapus.setFont(new java.awt.Font("Circe Slab C Bold", 0, 14)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setText("Hapus Menu");
        btnHapus.setBorder(null);
        btnHapus.addActionListener(this::btnHapusActionPerformed);
        pnlActions.add(btnHapus);

        btnRefresh.setBackground(new java.awt.Color(0, 83, 38));
        btnRefresh.setFont(new java.awt.Font("Circe Slab C Bold", 0, 14)); // NOI18N
        btnRefresh.setForeground(new java.awt.Color(247, 231, 198));
        btnRefresh.setText("Refresh");
        btnRefresh.setBorder(null);
        btnRefresh.addActionListener(this::btnRefreshActionPerformed);
        pnlActions.add(btnRefresh);

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
                    .addComponent(scrollMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                    .addComponent(pnlActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        pnlContentLayout.setVerticalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubtitle)
                .addGap(14, 14, 14)
                .addComponent(pnlSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(scrollMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        splitMain.setRightComponent(pnlContent);

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, 960, Short.MAX_VALUE)
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
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

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        tambahData();
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        editData();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        hapusData();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        txtCari.setText("");
        tampilkanData();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariActionPerformed
        terapkanFilter();
    }//GEN-LAST:event_txtCariActionPerformed

    private void btnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCariActionPerformed
        terapkanFilter();
    }//GEN-LAST:event_btnCariActionPerformed

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

        java.awt.EventQueue.invokeLater(() -> new DataMenuFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnTambah;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblRoleBadge;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlBrand;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JScrollPane scrollMenu;
    private javax.swing.JSplitPane splitMain;
    private javax.swing.JTable tabelMenu;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables
}
