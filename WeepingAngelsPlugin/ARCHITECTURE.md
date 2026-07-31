# WeepingAngels Universal Cross-Version Architecture

## Project Structure

```
weeping-angels-parent/
├── pom.xml                          # Ultimate Parent POM with all version properties
│
├── core/                            # Core Abstraction Module (Java 17)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/
│       ├── WeepingAngelsPlugin.java     # Main plugin class
│       ├── api/
│       │   ├── WeepingAngelAPI.java     # Version-agnostic interface
│       │   └── WeepingAngelFactory.java # Runtime version detector & loader
│       ├── command/
│       │   └── AngelCommand.java        # Shared command logic
│       ├── config/
│       │   └── AngelConfig.java         # Configuration manager
│       ├── listener/
│       │   └── AngelListener.java       # Event listeners (cross-version)
│       └── manager/
│           ├── AngelManager.java        # Entity management
│           └── SpawnManager.java        # Spawning logic
│
├── v1_7_R4/                         # Legacy Era: Minecraft 1.7.10 (Java 8)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_7_r4/
│       └── WeepingAngelImpl_v1_7_R4.java
│
├── v1_8_R1/                         # Legacy Era: Minecraft 1.8.8 (Java 8)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_8_r1/
│       └── WeepingAngelImpl_v1_8_R1.java
│
├── v1_9_R1/                         # Legacy Era: Minecraft 1.9.4 (Java 8)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_9_r1/
│       └── WeepingAngelImpl_v1_9_R1.java
│
├── v1_10_R1/                        # Legacy Era: Minecraft 1.10.2 (Java 8)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_10_r1/
│       └── WeepingAngelImpl_v1_10_R1.java
│
├── v1_11_R1/                        # Legacy Era: Minecraft 1.11.2 (Java 8)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_11_r1/
│       └── WeepingAngelImpl_v1_11_R1.java
│
├── v1_12_R1/                        # Classic Era: Minecraft 1.12.2 (Java 8/11)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_12_r1/
│       └── WeepingAngelImpl_v1_12_R1.java
│
├── v1_13_R1/                        # Classic Era: Minecraft 1.13.2 (Java 11)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_13_r1/
│       └── WeepingAngelImpl_v1_13_R1.java
│
├── v1_14_R1/                        # Classic Era: Minecraft 1.14.4 (Java 11)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_14_r1/
│       └── WeepingAngelImpl_v1_14_R1.java
│
├── v1_15_R1/                        # Classic Era: Minecraft 1.15.2 (Java 11)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_15_r1/
│       └── WeepingAngelImpl_v1_15_R1.java
│
├── v1_16_R1/                        # Classic Era: Minecraft 1.16.5 (Java 11/16)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_16_r1/
│       └── WeepingAngelImpl_v1_16_R1.java
│
├── v1_17_R1/                        # Modern Era: Minecraft 1.17.1 (Java 17)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_17_r1/
│       └── WeepingAngelImpl_v1_17_R1.java
│
├── v1_18_R1/                        # Modern Era: Minecraft 1.18.2 (Java 17)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_18_r1/
│       └── WeepingAngelImpl_v1_18_R1.java
│
├── v1_19_R1/                        # Modern Era: Minecraft 1.19.4 (Java 17)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_19_r1/
│       └── WeepingAngelImpl_v1_19_R1.java
│
├── v1_20_R1/                        # Modern Era: Minecraft 1.20.1 (Java 17)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_20_r1/
│       └── WeepingAngelImpl_1_20_R1.java
│
├── v1_20_R2/                        # Modern Era: Minecraft 1.20.4 (Java 17)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_20_r2/
│       └── WeepingAngelImpl_1_20_R2.java
│
├── v1_20_R3/                        # Contemporary Era: Minecraft 1.20.6 (Java 21)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_20_r3/
│       └── WeepingAngelImpl_1_20_R3.java
│
├── v1_21_R1/                        # Contemporary Era: Minecraft 1.21.1 (Java 21)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v1_21_r1/
│       └── WeepingAngelImpl_1_21_R1.java
│
├── v26_1_2/                         # Contemporary Era: PurpurMC 26.1.2 (Java 21)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v26_1_2/
│       └── WeepingAngelImpl_v26_1_2.java
│
├── v26_2_0/                         # Latest Era: PurpurMC 26.2.0+ (Java 21/25)
│   ├── pom.xml
│   └── src/main/java/com/example/weepingangels/impl/v26_2_0/
│       └── WeepingAngelImpl_v26_2_0.java
│
└── plugin/                          # Final Universal Plugin Module
    ├── pom.xml
    └── src/main/resources/
        ├── plugin.yml               # Legacy Bukkit plugin descriptor
        ├── paper-plugin.yml         # Modern Paper/Purpur plugin descriptor
        └── config.yml               # Default configuration
```

