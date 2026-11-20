/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.dao;

import pojat.model.MenuItem;
import pojat.projek.pbo.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO {

    // ==========================================
    // 1. READ - MENGAMBIL SEMUA DATA MENU
    // ==========================================
    public List<MenuItem> getAllMenu() throws SQLException {

        List<MenuItem> daftarMenu = new ArrayList<>();

        String sql = "SELECT * FROM menu_item";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                MenuItem menu = new MenuItem(
                    rs.getInt("id_menu"),
                    rs.getInt("id_umkm"),
                    rs.getString("nama_menu"),
                    rs.getString("deskripsi"),
                    rs.getDouble("harga"),
                    rs.getInt("stok_per_batch")
                );

                daftarMenu.add(menu);
            }
        }

        return daftarMenu;
    }

    // ==========================================
    // 2. CREATE - MENAMBAHKAN MENU
    // ==========================================
    public boolean tambahMenu(MenuItem menu) throws SQLException {

        String sql = "INSERT INTO menu_item "
                   + "(id_umkm, nama_menu, deskripsi, harga, stok_per_batch) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, menu.getIdUmkm());
            ps.setString(2, menu.getNamaMenu());
            ps.setString(3, menu.getDeskripsi());
            ps.setDouble(4, menu.getHarga());
            ps.setInt(5, menu.getStokPerBatch());

            return ps.executeUpdate() > 0;
        }
    }

    // ==========================================
    // 3. UPDATE - MENGUBAH DATA MENU
    // ==========================================
    public boolean updateMenu(MenuItem menu) throws SQLException {

        String sql = "UPDATE menu_item SET "
                   + "id_umkm = ?, "
                   + "nama_menu = ?, "
                   + "deskripsi = ?, "
                   + "harga = ?, "
                   + "stok_per_batch = ? "
                   + "WHERE id_menu = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, menu.getIdUmkm());
            ps.setString(2, menu.getNamaMenu());
            ps.setString(3, menu.getDeskripsi());
            ps.setDouble(4, menu.getHarga());
            ps.setInt(5, menu.getStokPerBatch());
            ps.setInt(6, menu.getIdMenu());

            return ps.executeUpdate() > 0;
        }
    }

    // ==========================================
    // 4. DELETE - MENGHAPUS MENU
    // ==========================================
    public boolean hapusMenu(int idMenu) throws SQLException {

        String sql = "DELETE FROM menu_item WHERE id_menu = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMenu);

            return ps.executeUpdate() > 0;
        }
    }
}
