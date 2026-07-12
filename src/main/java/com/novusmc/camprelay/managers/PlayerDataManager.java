package com.novusmc.camprelay.managers;

import com.novusmc.camprelay.Main;
import com.novusmc.camprelay.model.PlayerData;
import com.novusmc.camprelay.model.RespawnMode;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Gère la persistance et l'accès aux préférences de respawn des joueurs (players.yml).
 */
public class PlayerDataManager {

    private final Main plugin;
    private final File file;
    private final Map<UUID, PlayerData> cache = new HashMap<>();

    public PlayerDataManager(Main plugin) {
        this.plugin = plugin;
        File dataDir = new File(plugin.getDataFolder(), "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        this.file = new File(dataDir, "players.yml");
        load();
    }

    public void load() {
        cache.clear();
        if (!file.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.isConfigurationSection("players")) {
            return;
        }
        for (String key : config.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String path = "players." + key;
                PlayerData data = new PlayerData(uuid);

                String modeStr = config.getString(path + ".respawnMode", RespawnMode.BED.name());
                try {
                    data.setRespawnMode(RespawnMode.valueOf(modeStr));
                } catch (IllegalArgumentException ignored) {
                    data.setRespawnMode(RespawnMode.BED);
                }

                String selected = config.getString(path + ".selectedRelayId", null);
                if (selected != null) {
                    try {
                        data.setSelectedRelayId(UUID.fromString(selected));
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                String lastCamp = config.getString(path + ".lastCampRelayId", null);
                if (lastCamp != null) {
                    try {
                        data.setLastCampRelayId(UUID.fromString(lastCamp));
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                cache.put(uuid, data);
            } catch (Exception ex) {
                plugin.getLogger().log(Level.WARNING, "Impossible de charger les donnees du joueur " + key, ex);
            }
        }
    }

    public void save() {
        YamlConfiguration config = new YamlConfiguration();
        for (PlayerData data : cache.values()) {
            String path = "players." + data.getUuid();
            config.set(path + ".respawnMode", data.getRespawnMode().name());
            config.set(path + ".selectedRelayId", data.getSelectedRelayId() != null ? data.getSelectedRelayId().toString() : null);
            config.set(path + ".lastCampRelayId", data.getLastCampRelayId() != null ? data.getLastCampRelayId().toString() : null);
        }
        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Impossible de sauvegarder players.yml", ex);
        }
    }

    public PlayerData get(UUID uuid) {
        return cache.computeIfAbsent(uuid, PlayerData::new);
    }

    public java.util.Collection<PlayerData> all() {
        return cache.values();
    }
}
