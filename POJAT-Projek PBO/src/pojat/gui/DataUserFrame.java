/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pojat.gui;

import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import pojat.dao.UserDAO;
import pojat.model.User;
import pojat.util.PasswordUtil;
import pojat.util.UIHelper;

/**
 *
 * @author Jasmine
 */
public class DataUserFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DataUserFrame.class.getName());
    private User user;
    private UserDAO userDAO;
    private DefaultTableModel model;
    private List<User> semuaUser = new ArrayList<>();
    private List<User> filteredUser = new ArrayList<>();

    /**
     * Creates new form DataUserFrame
     */
    public DataUserFrame() {
        this.userDAO = new UserDAO();
        initComponents();
        setLocationRelativeTo(null);
        UIHelper.styleTable(tabelUser);
        UIHelper.applyHandCursor(btnCari, btnTambah, btnEdit, btnHapus, btnRefresh, btnKembali);
        tabelUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model = (DefaultTableModel) tabelUser.getModel();
        tampilkanData(false);
    }

    public DataUserFrame(User user) {
        this.user = user;
        this.userDAO = new UserDAO();
        initComponents();
        setLocationRelativeTo(null);
        UIHelper.styleTable(tabelUser);
        UIHelper.applyHandCursor(btnCari, btnTambah, btnEdit, btnHapus, btnRefresh, btnKembali);
        tabelUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model = (DefaultTableModel) tabelUser.getModel();
        tampilkanData(false);
    }

    private void tampilkanData() {
        tampilkanData(true);
    }

    private void tampilkanData(boolean showErrorDialog) {
        try {
            semuaUser = userDAO.getAllUsers();
            terapkanFilter();
        } catch (SQLException e) {
            if (showErrorDialog) {
                JOptionPane.showMessageDialog(
                    this,
                    "Gagal mengambil data pengguna: " + e.getMessage(),
                    "Kesalahan Database",
                    JOptionPane.ERROR_MESSAGE
                );
            } else {
                lblSubtitle.setText("Database belum terhubung (klik Refresh setelah MySQL aktif)");
            }
        }
    }

    private void terapkanFilter() {
        model.setRowCount(0);
        filteredUser.clear();
        String q = txtCari.getText().trim().toLowerCase();
        String roleFilter = String.valueOf(cmbRoleFilter.getSelectedItem());

        for (User u : semuaUser) {
            String role = u.getRole() == null ? "" : u.getRole();
            if (!"SEMUA".equalsIgnoreCase(roleFilter) && !roleFilter.equalsIgnoreCase(role)) {
                continue;
            }
            String username = u.getUsername() == null ? "" : u.getUsername();
            String nama = u.getNamaLengkap() == null ? "" : u.getNamaLengkap();
            String hp = u.getNoHp() == null ? "" : u.getNoHp();
            if (!q.isEmpty()
                    && !username.toLowerCase().contains(q)
                    && !nama.toLowerCase().contains(q)
                    && !hp.toLowerCase().contains(q)) {
                continue;
            }
            filteredUser.add(u);
            model.addRow(new Object[]{
                u.getIdUser(),
                u.getUsername(),
                u.getNamaLengkap(),
                u.getNoHp(),
                u.getRole()
            });
        }
    }

    private void tambahData() {
        JTextField txtUsername = new JTextField();
        JTextField txtNama = new JTextField();
        JTextField txtNoHp = new JTextField();
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"PEMBELI", "UMKM", "SUPERADMIN"});
        JPasswordField txtPass = new JPasswordField();

        JPanel panelForm = new JPanel(new GridLayout(5, 2, 8, 8));
        panelForm.add(new JLabel("Username:"));
        panelForm.add(txtUsername);
        panelForm.add(new JLabel("Nama Lengkap:"));
        panelForm.add(txtNama);
        panelForm.add(new JLabel("No. HP:"));
        panelForm.add(txtNoHp);
        panelForm.add(new JLabel("Role:"));
        panelForm.add(cmbRole);
        panelForm.add(new JLabel("Password (min 6):"));
        panelForm.add(txtPass);

        int pilihan = JOptionPane.showConfirmDialog(
            this, panelForm, "Tambah Pengguna",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (pilihan != JOptionPane.OK_OPTION) {
            return;
        }

        String username = txtUsername.getText().trim();
        String nama = txtNama.getText().trim();
        String noHp = txtNoHp.getText().trim();
        String role = String.valueOf(cmbRole.getSelectedItem());
        char[] passChars = txtPass.getPassword();
        String password = new String(passChars);
        Arrays.fill(passChars, '\0');

        if (username.isEmpty() || nama.isEmpty() || password.length() < 6) {
            JOptionPane.showMessageDialog(
                this,
                "Username, Nama Lengkap wajib diisi, dan Password minimal 6 karakter!"
            );
            return;
        }

        if (username.length() > 50 || nama.length() > 100 || noHp.length() > 20) {
            JOptionPane.showMessageDialog(
                this,
                "Panjang melebihi batas (Username max 50, Nama max 100, No HP max 20)!"
            );
            return;
        }

        try {
            if (userDAO.usernameExists(username)) {
                JOptionPane.showMessageDialog(this, "Username sudah digunakan!");
                return;
            }

            User baru = new User(0, username, PasswordUtil.hashPassword(password), nama, noHp, role);
            if (userDAO.tambahUser(baru)) {
                JOptionPane.showMessageDialog(this, "Pengguna berhasil ditambahkan!");
                tampilkanData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal menambah pengguna: " + e.getMessage(),
                "Kesalahan Database",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void editData() {
        int baris = tabelUser.getSelectedRow();
        if (baris == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pengguna yang ingin diedit!");
            return;
        }

        int idUser = (int) model.getValueAt(baris, 0);
        String usernameLama = String.valueOf(model.getValueAt(baris, 1));
        String namaLama = String.valueOf(model.getValueAt(baris, 2));
        String noHpLama = model.getValueAt(baris, 3) == null ? "" : String.valueOf(model.getValueAt(baris, 3));
        String roleLama = String.valueOf(model.getValueAt(baris, 4));

        JTextField txtUsername = new JTextField(usernameLama);
        JTextField txtNama = new JTextField(namaLama);
        JTextField txtNoHp = new JTextField(noHpLama);
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{"PEMBELI", "UMKM", "SUPERADMIN"});
        cmbRole.setSelectedItem(roleLama.toUpperCase());
        JPasswordField txtPassBaru = new JPasswordField();

        JPanel panelForm = new JPanel(new GridLayout(5, 2, 8, 8));
        panelForm.add(new JLabel("Username:"));
        panelForm.add(txtUsername);
        panelForm.add(new JLabel("Nama Lengkap:"));
        panelForm.add(txtNama);
        panelForm.add(new JLabel("No. HP:"));
        panelForm.add(txtNoHp);
        panelForm.add(new JLabel("Role:"));
        panelForm.add(cmbRole);
        panelForm.add(new JLabel("Password Baru (opsional):"));
        panelForm.add(txtPassBaru);

        int pilihan = JOptionPane.showConfirmDialog(
            this, panelForm, "Edit Pengguna",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (pilihan != JOptionPane.OK_OPTION) {
            return;
        }

        String username = txtUsername.getText().trim();
        String nama = txtNama.getText().trim();
        String noHp = txtNoHp.getText().trim();
        String role = String.valueOf(cmbRole.getSelectedItem());
        char[] passChars = txtPassBaru.getPassword();
        String passBaru = new String(passChars);
        Arrays.fill(passChars, '\0');

        if (username.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Nama Lengkap wajib diisi!");
            return;
        }

        if (username.length() > 50 || nama.length() > 100 || noHp.length() > 20) {
            JOptionPane.showMessageDialog(
                this,
                "Panjang melebihi batas (Username max 50, Nama max 100, No HP max 20)!"
            );
            return;
        }

        boolean updatePass = !passBaru.isEmpty();
        if (updatePass && passBaru.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password baru minimal 6 karakter!");
            return;
        }

        try {
            if (!username.equals(usernameLama) && userDAO.usernameExists(username)) {
                JOptionPane.showMessageDialog(this, "Username sudah digunakan pengguna lain!");
                return;
            }

            String hash = updatePass ? PasswordUtil.hashPassword(passBaru) : "";
            User edit = new User(idUser, username, hash, nama, noHp, role);

            if (userDAO.updateUser(edit, updatePass)) {
                JOptionPane.showMessageDialog(this, "Data pengguna berhasil diperbarui!");
                tampilkanData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal memperbarui pengguna: " + e.getMessage(),
                "Kesalahan Database",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void hapusData() {
        int baris = tabelUser.getSelectedRow();
        if (baris == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pengguna yang ingin dihapus!");
            return;
        }

        int idUser = (int) model.getValueAt(baris, 0);
        String username = String.valueOf(model.getValueAt(baris, 1));

        if (user != null && user.getIdUser() == idUser) {
            JOptionPane.showMessageDialog(
                this,
                "Tidak dapat menghapus akun yang sedang login!"
            );
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin menghapus pengguna " + username + "?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (userDAO.hapusUser(idUser)) {
                JOptionPane.showMessageDialog(this, "Pengguna berhasil dihapus!");
                tampilkanData();
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(
                this,
                "Pengguna tidak dapat dihapus karena masih terkait dengan data UMKM atau pesanan!",
                "Relasi Data",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal menghapus pengguna: " + e.getMessage(),
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
        btnKembali = new javax.swing.JButton();
        pnlContent = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        pnlSearch = new javax.swing.JPanel();
        txtCari = new javax.swing.JTextField();
        cmbRoleFilter = new javax.swing.JComboBox<>();
        btnCari = new javax.swing.JButton();
        scrollUser = new javax.swing.JScrollPane();
        tabelUser = new javax.swing.JTable();
        pnlActions = new javax.swing.JPanel();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("POJAT - Kelola Pengguna");
        setResizable(false);

        splitMain.setDividerLocation(210);
        splitMain.setDividerSize(0);

        pnlBrand.setBackground(new java.awt.Color(255, 198, 0));
        pnlBrand.setPreferredSize(new java.awt.Dimension(210, 580));

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pojat/resources/images/pojat-it.png"))); // NOI18N

        lblRoleBadge.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        lblRoleBadge.setForeground(new java.awt.Color(0, 83, 38));
        lblRoleBadge.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoleBadge.setText("DATA PENGGUNA");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 350, Short.MAX_VALUE)
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        splitMain.setLeftComponent(pnlBrand);

        pnlContent.setBackground(new java.awt.Color(247, 231, 198));

        lblTitle.setFont(new java.awt.Font("Circe Slab C Bold", 0, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(0, 83, 38));
        lblTitle.setText("Kelola Pengguna");

        lblSubtitle.setFont(new java.awt.Font("Circe Slab C", 0, 14)); // NOI18N
        lblSubtitle.setText("Tambah, ubah, dan hapus akun pengguna sistem POJAT");

        pnlSearch.setBackground(new java.awt.Color(247, 231, 198));

        txtCari.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        txtCari.addActionListener(this::txtCariActionPerformed);

        cmbRoleFilter.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        cmbRoleFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SEMUA", "PEMBELI", "UMKM", "SUPERADMIN" }));
        cmbRoleFilter.addActionListener(this::cmbRoleFilterActionPerformed);

        btnCari.setBackground(new java.awt.Color(0, 83, 38));
        btnCari.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnCari.setForeground(new java.awt.Color(247, 231, 198));
        btnCari.setText("Cari User");
        btnCari.setBorder(null);
        btnCari.addActionListener(this::btnCariActionPerformed);

        javax.swing.GroupLayout pnlSearchLayout = new javax.swing.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addComponent(txtCari)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbRoleFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cmbRoleFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabelUser.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        tabelUser.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID User", "Username", "Nama Lengkap", "No HP", "Role"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelUser.setRowHeight(28);
        tabelUser.setSelectionBackground(new java.awt.Color(145, 190, 0));
        tabelUser.setSelectionForeground(new java.awt.Color(0, 83, 38));
        scrollUser.setViewportView(tabelUser);

        pnlActions.setBackground(new java.awt.Color(247, 231, 198));
        pnlActions.setLayout(new java.awt.GridLayout(1, 4, 12, 0));

        btnTambah.setBackground(new java.awt.Color(145, 190, 0));
        btnTambah.setFont(new java.awt.Font("Circe Slab C Bold", 0, 14)); // NOI18N
        btnTambah.setForeground(new java.awt.Color(0, 83, 38));
        btnTambah.setText("Tambah Pengguna");
        btnTambah.setBorder(null);
        btnTambah.addActionListener(this::btnTambahActionPerformed);
        pnlActions.add(btnTambah);

        btnEdit.setBackground(new java.awt.Color(255, 198, 0));
        btnEdit.setFont(new java.awt.Font("Circe Slab C Bold", 0, 14)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(0, 83, 38));
        btnEdit.setText("Edit Pengguna");
        btnEdit.setBorder(null);
        btnEdit.addActionListener(this::btnEditActionPerformed);
        pnlActions.add(btnEdit);

        btnHapus.setBackground(new java.awt.Color(255, 82, 0));
        btnHapus.setFont(new java.awt.Font("Circe Slab C Bold", 0, 14)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setText("Hapus Pengguna");
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
                    .addComponent(scrollUser, javax.swing.GroupLayout.DEFAULT_SIZE, 772, Short.MAX_VALUE)
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
                .addComponent(scrollUser, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
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
            .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
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
        new DashboardAdmin(user).setVisible(true);
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
        cmbRoleFilter.setSelectedIndex(0);
        tampilkanData();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariActionPerformed
        terapkanFilter();
    }//GEN-LAST:event_txtCariActionPerformed

    private void cmbRoleFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRoleFilterActionPerformed
        terapkanFilter();
    }//GEN-LAST:event_cmbRoleFilterActionPerformed

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

        java.awt.EventQueue.invokeLater(() -> new DataUserFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbRoleFilter;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblRoleBadge;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlBrand;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JScrollPane scrollUser;
    private javax.swing.JSplitPane splitMain;
    private javax.swing.JTable tabelUser;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables
}
