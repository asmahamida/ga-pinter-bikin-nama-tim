/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.projek.pbo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CekDatabasePOJAT {

    public static void main(String[] args) {

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Cek database yang sedang digunakan Java
            ResultSet rsDB = stmt.executeQuery("SELECT DATABASE()");

            if (rsDB.next()) {
                System.out.println("Database Java: " + rsDB.getString(1));
            }

            // Cek data UMKM yang terbaca oleh Java
            ResultSet rs = stmt.executeQuery(
                "SELECT id_umkm, nama_toko FROM umkm"
            );

            System.out.println("\nData UMKM dari Java:");

            while (rs.next()) {
                System.out.println(
                    "ID: " + rs.getInt("id_umkm")
                    + " | Nama: " + rs.getString("nama_toko")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
