package fr.lobbyredirect.commands;

import fr.lobbyredirect.LobbyRedirect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LobbyRedirectCommand implements CommandExecutor, TabCompleter {

    private final LobbyRedirect plugin;

    public LobbyRedirectCommand(LobbyRedirect plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lobbyredirect.admin")) {
            sender.sendMessage(LobbyRedirect.colorize("&cVous n'avez pas la permission d'exécuter cette commande."));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                return handleReload(sender);
            case "lobby":
                return handleLobby(sender, args);
            case "status":
                return handleStatus(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(LobbyRedirect.colorize("&6&l=== LobbyRedirect - Aide ==="));
        sender.sendMessage(LobbyRedirect.colorize("&e/lobbyredirect reload &7- Recharge la configuration"));
        sender.sendMessage(LobbyRedirect.colorize("&e/lobbyredirect lobby [joueur] &7- Redirige un joueur vers le lobby"));
        sender.sendMessage(LobbyRedirect.colorize("&e/lobbyredirect status &7- Affiche le statut du plugin"));
    }

    private boolean handleReload(CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage(LobbyRedirect.colorize("&a[OK] &eConfiguration rechargée avec succès!"));
        sender.sendMessage(LobbyRedirect.colorize("&7Serveur lobby: &f" + plugin.getConfig().getString("lobby-server", "lobby")));
        return true;
    }

    private boolean handleLobby(CommandSender sender, String[] args) {
        Player targetPlayer;

        if (args.length > 1) {
            targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                sender.sendMessage(LobbyRedirect.colorize("&cJoueur introuvable: " + args[1]));
                return true;
            }
        } else {
            if (sender instanceof Player) {
                targetPlayer = (Player) sender;
            } else {
                sender.sendMessage(LobbyRedirect.colorize("&cSpécifiez un joueur: /lobbyredirect lobby <joueur>"));
                return true;
            }
        }

        plugin.redirectToLobby(targetPlayer);
        sender.sendMessage(LobbyRedirect.colorize("&a[OK] &e" + targetPlayer.getName() + " &7redirigé vers le lobby."));
        return true;
    }

    private boolean handleStatus(CommandSender sender) {
        sender.sendMessage(LobbyRedirect.colorize("&6&l=== LobbyRedirect - Statut ==="));
        sender.sendMessage(LobbyRedirect.colorize("&eVersion: &f" + plugin.getDescription().getVersion()));
        sender.sendMessage(LobbyRedirect.colorize("&eServeur lobby: &f" + plugin.getConfig().getString("lobby-server", "lobby")));
        sender.sendMessage(LobbyRedirect.colorize("&eJoueurs en ligne: &f" + Bukkit.getOnlinePlayers().size()));
        sender.sendMessage(LobbyRedirect.colorize("&eMode kick: &f" + (plugin.getConfig().getBoolean("kick-instead-of-redirect") ? "Activé" : "Désactivé")));
        sender.sendMessage(LobbyRedirect.colorize("&eDélai de redirection: &f" + plugin.getConfig().getInt("redirect-delay", 20) + " ticks"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("reload", "lobby", "status"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("lobby")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }

        String input = args[args.length - 1].toLowerCase();
        completions.removeIf(s -> !s.toLowerCase().startsWith(input));
        return completions;
    }
}