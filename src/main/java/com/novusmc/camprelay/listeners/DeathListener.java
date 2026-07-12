package com.novusmc.camprelay.listeners;

import com.novusmc.camprelay.Main;
import com.novusmc.camprelay.model.Relay;
import com.novusmc.camprelay.util.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère le stockage d'urgence : si un joueur meurt à moins de 150 blocs d'un Camp Relay,
 * ses items sont aspirés dans son stockage personnel au lieu de tomber au sol.
 */
public class DeathListener implements Listener {

    private static final double RADIUS = 150.0;

    private final Main plugin;

    public DeathListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Relay relay = plugin.getRelayManager().getNearestRelay(player.getLocation(), RADIUS);
        if (relay == null) {
            return; // Comportement vanilla : les items tombent au sol.
        }

        List<ItemStack> drops = new ArrayList<>(event.getDrops());
        if (drops.isEmpty()) {
            return;
        }

        List<ItemStack> overflow = plugin.getStorageManager().addItems(player.getUniqueId(), drops);

        event.getDrops().clear();
        event.getDrops().addAll(overflow);

        relay.setLastDeath(player.getName(), System.currentTimeMillis());
        plugin.getRelayManager().save();

        String msg = overflow.isEmpty()
                ? "&bTes affaires ont ete aspirees par un Camp Relay proche. Utilise /cr storage pour les recuperer."
                : "&bTes affaires ont ete partiellement aspirees par un Camp Relay proche (stockage plein, certains items sont restes au sol).";
        plugin.getServer().getScheduler().runTask(plugin, () -> player.sendMessage(Text.PREFIX + Text.c(msg)));
    }
}
