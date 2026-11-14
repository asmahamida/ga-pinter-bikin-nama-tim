/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.projek.pbo;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
            public static void main(String[] args) {

        System.out.println("Menghubungkan ke database POJAT...");

        try (Connection connection =
                DatabaseConnection.getConnection()) {

            System.out.println("KONEKSI DATABASE POJAT BERHASIL!");
            System.out.println("Database: " + connection.getCatalog());

        } catch (SQLException e) {

            System.out.println("KONEKSI GAGAL!");
            System.out.println(e.getMessage());
        }
    }
}
