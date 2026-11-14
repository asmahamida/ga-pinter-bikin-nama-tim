/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.projek.pbo;

import pojat.dao.UserDAO;
import pojat.model.User;
import pojat.util.PasswordUtil;

public class TestUserDAO {
        public static void main(String[] args) {

        UserDAO userDAO = new UserDAO();

        String username = "admin_pojat";
        String password = "AdminPojat123!";

        try {

            User user = userDAO.findByUsername(username);

            if (user == null) {

                System.out.println("Pengguna tidak ditemukan!");

            } else {

                boolean valid = PasswordUtil.verifyPassword(
                    password,
                    user.getPasswordHash()
                );

                if (valid) {

                    System.out.println("LOGIN BERHASIL!");
                    System.out.println(
                        "Nama: " + user.getNamaLengkap()
                    );
                    System.out.println(
                        "Role: " + user.getRole()
                    );

                } else {

                    System.out.println("Password salah!");
                }
            }

        } catch (Exception e) {

            System.out.println("Terjadi kesalahan!");
            e.printStackTrace();
        }
    }
}

