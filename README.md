# Jatinangor UMKM Hub

**Aplikasi Bisnis UMKM di Daerah Jatinangor**

Jatinangor UMKM Hub merupakan aplikasi desktop berbasis Java yang dirancang untuk membantu digitalisasi proses bisnis UMKM di daerah Jatinangor. Aplikasi ini menyediakan sistem pengelolaan UMKM, katalog produk, pemesanan berbasis Pre-Order (PO), serta pengelolaan transaksi yang terintegrasi dengan database.

---

## 1. Informasi Proyek

| Keterangan | Detail |
|---|---|
| Nama Proyek | Jatinangor UMKM Hub |
| Tema | Bisnis UMKM di Daerah Jatinangor |
| Jenis Aplikasi | Desktop Application |
| Bahasa Pemrograman | Java |
| GUI Framework | Java Swing |
| Database | MySQL / MariaDB |
| Database Management | Laragon |
| Database Interface | phpMyAdmin / HeidiSQL |
| Database Connectivity | JDBC |
| IDE | Apache NetBeans |
| Version Control | Git & GitHub |
| Anggota Tim | 3 orang |

## 2. Tujuan Proyek

Aplikasi ini dikembangkan untuk:

1. Membantu UMKM di daerah Jatinangor memasarkan produk secara digital.
2. Mempermudah pengelolaan katalog menu dan informasi produk.
3. Menyediakan sistem pemesanan berbasis Batch Pre-Order.
4. Mempermudah pembeli dalam melihat produk dan melakukan pemesanan.
5. Membantu pengelolaan data pengguna, UMKM, menu, batch, dan transaksi secara terintegrasi.
6. Menerapkan konsep Object-Oriented Programming (OOP), Java Swing, dan integrasi database.

## 3. Teknologi yang Digunakan

| Teknologi | Fungsi |
|---|---|
| Java | Bahasa pemrograman utama |
| Java Swing | Membangun antarmuka aplikasi |
| Apache NetBeans | IDE untuk pengembangan |
| MySQL / MariaDB | Sistem manajemen database |
| Laragon | Menjalankan database server lokal |
| JDBC | Menghubungkan aplikasi Java dengan database |
| Git | Mengelola versi kode |
| GitHub | Menyimpan repository proyek |

## 4. Fitur dan Hak Akses Pengguna

Aplikasi memiliki tiga jenis role pengguna.

### A. SuperAdmin

SuperAdmin bertugas mengelola dan memantau keseluruhan sistem.

Fitur:
- Login SuperAdmin.
- Melihat dashboard utama.
- Mengelola data pengguna.
- Mengelola data UMKM.
- Memantau data Batch PO.
- Melihat data pesanan.
- Mengelola informasi sistem.

### B. Admin UMKM

Admin UMKM bertugas mengelola operasional toko masing-masing.

Fitur:
- Login Admin UMKM.
- Mengelola profil UMKM.
- Menambahkan menu.
- Mengubah informasi menu.
- Menghapus menu.
- Mengelola Batch PO.
- Melihat pesanan masuk.
- Memantau kuota dan status Batch PO.

### C. Pembeli

Pembeli menggunakan aplikasi untuk mencari produk dan melakukan pemesanan.

Fitur:
- Registrasi akun.
- Login pembeli.
- Melihat katalog UMKM.
- Melihat katalog menu.
- Melihat informasi Batch PO.
- Melakukan pemesanan.
- Melihat riwayat pesanan.
- Melihat status pesanan.

## 5. Perancangan Database

Database yang digunakan bernama:

`db_umkm_jatinangor`

Database dirancang menggunakan enam tabel utama yang saling berelasi.

### A. Tabel users

Menyimpan informasi seluruh pengguna aplikasi.

| Field | Tipe Data | Keterangan |
|---|---|---|
| id_user | INT | Primary Key |
| username | VARCHAR | Username pengguna |
| password | VARCHAR | Password pengguna |
| nama_lengkap | VARCHAR | Nama lengkap |
| no_hp | VARCHAR | Nomor HP |
| role | VARCHAR | Hak akses pengguna |
| created_at | TIMESTAMP | Waktu pembuatan akun |

