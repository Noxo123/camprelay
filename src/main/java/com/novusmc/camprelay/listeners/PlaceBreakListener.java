package com.novusmc.camprelay.listeners;

import com.novusmc.camprelay.Main;
import com.novusmc.camprelay.model.PlayerData;
import com.novusmc.camprelay.model.Relay;
import com.novusmc.camprelay.util.ItemFactory;
import com.novusmc.camprelay.util.Text;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Gère la pose et la casse du bloc Camp Relay (Lodestone marqué).
 */
public class PlaceBreakListener implements Listener {

    private final Main plugin;

    public PlaceBreakListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.LODESTONE) {
            return;
        }
        if (!ItemFactory.isCampRelayItem(event.getItemInHand())) {
            return;
        }

        Player player = event.getPlayer();
        Relay relay = plugin.getRelayManager().createRelay(player.getUniqueId(), event.getBlock().getLocation());

        player.sendMessage(Text.PREFIX + Text.c("&aTon Camp Relay a ete enregistre !"));
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.LODESTONE) {
            return;
        }

        Relay relay = plugin.getRelayManager().getRelayAtBlock(event.getBlock().getLocation());
        if (relay == null) {
            return;
        }

        Player player = event.getPlayer();
        boolean isOwner = relay.getOwner().equals(player.getUniqueId());
        if (!isOwner && !player.hasPermission("camprelay.admin")) {
            event.setCancelled(true);
            player.sendMessage(Text.PREFIX + Text.c("&cSeul le proprietaire peut detruire ce Camp Relay."));
            return;
        }

        // Retire les références au relais chez tous les joueurs qui l'utilisaient.
        for (PlayerData data : plugin.getPlayerDataManager().all()) {
            if (relay.getId().equals(data.getSelectedRelayId())) {
                data.setSelectedRelayId(null);
            }
            if (relay.getId().equals(data.getLastCampRelayId())) {
                data.setLastCampRelayId(null);
            }
        }
        plugin.getPlayerDataManager().save();

        plugin.getRelayManager().removeRelay(relay.getId());

        event.setDropItems(false);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), ItemFactory.campRelayItem());
        player.sendMessage(Text.PREFIX + Text.c("&eCamp Relay retire."));
    }
}
