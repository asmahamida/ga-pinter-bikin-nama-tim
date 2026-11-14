/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.dao;

import pojat.model.User;
import pojat.projek.pbo.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
        // Mencari pengguna berdasarkan username
    public User findByUsername(String username)
            throws SQLException {

        String sql =
            "SELECT id_user, username, password, " +
            "nama_lengkap, no_hp, role " +
            "FROM users WHERE username = ?";

        try (
            Connection connection =
                DatabaseConnection.getConnection();

            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return new User(
                        result.getInt("id_user"),
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("nama_lengkap"),
                        result.getString("no_hp"),
                        result.getString("role")
                    );
                }
            }
        }

        return null;
    }
}
