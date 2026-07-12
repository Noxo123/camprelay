package com.novusmc.camprelay.tasks;

import com.novusmc.camprelay.Main;
import com.novusmc.camprelay.model.Relay;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Applique en permanence les effets de zone (mini-beacon) autour de chaque Camp Relay.
 * S'exécute toutes les 5 secondes (100 ticks).
 */
public class EffectTask extends BukkitRunnable {

    private static final double RADIUS = 20.0;
    private static final int DURATION_TICKS = 140; // un peu plus que 5s pour éviter les coupures

    private final Main plugin;

    public EffectTask(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        List<Relay> relays = plugin.getRelayManager().getAllRelays();
        if (relays.isEmpty()) {
            return;
        }

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
                    applyEffects(player);
                    break;
                }
            }
        }
    }

    private void applyEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, DURATION_TICKS, 0, true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, DURATION_TICKS, 0, true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, DURATION_TICKS, 0, true, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, DURATION_TICKS, 0, true, false, true));
    }
}
