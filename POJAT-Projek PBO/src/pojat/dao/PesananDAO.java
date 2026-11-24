/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pojat.dao;

import java.util.Map;
import java.util.HashMap;

import pojat.model.Pesanan;
import pojat.model.DetailPesanan;
import pojat.projek.pbo.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PesananDAO {

    // ==========================================
    // READ - MENGAMBIL SEMUA PESANAN
    // ==========================================
    public List<Pesanan> getAllPesanan() throws SQLException {

        List<Pesanan> daftarPesanan = new ArrayList<>();

        String sql = "SELECT * FROM pesanan ORDER BY id_pesanan DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pesanan pesanan = new Pesanan(
                    rs.getInt("id_pesanan"),
                    rs.getString("kode_transaksi"),
                    rs.getInt("id_batch"),
                    rs.getInt("id_pembeli"),
                    rs.getTimestamp("tgl_pesan"),
                    rs.getBigDecimal("total_harga"),
                    rs.getString("metode_pengambilan"),
                    rs.getString("bukti_pembayaran"),
                    rs.getString("status_pesanan")
                );

                daftarPesanan.add(pesanan);
            }
        }

        return daftarPesanan;
    }

    // ==========================================
    // CREATE - MENYIMPAN PESANAN + DETAIL
    // ==========================================
    public boolean tambahPesanan(
            Pesanan pesanan,
            List<DetailPesanan> daftarDetail) throws SQLException {

        String sqlPesanan = "INSERT INTO pesanan "
                + "(kode_transaksi, id_batch, id_pembeli, "
                + "total_harga, metode_pengambilan, "
                + "bukti_pembayaran, status_pesanan) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        String sqlDetail = "INSERT INTO detail_pesanan "
                + "(id_pesanan, id_menu, jumlah, subtotal, catatan_khusus) "
                + "VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            int idPesanan;

            // Simpan header pesanan
            try (PreparedStatement ps = conn.prepareStatement(
                    sqlPesanan, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, pesanan.getKodeTransaksi());
                ps.setInt(2, pesanan.getIdBatch());
                ps.setInt(3, pesanan.getIdPembeli());
                ps.setBigDecimal(4, pesanan.getTotalHarga());
                ps.setString(5, pesanan.getMetodePengambilan());
                ps.setString(6, pesanan.getBuktiPembayaran());
                ps.setString(7, pesanan.getStatusPesanan());

                ps.executeUpdate();

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idPesanan = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException(
                            "ID pesanan gagal diperoleh."
                        );
                    }
                }
            }

            // Simpan seluruh detail pesanan
            try (PreparedStatement psDetail =
                    conn.prepareStatement(sqlDetail)) {

                for (DetailPesanan detail : daftarDetail) {
                    psDetail.setInt(1, idPesanan);
                    psDetail.setInt(2, detail.getIdMenu());
                    psDetail.setInt(3, detail.getJumlah());
                    psDetail.setBigDecimal(4, detail.getSubtotal());
                    psDetail.setString(5, detail.getCatatanKhusus());

                    psDetail.addBatch();
                }

                psDetail.executeBatch();
            }

            // Jika semua berhasil
            conn.commit();
            return true;

        } catch (SQLException e) {

            // Jika ada kegagalan, batalkan semua proses
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
            }

            throw e;

        } finally {

            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
// ==========================================
// READ - MENGAMBIL DETAIL PESANAN
// ==========================================
// ==========================================
// READ - DETAIL PESANAN DENGAN NAMA MENU
// ==========================================
public List<String[]> getDetailPesananNamaMenu(int idPesanan)
        throws SQLException {

    List<String[]> daftar = new ArrayList<>();

    String sql = "SELECT m.nama_menu, d.jumlah, d.subtotal, "
            + "d.catatan_khusus "
            + "FROM detail_pesanan d "
            + "JOIN menu_item m ON d.id_menu = m.id_menu "
            + "WHERE d.id_pesanan = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idPesanan);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                daftar.add(new String[]{
                    rs.getString("nama_menu"),
                    String.valueOf(rs.getInt("jumlah")),
                    rs.getBigDecimal("subtotal").toString(),
                    rs.getString("catatan_khusus")
                });
            }
        }
    }

    return daftar;
}