### B. Tabel umkm

Menyimpan informasi UMKM yang terdaftar.

| Field | Tipe Data | Keterangan |
|---|---|---|
| id_umkm | INT | Primary Key |
| id_owner | INT | Foreign Key ke users |
| nama_toko | VARCHAR | Nama toko |
| deskripsi | TEXT | Deskripsi UMKM |
| alamat_penjemputan | TEXT | Alamat penjemputan |
| no_rekening | VARCHAR | Nomor rekening |
| bank | VARCHAR | Nama bank |

### C. Tabel menu_item

Menyimpan informasi menu atau produk UMKM.

| Field | Tipe Data | Keterangan |
|---|---|---|
| id_menu | INT | Primary Key |
| id_umkm | INT | Foreign Key ke umkm |
| nama_menu | VARCHAR | Nama menu |
| deskripsi | TEXT | Deskripsi menu |
| harga | DOUBLE | Harga produk |
| stok_per_batch | INT | Stok produk per batch |

### D. Tabel batch_po

Menyimpan informasi periode pemesanan produk.

| Field | Tipe Data | Keterangan |
|---|---|---|
| id_batch | INT | Primary Key |
| id_umkm | INT | Foreign Key ke umkm |
| nama_batch | VARCHAR | Nama batch |
| tgl_buka | TIMESTAMP | Tanggal pembukaan PO |
| tgl_tutup | TIMESTAMP | Tanggal penutupan PO |
| tgl_pengiriman | DATE | Tanggal pengiriman |
| kuota_max | INT | Kuota maksimal |
| status | VARCHAR | Status batch |

### E. Tabel pesanan

Menyimpan informasi transaksi pemesanan pembeli.

| Field | Tipe Data | Keterangan |
|---|---|---|
| id_pesanan | INT | Primary Key |
| kode_traksi | VARCHAR | Kode transaksi |
| id_batch | INT | Foreign Key ke batch_po |
| id_pembeli | INT | Foreign Key ke users |
| tgl_pesan | TIMESTAMP | Waktu pemesanan |
| total_harga | DOUBLE | Total harga |
| metode_pengambilan | VARCHAR | Metode pengambilan |
| bukti_pembayaran | VARCHAR | Bukti pembayaran |
| status_pesanan | VARCHAR | Status pesanan |

### F. Tabel detail_pesanan

Menyimpan rincian produk dalam setiap transaksi.

| Field | Tipe Data | Keterangan |
|---|---|---|
| id_detail | INT | Primary Key |
| id_pesanan | INT | Foreign Key ke pesanan |
| id_menu | INT | Foreign Key ke menu_item |
| jumlah | INT | Jumlah produk |
| subtotal | DOUBLE | Subtotal harga |
| catatan_khusus | TEXT | Catatan pembeli |

## 6. Relasi Antar Tabel

Hubungan antar tabel dalam database:

- users → umkm: satu pengguna pemilik dapat memiliki data UMKM.
- users → pesanan: satu pembeli dapat memiliki banyak pesanan.
- umkm → menu_item: satu UMKM dapat memiliki banyak menu.
- umkm → batch_po: satu UMKM dapat memiliki banyak batch.
- batch_po → pesanan: satu batch dapat memiliki banyak pesanan.
- pesanan → detail_pesanan: satu pesanan dapat memiliki beberapa detail produk.
- menu_item → detail_pesanan: satu menu dapat muncul dalam banyak detail pesanan.

## 7. Struktur Folder Project

Struktur project Java menggunakan pendekatan pemisahan fungsi berdasarkan package.

