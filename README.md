# LinkDiscord

**LinkDiscord** je Minecraft plugin, který propojuje Discord účet hráče s jeho Minecraft účtem. Plugin umožňuje synchronizaci rolí na Discordu s pravomocemi na serveru a naopak.

## 🚀 Funkce
- 🔗 **Propojení** Discord účtu s Minecraft účtem
- 🔄 **Synchronizace** rolí Discordu s pravomocemi na serveru
- ⚙️ **Automatická správa** rolí a oprávnění
- 🎛 **Podpora více skupin** a nastavitelných oprávnění
- 📜 **Jednoduché příkazy** pro správu propojení

## 🛠 Požadavky
- 🏗 **Minecraft server** s podporou pluginů (Paper, Spigot, Bukkit)
- 🤖 **Discord bot** s oprávněním správy rolí

## 📥 Instalace
1. 📂 Stáhněte si nejnovější verzi pluginu z [GitHub Releases](https://github.com/uzivatel/LinkDiscord/releases).
2. 📁 Nahrajte soubor `.jar` do složky `plugins` na vašem serveru.
3. 🔄 Restartujte server, aby se plugin nahrál.
4. ⚙️ Otevřete konfigurační soubor `config.yml` v `plugins/LinkDiscord/` a nastavte:
   - Token bota
   - Mapování rolí a oprávnění
5. 🔄 Restartujte server pro aplikaci změn.

## 🎮 Použití
Hráči mohou propojit svůj účet pomocí příkazu:
```bash
/dlink link
```
Po propojení se jejich role na Discordu automaticky synchronizují s pravomocemi na serveru.

### 🔧 Administrátorské příkazy
```bash
/dlink reload   # Znovu načte konfiguraci
/dlink unlink <hráč>   # Odpojí hráče od jeho Discord účtu
/dlink sync   # Synchronizuje role s Discordem
```

## ⚙️ Konfigurace
Příklad `config.yml`:
```yaml
token: "type_discord_token"
channelID: "type_bot_spam_channel_id"
channelName: "type_bot_spam_channel_name"
guildID: "type_guild_id"
groups:
  - "VIP"
```
