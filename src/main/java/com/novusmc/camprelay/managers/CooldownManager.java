package com.novusmc.camprelay.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gère le cooldown de téléportation du réseau de Camp Relay (en mémoire).
 */
public class CooldownManager {

    public static final long TELEPORT_COOLDOWN_MS = 30_000L;

    private final Map<UUID, Long> lastTeleport = new HashMap<>();

    public boolean isOnCooldown(UUID uuid) {
        return getRemainingSeconds(uuid) > 0;
    }

    public long getRemainingSeconds(UUID uuid) {
        Long last = lastTeleport.get(uuid);
        if (last == null) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - last;
        long remainingMs = TELEPORT_COOLDOWN_MS - elapsed;
        return remainingMs > 0 ? (remainingMs / 1000) + 1 : 0;
    }

    public void setUsed(UUID uuid) {
        lastTeleport.put(uuid, System.currentTimeMillis());
    }
}
