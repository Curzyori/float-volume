# 📋 Product Requirement Document (PRD) — Float Volume

*   **Product Name**: Float Volume
*   **Version**: `v3.0.0` (Current)
*   **Author**: Curzy (Project #10 of "50 Projects Challenge")
*   **Status**: Selesai (Phase 2 Completed)

---

## 📖 1. Visi Produk & Latar Belakang (Product Vision)
Aplikasi Android utilitas melayang bebas iklan, gratis, dan ramah privasi untuk mengontrol volume sistem. Dirancang untuk pengguna yang menghadapi kerusakan tombol volume fisik ponsel, dengan mengutamakan konsep **samaran (Stealth/Prestige-Safe)** untuk melindungi prestise pengguna dari stigma "HP rusak".

---

## 👥 2. Profil Pengguna Target (User Personas)
*   **Persona Utama (The Prestige-Conscious User)**: Pengguna ponsel Android yang tombol volumenya aus/rusak, tetapi enggan memakai aplikasi kontrol volume biasa di tempat umum karena ikonnya mencolok dan membuat mereka merasa tidak nyaman/gengsi saat dilihat orang lain.
*   **Persona Pendukung (The One-Hand Utility Seeker)**: Pengguna ponsel layar besar (tablet/phablet) yang ingin mengatur volume dengan mudah memakai satu tangan tanpa perlu menjangkau tombol fisik di bagian atas/samping bodi HP.

---

## 🛠️ 3. Kebutuhan Fungsional (Functional Requirements)

### 🟢 Fase 1: Fitur Utama (Selesai di v2.0.0)
| ID | Nama Fitur | Keterangan Teknis | Prioritas | Status |
| :--- | :--- | :--- | :---: | :---: |
| **FR-01** | Stealth Bubble Penyamar | Tombol melayang berbentuk lingkaran putih polos tanpa ikon pengatur suara untuk menyamar sebagai alat bantu aksesibilitas bawaan OS. | P0 (Critical) | **[SELESAI]** |
| **FR-02** | Faux Hardware Click | Mengetuk lingkaran memicu dialog volume bawaan sistem Android (`FLAG_SHOW_UI`) demi menyimulasikan penekanan tombol fisik secara instan. | P0 (Critical) | **[SELESAI]** |
| **FR-03** | DP-Sensitive Touch | Menghitung jarak klik/geser menggunakan konversi kerapatan layar (`density`) dengan batas geser `8dp` & klik `12dp` agar sentuhan 100% andal di HP resolusi tinggi. | P0 (Critical) | **[SELESAI]** |
| **FR-04** | Auto-Start on Boot | Layanan otomatis aktif kembali saat HP dinyalakan ulang (*reboot*) selama status toggle di dashboard menyala. | P1 (High) | **[SELESAI]** |
| **FR-05** | Persistensi Koordinat | Merekam posisi `x` & `y` tombol melayang secara persisten ke SharedPreferences saat jari diangkat (`ACTION_UP`) dan memuatnya kembali saat startup. | P1 (High) | **[SELESAI]** |
| **FR-06** | Silent Foreground Service | Berjalan stabil di latar belakang dengan tingkat kepentingan notifikasi senyap (`IMPORTANCE_MIN` & `PRIORITY_MIN`) untuk menjaga privasi. | P1 (High) | **[SELESAI]** |
| **FR-07** | Lokalisasi Indonesia | Semua strings sistem, validasi izin, deskripsi TalkBack, dan nama notifikasi disajikan dalam Bahasa Indonesia yang alami. | P2 (Medium) | **[SELESAI]** |

### 🟡 Fase 2: Kustomisasi & Gestur Siluman (Selesai di v3.0.0)
| ID | Nama Fitur | Keterangan Teknis | Prioritas | Status |
| :--- | :--- | :--- | :---: | :---: |
| **FR-08** | Kustomisasi Transparansi | Slider Compose (10% - 100%) menyimpan status ke `bubble_opacity` di SharedPreferences dan disinkronkan ke service secara real-time via `ACTION_UPDATE_OPACITY`. | P0 (Critical) | **[SELESAI]** |
| **FR-09** | Proportional Idle Dimming | Layanan melayang secara otomatis meredupkan tingkat transparansi lingkaran menjadi `0.3f * baseOpacity` setelah 5 detik diam. | P1 (High) | **[SELESAI]** |
| **FR-10** | Double Tap Mute/Unmute | Ketukan ganda (jeda < 300ms) memicu senyap instan (volume 0) dengan merekam volume aktif terakhir ke `last_volume_before_mute` untuk dipulihkan kembali saat ketukan ganda berikutnya. | P0 (Critical) | **[SELESAI]** |
| **FR-11** | Long Press Dashboard Launcher | Menekan tombol melayang selama 500ms akan meluncurkan dashboard utama aplikasi secara instan dari mana saja menggunakan intent `Intent.FLAG_ACTIVITY_NEW_TASK`. | P1 (High) | **[SELESAI]** |

---

## 🔒 4. Kebutuhan Non-Fungsional (Non-Functional Requirements)
*   **Keamanan & Privasi**: Aplikasi 100% bebas iklan, tidak memiliki modul pelacak analytics pihak ketiga, dan tidak membutuhkan izin koneksi internet (*zero-network permission*).
*   **Efisiensi Daya**: Konsumsi daya latar belakang harus kurang dari 1% penggunaan baterai harian dengan meminimalkan perulangan proses (*draw cycles*).
*   **Aksesibilitas**: Tombol dashboard wajib mematuhi panduan TalkBack (memiliki peran `Role.Switch` dan status verbal).
*   **Distribusi Mandiri**: Rencana rilis difokuskan melalui distribusi web pribadi dan repositori GitHub (non-Play Store), memungkinkan fungsionalitas overlay penuh dan optimasi sistem tanpa batasan kebijakan platform.
