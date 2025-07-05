# ToDoList - Aplikasi Daftar Tugas Android

Aplikasi daftar tugas (ToDo List) yang modern dan intuitif untuk Android, dibangun dengan Jetpack Compose dan mengikuti arsitektur MVVM.

## 🌟 Fitur Utama

- **Manajemen Tugas**: Buat, edit, dan hapus tugas dengan mudah
- **Kategori**: Organisir tugas berdasarkan kategori (Umum, Kerja, Pribadi, Belanja, Kesehatan)
- **Prioritas**: Tetapkan prioritas tugas (Rendah, Sedang, Tinggi, Mendesak)
- **Tanggal Jatuh Tempo**: Atur tanggal jatuh tempo untuk tugas
- **Statistik**: Lihat statistik penyelesaian tugas dan wawasan produktivitas
- **Filter & Pencarian**: Filter tugas berdasarkan status, prioritas, dan kategori
- **Lokalisasi**: Dukungan bahasa Indonesia dan Inggris
- **UI Modern**: Antarmuka yang indah dengan Material Design 3

## 🛠️ Teknologi yang Digunakan

- **Jetpack Compose**: UI toolkit modern untuk Android
- **SQLite Database**: Database lokal untuk semua data (tugas, kategori, preferensi)
- **ViewModel & LiveData**: Arsitektur MVVM untuk manajemen state
- **Navigation Compose**: Navigasi antar halaman
- **Material Design 3**: Sistem desain yang konsisten
- **Kotlin Coroutines**: Pemrograman asynchronous

## 📱 Screenshots

### Beranda
- Dashboard dengan ringkasan tugas
- Statistik cepat (total, selesai, aktif, terlambat)
- Akses cepat ke fitur utama

### Daftar Tugas
- Tampilan semua tugas dengan filter
- Filter berdasarkan status (Semua, Aktif, Selesai)
- Filter berdasarkan prioritas
- Pencarian dan pengurutan

### Kategori
- Manajemen kategori tugas
- Statistik per kategori
- Navigasi ke tugas dalam kategori tertentu

### Statistik
- Grafik penyelesaian tugas
- Wawasan produktivitas
- Analisis tren

## 🚀 Instalasi

### Prerequisites
- Android Studio Arctic Fox atau yang lebih baru
- Android SDK API level 24+
- Kotlin 1.8+

### Langkah Instalasi

1. **Clone repository**
   ```bash
   git clone https://github.com/yourusername/ToDoList.git
   cd ToDoList
   ```

2. **Buka di Android Studio**
   - Buka Android Studio
   - Pilih "Open an existing project"
   - Pilih folder ToDoList

3. **Sync project**
   - Tunggu Gradle sync selesai
   - Pastikan semua dependencies terinstall

4. **Run aplikasi**
   - Hubungkan device Android atau gunakan emulator
   - Klik tombol "Run" (▶️) di Android Studio

## 📁 Struktur Project

```
app/
├── src/main/
│   ├── java/com/kouseina/todolist/
│   │   ├── data/
│   │   │   ├── dao/           # Data Access Objects
│   │   │   ├── database/      # Room database setup
│   │   │   └── model/         # Data models
│   │   ├── navigation/        # Navigation setup
│   │   ├── repository/        # Repository layer
│   │   ├── ui/
│   │   │   ├── components/    # Reusable UI components
│   │   │   ├── screens/       # Screen composables
│   │   │   └── theme/         # App theme and styling
│   │   └── viewmodel/         # ViewModels
│   └── res/
│       ├── values/            # English strings
│       ├── values-in/         # Indonesian strings
│       └── ...                # Other resources
```

## 🎨 Arsitektur

Aplikasi mengikuti arsitektur **MVVM (Model-View-ViewModel)** dengan komponen-komponen berikut:

- **Model**: Entity data (Todo, Category)
- **View**: Jetpack Compose UI components
- **ViewModel**: Business logic dan state management
- **Repository**: Data access layer
- **Database**: SQLite database untuk semua data (tugas, kategori, preferensi)

## 🌍 Lokalisasi

Aplikasi mendukung dua bahasa:
- **English** (default): `values/strings.xml`
- **Indonesian**: `values-in/strings.xml`

Untuk menambah bahasa baru:
1. Buat folder `values-[language-code]/`
2. Salin `strings.xml` ke folder tersebut
3. Terjemahkan semua string

## 🔧 Konfigurasi

### Database
- Database: SQLite Native
- Migrasi: Manual dengan SQLiteOpenHelper
- Backup: Mendukung Android Auto Backup
- Struktur: 3 tabel (todos, categories, user_preferences)

### Build Configuration
- Min SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Compile SDK: 34

## 📊 Dependencies

```kotlin
// Jetpack Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui-tooling-preview")

// Navigation
implementation("androidx.navigation:navigation-compose")

// SQLite Database
// Built-in Android APIs, no additional dependencies needed

// ViewModel & LiveData
implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
implementation("androidx.lifecycle:lifecycle-runtime-compose")
```

## 🤝 Kontribusi

Kontribusi sangat dihargai! Berikut cara berkontribusi:

1. **Fork** repository
2. **Buat branch** untuk fitur baru (`git checkout -b feature/AmazingFeature`)
3. **Commit** perubahan (`git commit -m 'Add some AmazingFeature'`)
4. **Push** ke branch (`git push origin feature/AmazingFeature`)
5. **Buat Pull Request**

### Guidelines
- Ikuti style guide Kotlin
- Tambahkan unit tests untuk fitur baru
- Update dokumentasi jika diperlukan
- Pastikan semua tests pass

## 🐛 Bug Reports

Jika menemukan bug, silakan buat issue dengan informasi berikut:
- Deskripsi bug yang jelas
- Langkah-langkah reproduksi
- Screenshot (jika relevan)
- Device dan Android version
- Log error (jika ada)

## 📄 Lisensi

Project ini dilisensikan di bawah MIT License - lihat file [LICENSE](LICENSE) untuk detail.

## 👨‍💻 Developer

**Kouseina**
- GitHub: [@kouseina](https://github.com/kouseina)

## 🙏 Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit
- [Material Design 3](https://m3.material.io/) - Design system
- [Room Database](https://developer.android.com/training/data-storage/room) - Local database
- [Android Developer Documentation](https://developer.android.com/) - Comprehensive guides

## 📈 Roadmap

- [ ] Cloud sync dengan Firebase
- [ ] Notifikasi push untuk tugas yang akan jatuh tempo
- [ ] Widget untuk home screen
- [ ] Export/import data
- [ ] Dark mode toggle
- [ ] Backup ke Google Drive
- [ ] Kolaborasi tugas (multi-user)

---

⭐ Jika project ini membantu Anda, jangan lupa berikan star! 