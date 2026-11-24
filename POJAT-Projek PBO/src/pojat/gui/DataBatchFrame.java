/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pojat.gui;

import java.awt.GridLayout;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import pojat.dao.BatchPODAO;
import pojat.dao.UMKMDAO;
import pojat.model.BatchPO;
import pojat.model.UMKM;
import pojat.model.User;
import pojat.util.UIHelper;

/**
 *
 * @author Jasmine
 */
public class DataBatchFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DataBatchFrame.class.getName());
    private User user;
    private UMKM umkm;
    private BatchPODAO batchDAO;
    private UMKMDAO umkmDAO;
    private DefaultTableModel model;
    private List<BatchPO> semuaBatch = new ArrayList<>();
    private List<BatchPO> filteredBatch = new ArrayList<>();
    private List<UMKM> daftarUmkmCache = new ArrayList<>();
    private Map<Integer, String> namaUmkmMap = new HashMap<>();

    /**
     * Creates new form DataBatchFrame
     */
    public DataBatchFrame() {
        this.batchDAO = new BatchPODAO();
        this.umkmDAO = new UMKMDAO();
        initComponents();
        setLocationRelativeTo(null);
        UIHelper.styleTable(tabelBatch);
        UIHelper.applyHandCursor(btnCari, btnTambah, btnEdit, btnStatus, btnHapus, btnRefresh, btnKembali);
        tabelBatch.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model = (DefaultTableModel) tabelBatch.getModel();
        tampilkanData(false);
    }

    public DataBatchFrame(User user) {
        this(user, null);
    }

    public DataBatchFrame(User user, UMKM umkm) {
        this.user = user;
        this.umkm = umkm;
        this.batchDAO = new BatchPODAO();
        this.umkmDAO = new UMKMDAO();
        initComponents();
        setLocationRelativeTo(null);
        UIHelper.styleTable(tabelBatch);
        UIHelper.applyHandCursor(btnCari, btnTambah, btnEdit, btnStatus, btnHapus, btnRefresh, btnKembali);
        if (umkm != null) {
            lblSubtitle.setText("Kelola Batch PO untuk toko " + umkm.getNamaToko());
        }
        tabelBatch.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model = (DefaultTableModel) tabelBatch.getModel();
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
            semuaBatch = (umkm != null)
                    ? batchDAO.getBatchByUMKM(umkm.getIdUmkm())
                    : batchDAO.getAllBatch();
            terapkanFilter();
        } catch (SQLException e) {
            if (showErrorDialog) {
                JOptionPane.showMessageDialog(
                    this,
                    "Gagal mengambil data batch: " + e.getMessage()
                );
            } else {
                lblSubtitle.setText("Database belum terhubung (klik Refresh setelah MySQL aktif)");
            }
        }
    }

    private void terapkanFilter() {
        model.setRowCount(0);
        filteredBatch.clear();
        String q = txtCari.getText().trim().toLowerCase();
        String statusFilter = String.valueOf(cmbStatusFilter.getSelectedItem());

        for (BatchPO batch : semuaBatch) {
            String namaToko = namaUmkmMap.getOrDefault(batch.getIdUMKM(), "UMKM #" + batch.getIdUMKM());
            String namaBatch = batch.getNamaBatch() == null ? "" : batch.getNamaBatch();
            String status = batch.getStatus() == null ? "" : batch.getStatus();

            if (!"SEMUA".equalsIgnoreCase(statusFilter) && !statusFilter.equalsIgnoreCase(status)) {
                continue;
            }
            if (!q.isEmpty() && !namaBatch.toLowerCase().contains(q) && !namaToko.toLowerCase().contains(q)) {
                continue;
            }
            filteredBatch.add(batch);
            model.addRow(new Object[]{
                batch.getIdBatch(),
                namaToko,
                batch.getNamaBatch(),
                batch.getTglBuka(),
                batch.getTglTutup(),
                batch.getTglPengiriman(),
                batch.getKuotaMax(),
                batch.getStatus()
            });
        }
    }

    private java.util.Date parseTimestampOrDefault(String raw, int addDays) {
        if (raw != null && !raw.trim().isEmpty()) {
            try {
                String s = raw.trim();
                if (s.length() == 10) {
                    s += " 08:00:00";
                }
                return new java.util.Date(Timestamp.valueOf(s).getTime());
            } catch (IllegalArgumentException ignored) {
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, addDays);
        return cal.getTime();
    }

    private BatchPO tampilkanFormBatch(BatchPO lama) {
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
                if (lama != null && u.getIdUmkm() == lama.getIdUMKM()) {
                    selectedIdx = i;
                }
            }
            if (pilihanUmkm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Belum ada data UMKM terdaftar!");
                return null;
            }
            cmbUmkm.setSelectedIndex(selectedIdx);
        }

        JTextField txtNama = new JTextField(lama != null ? lama.getNamaBatch() : "");
        JSpinner spnBuka = new JSpinner(new SpinnerDateModel(
                parseTimestampOrDefault(lama != null ? lama.getTglBuka() : null, 0), null, null, Calendar.HOUR_OF_DAY));
        spnBuka.setEditor(new JSpinner.DateEditor(spnBuka, "yyyy-MM-dd HH:mm:ss"));

        JSpinner spnTutup = new JSpinner(new SpinnerDateModel(
                parseTimestampOrDefault(lama != null ? lama.getTglTutup() : null, 2), null, null, Calendar.HOUR_OF_DAY));
        spnTutup.setEditor(new JSpinner.DateEditor(spnTutup, "yyyy-MM-dd HH:mm:ss"));

        JSpinner spnKirim = new JSpinner(new SpinnerDateModel(
                parseTimestampOrDefault(lama != null ? lama.getTglPengiriman() : null, 3), null, null, Calendar.DAY_OF_MONTH));
        spnKirim.setEditor(new JSpinner.DateEditor(spnKirim, "yyyy-MM-dd"));

        int kuotaAwal = lama != null ? Math.max(1, lama.getKuotaMax()) : 50;
        JSpinner spnKuota = new JSpinner(new SpinnerNumberModel(kuotaAwal, 1, 10000, 5));

        JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"DRAFT", "OPEN", "CLOSED", "SELESAI"});
        cmbStatus.setSelectedItem(lama != null ? lama.getStatus() : "OPEN");

        JPanel panelForm = new JPanel(new GridLayout(7, 2, 8, 8));
        panelForm.add(new JLabel("Toko UMKM:"));
        panelForm.add(cmbUmkm);
        panelForm.add(new JLabel("Nama Batch:"));
        panelForm.add(txtNama);
        panelForm.add(new JLabel("Tanggal Buka:"));
        panelForm.add(spnBuka);
        panelForm.add(new JLabel("Tanggal Tutup:"));
        panelForm.add(spnTutup);
        panelForm.add(new JLabel("Tanggal Pengiriman:"));
        panelForm.add(spnKirim);
        panelForm.add(new JLabel("Kuota Maks:"));
        panelForm.add(spnKuota);
        panelForm.add(new JLabel("Status:"));
        panelForm.add(cmbStatus);

        int pilihan = JOptionPane.showConfirmDialog(
            this,
            panelForm,
            lama == null ? "Tambah Batch PO" : "Edit Batch PO",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (pilihan != JOptionPane.OK_OPTION) {
            return null;
        }

        SimpleDateFormat fmtTs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat fmtDate = new SimpleDateFormat("yyyy-MM-dd");

        int idUmkm = pilihanUmkm.get(cmbUmkm.getSelectedIndex()).getIdUmkm();
        String nama = txtNama.getText().trim();
        String strBuka = fmtTs.format((java.util.Date) spnBuka.getValue());
        String strTutup = fmtTs.format((java.util.Date) spnTutup.getValue());
        String strKirim = fmtDate.format((java.util.Date) spnKirim.getValue());
        Timestamp tsBuka = Timestamp.valueOf(strBuka);
        Timestamp tsTutup = Timestamp.valueOf(strTutup);
        Date tglKirim = Date.valueOf(strKirim);
        int kuota = ((Number) spnKuota.getValue()).intValue();
        String status = String.valueOf(cmbStatus.getSelectedItem());

        if (nama.isEmpty() || kuota <= 0) {
            JOptionPane.showMessageDialog(this, "Nama Batch dan Kuota (> 0) harus valid!");
            return null;
        }
        if (tsTutup.before(tsBuka) || tglKirim.toLocalDate().isBefore(tsTutup.toLocalDateTime().toLocalDate())) {
            JOptionPane.showMessageDialog(
                this,
                "Urutan tanggal harus valid: Buka <= Tutup <= Pengiriman!"
            );
            return null;
        }

        int idBatch = lama == null ? 0 : lama.getIdBatch();
        return new BatchPO(idBatch, idUmkm, nama, strBuka, strTutup, strKirim, kuota, status);
    }

    private void tambahData() {
        BatchPO batch = tampilkanFormBatch(null);
        if (batch == null) {
            return;
        }

        try {
            if (batchDAO.tambahBatch(batch)) {
                JOptionPane.showMessageDialog(this, "Batch PO berhasil ditambahkan!");
                tampilkanData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal menambahkan batch: " + e.getMessage(),
                "Kesalahan Database",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void editData() {
        int baris = tabelBatch.getSelectedRow();
        if (baris < 0 || baris >= filteredBatch.size()) {
            JOptionPane.showMessageDialog(this, "Pilih batch yang ingin diedit!");
            return;
        }

        BatchPO lama = filteredBatch.get(baris);
        BatchPO baru = tampilkanFormBatch(lama);
        if (baru == null) {
            return;
        }

        try {
            if (batchDAO.updateBatch(baru)) {
                JOptionPane.showMessageDialog(this, "Batch PO berhasil diperbarui!");
                tampilkanData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal memperbarui batch: " + e.getMessage(),
                "Kesalahan Database",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void hapusData() {
        int baris = tabelBatch.getSelectedRow();
        if (baris < 0 || baris >= filteredBatch.size()) {
            JOptionPane.showMessageDialog(this, "Pilih batch yang ingin dihapus!");
            return;
        }

        BatchPO target = filteredBatch.get(baris);
        int idBatch = target.getIdBatch();
        String namaBatch = target.getNamaBatch();

        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin menghapus batch " + namaBatch + "?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (batchDAO.hapusBatch(idBatch)) {
                JOptionPane.showMessageDialog(this, "Batch PO berhasil dihapus!");
                tampilkanData();
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(
                this,
                "Batch PO tidak dapat dihapus karena sudah memiliki data pesanan!",
                "Relasi Data",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal menghapus batch: " + e.getMessage(),
                "Kesalahan Database",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void ubahStatus() {
        int baris = tabelBatch.getSelectedRow();

        if (baris == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih batch terlebih dahulu!"
            );
            return;
        }

        int idBatch = (int) model.getValueAt(baris, 0);

        String[] pilihan = {
            "DRAFT",
            "OPEN",
            "CLOSED",
            "SELESAI"
        };

        String status = (String) JOptionPane.showInputDialog(
            this,
            "Pilih status batch:",
            "Ubah Status Batch",
            JOptionPane.QUESTION_MESSAGE,
            null,
            pilihan,
            model.getValueAt(baris, 7)
        );

        if (status == null) {
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Ubah status batch ID " + idBatch + " menjadi " + status + "?",
            "Konfirmasi",
            JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean berhasil = batchDAO.updateStatusBatch(idBatch, status);

            if (berhasil) {
                JOptionPane.showMessageDialog(
                    this,
                    "Status batch berhasil diperbarui!"
                );
                tampilkanData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal mengubah status batch: " + e.getMessage()
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
        cmbStatusFilter = new javax.swing.JComboBox<>();
        btnCari = new javax.swing.JButton();
        scrollBatch = new javax.swing.JScrollPane();
        tabelBatch = new javax.swing.JTable();
        pnlActions = new javax.swing.JPanel();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnStatus = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("POJAT - Kelola Batch PO");
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
        lblRoleBadge.setText("KELOLA BATCH PO");

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 370, Short.MAX_VALUE)
                .addComponent(btnKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        splitMain.setLeftComponent(pnlBrand);

        pnlContent.setBackground(new java.awt.Color(247, 231, 198));

        lblTitle.setFont(new java.awt.Font("Circe Slab C Bold", 0, 26)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(0, 83, 38));
        lblTitle.setText("Kelola Batch PO");

        lblSubtitle.setFont(new java.awt.Font("Circe Slab C", 0, 14)); // NOI18N
        lblSubtitle.setText("Buat, ubah jadwal, atau perbarui status periode Pre-Order UMKM");

        pnlSearch.setBackground(new java.awt.Color(247, 231, 198));

        txtCari.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        txtCari.addActionListener(this::txtCariActionPerformed);

        cmbStatusFilter.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        cmbStatusFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SEMUA", "DRAFT", "OPEN", "CLOSED", "SELESAI" }));
        cmbStatusFilter.addActionListener(this::cmbStatusFilterActionPerformed);

        btnCari.setBackground(new java.awt.Color(0, 83, 38));
        btnCari.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnCari.setForeground(new java.awt.Color(247, 231, 198));
        btnCari.setText("Cari Batch");
        btnCari.setBorder(null);
        btnCari.addActionListener(this::btnCariActionPerformed);

        javax.swing.GroupLayout pnlSearchLayout = new javax.swing.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addComponent(txtCari)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cmbStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnCari, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabelBatch.setFont(new java.awt.Font("Circe Slab C", 0, 13)); // NOI18N
        tabelBatch.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Batch", "Toko UMKM", "Nama Batch", "Tanggal Buka", "Tanggal Tutup", "Pengiriman", "Kuota Maks", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelBatch.setRowHeight(28);
        tabelBatch.setSelectionBackground(new java.awt.Color(145, 190, 0));
        tabelBatch.setSelectionForeground(new java.awt.Color(0, 83, 38));
        scrollBatch.setViewportView(tabelBatch);

        pnlActions.setBackground(new java.awt.Color(247, 231, 198));
        pnlActions.setLayout(new java.awt.GridLayout(1, 5, 10, 0));

        btnTambah.setBackground(new java.awt.Color(145, 190, 0));
        btnTambah.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnTambah.setForeground(new java.awt.Color(0, 83, 38));
        btnTambah.setText("Tambah Batch");
        btnTambah.setBorder(null);
        btnTambah.addActionListener(this::btnTambahActionPerformed);
        pnlActions.add(btnTambah);

        btnEdit.setBackground(new java.awt.Color(255, 198, 0));
        btnEdit.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(0, 83, 38));
        btnEdit.setText("Edit Batch");
        btnEdit.setBorder(null);
        btnEdit.addActionListener(this::btnEditActionPerformed);
        pnlActions.add(btnEdit);

        btnStatus.setBackground(new java.awt.Color(145, 190, 0));
        btnStatus.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnStatus.setForeground(new java.awt.Color(0, 83, 38));
        btnStatus.setText("Ubah Status");
        btnStatus.setBorder(null);
        btnStatus.addActionListener(this::btnStatusActionPerformed);
        pnlActions.add(btnStatus);

        btnHapus.setBackground(new java.awt.Color(255, 82, 0));
        btnHapus.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setText("Hapus Batch");
        btnHapus.setBorder(null);
        btnHapus.addActionListener(this::btnHapusActionPerformed);
        pnlActions.add(btnHapus);

        btnRefresh.setBackground(new java.awt.Color(0, 83, 38));
        btnRefresh.setFont(new java.awt.Font("Circe Slab C Bold", 0, 13)); // NOI18N
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
                    .addComponent(scrollBatch, javax.swing.GroupLayout.DEFAULT_SIZE, 832, Short.MAX_VALUE)
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
                .addComponent(scrollBatch, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void btnStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStatusActionPerformed
        ubahStatus();
    }//GEN-LAST:event_btnStatusActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        hapusData();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        txtCari.setText("");
        cmbStatusFilter.setSelectedIndex(0);
        tampilkanData();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariActionPerformed
        terapkanFilter();
    }//GEN-LAST:event_txtCariActionPerformed

    private void cmbStatusFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbStatusFilterActionPerformed
        terapkanFilter();
    }//GEN-LAST:event_cmbStatusFilterActionPerformed

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

        java.awt.EventQueue.invokeLater(() -> new DataBatchFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnStatus;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbStatusFilter;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblRoleBadge;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlActions;
    private javax.swing.JPanel pnlBrand;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JScrollPane scrollBatch;
    private javax.swing.JSplitPane splitMain;
    private javax.swing.JTable tabelBatch;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables
}
