package com.novusmc.camprelay.gui;

import com.novusmc.camprelay.Main;
import com.novusmc.camprelay.model.PlayerData;
import com.novusmc.camprelay.model.Relay;
import com.novusmc.camprelay.model.RespawnMode;
import com.novusmc.camprelay.util.ItemFactory;
import com.novusmc.camprelay.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Construit tous les menus (GUI) du plugin CampRelay.
 */
public class GuiManager {

    public static final String MAIN_TITLE = Text.c("&8&lCamp Relay &7- Menu");
    public static final String RESPAWN_TITLE = Text.c("&8&lCamp Relay &7- Respawn");
    public static final String TELEPORT_TITLE = Text.c("&8&lCamp Relay &7- Reseau");
    public static final String LOG_TITLE = Text.c("&8&lCamp Relay &7- Journal");
    public static final String SETTINGS_TITLE = Text.c("&8&lCamp Relay &7- Parametres");
    public static final String STORAGE_TITLE = Text.c("&8&lCamp Relay &7- Stockage");

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM HH:mm");

    private final Main plugin;

    public GuiManager(Main plugin) {
        this.plugin = plugin;
    }

    private ItemStack backButton() {
        return ItemFactory.icon(Material.BARRIER, "&cRetour au menu");
    }

    public Inventory buildMainMenu(Player player) {
        MainMenuHolder holder = new MainMenuHolder();
        Inventory inv = Bukkit.createInventory(holder, 27, MAIN_TITLE);
        holder.setInventory(inv);

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, ItemFactory.filler());
        }

        inv.setItem(10, ItemFactory.icon(Material.AMETHYST_SHARD, "&d&lRespawn",
                "&7Choisis ton mode", "&7de respawn."));
        inv.setItem(12, ItemFactory.icon(Material.COMPASS, "&b&lTeleportation",
                "&7Voyage entre tes", "&7Camp Relay."));
        inv.setItem(14, ItemFactory.icon(Material.BOOK, "&e&lStockage",
                "&7Recupere tes items", "&7sauves par un relais."));
        inv.setItem(16, ItemFactory.icon(Material.CLOCK, "&6&lJournal",
                "&7Historique de tes", "&7Camp Relay."));
        inv.setItem(22, ItemFactory.icon(Material.NETHER_STAR, "&3&lParametres",
                "&7Choisis quel Camp Relay", "&7utiliser pour le respawn."));

        return inv;
    }

    public Inventory buildSettingsMenu(Player player) {
        SettingsMenuHolder holder = new SettingsMenuHolder();
        Inventory inv = Bukkit.createInventory(holder, 27, RESPAWN_TITLE);
        holder.setInventory(inv);

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, ItemFactory.filler());
        }

        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());
        RespawnMode mode = data.getRespawnMode();

        inv.setItem(11, ItemFactory.icon(Material.NETHER_STAR,
                (mode == RespawnMode.CAMP_RELAY ? "&a&lCamp Relay (actif)" : "&f&lCamp Relay"),
                "&7Respawn au Camp Relay", "&7selectionne dans l'onglet Respawn."));
        inv.setItem(13, ItemFactory.icon(Material.RED_BED,
                (mode == RespawnMode.BED ? "&a&lLit (actif)" : "&f&lLit"),
                "&7Respawn classique au lit."));
        inv.setItem(15, ItemFactory.icon(Material.CLOCK,
                (mode == RespawnMode.LAST_CAMP_RELAY ? "&a&lDernier Camp Relay (actif)" : "&f&lDernier Camp Relay"),
                "&7Respawn au dernier", "&7Camp Relay utilise."));

        inv.setItem(22, backButton());
        return inv;
    }

    public Inventory buildRespawnMenu(Player player) {
        RespawnMenuHolder holder = new RespawnMenuHolder();
        Inventory inv = Bukkit.createInventory(holder, 54, SETTINGS_TITLE);
        holder.setInventory(inv);

        List<Relay> relays = plugin.getRelayManager().getRelaysByOwner(player.getUniqueId());
        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());

        if (relays.isEmpty()) {
            inv.setItem(22, ItemFactory.icon(Material.BARRIER, "&cAucun Camp Relay",
                    "&7Tu n'as pas encore place", "&7de Camp Relay."));
        } else {
            int slot = 0;
            for (Relay relay : relays) {
                boolean selected = relay.getId().equals(data.getSelectedRelayId());
                ItemStack item = ItemFactory.icon(Material.LODESTONE,
                        (selected ? "&a&lCamp Relay (selectionne)" : "&b&lCamp Relay"),
                        "&7Monde: &f" + relay.getWorldName(),
                        "&7Coord: &f" + relay.getX() + ", " + relay.getY() + ", " + relay.getZ(),
                        "",
                        "&eClique pour selectionner");
                inv.setItem(slot, item);
                holder.getSlotToRelay().put(slot, relay.getId());
                slot++;
                if (slot >= 53) {
                    break;
                }
            }
        }

        inv.setItem(53, backButton());
        return inv;
    }

    public Inventory buildTeleportMenu(Player player) {
        TeleportMenuHolder holder = new TeleportMenuHolder();
        Inventory inv = Bukkit.createInventory(holder, 54, TELEPORT_TITLE);
        holder.setInventory(inv);

        List<Relay> relays = plugin.getRelayManager().getRelaysByOwner(player.getUniqueId());

        if (relays.isEmpty()) {
            inv.setItem(22, ItemFactory.icon(Material.BARRIER, "&cAucun Camp Relay",
                    "&7Tu n'as pas encore place", "&7de Camp Relay."));
        } else {
            int slot = 0;
            for (Relay relay : relays) {
                ItemStack item = ItemFactory.icon(Material.COMPASS, "&b&lCamp Relay",
                        "&7Monde: &f" + relay.getWorldName(),
                        "&7Coord: &f" + relay.getX() + ", " + relay.getY() + ", " + relay.getZ(),
                        "",
                        "&7Cout: &f5 niveaux d'XP",
                        "&7Cooldown: &f30 secondes",
                        "",
                        "&eClique pour te teleporter");
                inv.setItem(slot, item);
                holder.getSlotToRelay().put(slot, relay.getId());
                slot++;
                if (slot >= 53) {
                    break;
                }
            }
        }

        inv.setItem(53, backButton());
        return inv;
    }

    public Inventory buildLogMenu(Player player) {
        LogMenuHolder holder = new LogMenuHolder();
        Inventory inv = Bukkit.createInventory(holder, 54, LOG_TITLE);
        holder.setInventory(inv);

        List<Relay> relays = plugin.getRelayManager().getRelaysByOwner(player.getUniqueId());

        if (relays.isEmpty()) {
            inv.setItem(22, ItemFactory.icon(Material.BARRIER, "&cAucun Camp Relay",
                    "&7Tu n'as pas encore place", "&7de Camp Relay."));
        } else {
            int slot = 0;
            for (Relay relay : relays) {
                ItemStack item = ItemFactory.icon(Material.CLOCK, "&6&lJournal du relais",
                        "&7Coord: &f" + relay.getX() + ", " + relay.getY() + ", " + relay.getZ(),
                        "",
                        "&cDerniere mort: &f" + formatEntry(relay.getLastDeathPlayer(), relay.getLastDeathTime()),
                        "&aDernier joueur approche: &f" + formatEntry(relay.getLastApproachPlayer(), relay.getLastApproachTime()),
                        "&bDernier relais utilise: &f" + formatEntry(relay.getLastUsedPlayer(), relay.getLastUsedTime()),
                        "&eDernier acces stockage: &f" + formatEntry(relay.getLastStorageAccessPlayer(), relay.getLastStorageAccessTime())
                );
                inv.setItem(slot, item);
                slot++;
                if (slot >= 53) {
                    break;
                }
            }
        }

        inv.setItem(53, backButton());
        return inv;
    }

    private String formatEntry(String playerName, long time) {
        if (playerName == null || time <= 0) {
            return "Aucun";
        }
        return playerName + " (" + DATE_FORMAT.format(new Date(time)) + ")";
    }

    public Inventory buildStorageMenu(UUID owner, ItemStack[] contents) {
        StorageMenuHolder holder = new StorageMenuHolder(owner);
        Inventory inv = Bukkit.createInventory(holder, 54, STORAGE_TITLE);
        holder.setInventory(inv);
        inv.setContents(contents);
        return inv;
    }
}
