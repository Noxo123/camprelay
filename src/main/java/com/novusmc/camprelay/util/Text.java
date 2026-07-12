package com.novusmc.camprelay.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Petit helper de formatage de texte (couleurs & lore).
 */
public final class Text {

    private Text() {
    }

    public static String c(String input) {
        if (input == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static List<String> lore(String... lines) {
        List<String> result = new ArrayList<>(Arrays.asList(lines));
        result.replaceAll(Text::c);
        return result;
    }

    public static final String PREFIX = c("&8[&bCamp&3Relay&8] &r");
}
