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

import java.util.ArrayList;
import java.util.List;

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

        String sql = "SELECT COALESCE(SUM(total_harga), 0) FROM pesanan WHERE status_pesanan <> 'DIBATALKAN'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        }

        return 0;
    }

    public int[] getRingkasanUMKM(int idUmkm) throws SQLException {
        int[] hasil = new int[4]; // [totalMenu, batchAktif, menungguValidasi, pesananAktif]
        String sqlMenu = "SELECT COUNT(*) FROM menu_item WHERE id_umkm = ?";
        String sqlBatch = "SELECT COUNT(*) FROM batch_po WHERE id_umkm = ? AND status IN ('OPEN', 'DIBUKA')";
        String sqlWait = "SELECT COUNT(*) FROM pesanan p JOIN batch_po b ON p.id_batch = b.id_batch WHERE b.id_umkm = ? AND p.status_pesanan = 'MENUNGGU_VALIDASI'";
        String sqlTotal = "SELECT COUNT(*) FROM pesanan p JOIN batch_po b ON p.id_batch = b.id_batch WHERE b.id_umkm = ? AND p.status_pesanan NOT IN ('SELESAI', 'DIBATALKAN')";

        try (Connection conn = DatabaseConnection.getConnection()) {
            hasil[0] = hitungById(conn, sqlMenu, idUmkm);
            hasil[1] = hitungById(conn, sqlBatch, idUmkm);
            hasil[2] = hitungById(conn, sqlWait, idUmkm);
            hasil[3] = hitungById(conn, sqlTotal, idUmkm);
        }
        return hasil;
    }

    public List<Object[]> getStatistikPerStatus() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT status_pesanan, COUNT(*) AS jumlah, COALESCE(SUM(total_harga), 0) AS total "
                   + "FROM pesanan GROUP BY status_pesanan ORDER BY jumlah DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("status_pesanan"),
                    rs.getInt("jumlah"),
                    rs.getDouble("total")
                });
            }
        }
        return list;
    }

    public List<Object[]> getStatistikPerUMKM() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT u.nama_toko, COUNT(p.id_pesanan) AS total_pesanan, "
                   + "COALESCE(SUM(CASE WHEN p.status_pesanan <> 'DIBATALKAN' THEN p.total_harga ELSE 0 END), 0) AS omzet "
                   + "FROM umkm u "
                   + "LEFT JOIN batch_po b ON b.id_umkm = u.id_umkm "
                   + "LEFT JOIN pesanan p ON p.id_batch = b.id_batch "
                   + "GROUP BY u.id_umkm, u.nama_toko "
                   + "ORDER BY omzet DESC, total_pesanan DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("nama_toko"),
                    rs.getInt("total_pesanan"),
                    rs.getDouble("omzet")
                });
            }
        }
        return list;
    }

    private int hitungById(Connection conn, String sql, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
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