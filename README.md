# ztab

`ztab` is a Paper plugin focused on TAB-list UX, built for broad compatibility (`1.19+`) and optional integration with `zSupportExtender`.

## Features

- Animated tab header/footer frames
- Rank-based tab sorting via permissions
- Custom tab list name format
- Built-in placeholders (no PlaceholderAPI required)
- Optional `zSupportExtender` soft integration
- `/ztab status` and `/ztab reload`

## All `%placeholders%`

- `%player%` -> player name
- `%world%` -> current world name
- `%online%` -> online players count
- `%max%` -> max player slots
- `%ping%` -> player ping
- `%tps%` -> server TPS (1m)
- `%x%` -> player X block coordinate
- `%y%` -> player Y block coordinate
- `%z%` -> player Z block coordinate
- `%prefix%` -> resolved group prefix from config
- `%suffix%` -> resolved group suffix from config
- `%zse_status%` -> `zSupportExtender` status token (`linked`, `working`, etc.)

## Example Strings

- Header: `&fOnline: &b%online%&7/&b%max%`
- Footer: `&7TPS: &a%tps% &7| Ping: &a%ping%ms`
- Footer: `&7World: &f%world% &7| XYZ: &f%x% %y% %z%`
- List name: `%prefix%&f%player%%suffix%`

## Build

```powershell
.\gradlew.bat clean build
```

Jar output:
- `jar/ztab-1.0.0.jar`
