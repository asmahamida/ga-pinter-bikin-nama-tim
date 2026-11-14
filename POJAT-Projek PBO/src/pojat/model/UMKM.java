/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.model;

/**
 *
 * @author LENOVO
 */
public class UMKM {
        private int idUmkm;
    private int idOwner;
    private String namaToko;
    private String deskripsi;
    private String alamatPenjemputan;
    private String noRekening;
    private String bank;

    // Constructor
    public UMKM(int idUmkm, int idOwner, String namaToko,
                String deskripsi, String alamatPenjemputan,
                String noRekening, String bank) {

        this.idUmkm = idUmkm;
        this.idOwner = idOwner;
        this.namaToko = namaToko;
        this.deskripsi = deskripsi;
        this.alamatPenjemputan = alamatPenjemputan;
        this.noRekening = noRekening;
        this.bank = bank;
    }

    // Getter
    public int getIdUmkm() {
        return idUmkm;
    }

    public int getIdOwner() {
        return idOwner;
    }

    public String getNamaToko() {
        return namaToko;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getAlamatPenjemputan() {
        return alamatPenjemputan;
    }

    public String getNoRekening() {
        return noRekening;
    }

    public String getBank() {
        return bank;
    }

    // Setter
    public void setIdOwner(int idOwner) {
        this.idOwner = idOwner;
    }

    public void setNamaToko(String namaToko) {
        this.namaToko = namaToko;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setAlamatPenjemputan(String alamatPenjemputan) {
        this.alamatPenjemputan = alamatPenjemputan;
    }

    public void setNoRekening(String noRekening) {
        this.noRekening = noRekening;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }
}
