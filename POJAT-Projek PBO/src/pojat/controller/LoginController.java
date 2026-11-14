/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.controller;

import pojat.dao.UserDAO;
import pojat.model.User;
import pojat.util.PasswordUtil;

public class LoginController {
        private UserDAO userDAO;

    public LoginController() {
        userDAO = new UserDAO();
    }

    public User login(String username, String password) {

        try {

            // Mencari pengguna berdasarkan username
            User user = userDAO.findByUsername(username);

            // Jika username tidak ditemukan
            if (user == null) {
                return null;
            }

            // Memeriksa password
            boolean passwordValid =
                    PasswordUtil.verifyPassword(
                        password,
                        user.getPasswordHash()
                    );

            if (passwordValid) {
                return user;
            }

            return null;

        } catch (Exception e) {

            System.out.println("Kesalahan login:");
            e.printStackTrace();

            return null;
        }
    }
}