// ==========================================
// REKAP STATUS PESANAN
// ==========================================
public Map<String, Integer> getRekapStatusPesanan()
        throws SQLException {

    Map<String, Integer> rekap = new HashMap<>();

    String sql = "SELECT status_pesanan, COUNT(*) AS jumlah "
            + "FROM pesanan GROUP BY status_pesanan";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            rekap.put(
                    rs.getString("status_pesanan"),
                    rs.getInt("jumlah")
            );
        }
    }

    return rekap;
}

// ==========================================
// UPDATE STATUS PESANAN
// ==========================================
public boolean updateStatusPesanan(int idPesanan, String status) 
        throws SQLException {

    String sql = "UPDATE pesanan SET status_pesanan = ? "
            + "WHERE id_pesanan = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, status);
        ps.setInt(2, idPesanan);

        return ps.executeUpdate() > 0;
    }
}
// ==========================================
// MENGAMBIL BUKTI PEMBAYARAN
// ==========================================
public String getBuktiPembayaran(int idPesanan)
        throws SQLException {

    String sql = "SELECT bukti_pembayaran FROM pesanan "
            + "WHERE id_pesanan = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idPesanan);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("bukti_pembayaran");
            }
        }
    }

    return null;
}

// ==========================================
// VALIDASI PEMBAYARAN
// ==========================================
public boolean validasiPembayaran(int idPesanan, String status)
        throws SQLException {

    String sql = "UPDATE pesanan SET status_pesanan = ? "
            + "WHERE id_pesanan = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, status);
        ps.setInt(2, idPesanan);

        return ps.executeUpdate() > 0;
    }
}


public List<Pesanan> getPesananPembeli(int idPembeli) throws SQLException {

    List<Pesanan> daftar = new ArrayList<>();

    String sql = "SELECT * FROM pesanan WHERE id_pembeli = ? "
               + "ORDER BY tgl_pesan DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idPembeli);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {

                Pesanan pesanan = new Pesanan(
                    rs.getInt("id_pesanan"),
                    rs.getString("kode_transaksi"),
                    rs.getInt("id_batch"),
                    rs.getInt("id_pembeli"),
                    rs.getTimestamp("tgl_pesan"),
                    rs.getBigDecimal("total_harga"),
                    rs.getString("metode_pengambilan"),
                    rs.getString("bukti_pembayaran"),
                    rs.getString("status_pesanan")
                );

                daftar.add(pesanan);
            }
        }
    }

    return daftar;
}

public List<Pesanan> getPesananByUMKM(int idUmkm) throws SQLException {
    List<Pesanan> daftar = new ArrayList<>();
    String sql = "SELECT p.* FROM pesanan p "
               + "JOIN batch_po b ON p.id_batch = b.id_batch "
               + "WHERE b.id_umkm = ? ORDER BY p.tgl_pesan DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, idUmkm);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                daftar.add(new Pesanan(
                    rs.getInt("id_pesanan"),
                    rs.getString("kode_transaksi"),
                    rs.getInt("id_batch"),
                    rs.getInt("id_pembeli"),
                    rs.getTimestamp("tgl_pesan"),
                    rs.getBigDecimal("total_harga"),
                    rs.getString("metode_pengambilan"),
                    rs.getString("bukti_pembayaran"),
                    rs.getString("status_pesanan")
                ));
            }
        }
    }
    return daftar;
}

public boolean uploadBuktiPembayaran(int idPesanan, String buktiPembayaran)
        throws SQLException {
    String sql = "UPDATE pesanan SET bukti_pembayaran = ? WHERE id_pesanan = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, buktiPembayaran);
        ps.setInt(2, idPesanan);
        return ps.executeUpdate() > 0;
    }
}

