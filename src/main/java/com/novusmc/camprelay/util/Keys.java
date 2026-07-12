package com.novusmc.camprelay.util;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

/**
 * Centralise les NamespacedKey utilisées pour le PersistentDataContainer.
 */
public final class Keys {

    public static NamespacedKey CAMP_RELAY_ITEM;
    public static NamespacedKey RELAY_ID;

    private Keys() {
    }

    public static void init(Plugin plugin) {
        CAMP_RELAY_ITEM = new NamespacedKey(plugin, "camp_relay_item");
        RELAY_ID = new NamespacedKey(plugin, "camp_relay_id");
    }
}
