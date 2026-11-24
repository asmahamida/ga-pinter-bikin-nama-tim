/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pojat.gui;

import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import pojat.dao.UMKMDAO;
import pojat.dao.UserDAO;
import pojat.model.UMKM;
import pojat.model.User;
import pojat.util.UIHelper;

/**
 *
 * @author Jasmine
 */
public class DataUMKMFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DataUMKMFrame.class.getName());
    private User user;
    private UMKMDAO umkmDAO;
    private UserDAO userDAO;
    private DefaultTableModel modelTabel;
    private List<UMKM> semuaUmkm = new ArrayList<>();
    private List<UMKM> filteredUmkm = new ArrayList<>();
    private List<User> daftarUserCache = new ArrayList<>();
    private Map<Integer, String> namaOwnerMap = new HashMap<>();

    /**
     * Creates new form DataUMKMFrame
     */
    public DataUMKMFrame() {
        this.umkmDAO = new UMKMDAO();
        this.userDAO = new UserDAO();
        initComponents();
        setLocationRelativeTo(null);
        UIHelper.styleTable(tabelUMKM);
        UIHelper.applyHandCursor(btnCari, btnTambah, btnEdit, btnHapus, btnRefresh, btnKembali);
        tabelUMKM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        modelTabel = (DefaultTableModel) tabelUMKM.getModel();
        tampilkanData(false);
    }

    public DataUMKMFrame(User user) {
        this.user = user;
        this.umkmDAO = new UMKMDAO();
        this.userDAO = new UserDAO();
        initComponents();
        setLocationRelativeTo(null);
        UIHelper.styleTable(tabelUMKM);
        UIHelper.applyHandCursor(btnCari, btnTambah, btnEdit, btnHapus, btnRefresh, btnKembali);
        tabelUMKM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (user != null) {
            lblAdmin.setText("Admin: " + user.getNamaLengkap());
        }
        modelTabel = (DefaultTableModel) tabelUMKM.getModel();
        tampilkanData(false);
    }

    private void tampilkanData() {
        tampilkanData(true);
    }

    private void tampilkanData(boolean showErrorDialog) {
        try {
            namaOwnerMap.clear();
            daftarUserCache = userDAO.getAllUsers();
            for (User u : daftarUserCache) {
                namaOwnerMap.put(u.getIdUser(), u.getNamaLengkap() + " (@" + u.getUsername() + ")");
            }
            semuaUmkm = umkmDAO.getAllUMKM();
            terapkanFilter();
        } catch (SQLException e) {
            if (showErrorDialog) {
                JOptionPane.showMessageDialog(
                    this,
                    "Gagal mengambil data UMKM!\n" + e.getMessage(),
                    "Kesalahan Database",
                    JOptionPane.ERROR_MESSAGE
                );
            } else {
                lblSubtitle.setText("Database belum terhubung (klik Refresh setelah MySQL aktif)");
            }
        }
    }

    private void terapkanFilter() {
        modelTabel.setRowCount(0);
        filteredUmkm.clear();
        String q = txtCari.getText().trim().toLowerCase();

        for (UMKM umkm : semuaUmkm) {
            String ownerLabel = namaOwnerMap.getOrDefault(umkm.getIdOwner(), "User #" + umkm.getIdOwner());
            String namaToko = umkm.getNamaToko() == null ? "" : umkm.getNamaToko();
            String alamat = umkm.getAlamatPenjemputan() == null ? "" : umkm.getAlamatPenjemputan();

            if (!q.isEmpty()
                    && !namaToko.toLowerCase().contains(q)
                    && !ownerLabel.toLowerCase().contains(q)
                    && !alamat.toLowerCase().contains(q)) {
                continue;
            }
            filteredUmkm.add(umkm);
            modelTabel.addRow(new Object[]{
                umkm.getIdUmkm(),
                ownerLabel,
                umkm.getNamaToko(),
                umkm.getDeskripsi(),
                umkm.getAlamatPenjemputan(),
                umkm.getNoRekening(),
                umkm.getBank()
            });
        }
    }

    private UMKM tampilkanForm(UMKM dataLama) {
        JComboBox<String> cmbOwner = new JComboBox<>();
        List<User> kandidatOwner = new ArrayList<>();
        for (User u : daftarUserCache) {
            if ("UMKM".equalsIgnoreCase(u.getRole())
                    || (dataLama != null && u.getIdUser() == dataLama.getIdOwner())) {
                kandidatOwner.add(u);
            }
        }
        if (kandidatOwner.isEmpty()) {
            kandidatOwner.addAll(daftarUserCache);
        }
        if (kandidatOwner.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Belum ada data pengguna untuk dipilih sebagai pemilik UMKM!");
            return null;
        }
        int selectedIdx = 0;
        for (int i = 0; i < kandidatOwner.size(); i++) {
            User u = kandidatOwner.get(i);
            cmbOwner.addItem(u.getIdUser() + " - " + u.getNamaLengkap() + " (@" + u.getUsername() + ")");
            if (dataLama != null && u.getIdUser() == dataLama.getIdOwner()) {
                selectedIdx = i;
            }
        }
        cmbOwner.setSelectedIndex(selectedIdx);

        JTextField txtNamaToko = new JTextField();
        JTextArea txtDeskripsi = new JTextArea(3, 20);
        JTextArea txtAlamat = new JTextArea(3, 20);
        JTextField txtNoRekening = new JTextField();
        JTextField txtBank = new JTextField();

        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);

        if (dataLama != null) {
            txtNamaToko.setText(dataLama.getNamaToko());
            txtDeskripsi.setText(dataLama.getDeskripsi());
            txtAlamat.setText(dataLama.getAlamatPenjemputan());
            txtNoRekening.setText(dataLama.getNoRekening());
            txtBank.setText(dataLama.getBank());
        }

        JPanel panelForm = new JPanel(new GridLayout(6, 2, 8, 8));
        panelForm.add(new JLabel("Pemilik UMKM:"));
        panelForm.add(cmbOwner);
        panelForm.add(new JLabel("Nama Toko:"));
        panelForm.add(txtNamaToko);
        panelForm.add(new JLabel("Deskripsi:"));
        panelForm.add(new JScrollPane(txtDeskripsi));
        panelForm.add(new JLabel("Alamat Penjemputan:"));
        panelForm.add(new JScrollPane(txtAlamat));
        panelForm.add(new JLabel("No Rekening:"));
        panelForm.add(txtNoRekening);
        panelForm.add(new JLabel("Bank:"));
        panelForm.add(txtBank);

        int hasil = JOptionPane.showConfirmDialog(
            this,
            panelForm,
            dataLama == null ? "Tambah Data UMKM" : "Edit Data UMKM",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (hasil != JOptionPane.OK_OPTION) {
            return null;
        }

        int idOwner = kandidatOwner.get(cmbOwner.getSelectedIndex()).getIdUser();
        String namaToko = txtNamaToko.getText().trim();
        String deskripsi = txtDeskripsi.getText().trim();
        String alamat = txtAlamat.getText().trim();
        String noRekening = txtNoRekening.getText().trim();
        String bank = txtBank.getText().trim();

        if (namaToko.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Nama Toko dan Alamat Penjemputan wajib diisi!"
            );
            return null;
        }

        int idUmkm = dataLama == null ? 0 : dataLama.getIdUmkm();

        return new UMKM(
            idUmkm,
            idOwner,
            namaToko,
            deskripsi,
            alamat,
            noRekening,
            bank
        );
    }

    private void tambahData() {
        UMKM umkm = tampilkanForm(null);
        if (umkm == null) {
            return;
        }

        try {
            boolean berhasil = umkmDAO.tambahUMKM(umkm);
            if (berhasil) {
                JOptionPane.showMessageDialog(
                    this,
                    "Data UMKM berhasil ditambahkan!"
                );
                tampilkanData();
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(
                this,
                "ID Owner tidak ditemukan pada daftar pengguna!",
                "Relasi Data",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal menambahkan data UMKM!\n" + e.getMessage(),
                "Kesalahan Database",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void editData() {
        int baris = tabelUMKM.getSelectedRow();
        if (baris == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih data UMKM yang ingin diedit!"
            );
            return;
        }

        int barisModel = tabelUMKM.convertRowIndexToModel(baris);
        int idUmkm = Integer.parseInt(modelTabel.getValueAt(barisModel, 0).toString());

        try {
            List<UMKM> daftarUMKM = umkmDAO.getAllUMKM();
            UMKM dataLama = null;

            for (UMKM umkm : daftarUMKM) {
                if (umkm.getIdUmkm() == idUmkm) {
                    dataLama = umkm;
                    break;
                }
            }

            if (dataLama == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Data UMKM tidak ditemukan!"
                );
                tampilkanData();
                return;
            }

            UMKM dataBaru = tampilkanForm(dataLama);
            if (dataBaru == null) {
                return;
            }

            boolean berhasil = umkmDAO.updateUMKM(dataBaru);
            if (berhasil) {
                JOptionPane.showMessageDialog(
                    this,
                    "Data UMKM berhasil diperbarui!"
                );
                tampilkanData();
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(
                this,
                "ID Owner tidak ditemukan pada daftar pengguna!",
                "Relasi Data",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal mengedit data UMKM!\n" + e.getMessage(),
                "Kesalahan Database",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void hapusData() {
        int baris = tabelUMKM.getSelectedRow();
        if (baris == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih data UMKM yang ingin dihapus!"
            );
            return;
        }

        int barisModel = tabelUMKM.convertRowIndexToModel(baris);
        int idUmkm = Integer.parseInt(modelTabel.getValueAt(barisModel, 0).toString());
        String namaToko = modelTabel.getValueAt(barisModel, 2).toString();

        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin menghapus UMKM " + namaToko + "?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean berhasil = umkmDAO.hapusUMKM(idUmkm);
            if (berhasil) {
                JOptionPane.showMessageDialog(
                    this,
                    "Data UMKM berhasil dihapus!"
                );
                tampilkanData();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Data UMKM tidak berhasil dihapus!"
                );
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(
                this,
                "UMKM tidak dapat dihapus karena masih memiliki data menu atau batch PO!",
                "Relasi Data",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal menghapus data UMKM!\n" + e.getMessage(),
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
        lblAdmin = new javax.swing.JLabel();
        btnKembali = new javax.swing.JButton();
        pnlContent = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        pnlSearch = new javax.swing.JPanel();
        txtCari = new javax.swing.JTextField();
        btnCari = new javax.swing.JButton();
        scrollUMKM = new javax.swing.JScrollPane();
        tabelUMKM = new javax.swing.JTable();
        pnlActions = new javax.swing.JPanel();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("POJAT - Data UMKM");
        setResizable(false);

        splitMain.setDividerLocation(210);
        splitMain.setDividerSize(0);

        pnlBrand.setBackground(new java.awt.Color(255, 198, 0));
        pnlBrand.setPreferredSize(new java.awt.Dimension(210, 600));

        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pojat/resources/images/pojat-it.png"))); // NOI18N

        lblRoleBadge.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        lblRoleBadge.setForeground(new java.awt.Color(0, 83, 38));
        lblRoleBadge.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoleBadge.setText("DATA UMKM");

        lblAdmin.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        lblAdmin.setForeground(new java.awt.Color(0, 83, 38));
        lblAdmin.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAdmin.setText("Administrator");

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
                    .addComponent(lblAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAdmin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 345, Short.MAX_VALUE)
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        splitMain.setLeftComponent(pnlBrand);

        pnlContent.setBackground(new java.awt.Color(247, 231, 198));

        lblTitle.setFont(new java.awt.Font("Circe Slab C Bold", 0, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(0, 83, 38));
        lblTitle.setText("Pengelolaan Data UMKM");

        lblSubtitle.setFont(new java.awt.Font("Circe Slab C", 0, 14)); // NOI18N
        lblSubtitle.setText("Tambah, ubah, dan hapus data UMKM terdaftar di POJAT");

        pnlSearch.setBackground(new java.awt.Color(247, 231, 198));

        txtCari.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        txtCari.addActionListener(this::txtCariActionPerformed);

        btnCari.setBackground(new java.awt.Color(0, 83, 38));
        btnCari.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnCari.setForeground(new java.awt.Color(247, 231, 198));
        btnCari.setText("Cari UMKM");
        btnCari.setBorder(null);
        btnCari.addActionListener(this::btnCariActionPerformed);

        javax.swing.GroupLayout pnlSearchLayout = new javax.swing.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addComponent(txtCari)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabelUMKM.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        tabelUMKM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID UMKM", "Pemilik", "Nama Toko", "Deskripsi", "Alamat Jemput", "No Rekening", "Bank"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelUMKM.setRowHeight(28);
        tabelUMKM.setSelectionBackground(new java.awt.Color(145, 190, 0));
        tabelUMKM.setSelectionForeground(new java.awt.Color(0, 83, 38));
        scrollUMKM.setViewportView(tabelUMKM);

        pnlActions.setBackground(new java.awt.Color(247, 231, 198));
        pnlActions.setLayout(new java.awt.GridLayout(1, 4, 12, 0));

        btnTambah.setBackground(new java.awt.Color(145, 190, 0));
        btnTambah.setFont(new java.awt.Font("Circe Slab C Bold", 0, 14)); // NOI18N
        btnTambah.setForeground(new java.awt.Color(0, 83, 38));
        btnTambah.setText("Tambah UMKM");
        btnTambah.setBorder(null);
        btnTambah.addActionListener(this::btnTambahActionPerformed);
        pnlActions.add(btnTambah);

        btnEdit.setBackground(new java.awt.Color(255, 198, 0));
        btnEdit.setFont(new java.awt.Font("Circe Slab C Bold", 0, 14)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(0, 83, 38));
        btnEdit.setText("Edit UMKM");
        btnEdit.setBorder(null);
        btnEdit.addActionListener(this::btnEditActionPerformed);
        pnlActions.add(btnEdit);

        btnHapus.setBackground(new java.awt.Color(255, 82, 0));
        btnHapus.setFont(new java.awt.Font("Circe Slab C Bold", 0, 14)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setText("Hapus UMKM");
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
                    .addComponent(scrollUMKM, javax.swing.GroupLayout.DEFAULT_SIZE, 832, Short.MAX_VALUE)
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
                .addComponent(scrollUMKM, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlActions, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        splitMain.setRightComponent(pnlContent);

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, 1100, Short.MAX_VALUE)
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitMain, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
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

        java.awt.EventQueue.invokeLater(() -> new DataUMKMFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnTambah;
    private javax.swing.JLabel lblAdmin;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblRoleBadge;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlBrand;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JScrollPane scrollUMKM;
    private javax.swing.JSplitPane splitMain;
    private javax.swing.JTable tabelUMKM;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables
}
