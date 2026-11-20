/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.model;

import java.math.BigDecimal;

public class DetailPesanan {

    private int idDetail;
    private int idPesanan;
    private int idMenu;
    private int jumlah;
    private BigDecimal subtotal;
    private String catatanKhusus;

    // Constructor kosong
    public DetailPesanan() {
    }

    // Constructor lengkap
    public DetailPesanan(int idDetail, int idPesanan,
                        int idMenu, int jumlah,
                        BigDecimal subtotal, String catatanKhusus) {

        this.idDetail = idDetail;
        this.idPesanan = idPesanan;
        this.idMenu = idMenu;
        this.jumlah = jumlah;
        this.subtotal = subtotal;
        this.catatanKhusus = catatanKhusus;
    }

    // Getter
    public int getIdDetail() {
        return idDetail;
    }

    public int getIdPesanan() {
        return idPesanan;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public int getJumlah() {
        return jumlah;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public String getCatatanKhusus() {
        return catatanKhusus;
    }

    // Setter
    public void setIdDetail(int idDetail) {
        this.idDetail = idDetail;
    }

    public void setIdPesanan(int idPesanan) {
        this.idPesanan = idPesanan;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public void setCatatanKhusus(String catatanKhusus) {
        this.catatanKhusus = catatanKhusus;
    }
}
