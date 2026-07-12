package com.novusmc.camprelay.managers;

import com.novusmc.camprelay.Main;
import com.novusmc.camprelay.model.Relay;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Gère la persistance et l'accès aux Camp Relay (fichier relays.yml).
 */
public class RelayManager {

    private final Main plugin;
    private final File file;
    private final Map<UUID, Relay> relays = new LinkedHashMap<>();

    public RelayManager(Main plugin) {
        this.plugin = plugin;
        File dataDir = new File(plugin.getDataFolder(), "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        this.file = new File(dataDir, "relays.yml");
        load();
    }

    public void load() {
        relays.clear();
        if (!file.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.isConfigurationSection("relays")) {
            return;
        }
        for (String key : config.getConfigurationSection("relays").getKeys(false)) {
            try {
                String path = "relays." + key;
                UUID id = UUID.fromString(key);
                UUID owner = UUID.fromString(config.getString(path + ".owner"));
                String world = config.getString(path + ".world");
                int x = config.getInt(path + ".x");
                int y = config.getInt(path + ".y");
                int z = config.getInt(path + ".z");

                org.bukkit.World bukkitWorld = plugin.getServer().getWorld(world);
                if (bukkitWorld == null) {
                    continue;
                }
                Relay relay = new Relay(id, owner, new Location(bukkitWorld, x, y, z));
                relay.setLastDeath(config.getString(path + ".lastDeathPlayer", null), config.getLong(path + ".lastDeathTime", 0));
                relay.setLastApproach(config.getString(path + ".lastApproachPlayer", null), config.getLong(path + ".lastApproachTime", 0));
                relay.setLastUsed(config.getString(path + ".lastUsedPlayer", null), config.getLong(path + ".lastUsedTime", 0));
                relay.setLastStorageAccess(config.getString(path + ".lastStorageAccessPlayer", null), config.getLong(path + ".lastStorageAccessTime", 0));

                relays.put(id, relay);
            } catch (Exception ex) {
                plugin.getLogger().log(Level.WARNING, "Impossible de charger un Camp Relay (" + key + ")", ex);
            }
        }
    }

    public void save() {
        YamlConfiguration config = new YamlConfiguration();
        for (Relay relay : relays.values()) {
            String path = "relays." + relay.getId();
            config.set(path + ".owner", relay.getOwner().toString());
            config.set(path + ".world", relay.getWorldName());
            config.set(path + ".x", relay.getX());
            config.set(path + ".y", relay.getY());
            config.set(path + ".z", relay.getZ());
            config.set(path + ".lastDeathPlayer", relay.getLastDeathPlayer());
            config.set(path + ".lastDeathTime", relay.getLastDeathTime());
            config.set(path + ".lastApproachPlayer", relay.getLastApproachPlayer());
            config.set(path + ".lastApproachTime", relay.getLastApproachTime());
            config.set(path + ".lastUsedPlayer", relay.getLastUsedPlayer());
            config.set(path + ".lastUsedTime", relay.getLastUsedTime());
            config.set(path + ".lastStorageAccessPlayer", relay.getLastStorageAccessPlayer());
            config.set(path + ".lastStorageAccessTime", relay.getLastStorageAccessTime());
        }
        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Impossible de sauvegarder relays.yml", ex);
        }
    }

    public Relay createRelay(UUID owner, Location location) {
        UUID id = UUID.randomUUID();
        Relay relay = new Relay(id, owner, location);
        relays.put(id, relay);
        save();
        return relay;
    }

    public void removeRelay(UUID id) {
        relays.remove(id);
        save();
    }

    public Relay getRelay(UUID id) {
        if (id == null) {
            return null;
        }
        return relays.get(id);
    }

    public List<Relay> getRelaysByOwner(UUID owner) {
        return relays.values().stream()
                .filter(r -> r.getOwner().equals(owner))
                .collect(Collectors.toList());
    }

    public List<Relay> getAllRelays() {
        return new ArrayList<>(relays.values());
    }

    /**
     * Trouve le Camp Relay dont le bloc correspond exactement à cet emplacement.
     */
    public Relay getRelayAtBlock(Location blockLocation) {
        if (blockLocation.getWorld() == null) {
            return null;
        }
        for (Relay relay : relays.values()) {
            if (relay.getWorldName().equals(blockLocation.getWorld().getName())
                    && relay.getX() == blockLocation.getBlockX()
                    && relay.getY() == blockLocation.getBlockY()
                    && relay.getZ() == blockLocation.getBlockZ()) {
                return relay;
            }
        }
        return null;
    }

    /**
     * Trouve le Camp Relay le plus proche (tout propriétaire confondu) dans le rayon donné.
     */
    public Relay getNearestRelay(Location from, double maxDistance) {
        if (from.getWorld() == null) {
            return null;
        }
        Relay nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Relay relay : relays.values()) {
            if (!relay.getWorldName().equals(from.getWorld().getName())) {
                continue;
            }
            Location relayLoc = relay.getLocation();
            if (relayLoc == null) {
                continue;
            }
            double dist = relayLoc.distance(from);
            if (dist <= maxDistance && dist < nearestDist) {
                nearest = relay;
                nearestDist = dist;
            }
        }
        return nearest;
    }
}
