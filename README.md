
# 🛒 WarungKu POS v2.0 - Sistem Kasir & Manajemen Stok Toko (Android & iOS Web)

![Android](https://img.shields.io/badge/Android-Native-green?style=for-the-badge&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-Jetpack%20Compose-purple?style=for-the-badge&logo=kotlin)
![Room DB](https://img.shields.io/badge/RoomDB-Offline%20First-blue?style=for-the-badge&logo=sqlite)
![PWA](https://img.shields.io/badge/iOS%20Web-PWA%20Ready-orange?style=for-the-badge&logo=apple)
![License](https://img.shields.io/badge/License-PRO%20Version-red?style=for-the-badge)

**WarungKu POS v2.0** adalah aplikasi Point of Sale (POS), Manajemen Kasir, dan Kelola Stok Toko ritel modern, cepat, dan responsif. Dirancang khusus untuk usaha UMKM, Minimarket, Toko Kelontong, dan Ritel Modern dengan dukungan offline-first tanpa memerlukan koneksi internet stabil.

---

## 🌟 Fitur Unggulan

### 🛍️ 1. Modul Kasir & Transaksi POS
- **Pencarian Cepat**: Cari barang berdasarkan nama, SKU, atau **Pindai Barcode Kamera/Scanner**.
- **Keranjang Belanja Multi-Item**: Penyesuaian Qty instan, diskon persen/nominal, dan potongan harga.
- **Perhitungan Kembalian Akurat**: Kalkulator otomatis mencegah kesalahan hitung pecahan uang.
- **Cetak Struk Bluetooth Thermal**: Mendukung printer Bluetooth 58mm & 80mm (*RPP02N, PT-210, MTP-II, dll*) menggunakan *Triple Fallback Socket Connection*.
- **Kirim Struk Digital via WhatsApp**: Kirim rincian nota belanja langsung ke HP pelanggan via WhatsApp.

### 📦 2. Manajemen Stok, Kategori, Merek & Gudang
- **Sistem Kategori & Merek**: Pengelompokan barang jualan agar rapi dan mudah dicari.
- **Peringatan Stok Menipis (*Low Stock Warning*)**: Indikator stok kritis untuk kulakan tepat waktu.
- **Log Penerimaan Stok Baru (*Weighted Moving Average Cost*)**: Perhitungan akuntansi harga modal rata-rata tertimbang secara otomatis saat pembelian stok baru dengan harga modal naik/berbeda.

### 📊 3. Laporan Penjualan & Keuangan
- **Dashboard Omzet Real-Time**: Ringkasan total pendapatan, jumlah transaksi, dan barang terlaris.
- **Filter Tanggal Kustom**: Laporan harian, mingguan, bulanan, atau rentang tanggal tertentu.
- **Ekspor Laporan PDF & CSV**: Unduh berkas laporan keuangan PDF (Format A4) dan CSV/Excel langsung ke folder `Download` penyimpanan internal HP.

### 💾 4. Keamanan & Cadangkan Data (.json)
- **Backup & Restore (.json)**: Cadangkan seluruh data produk, transaksi, dan kategori tanpa internet.
- **Pop-up Inspeksi Metadata**: Menampilkan rincian tanggal file, total produk, dan kategori sebelum eksekusi restore.
- **Screen Loading Animasi Layar Penuh**: Indikator progress hitungan data live (`100%`) saat pemulihan ribuan data agar aman dari penutupan paksa aplikasi.

### 🌐 5. Lintas Platform (iOS iPhone, iPad & Web PWA)
- Dilengkapi berkas Web PWA (`warungku_pos_web.html` & `index.html`) yang dapat langsung diunggah ke hosting gratis (*Netlify*, *Vercel*, *GitHub Pages*).
- Pengguna **iPhone & iPad** dapat membuka di browser Safari dan menekan *"Add to Home Screen"* untuk menginstal aplikasi tanpa biaya Apple Developer.

---

## 🔑 Kredensial Akun Default & Developer

| Peran / Akses | Email / Username | PIN / Password | Akses Fitur |
| :--- | :--- | :--- | :--- |
| 👑 **Administrator** | `admin@pos.com` *(atau `admin`)* | **`1234`** | Akses Penuh Kasir, Stok, Laporan, & Pengaturan |
| 🛒 **Kasir Toko** | `kasir@pos.com` *(atau `kasir`)* | **`1111`** | Akses Kasir & Transaksi POS |
| 🔐 **Super Admin Developer** | `yofidewo4@gmail.com` | **`911911`** | Konsol Generator Lisensi PRO & Remote Activation |

---

## 🛠️ Teknologi & Arsitektur Kode

- **Bahasa**: Kotlin (Android Native) & HTML5/JS (Web PWA)
- **UI Framework**: Android Jetpack Compose (Material Design 3)
- **Database**: Room Database (SQLite Engine)
- **State Management**: Android ViewModel & Kotlin StateFlow
- **PDF Engine**: Android `PdfDocument` Native API
- **Threading**: Kotlin Coroutines & Dispatchers.IO

---

## 🎨 Cara Custom & Modifikasi Aplikasi (Customization Guide)

Jika Anda ingin mengubah nama toko, logo, warna tampilan, PIN Developer, atau format cetak struk:

### 1. Mengubah Logo Toko & Halaman Login
- Ganti file gambar logo resmi di lokasi berikut:  
  `app/src/main/res/drawable/app_warungku_logo.jpg`
- Bebas menggunakan format `.jpg` atau `.png` dengan nama file yang sama.

### 2. Mengubah Nama Aplikasi & Judul
- **Nama Aplikasi Android**: Buka `app/src/main/res/values/strings.xml` lalu ubah:
  ```xml
  <string name="app_name">Nama Toko Anda</string>
  ```
- **ID Paket (Package Name)**: Buka `app/build.gradle.kts` lalu sesuaikan `applicationId`:
  ```kotlin
  applicationId = "com.namamerek.pos"
  ```

### 3. Mengubah Palet Warna Tema (Theme Palette)
- Buka file `app/src/main/java/com/yofidewo/pos/ui/theme/Color.kt`
- Ubah kode warna hex berikut sesuai identitas toko Anda:
  ```kotlin
  val PrimaryOrange = Color(0xFFFF6600) // Warna Aksen Utama
  val SlateNavy = Color(0xFF1E2B4D)     // Warna Header & Sekunder
  ```

### 4. Mengubah PIN Master Developer & Akun Verifikasi
- Buka file `app/src/main/java/com/yofidewo/pos/ui/screens/SettingsUsersScreen.kt`
- Cari baris verifikasi PIN dan ubah sesuai kebutuhan Anda:
  ```kotlin
  if (masterPinInput.trim() == "911911") // Ubah PIN sesuai keinginan Anda
  ```

### 5. Mengubah Format Cetak Struk Printer Bluetooth Thermal
- Buka file `app/src/main/java/com/yofidewo/pos/util/PrinterUtil.kt`
- Sesuaikan header toko, alamat, dan teks ucapan di bagian footer nota cetak.

---

## 💻 Panduan Kompilasi & Build Project (Gradle)

### 1. Kompilasi APK Android Release:
```powershell
$env:GRADLE_USER_HOME = "D:\GradleHome"; $env:ANDROID_HOME = "C:\Users\ommul\AndroidSDK"; .\gradlew.bat assembleRelease --no-daemon
```
- **Output Customer APK**: `app/build/outputs/apk/customer/release/app-customer-release.apk`
- **Output Developer APK**: `app/build/outputs/apk/developer/release/app-developer-release.apk`

### 2. Kompilasi App Bundle Google Play Store (.aab):
```powershell
$env:GRADLE_USER_HOME = "D:\GradleHome"; $env:ANDROID_HOME = "C:\Users\ommul\AndroidSDK"; .\gradlew.bat bundleCustomerRelease --no-daemon
```
- **Output Play Store AAB**: `app/build/outputs/bundle/customerRelease/app-customer-release.aab`

---

## 🌐 Cara Hosting Gratis Selamanya via GitHub Pages (Web PWA iOS & Android)

Karena kode aplikasi Web POS (`index.html` dan `warungku_pos_web.html`) sudah di-push ke GitHub, Anda dapat mengaktifkan hosting **GitHub Pages** langsung dari repositori Anda secara gratis selamanya:

1. Buka halaman utama repositori GitHub Anda: [https://github.com/YofiDewoPrayogo/POS-ANDROID-NEW](https://github.com/YofiDewoPrayogo/POS-ANDROID-NEW)
2. Klik tab **Settings** (Pengaturan) di bagian atas repositori.
3. Di menu sebelah kiri, cari bagian *Code and automation*, lalu pilih **Pages**.
4. Pada bagian **Build and deployment**:
   - Source: Biarkan terpilih **Deploy from a branch**.
   - Branch: Pilih **`main`** dan pilih folder **`/(root)`**, kemudian klik **Save**.
5. Tunggu beberapa menit (sekitar 1-3 menit) untuk proses deployment otomatis dari GitHub.
6. Refresh halaman *Pages* tersebut. Anda akan melihat tulisan _"Your site is live at..."_ berserta link URL hosting gratis Anda (Contoh URL: `https://yofidewoprayogo.github.io/POS-ANDROID-NEW/`).
7. Buka link URL tersebut di peramban Safari (iOS) atau Chrome (Android) pada perangkat Anda, lalu klik **"Add to Home Screen"** untuk menginstal Web PWA ini layaknya aplikasi native tanpa App Store.

---

## 👨‍💻 Pengembang (Developer)

- **Nama**: Yofi Dewo Prayogo
- **Email**: yofidewo4@gmail.com
- **WhatsApp Support**: +62 819-2949-1887
- **Repository**: [github.com/YofiDewoPrayogo/POS-ANDROID-NEW](https://github.com/YofiDewoPrayogo/POS-ANDROID-NEW.git)

© 2026 WarungKu POS. All Rights Reserved.
