/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pojat.dao;

import pojat.projek.pbo.DatabaseConnection;
import pojat.util.KeranjangPOJAT;
import pojat.util.KeranjangPOJAT.ItemKeranjang;

import java.sql.*;
import java.util.List;

public class CheckoutDAO {

    public boolean checkout(
            int idPembeli,
            int idBatch,
            String metodePengambilan,
            List<ItemKeranjang> items
    ) throws SQLException {

        Connection conn = DatabaseConnection.getConnection();

        try {
            conn.setAutoCommit(false);

            String sqlPesanan = "INSERT INTO pesanan "
                    + "(kode_transaksi, id_batch, id_pembeli, tgl_pesan, "
                    + "total_harga, metode_pengambilan, status_pesanan) "
                    + "VALUES (?, ?, ?, NOW(), ?, ?, ?)";

            double total = 0;

            for (ItemKeranjang item : items) {
                total += item.getSubtotal();
            }

            String kodeTransaksi = "POJAT-" + System.currentTimeMillis();

            int idPesanan;

            try (PreparedStatement ps = conn.prepareStatement(
                    sqlPesanan, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, kodeTransaksi);
                ps.setInt(2, idBatch);
                ps.setInt(3, idPembeli);
                ps.setDouble(4, total);
                ps.setString(5, metodePengambilan);
                ps.setString(6, "MENUNGGU");

                ps.executeUpdate();

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idPesanan = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("ID pesanan gagal dibuat.");
                    }
                }
            }

            String sqlDetail = "INSERT INTO detail_pesanan "
                    + "(id_pesanan, id_menu, jumlah, subtotal) "
                    + "VALUES (?, ?, ?, ?)";

            try (PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {

                for (ItemKeranjang item : items) {
                    psDetail.setInt(1, idPesanan);
                    psDetail.setInt(2, item.idMenu);
                    psDetail.setInt(3, item.jumlah);
                    psDetail.setDouble(4, item.getSubtotal());
                    psDetail.addBatch();
                }

                psDetail.executeBatch();
            }

            conn.commit();
            conn.setAutoCommit(true);

            return true;

        } catch (SQLException e) {
            conn.rollback();
            conn.setAutoCommit(true);
            throw e;
        } finally {
            conn.close();
        }
    }
}
