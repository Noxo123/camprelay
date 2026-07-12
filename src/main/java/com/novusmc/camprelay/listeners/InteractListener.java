package com.novusmc.camprelay.listeners;

import com.novusmc.camprelay.Main;
import com.novusmc.camprelay.model.Relay;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Ouvre le menu principal CampRelay lors d'un clic droit (sneak) sur un Camp Relay.
 */
public class InteractListener implements Listener {

    private final Main plugin;

    public InteractListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.LODESTONE) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }

        Relay relay = plugin.getRelayManager().getRelayAtBlock(block.getLocation());
        if (relay == null) {
            return;
        }

        if (!player.hasPermission("camprelay.use")) {
            return;
        }

        event.setCancelled(true);
        player.openInventory(plugin.getGuiManager().buildMainMenu(player));
    }
}
