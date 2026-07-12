package com.novusmc.camprelay;

import com.novusmc.camprelay.commands.CrCommand;
import com.novusmc.camprelay.gui.GuiManager;
import com.novusmc.camprelay.listeners.*;
import com.novusmc.camprelay.managers.*;
import com.novusmc.camprelay.tasks.ApproachTask;
import com.novusmc.camprelay.tasks.EffectTask;
import com.novusmc.camprelay.util.Keys;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Point d'entrée du plugin CampRelay - NovusMC SMP.
 */
public class Main extends JavaPlugin {

    private RelayManager relayManager;
    private PlayerDataManager playerDataManager;
    private StorageManager storageManager;
    private CooldownManager cooldownManager;
    private GuiManager guiManager;
    private MenuActions menuActions;

    private int effectTaskId = -1;
    private int approachTaskId = -1;
    private int autosaveTaskId = -1;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        Keys.init(this);

        this.relayManager = new RelayManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.storageManager = new StorageManager(this);
        this.cooldownManager = new CooldownManager();
        this.guiManager = new GuiManager(this);
        this.menuActions = new MenuActions(this);

        registerCommands();
        registerListeners();
        startTasks();

        getLogger().info("CampRelay active - " + relayManager.getAllRelays().size() + " Camp Relay charges.");
    }

    @Override
    public void onDisable() {
        if (effectTaskId != -1) {
            getServer().getScheduler().cancelTask(effectTaskId);
        }
        if (approachTaskId != -1) {
            getServer().getScheduler().cancelTask(approachTaskId);
        }
        if (autosaveTaskId != -1) {
            getServer().getScheduler().cancelTask(autosaveTaskId);
        }

        if (relayManager != null) {
            relayManager.save();
        }
        if (playerDataManager != null) {
            playerDataManager.save();
        }

        getLogger().info("CampRelay desactive, donnees sauvegardees.");
    }

    private void registerCommands() {
        CrCommand crCommand = new CrCommand(this);
        var command = getCommand("cr");
        if (command != null) {
            command.setExecutor(crCommand);
            command.setTabCompleter(crCommand);
        }
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlaceBreakListener(this), this);
        pm.registerEvents(new DeathListener(this), this);
        pm.registerEvents(new RespawnListener(this), this);
        pm.registerEvents(new GuiListener(this), this);
        pm.registerEvents(new InteractListener(this), this);
    }

    private void startTasks() {
        effectTaskId = new EffectTask(this).runTaskTimer(this, 20L, 100L).getTaskId();
        approachTaskId = new ApproachTask(this).runTaskTimer(this, 20L, 100L).getTaskId();
        autosaveTaskId = getServer().getScheduler().runTaskTimer(this, () -> {
            relayManager.save();
            playerDataManager.save();
        }, 1200L, 1200L).getTaskId();
    }

    public RelayManager getRelayManager() {
        return relayManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public MenuActions getMenuActions() {
        return menuActions;
    }
}