## Java Version Matrix by Era

| Era | Minecraft Versions | Java Version | Compiler Target | API Type |
|-----|-------------------|--------------|-----------------|----------|
| Legacy | 1.7 - 1.11 | Java 8 | 1.8 | Bukkit/Spigot (pre-Mojang mappings) |
| Classic | 1.12 - 1.16 | Java 11/16 | 1.8 or 11 | Spigot/Paper (transition period) |
| Modern | 1.17 - 1.20.4 | Java 17 | 17 | Paper (Mojang mappings) |
| Contemporary | 1.20.5 - 26.x | Java 21/25 | 21 | Paper/Purpur (Data Components API) |

## Key Architectural Decisions

### 1. Abstraction Layer Pattern
The `core` module defines the `WeepingAngelAPI` interface that all version-specific implementations must implement. This ensures:
- Consistent behavior across all Minecraft versions
- Clean separation of concerns
- Easy addition of new version modules

### 2. Dynamic Runtime Detection
`WeepingAngelFactory` uses reflection to:
- Detect server version at startup via `Bukkit.getServer().getBukkitVersion()`
- Load only the appropriate implementation class
- Avoid ClassNotFoundException from incompatible NMS code

### 3. Dual Plugin Descriptor Support
Both `plugin.yml` and `paper-plugin.yml` are packaged to ensure:
- Legacy Spigot servers (1.7-1.19) read `plugin.yml`
- Modern Paper/Purpur servers (26.x+) read `paper-plugin.yml`
- Maximum compatibility across the entire version range

### 4. Maven Shade Configuration
The shade plugin is configured to:
- Merge all version modules into a single universal JAR
- Exclude unnecessary metadata (signatures, manifests)
- Preserve service loaders for proper SPI handling

## Build Instructions

### Full Universal Build (All Versions)
```bash
mvn clean package
```

### Build Specific Era Only
```bash
# Legacy era only (1.7-1.11)
mvn clean package -Plegacy-era

# Classic era only (1.12-1.16)
mvn clean package -Pclassic-era

# Modern era only (1.17-1.20.4)
mvn clean package -Pmodern-era

# Contemporary era only (1.20.5+)
mvn clean package -Pcontemporary-era
```

### Skip Tests for Faster Builds
```bash
mvn clean package -DskipTests
```

## Adding New Version Modules

1. Create new module directory: `v1_XX_RX/`
2. Copy an existing module's `pom.xml` as template
3. Update artifact ID, name, and version properties
4. Implement `WeepingAngelAPI` interface in your version package
5. Add module declaration to parent `pom.xml`
6. Add dependency reference in `plugin/pom.xml`
7. Update `WeepingAngelFactory` version detection logic

## Common API Versioning Traps Addressed

### Particle Enums
- **Pre-1.13**: `Particle.BLOCK_CRACK` with data value
- **Post-1.13**: `Particle.BLOCK` with BlockData
- **Solution**: Each module handles its own particle format

### Sound Definitions
- **Pre-1.9**: Sound names like `ZOMBIE_PIG_AMBIENT`
- **Post-1.9**: Sound names like `ENTITY_ZOMBIE_PIG_AMBIENT`
- **Solution**: Module-specific sound key constants

### ItemMeta vs Data Components
- **Pre-1.20.5**: `ItemMeta` with NBT tags
- **Post-1.20.5**: `DataComponent` API
- **Solution**: Separate implementations in respective modules

### Material IDs vs NamespacedKeys
- **Pre-1.13**: Numeric material IDs (`Material.STONE.getId()`)
- **Post-1.13**: Namespaced keys (`Material.STONE.getKey()`)
- **Solution**: Core module uses only cross-version Material enum values

## Repository URLs

| Repository | URL | Purpose |
|-----------|-----|---------|
| spigotmc-repo | https://hub.spigotmc.org/nexus/content/repositories/snapshots/ | Legacy Spigot APIs |
| papermc | https://repo.papermc.io/repository/maven-public/ | Modern Paper/Purpur APIs |
| sonatype-snapshots | https://s01.oss.sonatype.org/content/repositories/snapshots/ | Bleeding-edge builds |
| codemc-repo | https://repo.codemc.io/repository/maven-public/ | Alternative mirror |
