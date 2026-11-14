# FEATURE SPECIFICATION
## Jatinangor UMKM Hub

### 1. Informasi Umum

| Komponen | Keterangan |
|---|---|
| Nama aplikasi | Jatinangor UMKM Hub |
| Jenis aplikasi | Desktop Application |
| Tujuan | Memfasilitasi pengelolaan UMKM dan pemesanan produk di wilayah Jatinangor |
| Bahasa pemrograman | Java |
| Antarmuka | Java Swing |
| Database | MySQL / MariaDB |
| Database connector | JDBC |
| Lingkungan database lokal | Laragon |

### 2. Tujuan Sistem

Jatinangor UMKM Hub dirancang sebagai aplikasi yang menghubungkan pengelola sistem, pelaku UMKM, dan pembeli dalam satu platform. Aplikasi ini diharapkan membantu pengelolaan informasi UMKM, produk, periode pemesanan, serta transaksi pemesanan secara terstruktur.

### 3. Jenis Pengguna dan Hak Akses

#### 3.1 SuperAdmin
SuperAdmin memiliki hak akses untuk mengelola dan memantau sistem secara keseluruhan.

Fitur:
- Login sebagai SuperAdmin.
- Melihat dashboard utama.
- Melihat dan mengelola data pengguna.
- Mengelola data UMKM.
- Memantau data menu atau produk.
- Memantau Batch PO.
- Melihat data pesanan.

#### 3.2 Admin UMKM
Admin UMKM bertanggung jawab mengelola informasi toko dan produk milik UMKM yang bersangkutan.

Fitur:
- Login sebagai Admin UMKM.
- Melihat dashboard UMKM.
- Mengelola profil UMKM.
- Menambahkan data menu atau produk.
- Mengubah data menu atau produk.
- Menghapus data menu atau produk.
- Membuat dan mengelola Batch PO.
- Melihat pesanan yang masuk.

#### 3.3 Pembeli
Pembeli menggunakan aplikasi untuk melihat produk dan melakukan pemesanan.

Fitur:
- Registrasi akun.
- Login sebagai Pembeli.
- Melihat katalog UMKM.
- Melihat daftar produk atau menu.
- Melihat informasi Batch PO.
- Melakukan pemesanan produk.
- Melihat riwayat dan status pesanan.

### 4. Spesifikasi Fitur

| ID | Nama Fitur | Deskripsi | Pengguna |
|---|---|---|---|
| F-01 | Registrasi | Membuat akun pengguna baru | Pembeli |
| F-02 | Login | Memverifikasi akun dan menentukan akses berdasarkan role | Semua pengguna |
| F-03 | Dashboard | Menampilkan informasi sesuai hak akses | Semua pengguna |
| F-04 | Manajemen Pengguna | Mengelola data pengguna | SuperAdmin |
| F-05 | Manajemen UMKM | Mengelola data UMKM | SuperAdmin |
| F-06 | Manajemen Menu | Menambah, melihat, mengubah, dan menghapus menu | Admin UMKM |
| F-07 | Manajemen Batch PO | Mengatur periode dan informasi pemesanan | Admin UMKM |
| F-08 | Katalog Produk | Menampilkan UMKM dan produk yang tersedia | Pembeli |
| F-09 | Pemesanan | Mencatat pesanan pembeli | Pembeli |
| F-10 | Riwayat Pesanan | Menampilkan data pesanan yang pernah dilakukan | Pembeli |
| F-11 | Monitoring Pesanan | Melihat pesanan yang masuk | Admin UMKM, SuperAdmin |

### 5. Alur Penggunaan Umum

1. Pengguna membuka aplikasi.
2. Pengguna melakukan registrasi jika belum memiliki akun.
3. Pengguna melakukan login.
4. Sistem memvalidasi informasi akun.
5. Sistem mengarahkan pengguna ke dashboard berdasarkan role.
6. Pengguna menjalankan fitur sesuai hak akses.
7. Sistem memproses dan menyimpan perubahan data ke database.
8. Pengguna melakukan logout.

### 6. Aturan dan Validasi Sistem

- Username dan password harus diisi saat login.
- Sistem harus menolak login dengan kredensial yang tidak sesuai.
- Setiap pengguna hanya dapat mengakses fitur sesuai role.
- Data wajib tidak boleh kosong.
- Harga dan kuota harus menggunakan nilai numerik yang valid.
- Tanggal Batch PO harus mengikuti aturan tanggal yang ditetapkan sistem.
- Sistem harus memberikan pesan ketika proses penyimpanan, perubahan, atau penghapusan berhasil maupun gagal.
- Pemesanan harus mengacu pada produk dan Batch PO yang valid.

### 7. Kebutuhan Nonfungsional

| Aspek | Kebutuhan |
|---|---|
| Usability | Antarmuka mudah dipahami oleh pengguna |
| Security | Password tidak disimpan dalam bentuk teks biasa; akses dibatasi berdasarkan role |
| Reliability | Sistem menampilkan pesan ketika terjadi kesalahan |
| Data integrity | Data yang disimpan harus konsisten dengan relasi database |
| Maintainability | Kode dipisahkan ke dalam package sesuai tanggung jawabnya |
| Compatibility | Aplikasi dapat dijalankan pada lingkungan Java yang sesuai |

### 8. Catatan Pengembangan

Dokumen ini merupakan spesifikasi rancangan awal. Nama fitur, hak akses, dan aturan bisnis harus disesuaikan kembali dengan ERD, kebutuhan tugas, serta implementasi aktual. Fitur yang belum dibuat tidak boleh dianggap sudah tersedia.