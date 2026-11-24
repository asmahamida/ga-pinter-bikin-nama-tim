/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pojat.gui;

import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import pojat.dao.UMKMDAO;
import pojat.model.UMKM;
import pojat.model.User;

/**
 *
 * @author Jasmine
 */
public class DashboardUMKM extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DashboardUMKM.class.getName());
    private User user;
    private UMKMDAO umkmDAO;
    private UMKM umkm;

    /**
     * Creates new form DashboardUMKM
     */
    public DashboardUMKM() {
        this.umkmDAO = new UMKMDAO();
        initComponents();
        setLocationRelativeTo(null);
        pojat.util.UIHelper.applyHandCursor(
            btnProfilUMKM, btnKelolaMenu, btnKelolaBatch, btnPesananMasuk, btnLogout
        );
    }

    public DashboardUMKM(User user) {
        this.user = user;
        this.umkmDAO = new UMKMDAO();
        initComponents();
        setLocationRelativeTo(null);
        pojat.util.UIHelper.applyHandCursor(
            btnProfilUMKM, btnKelolaMenu, btnKelolaBatch, btnPesananMasuk, btnLogout
        );
        muatProfilUMKM();
    }

    private void muatProfilUMKM() {
        if (user == null) {
            return;
        }
        lblSubtitle.setText("Selamat datang, " + user.getNamaLengkap() + " (" + user.getUsername() + ")");
        lblUserSidebar.setText(user.getNamaLengkap());

        try {
            umkm = umkmDAO.getUMKMByOwner(user.getIdUser());
            if (umkm != null) {
                lblWelcome.setText("Toko: " + umkm.getNamaToko() + "  (" + umkm.getBank() + " " + umkm.getNoRekening() + ")");
                int[] ringkasan = new pojat.dao.StatistikDAO().getRingkasanUMKM(umkm.getIdUmkm());
                lblInfo.setText("Menu: " + ringkasan[0]
                        + "  |  Batch Aktif: " + ringkasan[1]
                        + "  |  Perlu Validasi: " + ringkasan[2]
                        + "  |  Alamat: " + umkm.getAlamatPenjemputan());
                btnPesananMasuk.setText(ringkasan[2] > 0
                        ? "Kelola Pesanan Masuk (" + ringkasan[2] + " validasi)"
                        : "Kelola Pesanan Masuk");
            } else {
                lblWelcome.setText("Profil Toko UMKM Belum Diatur");
                lblInfo.setText("Klik 'Kelola Profil Toko' untuk mendaftarkan toko Anda sebelum menambah menu.");
                btnPesananMasuk.setText("Kelola Pesanan Masuk");
            }
        } catch (SQLException e) {
            lblWelcome.setText("Gagal memuat profil UMKM");
            lblInfo.setText(e.getMessage());
        }
    }

    private boolean pastikanTokoAda() {
        if (umkm == null) {
            JOptionPane.showMessageDialog(
                this,
                "Anda belum memiliki profil toko UMKM.\nSilakan isi 'Kelola Profil Toko' terlebih dahulu.",
                "Profil Toko Diperlukan",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }

    private void kelolaProfilUMKM() {
        if (user == null) {
            return;
        }

        JTextField txtNamaToko = new JTextField(umkm != null ? umkm.getNamaToko() : "");
        JTextArea txtDeskripsi = new JTextArea(umkm != null ? umkm.getDeskripsi() : "", 3, 20);
        JTextArea txtAlamat = new JTextArea(umkm != null ? umkm.getAlamatPenjemputan() : "", 3, 20);
        JTextField txtNoRek = new JTextField(umkm != null ? umkm.getNoRekening() : "");
        JTextField txtBank = new JTextField(umkm != null ? umkm.getBank() : "");

        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);

        JPanel panelForm = new JPanel(new GridLayout(5, 2, 8, 8));
        panelForm.add(new JLabel("Nama Toko:"));
        panelForm.add(txtNamaToko);
        panelForm.add(new JLabel("Deskripsi:"));
        panelForm.add(new JScrollPane(txtDeskripsi));
        panelForm.add(new JLabel("Alamat Penjemputan:"));
        panelForm.add(new JScrollPane(txtAlamat));
        panelForm.add(new JLabel("No. Rekening:"));
        panelForm.add(txtNoRek);
        panelForm.add(new JLabel("Bank:"));
        panelForm.add(txtBank);

        int pilihan = JOptionPane.showConfirmDialog(
            this,
            panelForm,
            umkm == null ? "Buat Profil Toko UMKM" : "Edit Profil Toko UMKM",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (pilihan != JOptionPane.OK_OPTION) {
            return;
        }

        String namaToko = txtNamaToko.getText().trim();
        String deskripsi = txtDeskripsi.getText().trim();
        String alamat = txtAlamat.getText().trim();
        String noRek = txtNoRek.getText().trim();
        String bank = txtBank.getText().trim();

        if (namaToko.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Nama Toko dan Alamat Penjemputan wajib diisi!"
            );
            return;
        }

        try {
            boolean berhasil;
            if (umkm == null) {
                UMKM baru = new UMKM(0, user.getIdUser(), namaToko, deskripsi, alamat, noRek, bank);
                berhasil = umkmDAO.tambahUMKM(baru);
            } else {
                UMKM update = new UMKM(umkm.getIdUmkm(), user.getIdUser(), namaToko, deskripsi, alamat, noRek, bank);
                berhasil = umkmDAO.updateUMKM(update);
            }

            if (berhasil) {
                JOptionPane.showMessageDialog(this, "Profil toko UMKM berhasil disimpan!");
                muatProfilUMKM();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal menyimpan profil UMKM: " + e.getMessage(),
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
        lblUserSidebar = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        pnlContent = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        pnlCard = new javax.swing.JPanel();
        lblWelcome = new javax.swing.JLabel();
        lblInfo = new javax.swing.JLabel();
        pnlMenuGrid = new javax.swing.JPanel();
        btnProfilUMKM = new javax.swing.JButton();
        btnKelolaMenu = new javax.swing.JButton();
        btnKelolaBatch = new javax.swing.JButton();
        btnPesananMasuk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("POJAT - Dashboard UMKM");
        setResizable(false);

        splitMain.setDividerLocation(210);
        splitMain.setDividerSize(0);

        pnlBrand.setBackground(new java.awt.Color(255, 198, 0));
        pnlBrand.setPreferredSize(new java.awt.Dimension(210, 540));

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pojat/resources/images/pojat-it.png"))); // NOI18N

        lblRoleBadge.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        lblRoleBadge.setForeground(new java.awt.Color(0, 83, 38));
        lblRoleBadge.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoleBadge.setText("ADMIN UMKM");

        lblUserSidebar.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        lblUserSidebar.setForeground(new java.awt.Color(0, 83, 38));
        lblUserSidebar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUserSidebar.setText("Pre-Order Jatinangor");

        btnLogout.setBackground(new java.awt.Color(255, 82, 0));
        btnLogout.setFont(new java.awt.Font("Circe Slab C Bold", 0, 14)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("Logout");
        btnLogout.setBorder(null);
        btnLogout.addActionListener(this::btnLogoutActionPerformed);

        javax.swing.GroupLayout pnlBrandLayout = new javax.swing.GroupLayout(pnlBrand);
        pnlBrand.setLayout(pnlBrandLayout);
        pnlBrandLayout.setHorizontalGroup(
            pnlBrandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBrandLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBrandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                    .addComponent(lblRoleBadge, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUserSidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlBrandLayout.setVerticalGroup(
            pnlBrandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBrandLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblRoleBadge)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblUserSidebar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 285, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        splitMain.setLeftComponent(pnlBrand);

        pnlContent.setBackground(new java.awt.Color(247, 231, 198));

        lblTitle.setFont(new java.awt.Font("Circe Slab C Bold", 0, 28)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(0, 83, 38));
        lblTitle.setText("Dashboard Admin UMKM");

        lblSubtitle.setFont(new java.awt.Font("Circe Slab C", 0, 14)); // NOI18N
        lblSubtitle.setText("Portal pengelola toko UMKM Jatinangor");

        pnlCard.setBackground(new java.awt.Color(255, 249, 236));
        pnlCard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 83, 38)));

        lblWelcome.setFont(new java.awt.Font("Circe Slab C Bold", 0, 18)); // NOI18N
        lblWelcome.setForeground(new java.awt.Color(0, 83, 38));
        lblWelcome.setText("Toko UMKM Anda");

        lblInfo.setFont(new java.awt.Font("Circe Slab C", 0, 14)); // NOI18N
        lblInfo.setText("Kelola profil toko, menu produk, batch PO, dan pesanan masuk.");

        javax.swing.GroupLayout pnlCardLayout = new javax.swing.GroupLayout(pnlCard);
        pnlCard.setLayout(pnlCardLayout);
        pnlCardLayout.setHorizontalGroup(
            pnlCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblWelcome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        pnlCardLayout.setVerticalGroup(
            pnlCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCardLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblWelcome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblInfo)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pnlMenuGrid.setBackground(new java.awt.Color(247, 231, 198));
        pnlMenuGrid.setLayout(new java.awt.GridLayout(2, 2, 16, 16));

        btnProfilUMKM.setBackground(new java.awt.Color(255, 198, 0));
        btnProfilUMKM.setFont(new java.awt.Font("Circe Slab C Bold", 0, 15)); // NOI18N
        btnProfilUMKM.setForeground(new java.awt.Color(0, 83, 38));
        btnProfilUMKM.setText("Kelola Profil Toko");
        btnProfilUMKM.setBorder(null);
        btnProfilUMKM.addActionListener(this::btnProfilUMKMActionPerformed);
        pnlMenuGrid.add(btnProfilUMKM);

        btnKelolaMenu.setBackground(new java.awt.Color(145, 190, 0));
        btnKelolaMenu.setFont(new java.awt.Font("Circe Slab C Bold", 0, 15)); // NOI18N
        btnKelolaMenu.setForeground(new java.awt.Color(0, 83, 38));
        btnKelolaMenu.setText("Kelola Menu Produk");
        btnKelolaMenu.setBorder(null);
        btnKelolaMenu.addActionListener(this::btnKelolaMenuActionPerformed);
        pnlMenuGrid.add(btnKelolaMenu);

        btnKelolaBatch.setBackground(new java.awt.Color(145, 190, 0));
        btnKelolaBatch.setFont(new java.awt.Font("Circe Slab C Bold", 0, 15)); // NOI18N
        btnKelolaBatch.setForeground(new java.awt.Color(0, 83, 38));
        btnKelolaBatch.setText("Kelola Batch PO");
        btnKelolaBatch.setBorder(null);
        btnKelolaBatch.addActionListener(this::btnKelolaBatchActionPerformed);
        pnlMenuGrid.add(btnKelolaBatch);

        btnPesananMasuk.setBackground(new java.awt.Color(0, 83, 38));
        btnPesananMasuk.setFont(new java.awt.Font("Circe Slab C Bold", 0, 15)); // NOI18N
        btnPesananMasuk.setForeground(new java.awt.Color(247, 231, 198));
        btnPesananMasuk.setText("Kelola Pesanan Masuk");
        btnPesananMasuk.setBorder(null);
        btnPesananMasuk.addActionListener(this::btnPesananMasukActionPerformed);
        pnlMenuGrid.add(btnPesananMasuk);

        javax.swing.GroupLayout pnlContentLayout = new javax.swing.GroupLayout(pnlContent);
        pnlContent.setLayout(pnlContentLayout);
        pnlContentLayout.setHorizontalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                    .addComponent(lblSubtitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlMenuGrid, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        pnlContentLayout.setVerticalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubtitle)
                .addGap(18, 18, 18)
                .addComponent(pnlCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlMenuGrid, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        splitMain.setRightComponent(pnlContent);

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, 860, Short.MAX_VALUE)
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
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

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        new LoginFrame().setVisible(true);
        dispose();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnProfilUMKMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfilUMKMActionPerformed
        kelolaProfilUMKM();
    }//GEN-LAST:event_btnProfilUMKMActionPerformed

    private void btnKelolaMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKelolaMenuActionPerformed
        if (pastikanTokoAda()) {
            new DataMenuFrame(user, umkm).setVisible(true);
            dispose();
        }
    }//GEN-LAST:event_btnKelolaMenuActionPerformed

    private void btnKelolaBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKelolaBatchActionPerformed
        if (pastikanTokoAda()) {
            new DataBatchFrame(user, umkm).setVisible(true);
            dispose();
        }
    }//GEN-LAST:event_btnKelolaBatchActionPerformed

    private void btnPesananMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesananMasukActionPerformed
        if (pastikanTokoAda()) {
            new DataPesananFrame(user, umkm).setVisible(true);
            dispose();
        }
    }//GEN-LAST:event_btnPesananMasukActionPerformed

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

        java.awt.EventQueue.invokeLater(() -> new DashboardUMKM().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnKelolaBatch;
    private javax.swing.JButton btnKelolaMenu;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPesananMasuk;
    private javax.swing.JButton btnProfilUMKM;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblRoleBadge;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUserSidebar;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel pnlBrand;
    private javax.swing.JPanel pnlCard;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlMenuGrid;
    private javax.swing.JSplitPane splitMain;
    // End of variables declaration//GEN-END:variables
}
