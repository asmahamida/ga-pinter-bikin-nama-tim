/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Pesanan {

    private int idPesanan;
    private String kodeTransaksi;
    private int idBatch;
    private int idPembeli;
    private Timestamp tglPesanan;
    private BigDecimal totalHarga;
    private String metodePengambilan;
    private String buktiPembayaran;
    private String statusPesanan;

    // Constructor kosong
    public Pesanan() {
    }

    // Constructor lengkap
    public Pesanan(int idPesanan, String kodeTransaksi,
                  int idBatch, int idPembeli,
                  Timestamp tglPesanan, BigDecimal totalHarga,
                  String metodePengambilan, String buktiPembayaran,
                  String statusPesanan) {

        this.idPesanan = idPesanan;
        this.kodeTransaksi = kodeTransaksi;
        this.idBatch = idBatch;
        this.idPembeli = idPembeli;
        this.tglPesanan = tglPesanan;
        this.totalHarga = totalHarga;
        this.metodePengambilan = metodePengambilan;
        this.buktiPembayaran = buktiPembayaran;
        this.statusPesanan = statusPesanan;
    }

    // Getter
    public int getIdPesanan() {
        return idPesanan;
    }

    public String getKodeTransaksi() {
        return kodeTransaksi;
    }

    public int getIdBatch() {
        return idBatch;
    }

    public int getIdPembeli() {
        return idPembeli;
    }

    public Timestamp getTglPesanan() {
        return tglPesanan;
    }

    public BigDecimal getTotalHarga() {
        return totalHarga;
    }

    public String getMetodePengambilan() {
        return metodePengambilan;
    }

    public String getBuktiPembayaran() {
        return buktiPembayaran;
    }

    public String getStatusPesanan() {
        return statusPesanan;
    }

    // Setter
    public void setIdPesanan(int idPesanan) {
        this.idPesanan = idPesanan;
    }

    public void setKodeTransaksi(String kodeTransaksi) {
        this.kodeTransaksi = kodeTransaksi;
    }

    public void setIdBatch(int idBatch) {
        this.idBatch = idBatch;
    }

    public void setIdPembeli(int idPembeli) {
        this.idPembeli = idPembeli;
    }

    public void setTglPesanan(Timestamp tglPesanan) {
        this.tglPesanan = tglPesanan;
    }

    public void setTotalHarga(BigDecimal totalHarga) {
        this.totalHarga = totalHarga;
    }

    public void setMetodePengambilan(String metodePengambilan) {
        this.metodePengambilan = metodePengambilan;
    }

    public void setBuktiPembayaran(String buktiPembayaran) {
        this.buktiPembayaran = buktiPembayaran;
    }

    public void setStatusPesanan(String statusPesanan) {
        this.statusPesanan = statusPesanan;
    }
}
