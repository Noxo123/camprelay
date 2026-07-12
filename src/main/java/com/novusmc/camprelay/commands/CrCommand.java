package com.novusmc.camprelay.commands;

import com.novusmc.camprelay.Main;
import com.novusmc.camprelay.util.ItemFactory;
import com.novusmc.camprelay.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gère la commande /cr (alias /camprelay) et ses sous-commandes.
 */
public class CrCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public CrCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Text.PREFIX + Text.c("&cCette commande doit etre executee par un joueur."));
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("camprelay.use")) {
                player.sendMessage(Text.PREFIX + Text.c("&cTu n'as pas la permission d'utiliser CampRelay."));
                return true;
            }
            player.openInventory(plugin.getGuiManager().buildMainMenu(player));
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "give" -> handleGive(sender, args);
            case "tp" -> handleTp(sender);
            case "storage" -> handleStorage(sender);
            case "log" -> handleLog(sender);
            case "reload" -> handleReload(sender);
            case "help" -> sendHelp(sender);
            default -> sendHelp(sender);
        }
        return true;
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("camprelay.admin")) {
            sender.sendMessage(Text.PREFIX + Text.c("&cTu n'as pas la permission d'utiliser cette commande."));
            return;
        }
        if (args.length < 2 || !args[1].equalsIgnoreCase("camp")) {
            sender.sendMessage(Text.PREFIX + Text.c("&cUsage: /cr give camp [joueur]"));
            return;
        }

        Player target;
        if (args.length >= 3) {
            target = Bukkit.getPlayerExact(args[2]);
            if (target == null) {
                sender.sendMessage(Text.PREFIX + Text.c("&cJoueur introuvable : " + args[2]));
                return;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(Text.PREFIX + Text.c("&cPrecise un joueur : /cr give camp <joueur>"));
            return;
        }

        target.getInventory().addItem(ItemFactory.campRelayItem());
        target.sendMessage(Text.PREFIX + Text.c("&aTu as recu un Camp Relay !"));
        if (sender != target) {
            sender.sendMessage(Text.PREFIX + Text.c("&aCamp Relay donne a " + target.getName() + "."));
        }
    }

    private void handleTp(CommandSender sender) {
        Player player = requirePlayer(sender);
        if (player == null) {
            return;
        }
        if (!player.hasPermission("camprelay.tp")) {
            player.sendMessage(Text.PREFIX + Text.c("&cTu n'as pas la permission d'utiliser le reseau de teleportation."));
            return;
        }
        player.openInventory(plugin.getGuiManager().buildTeleportMenu(player));
    }

    private void handleStorage(CommandSender sender) {
        Player player = requirePlayer(sender);
        if (player == null) {
            return;
        }
        if (!player.hasPermission("camprelay.storage")) {
            player.sendMessage(Text.PREFIX + Text.c("&cTu n'as pas la permission d'acceder au stockage."));
            return;
        }
        plugin.getMenuActions().openStorage(player);
    }

    private void handleLog(CommandSender sender) {
        Player player = requirePlayer(sender);
        if (player == null) {
            return;
        }
        if (!player.hasPermission("camprelay.use")) {
            player.sendMessage(Text.PREFIX + Text.c("&cTu n'as pas la permission d'utiliser CampRelay."));
            return;
        }
        player.openInventory(plugin.getGuiManager().buildLogMenu(player));
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("camprelay.admin")) {
            sender.sendMessage(Text.PREFIX + Text.c("&cTu n'as pas la permission d'utiliser cette commande."));
            return;
        }
        plugin.getRelayManager().load();
        plugin.getPlayerDataManager().load();
        sender.sendMessage(Text.PREFIX + Text.c("&aDonnees CampRelay rechargees."));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Text.c("&8&m----------&r " + Text.c("&b&lCampRelay") + " &8&m----------"));
        sender.sendMessage(Text.c("&b/cr &7- Ouvre le menu principal"));
        sender.sendMessage(Text.c("&b/cr give camp [joueur] &7- Donne un Camp Relay &8(admin)"));
        sender.sendMessage(Text.c("&b/cr tp &7- Ouvre le reseau de teleportation"));
        sender.sendMessage(Text.c("&b/cr storage &7- Ouvre ton stockage d'urgence"));
        sender.sendMessage(Text.c("&b/cr log &7- Ouvre le journal de tes relais"));
        sender.sendMessage(Text.c("&b/cr reload &7- Recharge les donnees &8(admin)"));
        sender.sendMessage(Text.c("&8&m--------------------------------"));
    }

    private Player requirePlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Text.PREFIX + Text.c("&cCette commande doit etre executee par un joueur."));
            return null;
        }
        return (Player) sender;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(Arrays.asList("give", "tp", "storage", "log", "reload", "help"), args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return filter(List.of("camp"), args[1]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give") && args[1].equalsIgnoreCase("camp")) {
            return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[2]);
        }
        return new ArrayList<>();
    }

    private List<String> filter(List<String> options, String prefix) {
        return options.stream()
                .filter(o -> o.toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }
}
