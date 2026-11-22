# POJAT

### Platform Pemesanan Produk UMKM Jatinangor

**Pesan Lebih Mudah, Dukung UMKM Jatinangor!**

POJAT (Platform Pemesanan Produk UMKM Jatinangor) merupakan aplikasi desktop yang dirancang untuk memfasilitasi pengelolaan UMKM dan pemesanan produk UMKM di wilayah Jatinangor.

Aplikasi ini menghubungkan tiga jenis pengguna, yaitu SuperAdmin, Admin UMKM, dan Pembeli, dalam satu sistem yang terintegrasi. Melalui POJAT, pengelolaan informasi UMKM, produk, periode pemesanan, dan transaksi diharapkan dapat dilakukan secara lebih terstruktur dan efisien.
---
## 1. Background

UMKM memiliki peran penting dalam aktivitas ekonomi lokal, termasuk di wilayah Jatinangor. Namun, pengelolaan informasi usaha, produk, periode pemesanan, serta transaksi dapat menjadi kurang praktis apabila dilakukan secara terpisah atau masih manual. Pembeli juga membutuhkan cara yang lebih mudah untuk menemukan produk UMKM dan melakukan pemesanan.

POJAT dikembangkan sebagai aplikasi desktop yang mempertemukan pengelola UMKM dan pembeli dalam satu platform. Sistem ini menyediakan pengelolaan data UMKM dan produk, informasi Batch PO, serta proses pemesanan dan pemantauan pesanan. Dengan dukungan aplikasi terintegrasi, POJAT diharapkan dapat membantu proses operasional UMKM menjadi lebih terstruktur dan memudahkan pembeli dalam melakukan pemesanan produk.

## 2. User Roles

POJAT memiliki tiga jenis pengguna dengan hak akses yang berbeda.

### 2.1 SuperAdmin

Bertanggung jawab mengelola dan memantau sistem secara keseluruhan.

- Mengakses dashboard SuperAdmin.

- Mengelola data pengguna.

- Mengelola data UMKM.

- Memantau data produk.

- Memantau Batch PO.

- Melihat data pesanan.

### 2.2 Admin UMKM

Bertanggung jawab mengelola toko dan produk UMKM masing-masing.

- Mengakses dashboard Admin UMKM.

- Mengelola profil UMKM.

- Menambahkan produk atau menu.

- Mengubah informasi produk.

- Menghapus produk.

- Membuat dan mengelola Batch PO.

- Melihat pesanan yang masuk.

### 2.3 Pembeli

Menggunakan aplikasi untuk menemukan dan memesan produk UMKM.

- Melakukan registrasi dan login.

- Melihat katalog UMKM.

- Melihat produk yang tersedia.

- Melihat informasi Batch PO.

- Melakukan pemesanan.

- Melihat riwayat dan status pesanan.

## 3. ERD (Entity Relationship Diagram)

ERD berikut merupakan gambaran konseptual hubungan enam tabel utama POJAT. Detail kolom, nama foreign key, dan kardinalitas perlu disesuaikan kembali dengan skema database aktual.

```mermaid
erDiagram
    USERS ||--o| UMKM : "akun pengelola"
    UMKM ||--o{ MENU_ITEM : "memiliki produk"
    UMKM ||--o{ BATCH_PO : "membuat periode PO"
    USERS ||--o{ PESANAN : "melakukan pesanan"
    BATCH_PO ||--o{ PESANAN : "periode pesanan"
    PESANAN ||--|{ DETAIL_PESANAN : "memiliki rincian"
    MENU_ITEM ||--o{ DETAIL_PESANAN : "item dalam detail"

    USERS {
        int id_user PK
        string username
        string password
        string nama_lengkap
        string no_hp
        string role
    }
    UMKM {
        int id_umkm PK
        int id_user FK
    }
    MENU_ITEM {
        int id_menu PK
        int id_umkm FK
    }
    BATCH_PO {
        int id_batch PK
        int id_umkm FK
    }
    PESANAN {
        int id_pesanan PK
        int id_user FK
        int id_batch FK
    }
    DETAIL_PESANAN {
        int id_detail PK
        int id_pesanan FK
        int id_menu FK
    }
```

