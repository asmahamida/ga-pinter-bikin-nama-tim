:::writing{variant="document" id="73102"}
# TESTING DOCUMENTATION
## Jatinangor UMKM Hub

### 1. Informasi Pengujian

| Komponen | Keterangan |
|---|---|
| Nama aplikasi | Jatinangor UMKM Hub |
| Jenis pengujian | Functional Testing |
| Metode | Black Box Testing |
| Platform | Desktop Application |
| Bahasa pemrograman | Java |
| Framework antarmuka | Java Swing |
| Database | MySQL / MariaDB |
| Database environment | Laragon |
| IDE | Apache NetBeans |

### 2. Tujuan Pengujian

Pengujian dilakukan untuk memastikan bahwa setiap fitur aplikasi berjalan sesuai spesifikasi, input pengguna divalidasi dengan benar, hak akses setiap role diterapkan, dan proses penyimpanan maupun pengambilan data berjalan sesuai rancangan.

### 3. Lingkungan Pengujian

| Komponen | Lingkungan |
|---|---|
| Operating System | Windows |
| IDE | Apache NetBeans |
| Java Development Kit | Sesuaikan dengan versi JDK yang digunakan |
| Database Server | MySQL / MariaDB melalui Laragon |
| Database Connector | JDBC |
| Database Management Tool | phpMyAdmin / HeidiSQL |

Catatan: Versi perangkat lunak dan konfigurasi aktual perlu dicatat ketika pengujian dilakukan.

### 4. Skenario Pengujian

#### 4.1 Pengujian Login

| ID | Skenario | Input | Hasil yang Diharapkan |
|---|---|---|---|
| TC-01 | Login SuperAdmin | Akun SuperAdmin valid | Masuk ke dashboard SuperAdmin |
| TC-02 | Login Admin UMKM | Akun Admin UMKM valid | Masuk ke dashboard Admin UMKM |
| TC-03 | Login Pembeli | Akun Pembeli valid | Masuk ke dashboard Pembeli |
| TC-04 | Login dengan password salah | Password tidak sesuai | Sistem menampilkan pesan kesalahan |
| TC-05 | Login dengan input kosong | Username atau password kosong | Sistem meminta pengguna melengkapi input |

#### 4.2 Pengujian Manajemen Menu

| ID | Skenario | Input | Hasil yang Diharapkan |
|---|---|---|---|
| TC-06 | Menambah menu | Data menu valid | Menu tersimpan di database |
| TC-07 | Mengubah menu | Data menu diperbarui | Perubahan tersimpan |
| TC-08 | Menghapus menu | Memilih menu yang tersedia | Menu terhapus sesuai aturan sistem |
| TC-09 | Input harga tidak valid | Harga berisi huruf | Sistem menolak input dan menampilkan pesan |

#### 4.3 Pengujian Batch PO

| ID | Skenario | Input | Hasil yang Diharapkan |
|---|---|---|---|
| TC-10 | Membuat Batch PO | Data periode dan kuota valid | Batch PO tersimpan |
| TC-11 | Mengubah Batch PO | Data Batch PO diperbarui | Perubahan tersimpan |
| TC-12 | Input tanggal tidak valid | Tanggal tidak sesuai aturan | Sistem menolak input |
| TC-13 | Input kuota tidak valid | Kuota berupa teks atau nilai tidak valid | Sistem menampilkan pesan kesalahan |

#### 4.4 Pengujian Pemesanan

| ID | Skenario | Input | Hasil yang Diharapkan |
|---|---|---|---|
| TC-14 | Melihat katalog | Membuka halaman katalog | Daftar UMKM dan produk ditampilkan |
| TC-15 | Melakukan pemesanan | Produk dan jumlah valid | Pesanan tercatat di database |
| TC-16 | Jumlah pesanan tidak valid | Jumlah kosong atau tidak valid | Sistem menolak pemesanan |
| TC-17 | Melihat riwayat pesanan | Membuka halaman riwayat | Pesanan milik pembeli ditampilkan |

### 5. Format Pencatatan Hasil Pengujian

Status pengujian hanya boleh diisi setelah skenario benar-benar dijalankan.

| Status | Arti |
|---|---|
| Belum Diuji | Pengujian belum dilakukan |
| Berhasil | Hasil aktual sesuai hasil yang diharapkan |
| Gagal | Hasil aktual tidak sesuai hasil yang diharapkan |
| Perlu Perbaikan | Ditemukan masalah yang perlu ditindaklanjuti |

### 6. Tabel Hasil Pengujian

| ID | Hasil Aktual | Status | Bukti |
|---|---|---|---|
| TC-01 | Belum diuji | Belum Diuji | - |
| TC-02 | Belum diuji | Belum Diuji | - |
| TC-03 | Belum diuji | Belum Diuji | - |
| TC-04 | Belum diuji | Belum Diuji | - |
| TC-05 | Belum diuji | Belum Diuji | - |
| TC-06 | Belum diuji | Belum Diuji | - |
| TC-07 | Belum diuji | Belum Diuji | - |
| TC-08 | Belum diuji | Belum Diuji | - |
| TC-09 | Belum diuji | Belum Diuji | - |
| TC-10 | Belum diuji | Belum Diuji | - |
| TC-11 | Belum diuji | Belum Diuji | - |
| TC-12 | Belum diuji | Belum Diuji | - |
| TC-13 | Belum diuji | Belum Diuji | - |
| TC-14 | Belum diuji | Belum Diuji | - |
| TC-15 | Belum diuji | Belum Diuji | - |
| TC-16 | Belum diuji | Belum Diuji | - |
| TC-17 | Belum diuji | Belum Diuji | - |

### 7. Dokumentasi Bukti Pengujian

Untuk setiap pengujian, simpan bukti berupa:
- Screenshot halaman sebelum pengujian.
- Screenshot input yang digunakan.
- Screenshot hasil setelah pengujian.
- Catatan pesan kesalahan jika pengujian gagal.
- Catatan perubahan database jika relevan.

Contoh struktur folder:

```text
JatinangorUMKM/
├── docs/
│   ├── FEATURE_SPECIFICATION.md
│   └── TESTING_DOCUMENTATION.md
└── screenshots/
    └── testing/
        ├── login/
        ├── menu/
        ├── batch-po/
        └── pemesanan/
```

### 8. Kesimpulan Pengujian

Kesimpulan akhir dibuat setelah seluruh skenario pengujian dijalankan. Aplikasi dapat dinyatakan memenuhi kebutuhan pengujian apabila seluruh fitur utama berjalan sesuai spesifikasi, validasi input bekerja, hak akses diterapkan dengan benar, dan tidak terdapat kesalahan kritis yang belum diselesaikan.
:::