package com.novusmc.camprelay.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * Construit les ItemStack utilisés par le plugin (item Camp Relay, icônes de GUI).
 */
public final class ItemFactory {

    private ItemFactory() {
    }

    /**
     * Construit l'item Camp Relay (Lodestone renommé + lore + PDC de marquage).
     */
    public static ItemStack campRelayItem() {
        ItemStack item = new ItemStack(Material.LODESTONE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Text.c("&b&lCamp Relay"));
            meta.setLore(Text.lore(
                    "&7Centre logistique personnel",
                    "&7Relais de survie avance",
                    "&3NovusMC SMP"
            ));
            meta.getPersistentDataContainer().set(Keys.CAMP_RELAY_ITEM, PersistentDataType.BOOLEAN, true);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static boolean isCampRelayItem(ItemStack item) {
        if (item == null || item.getType() != Material.LODESTONE || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        Boolean flag = meta.getPersistentDataContainer().get(Keys.CAMP_RELAY_ITEM, PersistentDataType.BOOLEAN);
        return flag != null && flag;
    }

    public static ItemStack icon(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Text.c(name));
            if (lore.length > 0) {
                meta.setLore(Text.lore(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack filler() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack namedList(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Text.c(name));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
