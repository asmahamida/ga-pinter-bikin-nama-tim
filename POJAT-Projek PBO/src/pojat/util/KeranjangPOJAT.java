/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pojat.util;

import java.util.ArrayList;
import java.util.List;

public class KeranjangPOJAT {

    public static class ItemKeranjang {
        public int idMenu;
        public int idUmkm;
        public String namaToko;
        public String namaMenu;
        public double harga;
        public int jumlah;
        public int stok;
        public String catatanKhusus;

        public ItemKeranjang(int idMenu, String namaMenu,
                           double harga, int jumlah, int stok) {
            this(idMenu, 0, "", namaMenu, harga, jumlah, stok, null);
        }

        public ItemKeranjang(int idMenu, String namaMenu,
                           double harga, int jumlah, int stok, String catatanKhusus) {
            this(idMenu, 0, "", namaMenu, harga, jumlah, stok, catatanKhusus);
        }

        public ItemKeranjang(int idMenu, int idUmkm, String namaToko, String namaMenu,
                           double harga, int jumlah, int stok, String catatanKhusus) {
            this.idMenu = idMenu;
            this.idUmkm = idUmkm;
            this.namaToko = namaToko == null ? "" : namaToko;
            this.namaMenu = namaMenu;
            this.harga = harga;
            this.jumlah = jumlah;
            this.stok = stok;
            this.catatanKhusus = catatanKhusus;
        }

        public double getSubtotal() {
            return harga * jumlah;
        }
    }

    private static final List<ItemKeranjang> items = new ArrayList<>();

    public static void tambah(int idMenu, String namaMenu,
                            double harga, int jumlah, int stok) {
        tambah(idMenu, 0, "", namaMenu, harga, jumlah, stok, null);
    }

    public static void tambah(int idMenu, String namaMenu,
                            double harga, int jumlah, int stok, String catatanKhusus) {
        tambah(idMenu, 0, "", namaMenu, harga, jumlah, stok, catatanKhusus);
    }

    public static void tambah(int idMenu, int idUmkm, String namaToko, String namaMenu,
                            double harga, int jumlah, int stok, String catatanKhusus) {

        for (ItemKeranjang item : items) {
            if (item.idMenu == idMenu) {
                int jumlahBaru = item.jumlah + jumlah;

                if (jumlahBaru <= stok) {
                    item.jumlah = jumlahBaru;
                    if (catatanKhusus != null && !catatanKhusus.trim().isEmpty()) {
                        item.catatanKhusus = catatanKhusus.trim();
                    }
                }
                if (item.idUmkm == 0 && idUmkm > 0) {
                    item.idUmkm = idUmkm;
                    item.namaToko = namaToko == null ? "" : namaToko;
                }
                return;
            }
        }

        items.add(new ItemKeranjang(idMenu, idUmkm, namaToko, namaMenu, harga, jumlah, stok, catatanKhusus));
    }

    public static boolean updateItem(int index, int jumlahBaru, String catatanBaru) {
        if (index < 0 || index >= items.size()) {
            return false;
        }
        ItemKeranjang item = items.get(index);
        if (jumlahBaru <= 0 || jumlahBaru > item.stok) {
            return false;
        }
        item.jumlah = jumlahBaru;
        item.catatanKhusus = (catatanBaru == null || catatanBaru.trim().isEmpty()) ? null : catatanBaru.trim();
        return true;
    }

    public static List<ItemKeranjang> getItems() {
        return items;
    }

    public static double getTotal() {
        double total = 0;

        for (ItemKeranjang item : items) {
            total += item.getSubtotal();
        }

        return total;
    }

    public static void hapus(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public static void kosongkan() {
        items.clear();
    }
}
