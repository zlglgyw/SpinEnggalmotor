# Roda Keberuntungan Enggal Motor

Aplikasi Android **offline** untuk undian nama dengan roda keberuntungan.
Dibuat untuk Enggal Motor — tema promo "PEMBELIAN OLI FEDERAL GRATIS HADIAH!".

- **Paket:** `com.enggalmotor.rodakeberuntungan`
- **Teknologi:** Kotlin + Jetpack Compose, satu Activity, tanpa server.
- **Offline saja:** tidak ada izin internet, tidak ada Firebase, tidak ada login, tidak ada iklan.
- **Ringan:** tidak ada library berat selain AndroidX / Compose bawaan.

## Fitur

- Logo Enggal Motor (botol oli merah, tanpa lubang pegangan) dipakai sebagai
  ikon launcher **dan** gambar header di dalam aplikasi. Logo ini bukan logo
  resmi Federal Oil — hanya tulisan polos "OLI FEDERAL" di botolnya.
- Latar gradient biru gelap, border roda emas, lampu kelap-kelip di sekeliling roda.
- Judul **RODA KEBERUNTUNGAN** dan subjudul **PEMBELIAN OLI FEDERAL GRATIS HADIAH!**
- Roda dinamis dari daftar nama: warna segmen bergantian merah / oranye /
  kuning / biru / biru gelap. Nama tampil **lengkap** dengan huruf tebal putih
  di setiap segmen (bukan 1 huruf).
- Tombol **PUTAR** besar, gradient oranye, dengan bayangan. Putaran melambat
  (deceleration) dan berhenti acak di satu nama. Pemenang tampil di popup
  dengan confetti dan nama besar, tombol **Putar Lagi** dan **Tutup**.
- Atur nama **hanya** lewat tombol floating kecil (FAB 56dp, ikon pensil)
  di pojok kanan bawah **layar** — terpisah dari tombol PUTAR dan tidak
  menimpanya. Ketuk FAB untuk membuka bottom sheet kecil (bukan halaman penuh):
  tambah nama, hapus satu nama, hapus semua nama, dan lihat jumlah nama.
- Nama tersimpan lokal di HP (SharedPreferences) dan tidak hilang saat
  aplikasi ditutup. Daftar awal: Budi, Andi, Sari, Joko, Dewi, Rina.

## Cara build APK lewat GitHub Actions (gratis, tanpa PC / kabel)

1. Buat repo baru di GitHub, lalu push seluruh isi folder ini ke branch `main`
   (atau buat repo dari tombol **GitHub** di Google AI Studio, lalu ganti isinya
   dengan project ini).
2. Buka tab **Actions** di repo. Setiap push ke `main` (atau jalankan manual
   lewat **Run workflow**) akan menjalankan workflow `.github/workflows/android.yml`:
   checkout → setup Java 17 (Temurin) → kembalikan logo PNG dari file
   `logo_enggal_motor.png.b64` (logo disimpan sebagai teks base64 di repo)
   → setup Gradle 8.7 → `gradle assembleDebug` → upload artifact bernama `apk`.
3. Setelah build selesai (tanda hijau), buka hasil run tersebut, scroll ke
   **Artifacts**, download `apk` (file ZIP). Di dalamnya ada file `.apk`.
4. Ekstrak ZIP-nya di HP (misalnya pakai ZArchiver), ketuk file `.apk` untuk
   menginstal. Izinkan "Install dari sumber tidak dikenal" kalau diminta.

Catatan: build pertama bisa memakan waktu beberapa menit karena GitHub
mengunduh Android SDK dan dependency Gradle untuk pertama kalinya.

## Cara build lokal (opsional)

Butuh Android SDK + JDK 17 di komputer:

```sh
./gradlew assembleDebug
# hasil: app/build/outputs/apk/debug/app-debug.apk
```

## Cara install

- Dari GitHub Actions seperti di atas, atau
- dari Android Studio: buka project ini, lalu **Run** ke HP yang tersambung.

Aplikasi hanya untuk pemakaian sendiri di HP. Tidak dipublikasikan ke Play Store.