public List<Object[]> getAllPesananLengkap() throws SQLException {
    List<Object[]> daftar = new ArrayList<>();
    String sql = "SELECT p.id_pesanan, p.kode_transaksi, usr.nama_lengkap, usr.no_hp, "
               + "u.nama_toko, b.nama_batch, p.tgl_pesan, p.total_harga, "
               + "p.metode_pengambilan, p.bukti_pembayaran, p.status_pesanan "
               + "FROM pesanan p "
               + "JOIN users usr ON p.id_pembeli = usr.id_user "
               + "JOIN batch_po b ON p.id_batch = b.id_batch "
               + "JOIN umkm u ON b.id_umkm = u.id_umkm "
               + "ORDER BY p.tgl_pesan DESC, p.id_pesanan DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            daftar.add(new Object[]{
                rs.getInt("id_pesanan"),
                rs.getString("kode_transaksi"),
                rs.getString("nama_lengkap"),
                rs.getString("no_hp"),
                rs.getString("nama_toko") + " - " + rs.getString("nama_batch"),
                rs.getTimestamp("tgl_pesan"),
                rs.getBigDecimal("total_harga"),
                rs.getString("metode_pengambilan"),
                rs.getString("bukti_pembayaran"),
                rs.getString("status_pesanan")
            });
        }
    }
    return daftar;
}

public List<Object[]> getPesananUMKMLengkap(int idUmkm) throws SQLException {
    List<Object[]> daftar = new ArrayList<>();
    String sql = "SELECT p.id_pesanan, p.kode_transaksi, usr.nama_lengkap, usr.no_hp, "
               + "u.nama_toko, b.nama_batch, p.tgl_pesan, p.total_harga, "
               + "p.metode_pengambilan, p.bukti_pembayaran, p.status_pesanan "
               + "FROM pesanan p "
               + "JOIN users usr ON p.id_pembeli = usr.id_user "
               + "JOIN batch_po b ON p.id_batch = b.id_batch "
               + "JOIN umkm u ON b.id_umkm = u.id_umkm "
               + "WHERE b.id_umkm = ? "
               + "ORDER BY p.tgl_pesan DESC, p.id_pesanan DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, idUmkm);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                daftar.add(new Object[]{
                    rs.getInt("id_pesanan"),
                    rs.getString("kode_transaksi"),
                    rs.getString("nama_lengkap"),
                    rs.getString("no_hp"),
                    rs.getString("nama_toko") + " - " + rs.getString("nama_batch"),
                    rs.getTimestamp("tgl_pesan"),
                    rs.getBigDecimal("total_harga"),
                    rs.getString("metode_pengambilan"),
                    rs.getString("bukti_pembayaran"),
                    rs.getString("status_pesanan")
                });
            }
        }
    }
    return daftar;
}

public List<Object[]> getRiwayatPembeliLengkap(int idPembeli) throws SQLException {
    List<Object[]> daftar = new ArrayList<>();
    String sql = "SELECT p.id_pesanan, p.kode_transaksi, u.nama_toko, b.nama_batch, "
               + "p.tgl_pesan, p.total_harga, p.metode_pengambilan, "
               + "p.bukti_pembayaran, p.status_pesanan "
               + "FROM pesanan p "
               + "JOIN batch_po b ON p.id_batch = b.id_batch "
               + "JOIN umkm u ON b.id_umkm = u.id_umkm "
               + "WHERE p.id_pembeli = ? "
               + "ORDER BY p.tgl_pesan DESC, p.id_pesanan DESC";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, idPembeli);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                daftar.add(new Object[]{
                    rs.getInt("id_pesanan"),
                    rs.getString("kode_transaksi"),
                    rs.getString("nama_toko") + " - " + rs.getString("nama_batch"),
                    rs.getTimestamp("tgl_pesan"),
                    rs.getBigDecimal("total_harga"),
                    rs.getString("metode_pengambilan"),
                    rs.getString("bukti_pembayaran"),
                    rs.getString("status_pesanan")
                });
            }
        }
    }
    return daftar;
}

}



