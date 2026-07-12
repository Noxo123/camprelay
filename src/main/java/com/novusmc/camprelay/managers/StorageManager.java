package com.novusmc.camprelay.managers;

import com.novusmc.camprelay.Main;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Gère le stockage d'urgence (inventaire de 54 slots) de chaque joueur.
 * Fichier : plugins/CampRelay/data/storage/UUID.yml
 */
public class StorageManager {

    public static final int SIZE = 54;

    private final Main plugin;
    private final File storageDir;

    public StorageManager(Main plugin) {
        this.plugin = plugin;
        this.storageDir = new File(new File(plugin.getDataFolder(), "data"), "storage");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
    }

    private File fileFor(UUID uuid) {
        return new File(storageDir, uuid.toString() + ".yml");
    }

    public ItemStack[] load(UUID uuid) {
        ItemStack[] contents = new ItemStack[SIZE];
        File file = fileFor(uuid);
        if (!file.exists()) {
            return contents;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (int i = 0; i < SIZE; i++) {
            Object obj = config.get("items." + i);
            if (obj instanceof ItemStack) {
                contents[i] = (ItemStack) obj;
            }
        }
        return contents;
    }

    public void save(UUID uuid, ItemStack[] contents) {
        YamlConfiguration config = new YamlConfiguration();
        for (int i = 0; i < contents.length && i < SIZE; i++) {
            if (contents[i] != null) {
                config.set("items." + i, contents[i]);
            }
        }
        try {
            config.save(fileFor(uuid));
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Impossible de sauvegarder le stockage de " + uuid, ex);
        }
    }

    /**
     * Ajoute des items dans le premier stockage disponible du joueur.
     * Retourne la liste des items n'ayant pas pu être stockés (stockage plein).
     */
    public List<ItemStack> addItems(UUID uuid, List<ItemStack> items) {
        ItemStack[] contents = load(uuid);
        List<ItemStack> overflow = new ArrayList<>();

        for (ItemStack item : items) {
            if (item == null) {
                continue;
            }
            boolean placed = false;
            for (int i = 0; i < SIZE; i++) {
                if (contents[i] == null) {
                    contents[i] = item;
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                overflow.add(item);
            }
        }

        save(uuid, contents);
        return overflow;
    }
}
