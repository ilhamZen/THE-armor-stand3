# Petrified

A vanilla Minecraft plugin implementing Weeping Angels using ArmorStands. Petrified stone statues that quantum-lock when observed and hunt when unseen.

## Features

- **Quantum Locking**: Angels freeze when players look at them
- **Mirror Trap**: Angels seeing their own reflection become permanently trapped
- **Time Displacement**: Angels can send players "back in time" (teleport with effects)
- **Flock AI**: Angels coordinate attacks on nearby players
- **Pickaxe Combat**: Only pickaxes can damage angels (3 hits to kill)
- **Pose System**: Multiple frozen and pursuit poses with smooth interpolation
- **Natural Spawning**: Angels spawn naturally near players in dark areas
- **Persistence**: Angels survive server restarts
- **Multi-language**: English and Indonesian support
- **No Dependencies**: Works standalone (optional PlaceholderAPI support)

## Version Compatibility

| Minecraft Version | Java Floor | JAR | Notes |
|-------------------|------------|-----|-------|
| 1.21.0 - 1.21.11 | Java 21 | Petrified.jar | Modern range |
| 1.20.4 - 1.20.6 | Java 21 | Petrified.jar | Modern range |
| 1.17.1 - 1.20.1 | Java 17 | Petrified.jar | Modern range |
| 1.16.5 | Java 8 | Petrified-Legacy.jar | Legacy range |
| 1.12.2 | Java 8 | Petrified-Legacy.jar | Legacy range |
| 1.8.8 | Java 8 | Petrified-Legacy.jar | Legacy range |

**Note**: The JVM requires different bytecode versions for different MC ranges. You cannot have a single JAR that works on both Java 8 and Java 21 servers. Use `Petrified.jar` for modern servers (1.17+) and `Petrified-Legacy.jar` for legacy servers (1.8.8-1.16.5).

## Building

### Prerequisites
- JDK 17 or higher (for modern builds)
- JDK 8 (for legacy builds)

### Build Script

```bash
cd Petrified
python scripts/build_petrified.py
```

### Build Options

```bash
# Build for latest version (default)
python scripts/build_petrified.py

# Build for specific version
python scripts/build_petrified.py --target 1_20_1

# Include legacy JAR
python scripts/build_petrified.py --include-legacy

# Offline build
python scripts/build_petrified.py --offline

# Custom output directory
python scripts/build_petrified.py --out ./releases
```

### Manual Maven Build

```bash
./mvnw clean package -Dmc.target=1_21_11 -Pmc-1_21_11
```

## Installation

1. Download the appropriate JAR for your server version
2. Place in your server's `plugins/` folder
3. Restart the server
4. Configure `plugins/Petrified/config.yml` if needed

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/petrified help` | petrified.use | Show help |
| `/petrified spawn [here]` | petrified.spawn | Spawn an angel |
| `/petrified remove` | petrified.remove | Remove nearby angels |
| `/petrified reload` | petrified.reload | Reload config |
| `/petrified info` | petrified.info | View angel info |
| `/petrified freeze` | petrified.freeze | Freeze an angel |
| `/petrified unfreeze` | petrified.unfreeze | Unfreeze an angel |
| `/petrified pose <name>` | petrified.pose | Set angel pose |
| `/petrified stats` | petrified.stats | View statistics |
| `/petrified debug` | petrified.debug | Toggle debug mode |
| `/petrified givestatue` | petrified.givestatue | Get decorative statue |
| `/petrified tp` | petrified.tp | Teleport angel to you |
| `/petrified list` | petrified.list | List all angels |
| `/petrified credit` | petrified.credit | Show credits |
| `/petrified ping` | petrified.ping | Ping command |

## Configuration

Key configuration options in `config.yml`:

- `max-angels`: Maximum number of angels on server
- `detection-radius`: How far angels detect players
- `combat.required-pickaxe-hits`: Hits needed to kill an angel
- `displacement.mode`: DISPLACE, KILL, or DAMAGE
- `mirror-reflective-blocks`: Blocks that trigger mirror trap
- `natural-spawning.enabled`: Enable natural spawning

## Lore Accuracy

This plugin implements key Weeping Angel mechanics from Doctor Who:

1. **Don't Blink**: Angels only move when unobserved
2. **Mirror Trap**: Angels seeing themselves remain frozen forever
3. **Time Displacement**: Victims are sent to the past, not killed
4. **Stone Appearance**: Angels appear as stone statues when locked

## API

Developers can use the `PetrifiedAPI` to interact with angels programmatically:

```java
PetrifiedAPI api = PetrifiedAPI.getAPI();
if (api != null && api.isAvailable()) {
    Collection<PetrifiedAngel> angels = api.getAllAngels();
    // ...
}
```

## Events

- `AngelLockEvent`: When an angel becomes locked
- `AngelUnlockEvent`: When an angel becomes unlocked
- `AngelMoveEvent`: When an angel moves
- `AngelAttackEvent`: When an angel attacks
- `AngelSpawnEvent`: When an angel spawns
- `AngelDeathEvent`: When an angel dies
- `AngelDisplaceEvent`: When a player is displaced

## Credits

- Author: nyx1024
- License: All rights reserved

## Support

Report issues on GitHub or contact the author.
