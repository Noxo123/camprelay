package com.novusmc.camprelay.managers;

import com.novusmc.camprelay.Main;
import com.novusmc.camprelay.model.Relay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Actions partagées entre les commandes /cr et les clics de GUI.
 */
public class MenuActions {

    private static final double LOG_RADIUS = 150.0;

    private final Main plugin;

    public MenuActions(Main plugin) {
        this.plugin = plugin;
    }

    public void openStorage(Player player) {
        ItemStack[] contents = plugin.getStorageManager().load(player.getUniqueId());
        player.openInventory(plugin.getGuiManager().buildStorageMenu(player.getUniqueId(), contents));

        Relay nearest = plugin.getRelayManager().getNearestRelay(player.getLocation(), LOG_RADIUS);
        if (nearest != null) {
            nearest.setLastStorageAccess(player.getName(), System.currentTimeMillis());
            plugin.getRelayManager().save();
        }
    }
}
