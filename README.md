# CommandScheduler

[![Build Plugin](https://github.com/YOUR_USERNAME/YOUR_REPO/actions/workflows/build.yml/badge.svg)](https://github.com/YOUR_USERNAME/YOUR_REPO/actions/workflows/build.yml)

Plugin Minecraft untuk menjadwalkan command yang berjalan otomatis dengan interval tertentu.

## Features

- Jadwalkan command untuk berjalan otomatis setiap X detik
- Support command console dan player
- Persistent storage (tersimpan saat restart)
- Support Folia
- Mudah digunakan

## Commands

- `/cs add <interval> <command>` - Tambah scheduled command
- `/cs remove <id>` - Hapus scheduled command
- `/cs list` - Lihat semua scheduled commands

## Permissions

- `commandscheduler.admin` - Akses semua command (default: op)

## Build

### Otomatis via GitHub Actions

Push ke repository GitHub, dan plugin akan otomatis ter-build. Download artifact dari tab Actions.

### Manual

```bash
./gradlew build
```

File JAR akan ada di `build/libs/`

## Installation

1. Download file JAR dari [Releases](https://github.com/YOUR_USERNAME/YOUR_REPO/releases) atau GitHub Actions artifacts
2. Letakkan di folder `plugins/`
3. Restart server
4. Edit `plugins/CommandScheduler/config.yml` jika perlu
5. Reload dengan `/reload confirm` atau restart

## Requirements

- Java 17+
- Paper/Spigot/Folia 1.20.1+

## Support

Buat issue di GitHub jika ada bug atau request feature.

## License

MIT License