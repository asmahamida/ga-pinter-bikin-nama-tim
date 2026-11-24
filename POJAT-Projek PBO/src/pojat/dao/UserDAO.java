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
import java.util.ArrayList;
import java.util.List;

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

    public List<User> getAllUsers() throws SQLException {
        List<User> daftar = new ArrayList<>();
        String sql = "SELECT id_user, username, password, nama_lengkap, no_hp, role FROM users ORDER BY id_user DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                daftar.add(new User(
                    result.getInt("id_user"),
                    result.getString("username"),
                    result.getString("password"),
                    result.getString("nama_lengkap"),
                    result.getString("no_hp"),
                    result.getString("role")
                ));
            }
        }
        return daftar;
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public boolean tambahUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, nama_lengkap, no_hp, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getNamaLengkap());
            statement.setString(4, user.getNoHp());
            statement.setString(5, user.getRole());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateUser(User user, boolean updatePassword) throws SQLException {
        String sql = updatePassword
                ? "UPDATE users SET username = ?, password = ?, nama_lengkap = ?, no_hp = ?, role = ? WHERE id_user = ?"
                : "UPDATE users SET username = ?, nama_lengkap = ?, no_hp = ?, role = ? WHERE id_user = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int idx = 1;
            statement.setString(idx++, user.getUsername());
            if (updatePassword) {
                statement.setString(idx++, user.getPasswordHash());
            }
            statement.setString(idx++, user.getNamaLengkap());
            statement.setString(idx++, user.getNoHp());
            statement.setString(idx++, user.getRole());
            statement.setInt(idx, user.getIdUser());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean hapusUser(int idUser) throws SQLException {
        String sql = "DELETE FROM users WHERE id_user = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idUser);
            return statement.executeUpdate() > 0;
        }
    }
}
