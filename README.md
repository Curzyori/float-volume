# Float Volume 🔊📱

> **Projects #10 of massive "50 Projects Challenge" — Building 50 real-world projects before September 2026.**
> 
> Pembagian Bahasa / Language Menu:
> - [🇮🇩 Bahasa Indonesia](#-bahasa-indonesia)
> - [🇬🇧 English Version](#-english-version)

---

# 🇮🇩 Bahasa Indonesia

## Deskripsi Singkat
**Float Volume** adalah aplikasi kontrol volume melayang bebas iklan, gratis, dan ramah privasi untuk perangkat Android. Aplikasi ini dirancang khusus untuk mempermudah pengaturan volume sistem tanpa perlu menekan tombol fisik secara langsung, sekaligus menjaga estetika tampilan ponsel Anda tetap bersih dan berkelas.

---

## 🛠️ Masalah & Motivasi (Problem)
Banyak pengguna Android menghadapi masalah tombol fisik volume yang rusak atau aus. Ketika beralih ke aplikasi di Google Play Store untuk mencari solusi alternatif, mereka seringkali menemui kendala:
1. **Iklan yang Sangat Mengganggu**: Aplikasi volume gratisan dipenuhi iklan pop-up dan banner yang merusak kenyamanan penggunaan sehari-hari.
2. **Desain UI/UX yang Buruk & Murahan**: Banyak widget melayang yang ukurannya terlampau besar, tidak rapi, dan merusak tampilan elegan *homescreen* ponsel.
3. **Ada Rasa Gengsi / Malu**: Menampilkan ikon besar bertuliskan "VOLUME" di layar membuat orang lain tahu bahwa ponsel kita sedang dalam kondisi tombol rusak. Pengguna merasa tidak percaya diri saat ponselnya dipinjam atau dilihat orang lain.
4. **Pembatasan Durasi Aktif**: Layanan melayang sering terhenti otomatis dalam latar belakang atau dibatasi waktu aktifnya sehingga memaksa pengguna membuka ulang aplikasi berkali-kali.

---

## 💡 Solusi yang Ditawarkan (Solution)
**Float Volume** hadir untuk menyelesaikan seluruh tantangan di atas dengan pendekatan modern:
* **Antarmuka (UI/UX) yang Sangat Bersih**: Mengadopsi prinsip desain **Material Design 3 (M3)** premium dengan warna *Dark Mode* mewah (*Elegant Deep Indigo* & *Lavender Accent*).
* **Bebas Iklan & 100% Gratis Selamanya**: Tanpa ada modul analitik mencurigakan, penjejak data, atau iklan layar penuh.
* **Fitur Samaran Tombol (Prestige-Safe Stealth Aesthetic)**: Ikon melayang didesain sebagai lingkaran putih polos yang elegan (*Stealth White Circle*). Orang lain akan mengiranya sebagai pintasan navigasi sistem bawaan (*assistive touch* atau alat bantu aksesibilitas) alih-alih penanda tombol volume rusak. Sangat menjaga prestise Anda!
* **Layanan Aktif Selamanya**: Dilengkapi dengan *Foreground Service* bersertifikasi Google Task priority untuk memastikan kontrol melayang tetap berjalan stabil sepanjang waktu tanpa termakan oleh pembersihan RAM otomatis.

---

## ✨ Fitur Utama (Main Features)
* **Kontrol Sekali Ketuk**: Cukup ketuk lingkaran putih halus untuk menampilkan bar geser (*slider*) volume premium di layar secara instan.
* **Samaran Aksesibilitas**: Lingkaran melayang di layar bisa diatur transparansinya atau didesain polos sehingga menyatu sempurna dengan navigasi bawaan sistem Android.
* **Sinkronisasi Tombol Fisik**: Saat Anda menaikkan volume dari panel melayang, sistem Android akan menyesuaikan secara *real-time* dengan transisi animasi yang mulus.
* **Diagnostik Sistem Canggih**: Menyediakan sistem log aktivitas dan pemantau keadaan perizinan (*overlay status*) secara transparan.
* **Ringan & Rendah Konsumsi Baterai**: Tidak ada beban proses latar belakang yang sia-sia, sangat mengutamakan daya tahan baterai ponsel.

---

## 📱 Foto Preview & Tampilan Antarmuka (Screenshots)

Berikut preview antarmuka aplikasi Float Volume dengan rasio aspek layar 9:16:

| Tampilan Utama (Dark M3 Mode) | Desain Bersih & Elegan |
| :---: | :---: |
| <img src="https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=350&q=80" width="280px" alt="Splash Screen Mockup" /> | <img src="https://images.unsplash.com/photo-1618005198143-d366bc9b79be?auto=format&fit=crop&w=350&q=80" width="280px" alt="Main UI Screen Mockup" /> |
| *Antarmuka Utama Mewah dengan Kontrol Berbasis Gestur* | *Integrasi Desain Menyeluruh Bertaraf Internasional* |

---

## 🚀 Teknologi yang Digunakan (Tech Stack)
* **Kotlin (Modern Native Android)**: Bahasa pemrograman kelas satu untuk stabilitas memori tingkat tinggi.
* **Jetpack Compose**: Alat deklaratif modern untuk merajut antarmuka yang dinamis, responsif, dan kaya akan animasi Material 3.
* **Android WindowManager Overlay**: Memungkinkan penguncian posisi tombol melayang (*drawing over other apps*) pada koordinat layar manapun.
* **Google Priority Foreground Service**: Berjalan di latar belakang dengan notifikasi senyap berprioritas tinggi agar tidak terduplikasi oleh sistem.

---

## 📥 Unduh Aplikasi (Download Build APK)
Anda dapat langsung mengunduh dan memasang aplikasi Float Volume versi final yang siap pakai di ponsel Android Anda:

💾 **[Unduh Float-Volume-v2.0.0.apk (Direct Download)](./version/Float-Volume-v2.0.0.apk)**  
*(Pastikan perizinan pemasangan APK dari sumber tidak dikenal telah diizinkan).*

---

## 🔧 Jalankan Secara Lokal (Run Locally)
### 1. Kloning Repositori
```bash
git clone https://github.com/Curzyori/Float-Volume-10.git
cd Float-Volume-10
```
### 2. Membuka di Android Studio
* Jalankan Android Studio (versi Ladybug / Jellyfish terbaru).
* Klik **File -> Open** lalu pilih folder hasil kloning tadi.
* Biarkan proyek menyinkronkan Gradle secara otomatis.
### 3. Pemasangan ke Emulator / Perangkat Fisik
* Sambungkan perangkat Android Anda via kabel USB dengan *Developer Options -> USB Debugging* diaktifkan.
* Klik tombol **Run** (Ikon Segitiga Hijau) di navigasi atas Android Studio.

---

## 📖 Alur Penggunaan (Usage Flow)
1. **Buka Aplikasi**: Klik ikon aplikasi Float Volume di beranda atau laci aplikasi ponsel Anda.
2. **Izin Overlay**: Berikan izin **Akses Menampilkan di Atas Aplikasi Lain** (*Appear on Top*) agar lingkaran melayang dapat berfungsi.
3. **Aktifkan Sakelar Layanan**: Geser tombol *Layanan Melayang* di bagian dalam menu utama aplikasi.
4. **Gunakan Ikon Melayang**: Lingkaran putih bersih akan melayang di layar bagian kanan/kiri. Cukup ketuk sekali untuk menampilkan slider kontrol volume. Kontrol volume siap digunakan tanpa perlu menekan tombol fisik lagi!

---

## 🎯 Target & Luaran (Goals & Roadmap)
Aplikasi ini merupakan bagian penting dari target **50 Projects Challenge** yang dicanangkan oleh pembuat:
- [x] Rilis Versi Stabil Versi 1.0 (Layanan Melayang Stabil)
- [x] Transisi Nama Aplikasi Lebih Ringkas dari "Floating Volume" ke **Float Volume** (Versi 2.0.0)
- [x] Pembaruan Ikon & UI premium berbasis vector modern (Versi 2.0.0)
- [ ] Penambahan kustomisasi ukuran lingkaran melayang secara adaptif.
- [ ] Penambahan kustomisasi sensitivitas getaran taktil saat mengatur slider volume.

---

# 🇬🇧 English Version

## Short Description
**Float Volume** is an ad-free, completely free, and prestige-safe persistent floating volume controller for Android devices. Designed to easily adjust system volume without touching physical buttons while keeping your smartphone interface perfectly clean and professional.

---

## 🛠️ The Problem (Problem)
Many Android users experience broken, soft, or damaged physical volume buttons. When searching for external software solutions on the Google Play Store, they often face:
1. **Intrusive Advertisements**: Free replacement utility apps are saturated with full-screen ads, popups, and track telemetry that lags the phone resource.
2. **Cheap & Clunky UI Design**: Most overlay triggers are massive, brightly-colored badges that destroy the phone's aesthetic.
3. **The Embarrassment Factor (Gengsi)**: Having a giant icon that screaming "VOLUME ADJUSTMENT" on your screen makes your peers realize your phone has hardware damages, reducing confidence.
4. **Background Execution Termination**: The operating system frequently kills background overlay apps, forcing users to constantly relaunch them.

---

## 💡 The Solution (Solution)
**Float Volume** solves all of these friction points effortlessly:
* **Premium Material 3 (M3) Canvas**: Painted in dynamic *Elegant Deep Indigo* and *Lavender Accent* palette designed for pure sensory luxury.
* **100% Free & Empty of Ads**: Safe of tracking, trackers, or hidden subscription gates.
* **Prestige-Safe Stealth Aesthetic**: The floating element is styled as a minimal, solid-white assistive circle. It mimics the system's assistive navigation shortcut blocks (like assistive touch) instead of a broken-button utility. Your smartphone prestige remains intact!
* **Active-Infinity Lifetime Service**: Anchored by priority-certified *Google Foreground Service* to prevent system RAM cleaning mechanisms from dismissing the widget overlay.

---

## ✨ Main Features (Main Features)
* **Single-Tap Trigger**: Tap the clean soft circle to instantly reveal/hide the Material 3 fluid volume slider.
* **Disguised Gesture Touch Point**: Blends into your screen edges, masquerading as high-tech accessibility touch tools.
* **Real-Time Level Synchronizer**: Listens continuously to system audio change intents, syncing volume level indicators dynamically.
* **Visual Activity Logging**: Includes active debug log consoles and system permissions status cards.
* **Extreme Memory Optimization**: Extremely light on execution loops, preserving maximum device battery life.

---

## 🚀 Tech Stack + Motivation
* **Kotlin (Android Standard)**: Clean architecture, strict type safety, and memory-safe implementation.
* **Jetpack Compose UI**: Declarative layouts facilitating modern transitions, responsive ripple effects, and dynamic M3 rendering.
* **Overlay WindowManager API**: Direct system-registered canvas mapping to host views over any executing foreground program.
* **Foreground Service**: Ensures maximum persistence with a quiet, high-priority system status notification card.

---

## 📥 Direct APK Build Access (Download App)
Instantly retrieve the compiled application package block:

💾 **[Download Float-Volume-v2.0.0.apk (Direct Installer link)](./version/Float-Volume-v2.0.0.apk)**  
*(Enable 'Unset Sources / External App Installations' in your security tab to install).*

---

## 🔧 Run Locally
### 1. Clone Source
```bash
git clone https://github.com/Curzyori/Float-Volume-10.git
cd Float-Volume-10
```
### 2. Gradle Sync
* Open Android Studio, navigate to **File -> Open**, choose the root file folder.
* Wait for Gradle configuration cache lookup to finalize.
### 3. Deploy App
* Hook your Android device on USB debugging mode and select **Run Debug Module**.

---

## 📖 Usage Flow
1. **Initialize App**: Launch the Float Volume application.
2. **Authorize Draw Permission**: Allow the **Draw Over Other Apps / Display Overlay** permission.
3. **Engage The Service**: Power the master dashboard toggle.
4. **Tap the Assistive Touch Icon**: Use the quiet white circular widget on your screen edges to control your system audio on demand!

---

## 🎯 Goals & Roadmap
🚀 **50 Projects Challenge — Building 50 real-world projects before September 2026.**

- [x] Initial Beta Release (Stable Volume Overlays)
- [x] Rename branding from "Floating Volume" to **Float Volume** (v2.0.0)
- [x] Design brand identity, icons, and vector logo masks (v2.0.0)
- [ ] Implement customizable touch size sliders.
- [ ] Add lightweight haptic/vibration feedback triggers.

🔗 **Challenge Tracking List**: You can trace the progress of all my projects directly on my **[GitHub Project List (ᴘʀᴏᴊᴇᴄᴛs)](https://github.com/Curzyori)** page.

---

## 🏷️ System Version
* **Active Version**: `v2.0.0`
* **Target OS level**: Android 7.0 (API level 24) up to Android 15 (API level 36)

---

## 📄 License
Licensed under the [MIT License](LICENSE) — free for modification and educational usage.

---
*Created with 💜 by **Curzy** as the 10th landmark of the 50-projects challenge series.*
