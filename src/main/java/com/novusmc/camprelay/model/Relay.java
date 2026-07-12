package com.novusmc.camprelay.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

/**
 * Représente un Camp Relay placé dans le monde.
 */
public class Relay {

    private final UUID id;
    private final UUID owner;
    private String worldName;
    private int x;
    private int y;
    private int z;

    private String lastDeathPlayer;
    private long lastDeathTime;

    private String lastApproachPlayer;
    private long lastApproachTime;

    private String lastUsedPlayer;
    private long lastUsedTime;

    private String lastStorageAccessPlayer;
    private long lastStorageAccessTime;

    public Relay(UUID id, UUID owner, Location location) {
        this.id = id;
        this.owner = owner;
        setLocation(location);
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setLocation(Location location) {
        this.worldName = location.getWorld() != null ? location.getWorld().getName() : "world";
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, x + 0.5, y + 1, z + 0.5);
    }

    public String getWorldName() {
        return worldName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getLastDeathPlayer() {
        return lastDeathPlayer;
    }

    public long getLastDeathTime() {
        return lastDeathTime;
    }

    public void setLastDeath(String playerName, long time) {
        this.lastDeathPlayer = playerName;
        this.lastDeathTime = time;
    }

    public String getLastApproachPlayer() {
        return lastApproachPlayer;
    }

    public long getLastApproachTime() {
        return lastApproachTime;
    }

    public void setLastApproach(String playerName, long time) {
        this.lastApproachPlayer = playerName;
        this.lastApproachTime = time;
    }

    public String getLastUsedPlayer() {
        return lastUsedPlayer;
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsed(String playerName, long time) {
        this.lastUsedPlayer = playerName;
        this.lastUsedTime = time;
    }

    public String getLastStorageAccessPlayer() {
        return lastStorageAccessPlayer;
    }

    public long getLastStorageAccessTime() {
        return lastStorageAccessTime;
    }

    public void setLastStorageAccess(String playerName, long time) {
        this.lastStorageAccessPlayer = playerName;
        this.lastStorageAccessTime = time;
    }
}
