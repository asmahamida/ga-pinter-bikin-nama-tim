/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.projek.pbo;

import pojat.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SetupDemoPasswords {
        public static void main(String[] args) {

        String[][] accounts = {
            {"admin_pojat", "AdminPojat123!"},
            {"admin_umkm", "UmkmPojat123!"},
            {"pembeli_demo", "PembeliPojat123!"}
        };

        String sql =
            "UPDATE users SET password = ? WHERE username = ?";

        try (
            Connection connection =
                DatabaseConnection.getConnection();

            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {

            for (String[] account : accounts) {

                String hash = PasswordUtil.hashPassword(
                    account[1]
                );

                statement.setString(1, hash);
                statement.setString(2, account[0]);

                int updated = statement.executeUpdate();

                System.out.println(
                    account[0] + ": " +
                    (updated > 0 ? "BERHASIL" : "TIDAK DITEMUKAN")
                );
            }

        } catch (Exception e) {
            System.out.println("Gagal memperbarui password!");
            e.printStackTrace();
        }
    }
}
