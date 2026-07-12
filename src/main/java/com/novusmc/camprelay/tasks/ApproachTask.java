package com.novusmc.camprelay.tasks;

import com.novusmc.camprelay.Main;
import com.novusmc.camprelay.model.Relay;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Détecte les joueurs approchant un Camp Relay (rayon 10 blocs) pour le journal de base.
 * S'exécute toutes les 5 secondes (100 ticks).
 */
public class ApproachTask extends BukkitRunnable {

    private static final double RADIUS = 10.0;

    private final Main plugin;

    public ApproachTask(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        List<Relay> relays = plugin.getRelayManager().getAllRelays();
        if (relays.isEmpty()) {
            return;
        }

        boolean changed = false;
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Location playerLoc = player.getLocation();
            for (Relay relay : relays) {
                if (!relay.getWorldName().equals(player.getWorld().getName())) {
                    continue;
                }
                Location relayLoc = relay.getLocation();
                if (relayLoc == null) {
                    continue;
                }
                if (relayLoc.distanceSquared(playerLoc) <= RADIUS * RADIUS) {
                    relay.setLastApproach(player.getName(), System.currentTimeMillis());
                    changed = true;
                }
            }
        }

        if (changed) {
            plugin.getRelayManager().save();
        }
    }
}
