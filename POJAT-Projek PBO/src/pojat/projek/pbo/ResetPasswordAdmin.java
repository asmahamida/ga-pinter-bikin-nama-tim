/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pojat.projek.pbo;

import pojat.util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ResetPasswordAdmin {

    public static void main(String[] args) {

        String[][] akun = {
            {"admin_pojat", "AdminPojat123!"},
            {"admin_umkm", "UmkmPojat123!"},
            {"pembeli_demo", "PembeliPojat123!"}
        };

        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (String[] data : akun) {
                String username = data[0];
                String passwordBaru = data[1];
                String hash = PasswordUtil.hashPassword(passwordBaru);

                ps.setString(1, hash);
                ps.setString(2, username);

                int hasil = ps.executeUpdate();

                if (hasil > 0) {
                    System.out.println("Berhasil reset: " + username);
                } else {
                    System.out.println("Username tidak ditemukan: " + username);
                }
            }

            System.out.println("\nRESET SEMUA PASSWORD SELESAI!");

        } catch (Exception e) {
            System.out.println("Gagal reset password!");
            e.printStackTrace();
        }
    }
}
