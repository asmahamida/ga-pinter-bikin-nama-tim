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
        public String namaMenu;
        public double harga;
        public int jumlah;
        public int stok;

        public ItemKeranjang(int idMenu, String namaMenu,
                           double harga, int jumlah, int stok) {
            this.idMenu = idMenu;
            this.namaMenu = namaMenu;
            this.harga = harga;
            this.jumlah = jumlah;
            this.stok = stok;
        }

        public double getSubtotal() {
            return harga * jumlah;
        }
    }

    private static final List<ItemKeranjang> items = new ArrayList<>();

    public static void tambah(int idMenu, String namaMenu,
                            double harga, int jumlah, int stok) {

        for (ItemKeranjang item : items) {
            if (item.idMenu == idMenu) {
                int jumlahBaru = item.jumlah + jumlah;

                if (jumlahBaru <= stok) {
                    item.jumlah = jumlahBaru;
                }
                return;
            }
        }

        items.add(new ItemKeranjang(idMenu, namaMenu, harga, jumlah, stok));
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
