/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pojat.dao;

import pojat.model.UMKM;
import pojat.projek.pbo.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class UMKMDAO {

    // ==================================
    // READ: Mengambil seluruh data UMKM
    // ==================================
    public List<UMKM> getAllUMKM() throws SQLException {

        List<UMKM> daftarUMKM = new ArrayList<>();

        String sql = "SELECT * FROM umkm";

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                UMKM umkm = new UMKM(
                    rs.getInt("id_umkm"),
                    rs.getInt("id_owner"),
                    rs.getString("nama_toko"),
                    rs.getString("deskripsi"),
                    rs.getString("alamat_penjemputan"),
                    rs.getString("no_rekening"),
                    rs.getString("bank")
                );

                daftarUMKM.add(umkm);
            }
        }

        return daftarUMKM;
    }

    // Mengambil data UMKM berdasarkan ID owner
    public UMKM getUMKMByOwner(int idOwner) throws SQLException {
        String sql = "SELECT * FROM umkm WHERE id_owner = ? LIMIT 1";

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, idOwner);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UMKM(
                        rs.getInt("id_umkm"),
                        rs.getInt("id_owner"),
                        rs.getString("nama_toko"),
                        rs.getString("deskripsi"),
                        rs.getString("alamat_penjemputan"),
                        rs.getString("no_rekening"),
                        rs.getString("bank")
                    );
                }
            }
        }

        return null;
    }

    // Mengambil data UMKM berdasarkan ID UMKM
    public UMKM getUMKMById(int idUmkm) throws SQLException {
        String sql = "SELECT * FROM umkm WHERE id_umkm = ? LIMIT 1";

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, idUmkm);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UMKM(
                        rs.getInt("id_umkm"),
                        rs.getInt("id_owner"),
                        rs.getString("nama_toko"),
                        rs.getString("deskripsi"),
                        rs.getString("alamat_penjemputan"),
                        rs.getString("no_rekening"),
                        rs.getString("bank")
                    );
                }
            }
        }

        return null;
    }


    // ==================================
    // CREATE: Menambahkan data UMKM
    // ==================================
    public boolean tambahUMKM(UMKM umkm) throws SQLException {

        String sql = "INSERT INTO umkm "
                + "(id_owner, nama_toko, deskripsi, "
                + "alamat_penjemputan, no_rekening, bank) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, umkm.getIdOwner());
            ps.setString(2, umkm.getNamaToko());
            ps.setString(3, umkm.getDeskripsi());
            ps.setString(4, umkm.getAlamatPenjemputan());
            ps.setString(5, umkm.getNoRekening());
            ps.setString(6, umkm.getBank());

            return ps.executeUpdate() > 0;
        }
    }


    // ==================================
    // UPDATE: Mengubah data UMKM
    // ==================================
    public boolean updateUMKM(UMKM umkm) throws SQLException {

        String sql = "UPDATE umkm SET "
                + "id_owner = ?, "
                + "nama_toko = ?, "
                + "deskripsi = ?, "
                + "alamat_penjemputan = ?, "
                + "no_rekening = ?, "
                + "bank = ? "
                + "WHERE id_umkm = ?";

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, umkm.getIdOwner());
            ps.setString(2, umkm.getNamaToko());
            ps.setString(3, umkm.getDeskripsi());
            ps.setString(4, umkm.getAlamatPenjemputan());
            ps.setString(5, umkm.getNoRekening());
            ps.setString(6, umkm.getBank());
            ps.setInt(7, umkm.getIdUmkm());

            return ps.executeUpdate() > 0;
        }
    }


    // ==================================
    // DELETE: Menghapus data UMKM
    // ==================================
    public boolean hapusUMKM(int idUmkm) throws SQLException {

        String sql = "DELETE FROM umkm WHERE id_umkm = ?";

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idUmkm);

            return ps.executeUpdate() > 0;
        }
    }

}