```text
JatinangorUMKM/
│
├── src/
│   ├── config/
│   │   └── DatabaseConnection.java
│   │
│   ├── model/
│   │   ├── User.java
│   │   ├── UMKM.java
│   │   ├── MenuItem.java
│   │   ├── BatchPO.java
│   │   ├── Pesanan.java
│   │   └── DetailPesanan.java
│   │
│   ├── dao/
│   │   ├── UserDAO.java
│   │   ├── UMKMDAO.java
│   │   ├── MenuDAO.java
│   │   └── BatchPODAO.java
│   │
│   ├── view/
│   │   ├── LoginFrame.java
│   │   ├── RegisterFrame.java
│   │   ├── SuperAdminDashboard.java
│   │   ├── UMKMDashboard.java
│   │   └── PembeliDashboard.java
│   │
│   ├── controller/
│   │   ├── LoginController.java
│   │   ├── MenuController.java
│   │   └── BatchPOController.java
│   │
│   ├── util/
│   │   ├── PasswordUtil.java
│   │   └── Validator.java
│   │
│   └── Main.java
│
├── libraries/
│   └── JDBC Connector
│
├── README.md
└── .gitignore
```

Keterangan:

- `config`: konfigurasi koneksi database.
- `model`: representasi objek dan struktur data.
- `dao`: pengelolaan operasi database.
- `view`: tampilan antarmuka pengguna.
- `controller`: penghubung view dan model/DAO.
- `util`: fungsi bantuan dan validasi.
- `Main.java`: titik awal eksekusi aplikasi.

## 8. Instalasi dan Konfigurasi

### A. Persiapan Perangkat

Pastikan perangkat telah memiliki:

1. Java Development Kit (JDK).
2. Apache NetBeans.
3. Laragon.
4. MySQL atau MariaDB.
5. JDBC Connector yang sesuai dengan database.
6. Git.

### B. Menjalankan Database

1. Buka Laragon.
2. Klik Start All atau jalankan layanan database.
3. Pastikan MySQL/MariaDB aktif.
4. Buka HeidiSQL atau phpMyAdmin.
5. Pastikan database `db_umkm_jatinangor` sudah tersedia.
6. Pastikan keenam tabel telah dibuat.

### C. Membuat Project NetBeans

1. Buka Apache NetBeans.
2. Pilih File → New Project.
3. Pilih Java Application.
4. Beri nama project `JatinangorUMKM`.
5. Buat package sesuai struktur project.

### D. Memasang JDBC Connector

1. Unduh JDBC Connector sesuai database yang digunakan.
2. Buka project di NetBeans.
3. Klik kanan Libraries.
4. Pilih Add JAR/Folder.
5. Pilih file JDBC Connector berekstensi `.jar`.
6. Pastikan connector muncul pada Libraries.

### E. Konfigurasi DatabaseConnection.java

Atur informasi koneksi sesuai konfigurasi Laragon.

Contoh konfigurasi MariaDB:

```java
private static final String URL =
    "jdbc:mariadb://localhost:3306/db_umkm_jatinangor";

private static final String USER = "root";

private static final String PASSWORD = "";
```

Untuk MySQL, gunakan URL:

```java
jdbc:mysql://localhost:3306/db_umkm_jatinangor
```

Sesuaikan port, username, dan password dengan konfigurasi database lokal.

### F. Menguji Koneksi

Jalankan class `TestConnection.java`.

Jika berhasil, console akan menampilkan:

```text
KONEKSI DATABASE BERHASIL!
```

## 9. Alur Sistem Aplikasi

### A. Alur Login

1. Pengguna membuka aplikasi.
2. Pengguna memasukkan username dan password.
3. Sistem melakukan validasi akun.
4. Sistem memeriksa role pengguna.
5. Pengguna diarahkan ke dashboard sesuai hak akses.

### B. Alur Pengelolaan Menu

1. Admin UMKM login.
2. Admin membuka tab Kelola Menu.
3. Admin memasukkan informasi produk.
4. Sistem melakukan validasi.
5. DAO menjalankan perintah SQL.
6. Database menyimpan perubahan.
7. JTable diperbarui sesuai data terbaru.

### C. Alur Batch PO

1. Admin UMKM membuka tab Batch PO.
2. Admin membuat batch baru.
3. Admin menentukan periode pembukaan dan penutupan.
4. Admin menentukan tanggal pengiriman.
5. Admin memasukkan kuota maksimal.
6. Sistem memvalidasi informasi.
7. Data batch disimpan ke database.

