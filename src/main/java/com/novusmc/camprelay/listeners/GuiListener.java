package com.novusmc.camprelay.listeners;

import com.novusmc.camprelay.Main;
import com.novusmc.camprelay.gui.*;
import com.novusmc.camprelay.model.PlayerData;
import com.novusmc.camprelay.model.Relay;
import com.novusmc.camprelay.model.RespawnMode;
import com.novusmc.camprelay.util.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Gère les clics et fermetures d'inventaire pour tous les menus CampRelay.
 */
public class GuiListener implements Listener {

    private final Main plugin;

    public GuiListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof CrHolder)) {
            return;
        }

        // Le stockage se comporte comme un vrai coffre : on autorise le déplacement d'items.
        if (holder instanceof StorageMenuHolder) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        int slot = event.getRawSlot();

        if (clicked != null && clicked.getType() == Material.BARRIER) {
            openMain(player);
            return;
        }

        if (holder instanceof MainMenuHolder) {
            handleMainMenuClick(player, slot);
        } else if (holder instanceof SettingsMenuHolder) {
            handleModeMenuClick(player, slot);
        } else if (holder instanceof RespawnMenuHolder) {
            handleRelaySelectClick(player, slot, (RespawnMenuHolder) holder);
        } else if (holder instanceof TeleportMenuHolder) {
            handleTeleportClick(player, slot, (TeleportMenuHolder) holder);
        } else if (holder instanceof LogMenuHolder) {
            // Menu de lecture seule.
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof StorageMenuHolder) {
            StorageMenuHolder storageHolder = (StorageMenuHolder) holder;
            plugin.getStorageManager().save(storageHolder.getOwner(), event.getInventory().getContents());
        }
    }

    private void openMain(Player player) {
        player.openInventory(plugin.getGuiManager().buildMainMenu(player));
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.7f, 1f);
    }

    private void handleMainMenuClick(Player player, int slot) {
        switch (slot) {
            case 10 -> player.openInventory(plugin.getGuiManager().buildSettingsMenu(player));
            case 12 -> player.openInventory(plugin.getGuiManager().buildTeleportMenu(player));
            case 14 -> plugin.getMenuActions().openStorage(player);
            case 16 -> player.openInventory(plugin.getGuiManager().buildLogMenu(player));
            case 22 -> player.openInventory(plugin.getGuiManager().buildRespawnMenu(player));
            default -> {
            }
        }
        if (slot == 10 || slot == 12 || slot == 16 || slot == 22) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.7f, 1f);
        }
    }

    private void handleModeMenuClick(Player player, int slot) {
        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());
        RespawnMode newMode = null;
        if (slot == 11) {
            newMode = RespawnMode.CAMP_RELAY;
        } else if (slot == 13) {
            newMode = RespawnMode.BED;
        } else if (slot == 15) {
            newMode = RespawnMode.LAST_CAMP_RELAY;
        }
        if (newMode != null) {
            data.setRespawnMode(newMode);
            plugin.getPlayerDataManager().save();
            player.sendMessage(Text.PREFIX + Text.c("&aMode de respawn mis a jour : &f" + newMode.name()));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            player.openInventory(plugin.getGuiManager().buildSettingsMenu(player));
        }
    }

    private void handleRelaySelectClick(Player player, int slot, RespawnMenuHolder holder) {
        UUID relayId = holder.getSlotToRelay().get(slot);
        if (relayId == null) {
            return;
        }
        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());
        data.setSelectedRelayId(relayId);
        plugin.getPlayerDataManager().save();
        player.sendMessage(Text.PREFIX + Text.c("&aCamp Relay selectionne pour le respawn."));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        player.openInventory(plugin.getGuiManager().buildRespawnMenu(player));
    }

    private void handleTeleportClick(Player player, int slot, TeleportMenuHolder holder) {
        UUID relayId = holder.getSlotToRelay().get(slot);
        if (relayId == null) {
            return;
        }
        Relay relay = plugin.getRelayManager().getRelay(relayId);
        if (relay == null) {
            player.sendMessage(Text.PREFIX + Text.c("&cCe Camp Relay n'existe plus."));
            return;
        }

        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId())) {
            long remaining = plugin.getCooldownManager().getRemainingSeconds(player.getUniqueId());
            player.sendMessage(Text.PREFIX + Text.c("&cAttends encore &f" + remaining + "s &cavant de reutiliser le reseau."));
            return;
        }

        if (player.getLevel() < 5) {
            player.sendMessage(Text.PREFIX + Text.c("&cIl te faut au moins &f5 niveaux d'XP &cpour te teleporter."));
            return;
        }

        Location target = relay.getLocation();
        if (target == null || target.getWorld() == null) {
            player.sendMessage(Text.PREFIX + Text.c("&cCe Camp Relay est dans un monde introuvable."));
            return;
        }

        if (!target.getWorld().equals(player.getWorld())) {
            player.sendMessage(Text.PREFIX + Text.c("&cCe Camp Relay se trouve dans un autre monde."));
            return;
        }

        double distance = target.distance(player.getLocation());
        if (distance > 5000) {
            player.sendMessage(Text.PREFIX + Text.c("&cCe Camp Relay est trop loin (max 5000 blocs)."));
            return;
        }

        player.setLevel(player.getLevel() - 5);
        plugin.getCooldownManager().setUsed(player.getUniqueId());

        relay.setLastUsed(player.getName(), System.currentTimeMillis());
        plugin.getRelayManager().save();

        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());
        data.setLastCampRelayId(relay.getId());
        plugin.getPlayerDataManager().save();

        player.closeInventory();
        player.teleport(target);
        player.sendMessage(Text.PREFIX + Text.c("&bTeleportation vers ton Camp Relay reussie."));
        player.playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
    }
}
