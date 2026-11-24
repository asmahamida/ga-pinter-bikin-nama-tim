/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package pojat.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class BatchPO {

    private int idBatch;
    private int idUMKM;
    private String namaBatch;
    private String tglBuka;
    private String tglTutup;
    private String tglPengiriman;
    private int kuotaMax;
    private String status;

    public BatchPO() {
    }

    // Constructor untuk data dari database (DATETIME + DATE)
    public BatchPO(int idBatch, int idUMKM, String namaBatch,
                  Timestamp tglBuka, Timestamp tglTutup,
                  Date tglPengiriman, int kuotaMax, String status) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.idBatch = idBatch;
        this.idUMKM = idUMKM;
        this.namaBatch = namaBatch;
        this.tglBuka = (tglBuka != null) ? sdf.format(tglBuka) : "";
        this.tglTutup = (tglTutup != null) ? sdf.format(tglTutup) : "";
        this.tglPengiriman = (tglPengiriman != null) ? tglPengiriman.toString() : "";
        this.kuotaMax = kuotaMax;
        this.status = status;
    }

    // Constructor untuk data dari database (java.sql.Date)
    public BatchPO(int idBatch, int idUMKM, String namaBatch,
                  Date tglBuka, Date tglTutup,
                  Date tglPengiriman, int kuotaMax, String status) {

        this.idBatch = idBatch;
        this.idUMKM = idUMKM;
        this.namaBatch = namaBatch;
        this.tglBuka = (tglBuka != null) ? tglBuka.toString() : "";
        this.tglTutup = (tglTutup != null) ? tglTutup.toString() : "";
        this.tglPengiriman = (tglPengiriman != null) ? tglPengiriman.toString() : "";
        this.kuotaMax = kuotaMax;
        this.status = status;
    }

    // Constructor jika tanggal diberikan sebagai String
    public BatchPO(int idBatch, int idUMKM, String namaBatch,
                  String tglBuka, String tglTutup,
                  String tglPengiriman, int kuotaMax, String status) {

        this.idBatch = idBatch;
        this.idUMKM = idUMKM;
        this.namaBatch = namaBatch;
        this.tglBuka = tglBuka;
        this.tglTutup = tglTutup;
        this.tglPengiriman = tglPengiriman;
        this.kuotaMax = kuotaMax;
        this.status = status;
    }

    public int getIdBatch() {
        return idBatch;
    }

    public void setIdBatch(int idBatch) {
        this.idBatch = idBatch;
    }

    public int getIdUMKM() {
        return idUMKM;
    }

    public void setIdUMKM(int idUMKM) {
        this.idUMKM = idUMKM;
    }

    public String getNamaBatch() {
        return namaBatch;
    }

    public void setNamaBatch(String namaBatch) {
        this.namaBatch = namaBatch;
    }

    public String getTglBuka() {
        return tglBuka;
    }

    public void setTglBuka(String tglBuka) {
        this.tglBuka = tglBuka;
    }

    public String getTglTutup() {
        return tglTutup;
    }

    public void setTglTutup(String tglTutup) {
        this.tglTutup = tglTutup;
    }

    public String getTglPengiriman() {
        return tglPengiriman;
    }

    public void setTglPengiriman(String tglPengiriman) {
        this.tglPengiriman = tglPengiriman;
    }

    public int getKuotaMax() {
        return kuotaMax;
    }

    public void setKuotaMax(int kuotaMax) {
        this.kuotaMax = kuotaMax;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