### D. Alur Pemesanan

1. Pembeli membuka katalog.
2. Pembeli memilih produk.
3. Pembeli memilih batch yang tersedia.
4. Pembeli menentukan jumlah produk.
5. Sistem menghitung total harga.
6. Pembeli melakukan konfirmasi pesanan.
7. Sistem menyimpan transaksi dan detail pesanan.
8. Status pesanan dapat dipantau melalui aplikasi.

## 10. Pembagian Tugas Pengembangan

### Jasmine — Lead Back-End & Database

Tanggung jawab:
- Membuat database MySQL.
- Mengembangkan Model Java.
- Membuat koneksi database.
- Mengembangkan DAO.
- Mengimplementasikan fungsi CRUD.
- Mengelola query transaksi dan database.

### Asma — Lead Front-End & UI

Tanggung jawab:
- Menyiapkan project NetBeans.
- Membuat struktur folder.
- Mendesain Login dan Register.
- Membuat dashboard tiga role.
- Membuat UI katalog dan pengelolaan menu.
- Membuat UI Batch PO.
- Melakukan polishing tampilan aplikasi.

### Raffa — Controller, QA & Documentation

Tanggung jawab:
- Mengembangkan Controller.
- Membuat SQL Seeder dan Data Dummy.
- Mengimplementasikan validasi role.
- Mengembangkan validasi input.
- Melakukan integration testing.
- Melakukan stress testing.
- Menyusun laporan dan slide presentasi.

## 11. Version Control dengan GitHub

Repository menggunakan Git untuk mengelola perubahan kode.

Contoh perintah:

```bash
# Melihat status perubahan
git status

# Menambahkan perubahan
git add .

# Membuat commit
git commit -m "Update project JatinangorUMKM"

# Mengirim perubahan ke GitHub
git push origin main
```

Branch yang direncanakan:

- `main`: versi stabil aplikasi.
- `feature/backend`: pengembangan database dan DAO.
- `feature/frontend`: pengembangan UI.
- `feature/controller`: pengembangan controller dan validasi.

Setiap perubahan sebaiknya diuji sebelum digabungkan ke branch utama.

## 12. Target Pengembangan

| Periode | Target |
|---|---|
| 5–8 November 2026 | Setup database, project, login/register, data dummy |
| 9–12 November 2026 | DAO, CRUD menu, Batch PO, validasi |
| 13–16 November 2026 | Katalog pembeli, konfirmasi pesanan, transaksi |
| 17–20 November 2026 | Optimasi, polishing UI, testing dan dokumentasi |
| 21 November 2026 | Finalisasi dan presentasi |

## 13. Status Pengembangan

Status berikut merupakan target pekerjaan dan perlu diperbarui berdasarkan implementasi aktual.

- [ ] Database dan relasi tabel.
- [ ] JDBC Connector dan koneksi database.
- [ ] Model seluruh entitas.
- [ ] DAO dan fungsi CRUD.
- [ ] Login multi-role.
- [ ] Register pembeli.
- [ ] Dashboard SuperAdmin.
- [ ] Dashboard UMKM.
- [ ] Dashboard Pembeli.
- [ ] UI Kelola Menu.
- [ ] UI Batch PO.
- [ ] Validasi Role dan Input.
- [ ] Katalog Pembeli.
- [ ] Konfirmasi Pesanan.
- [ ] Integrasi transaksi.
- [ ] Integration Testing.
- [ ] Dokumentasi dan presentasi.

## 14. Pengembangan Selanjutnya

Pengembangan aplikasi akan difokuskan pada integrasi seluruh fitur, peningkatan keamanan, pengujian transaksi, serta penyempurnaan antarmuka agar aplikasi dapat digunakan dengan baik oleh SuperAdmin, Admin UMKM, dan Pembeli.

---

**Jatinangor UMKM Hub**

*Empowering Local UMKM Through Digital Innovation.*