## 🔄 4. User Flow

```mermaid
flowchart TD
    A([Mulai]) --> B[Launch POJAT]
    B --> C[Login / Registrasi]
    C --> D{Akun valid?}
    D -- Tidak --> E[Tampilkan pesan gagal]
    E --> C
    D -- Ya --> F{Role}
    F -- SUPERADMIN --> G[Dashboard SuperAdmin]
    F -- UMKM --> H[Dashboard Admin UMKM]
    F -- PEMBELI --> I[Dashboard Pembeli]

    G --> G1[Kelola Pengguna]
    G --> G2[Kelola UMKM]
    G --> G3[Kelola Produk/Menu]
    G --> G4[Kelola Batch PO]
    G --> G5[Monitor Pesanan]

    H --> H1[Kelola Profil UMKM]
    H --> H2[Kelola Produk/Menu]
    H --> H3[Kelola Batch PO]
    H --> H4[Kelola Pesanan Masuk]

    I --> I1[Lihat Katalog]
    I1 --> I2[Pilih Produk dan Batch PO]
    I2 --> I3[Masukkan ke Keranjang]
    I3 --> I4[Checkout / Buat Pesanan]
    I4 --> I5[Lihat Riwayat dan Status Pesanan]

    G1 --> J[Logout]
    G2 --> J
    G3 --> J
    G4 --> J
    G5 --> J
    H1 --> J
    H2 --> J
    H3 --> J
    H4 --> J
    I5 --> J
    J --> K([Selesai])
```

## 5. Class Diagram

```mermaid
classDiagram
    direction TB

    class User {
        -int idUser
        -String username
        -String password
        -String namaLengkap
        -String noHp
        -String role
    }
    class UMKM {
        -int idUmkm
        -String namaUsaha
    }
    class MenuItem {
        -int idMenu
        -String namaMenu
        -double harga
    }
    class BatchPO {
        -int idBatch
        -Date tanggalMulai
        -Date tanggalSelesai
    }
    class Pesanan {
        -int idPesanan
        -Date tanggalPesanan
        -String status
    }
    class DetailPesanan {
        -int idDetail
        -int jumlah
        -double subtotal
    }

    class UserDAO
    class UMKMDAO
    class MenuItemDAO
    class BatchPODAO
    class PesananDAO
    class LoginController
    class UMKMController
    class MenuController
    class PesananController

    class LoginFrame
    class DashboardAdmin
    class DashboardUMKM
    class DashboardPembeli
    class KatalogMenuFrame
    class KeranjangFrame
    class RiwayatPesananFrame

    User "1" --> "0..1" UMKM : akun pengelola
    UMKM "1" --> "*" MenuItem : mengelola
    UMKM "1" --> "*" BatchPO : membuat
    User "1" --> "*" Pesanan : melakukan
    BatchPO "1" --> "*" Pesanan : periode
    Pesanan "1" --> "*" DetailPesanan : terdiri dari
    DetailPesanan "*" --> "1" MenuItem : item pesanan

    UserDAO ..> User : data
    UMKMDAO ..> UMKM : data
    MenuItemDAO ..> MenuItem : data
    BatchPODAO ..> BatchPO : data
    PesananDAO ..> Pesanan : data
    PesananDAO ..> DetailPesanan : data detail

    LoginFrame ..> LoginController : login
    DashboardAdmin ..> UMKMController : aksi admin
    DashboardUMKM ..> MenuController : aksi UMKM
    KatalogMenuFrame ..> PesananController : pemesanan
    KeranjangFrame ..> PesananController : checkout
    RiwayatPesananFrame ..> PesananController : riwayat
```