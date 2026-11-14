/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.model;

/**
 *
 * @author LENOVO
 */
public class User {
        private int idUser;
    private String username;
    private String passwordHash;
    private String namaLengkap;
    private String noHp;
    private String role;

    // Constructor
    public User(int idUser, String username,
                String passwordHash, String namaLengkap,
                String noHp, String role) {

        this.idUser = idUser;
        this.username = username;
        this.passwordHash = passwordHash;
        this.namaLengkap = namaLengkap;
        this.noHp = noHp;
        this.role = role;
    }

    // Getter
    public int getIdUser() {
        return idUser;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public String getNoHp() {
        return noHp;
    }

    public String getRole() {
        return role;
    }
}
