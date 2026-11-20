/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.dao;

import pojat.projek.pbo.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatistikDAO {

    public int getTotalUMKM() throws SQLException {
        return hitung("SELECT COUNT(*) FROM umkm");
    }

    public int getTotalMenu() throws SQLException {
        return hitung("SELECT COUNT(*) FROM menu_item");
    }

    public int getTotalPesanan() throws SQLException {
        return hitung("SELECT COUNT(*) FROM pesanan");
    }

    public int getTotalBatch() throws SQLException {
        return hitung("SELECT COUNT(*) FROM batch_po");
    }

    public double getTotalOmzet() throws SQLException {

        String sql = "SELECT COALESCE(SUM(total_harga), 0) FROM pesanan";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        }

        return 0;
    }

    private int hitung(String sql) throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }
}