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

        String username = "admin";
        String passwordBaru = "AdminPojat123!";

        String hashBaru = PasswordUtil.hashPassword(passwordBaru);

        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hashBaru);
            ps.setString(2, username);

            int hasil = ps.executeUpdate();

            if (hasil > 0) {
                System.out.println("PASSWORD BERHASIL DIRESET!");
                System.out.println("Username: " + username);
                System.out.println("Password baru: " + passwordBaru);
            } else {
                System.out.println("Username tidak ditemukan!");
            }

        } catch (Exception e) {
            System.out.println("Gagal reset password!");
            e.printStackTrace();
        }
    }
}
