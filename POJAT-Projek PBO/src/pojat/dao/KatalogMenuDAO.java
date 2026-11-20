/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pojat.dao;

import pojat.projek.pbo.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import pojat.model.MenuItem;

public class KatalogMenuDAO {

    public List<MenuItem> getSemuaMenu() throws SQLException {
        List<MenuItem> daftar = new ArrayList<>();

        String sql = "SELECT * FROM menu_item WHERE stok_per_batch > 0";

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

                daftar.add(menu);
            }
        }

        return daftar;
    }
}

