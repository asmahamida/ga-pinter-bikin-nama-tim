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
                    rs.getDate("tgl_buka"),
                    rs.getDate("tgl_tutup"),
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
