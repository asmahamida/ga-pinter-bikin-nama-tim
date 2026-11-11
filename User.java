package model;

public class User {

    private int idUser;
    private String username;
    private String password;
    private String namaLengkap;
    private String noHp;
    private String role;

    public User(int idUser, String username,
                String password, String namaLengkap,
                String noHp, String role) {

        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.namaLengkap = namaLengkap;
        this.noHp = noHp;
        this.role = role;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getUsername() {
        return username;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public String getRole() {
        return role;
    }
}