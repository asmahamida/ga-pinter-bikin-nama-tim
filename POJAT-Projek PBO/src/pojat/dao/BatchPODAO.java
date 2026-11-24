/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.dao;

import pojat.model.BatchPO;
import pojat.projek.pbo.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BatchPODAO {

    // ==========================================
    // READ - MENGAMBIL SEMUA BATCH
    // ==========================================
    public List<BatchPO> getAllBatch() throws SQLException {

        List<BatchPO> daftar = new ArrayList<>();

        String sql = "SELECT * FROM batch_po ORDER BY id_batch DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BatchPO batch = new BatchPO(
                    rs.getInt("id_batch"),
                    rs.getInt("id_umkm"),
                    rs.getString("nama_batch"),
                    rs.getTimestamp("tgl_buka"),
                    rs.getTimestamp("tgl_tutup"),
                    rs.getDate("tgl_pengiriman"),
                    rs.getInt("kuota_max"),
                    rs.getString("status")
                );

                daftar.add(batch);
            }
        }

        return daftar;
    }

    // ==========================================
    // READ - MENGAMBIL BATCH BERDASARKAN UMKM
    // ==========================================
    public List<BatchPO> getBatchByUMKM(int idUmkm) throws SQLException {
        List<BatchPO> daftar = new ArrayList<>();
        String sql = "SELECT * FROM batch_po WHERE id_umkm = ? ORDER BY id_batch DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUmkm);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(new BatchPO(
                        rs.getInt("id_batch"),
                        rs.getInt("id_umkm"),
                        rs.getString("nama_batch"),
                        rs.getTimestamp("tgl_buka"),
                        rs.getTimestamp("tgl_tutup"),
                        rs.getDate("tgl_pengiriman"),
                        rs.getInt("kuota_max"),
                        rs.getString("status")
                    ));
                }
            }
        }
        return daftar;
    }

    private Timestamp parseDateTime(String value, boolean endOfDay) {
        String clean = value == null ? "" : value.trim();
        if (clean.length() == 10) {
            clean = clean + (endOfDay ? " 23:59:59" : " 00:00:00");
        }
        return Timestamp.valueOf(clean);
    }

    // ==========================================
    // CREATE - MENAMBAHKAN BATCH PO
    // ==========================================
    public boolean tambahBatch(BatchPO batch) throws SQLException {
        String sql = "INSERT INTO batch_po "
                + "(id_umkm, nama_batch, tgl_buka, tgl_tutup, tgl_pengiriman, kuota_max, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, batch.getIdUMKM());
            ps.setString(2, batch.getNamaBatch());
            ps.setTimestamp(3, parseDateTime(batch.getTglBuka(), false));
            ps.setTimestamp(4, parseDateTime(batch.getTglTutup(), true));
            ps.setDate(5, Date.valueOf(batch.getTglPengiriman().trim()));
            ps.setInt(6, batch.getKuotaMax());
            ps.setString(7, batch.getStatus());
            return ps.executeUpdate() > 0;
        }
    }

    // ==========================================
    // UPDATE - MENGUBAH DATA BATCH PO
    // ==========================================
    public boolean updateBatch(BatchPO batch) throws SQLException {
        String sql = "UPDATE batch_po SET id_umkm = ?, nama_batch = ?, tgl_buka = ?, "
                + "tgl_tutup = ?, tgl_pengiriman = ?, kuota_max = ?, status = ? "
                + "WHERE id_batch = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, batch.getIdUMKM());
            ps.setString(2, batch.getNamaBatch());
            ps.setTimestamp(3, parseDateTime(batch.getTglBuka(), false));
            ps.setTimestamp(4, parseDateTime(batch.getTglTutup(), true));
            ps.setDate(5, Date.valueOf(batch.getTglPengiriman().trim()));
            ps.setInt(6, batch.getKuotaMax());
            ps.setString(7, batch.getStatus());
            ps.setInt(8, batch.getIdBatch());
            return ps.executeUpdate() > 0;
        }
    }

    // ==========================================
    // DELETE - MENGHAPUS BATCH PO
    // ==========================================
    public boolean hapusBatch(int idBatch) throws SQLException {
        String sql = "DELETE FROM batch_po WHERE id_batch = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBatch);
            return ps.executeUpdate() > 0;
        }
    }

    // ==========================================
    // UPDATE - MENGUBAH STATUS BATCH
    // ==========================================
    public boolean updateStatusBatch(int idBatch, String status)
            throws SQLException {

        String sql = "UPDATE batch_po SET status = ? WHERE id_batch = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, idBatch);

            return ps.executeUpdate() > 0;
        }
    }
}
