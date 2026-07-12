package com.novusmc.camprelay.listeners;

import com.novusmc.camprelay.Main;
import com.novusmc.camprelay.model.PlayerData;
import com.novusmc.camprelay.model.Relay;
import com.novusmc.camprelay.model.RespawnMode;
import com.novusmc.camprelay.util.Text;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Gère le Smart Respawn : téléporte le joueur vers son Camp Relay selon son mode choisi.
 */
public class RespawnListener implements Listener {

    private final Main plugin;

    public RespawnListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());
        RespawnMode mode = data.getRespawnMode();

        if (mode == RespawnMode.BED) {
            // Comportement vanilla (lit / point d'apparition par défaut).
            return;
        }

        Relay relay = null;
        if (mode == RespawnMode.CAMP_RELAY) {
            relay = plugin.getRelayManager().getRelay(data.getSelectedRelayId());
        } else if (mode == RespawnMode.LAST_CAMP_RELAY) {
            relay = plugin.getRelayManager().getRelay(data.getLastCampRelayId());
        }

        if (relay == null) {
            // Pas de relais valide enregistré, on laisse le comportement vanilla.
            return;
        }

        Location location = relay.getLocation();
        if (location == null) {
            return;
        }

        event.setRespawnLocation(location);
        data.setLastCampRelayId(relay.getId());
        plugin.getPlayerDataManager().save();

        plugin.getServer().getScheduler().runTask(plugin, () ->
                player.sendMessage(Text.PREFIX + Text.c("&bVous avez ete ramene a votre Camp Relay.")));
    }
}
