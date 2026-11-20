/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.model;

/**
 *
 * @author LENOVO
 */
public class MenuItem {

    private int idMenu;
    private int idUmkm;
    private String namaMenu;
    private String deskripsi;
    private double harga;
    private int stokPerBatch;

    // Constructor kosong
    public MenuItem() {
    }

    // Constructor lengkap
    public MenuItem(int idMenu, int idUmkm, String namaMenu,
                   String deskripsi, double harga, int stokPerBatch) {

        this.idMenu = idMenu;
        this.idUmkm = idUmkm;
        this.namaMenu = namaMenu;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.stokPerBatch = stokPerBatch;
    }

    // Getter
    public int getIdMenu() {
        return idMenu;
    }

    public int getIdUmkm() {
        return idUmkm;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public double getHarga() {
        return harga;
    }

    public int getStokPerBatch() {
        return stokPerBatch;
    }

    // Setter
    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public void setIdUmkm(int idUmkm) {
        this.idUmkm = idUmkm;
    }

    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public void setStokPerBatch(int stokPerBatch) {
        this.stokPerBatch = stokPerBatch;
    }
}
