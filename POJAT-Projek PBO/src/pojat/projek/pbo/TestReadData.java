/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.projek.pbo;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author LENOVO
 */
public class TestReadData {
        public static void main(String[] args) {

        String sql = "SELECT * FROM users";

        try (
            Connection connection =
                DatabaseConnection.getConnection();

            Statement statement =
                connection.createStatement();

            ResultSet result =
                statement.executeQuery(sql)
        ) {

            System.out.println("================================");
            System.out.println("       DATA PENGGUNA POJAT");
            System.out.println("================================");

            boolean adaData = false;

            while (result.next()) {

                adaData = true;

                System.out.println(
                    "ID User       : " +
                    result.getInt("id_user")
                );

                System.out.println(
                    "Username      : " +
                    result.getString("username")
                );

                System.out.println(
                    "Nama Lengkap  : " +
                    result.getString("nama_lengkap")
                );

                System.out.println(
                    "Nomor HP      : " +
                    result.getString("no_hp")
                );

                System.out.println(
                    "Role          : " +
                    result.getString("role")
                );

                System.out.println("--------------------------------");
            }

            if (!adaData) {
                System.out.println(
                    "Belum ada data pengguna di database."
                );
            }

        } catch (SQLException e) {

            System.out.println("Gagal mengambil data!");
            e.printStackTrace();
        }
    }
}
