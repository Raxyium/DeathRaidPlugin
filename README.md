# DeathRaid Plugin for Hytale

A death-based raiding mechanics plugin for Hytale servers that integrates with Simple Claims. When a party accumulates a configurable number of deaths, their claim protections are temporarily disabled, allowing other players to raid them.

> **⚠️ API Note (January 14, 2026):** This plugin has been updated to use the correct Hytale Early Access API based on community documentation. Since Hytale just launched, the API may change - check the [Hytale Modding Documentation](https://hytale-docs.pages.dev) for the latest information.

## 🎮 Features

- **Death Tracking**: Tracks deaths per party (not per player)
- **Raid Windows**: Configurable temporary vulnerability periods
- **Simple Claims Integration**: Works with Simple Claims mod protections
- **Notifications**: Party and server-wide announcements
- **Data Persistence**: Survives server restarts
- **Admin Tools**: Full configuration and management commands

## 📋 Requirements

- **Hytale** Early Access (January 2026 or later)
- **Java 25** or higher
- **Simple Claims** mod by Buuz135 (v1.0.4+)

## 📦 Installation

### Step 1: Download Required Files

1. Download the **DeathRaid** plugin JAR from releases
2. Download **Simple Claims** from [CurseForge](https://www.curseforge.com/hytale/mods/simple-claims)

### Step 2: Install the Plugins

1. Navigate to your Hytale mods folder:
   ```
   Windows: %appdata%\Hytale\UserData\Mods\
   ```

2. Copy both JAR files into the `Mods` folder:
   - `DeathRaid-1.0.0.jar`
   - `SimpleClaims-1.0.4.jar`

### Step 3: Start Your Server

1. Launch your Hytale server
2. The plugin will create its configuration files automatically
3. Check the server console for successful loading messages

## ⚙️ Configuration

Configuration is stored in:
```
%appdata%\Hytale\UserData\Mods\DeathRaid\config.json
```

### Default Configuration

```json
{
  "deathThreshold": 20,
  "raidDurationMinutes": 30,
  "enableBroadcastAlerts": true,
  "deathAnnouncementToParty": true,
  "deathAnnouncementInterval": 5,
  "saveIntervalMinutes": 5
}
```

### Configuration Options

| Setting | Default | Description |
|---------|---------|-------------|
| `deathThreshold` | 20 | Number of deaths before raid window triggers |
| `raidDurationMinutes` | 30 | How long the raid window lasts |
| `enableBroadcastAlerts` | true | Announce raids server-wide |
| `deathAnnouncementToParty` | true | Notify party members of deaths |
| `deathAnnouncementInterval` | 5 | Announce every N deaths |
| `saveIntervalMinutes` | 5 | Auto-save interval |

## 📝 Commands

### Player Commands

| Command | Description |
|---------|-------------|
| `/deathcount` | View your party's current death count |
| `/raidstatus` | List all currently raidable parties |

### Admin Commands

| Command | Description |
|---------|-------------|
| `/resetdeaths <party\|all>` | Reset death counter for a party |
| `/raidconfig` | View current configuration |
| `/raidconfig <setting> <value>` | Change a setting |
| `/raidconfig reload` | Reload configuration from file |
| `/raidconfig save` | Save current configuration |
| `/raidconfig endraid <party>` | End a raid window early |
| `/raidconfig status` | View plugin status info |

### Configuration Settings via Commands

```
/raidconfig threshold 20       - Set death threshold
/raidconfig duration 30        - Set raid duration (minutes)
/raidconfig broadcast true     - Enable/disable broadcasts
/raidconfig partynotify true   - Enable/disable party notifications
/raidconfig interval 5         - Set announcement interval
```

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `deathraid.use` | Use player commands | Everyone |
| `deathraid.admin` | Use admin commands | Operators |

## 🎯 How It Works

1. **Death Tracking**
   - When any party member dies, it counts toward the party total
   - Deaths are tracked per party, not per player
   - Party members receive notifications at configured intervals

2. **Raid Trigger**
   - When deaths reach the threshold (default: 20), a raid window opens
   - Death counter resets to zero
   - Server-wide announcement (if enabled)

3. **Raid Window**
   - Party's Simple Claims protections are disabled
   - Other players can break/place blocks in their claimed chunks
   - Window lasts for the configured duration (default: 30 minutes)

4. **Protection Restoration**
   - After the raid window expires, protections are automatically restored
   - Party receives a notification that they're protected again
   - New death counting begins for the next potential raid

## 🔧 Simple Claims Integration

This plugin integrates with Simple Claims in the following ways:

- **Party Detection**: Identifies which party a player belongs to
- **Protection Toggle**: Disables/enables chunk protections during raids
- **Party Messaging**: Sends notifications to all party members

### Integration Modes

1. **API Mode** (Preferred): Uses Simple Claims' internal API via reflection
2. **Command Fallback**: Uses admin commands if API is unavailable

The plugin automatically detects the best integration mode on startup.

## 🛠️ Building from Source

### Prerequisites

- Java 25 JDK
- Gradle 8.5+

### Build Commands

```bash
# Clone the repository
git clone https://github.com/deathraid/deathraid-plugin.git
cd deathraid-plugin

# Build the plugin
./gradlew build

# The JAR will be in build/libs/DeathRaid-1.0.0.jar
```

### Development Setup

1. Open the project in IntelliJ IDEA
2. Import as a Gradle project
3. Wait for dependencies to download
4. Use the `runServer` task to test

## 📁 File Structure

```
DeathRaidPlugin/
├── src/main/
│   ├── java/com/deathraid/deathraid/
│   │   ├── DeathRaidPlugin.java      # Main plugin class
│   │   ├── managers/
│   │   │   ├── DeathManager.java     # Death tracking
│   │   │   ├── RaidManager.java      # Raid window management
│   │   │   └── DataManager.java      # Data persistence
│   │   ├── listeners/
│   │   │   └── PlayerDeathListener.java
│   │   ├── commands/
│   │   │   ├── DeathCountCommand.java
│   │   │   ├── RaidStatusCommand.java
│   │   │   ├── ResetDeathsCommand.java
│   │   │   └── RaidConfigCommand.java
│   │   ├── integration/
│   │   │   └── SimpleClaimsHook.java # Simple Claims integration
│   │   ├── models/
│   │   │   ├── PartyData.java
│   │   │   └── RaidWindow.java
│   │   └── utils/
│   │       ├── MessageUtil.java
│   │       └── TimeUtil.java
│   └── resources/
│       ├── manifest.json
│       └── config.json
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

## ❗ Troubleshooting

### Plugin Not Loading

1. Check that Java 25+ is installed
2. Verify Simple Claims is also installed
3. Check console for error messages

### Simple Claims Integration Failed

1. Ensure Simple Claims version is 1.0.4+
2. Check console for integration mode (API vs Command fallback)
3. Run `/raidconfig status` to see integration details

### Deaths Not Being Tracked

1. Verify the player is in a Simple Claims party
2. Check if the player created a party with `/scp create`
3. Run `/deathcount` to verify tracking is working

### Raid Window Not Triggering

1. Check current death count with `/deathcount`
2. Verify threshold setting with `/raidconfig`
3. Check console for any error messages

### Data Not Persisting

1. Check file permissions in the Mods/DeathRaid folder
2. Verify auto-save is working (check saveIntervalMinutes)
3. Run `/raidconfig save` to manually save

## 🐛 Known Limitations

1. **Simple Claims Hammer**: Currently no way to cancel hammer interactions (Simple Claims limitation)
2. **Party Changes During Raid**: If a player leaves a party during a raid, tracking may be affected
3. **Server Crashes**: Data saves every 5 minutes; deaths in between may be lost

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit issues and pull requests.

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/deathraid/deathraid-plugin/issues)
- **Discord**: Join our community server for help

## 🙏 Credits

- **Simple Claims** by [Buuz135](https://github.com/Buuz135) - The claims system this plugin integrates with
- **Hytale Modding Community** - Documentation and support

---

Made with ❤️ for the Hytale community
