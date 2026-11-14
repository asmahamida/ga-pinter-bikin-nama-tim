/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.projek.pbo;

import pojat.dao.UMKMDAO;
import pojat.model.UMKM;

import java.sql.SQLException;
import java.util.List;

public class TestUMKMDAO {
        public static void main(String[] args) {

        UMKMDAO umkmDAO = new UMKMDAO();

        try {

            List<UMKM> daftarUMKM =
                    umkmDAO.getAllUMKM();

            System.out.println("================================");
            System.out.println("       DATA UMKM POJAT");
            System.out.println("================================");

            if (daftarUMKM.isEmpty()) {

                System.out.println(
                    "Belum ada data UMKM."
                );

            } else {

                for (UMKM umkm : daftarUMKM) {

                    System.out.println(
                        "ID UMKM: " + umkm.getIdUmkm()
                    );

                    System.out.println(
                        "ID Owner: " + umkm.getIdOwner()
                    );

                    System.out.println(
                        "Nama Toko: " + umkm.getNamaToko()
                    );

                    System.out.println(
                        "Deskripsi: " + umkm.getDeskripsi()
                    );

                    System.out.println(
                        "Alamat: " +
                        umkm.getAlamatPenjemputan()
                    );

                    System.out.println(
                        "No Rekening: " +
                        umkm.getNoRekening()
                    );

                    System.out.println(
                        "Bank: " + umkm.getBank()
                    );

                    System.out.println(
                        "--------------------------------"
                    );
                }
            }

            System.out.println(
                "Total UMKM: " + daftarUMKM.size()
            );

        } catch (SQLException e) {

            System.out.println(
                "Gagal membaca data UMKM!"
            );

            e.printStackTrace();
        }
    }
}
