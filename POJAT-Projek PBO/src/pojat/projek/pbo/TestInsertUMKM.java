/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pojat.projek.pbo;

import pojat.dao.UMKMDAO;
import pojat.model.UMKM;

import java.sql.SQLException;

public class TestInsertUMKM {
        public static void main(String[] args) {

        UMKMDAO umkmDAO = new UMKMDAO();

        // Data UMKM untuk pengujian
        UMKM umkm = new UMKM(
            0,
            2,
            "Toko Sayur Segar",
            "Menyediakan berbagai sayuran segar",
            "Jatinangor, Sumedang",
            "",
            ""
        );

        try {

            boolean berhasil = umkmDAO.tambahUMKM(umkm);

            if (berhasil) {

                System.out.println(
                    "DATA UMKM BERHASIL DITAMBAHKAN!"
                );

            } else {

                System.out.println(
                    "DATA UMKM GAGAL DITAMBAHKAN!"
                );
            }

        } catch (SQLException e) {

            System.out.println(
                "Terjadi kesalahan saat menambahkan UMKM."
            );

            e.printStackTrace();
        }
    }
}
