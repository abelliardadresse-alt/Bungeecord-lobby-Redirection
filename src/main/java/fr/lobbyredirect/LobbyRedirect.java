package fr.lobbyredirect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import fr.lobbyredirect.commands.LobbyRedirectCommand;
import fr.lobbyredirect.listeners.PlayerListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LobbyRedirect extends JavaPlugin implements Listener {

    private static LobbyRedirect instance;
    private List<String> initialWorlds;
    private boolean isShuttingDown = false;

    @Override
    public void onEnable() {
        instance = this;
        
        loadConfig();
        
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveResource("config.yml", false);
        }
        
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        LobbyRedirectCommand lobbyCommand = new LobbyRedirectCommand(this);
        getCommand("lobbyredirect").setExecutor(lobbyCommand);
        getCommand("lobbyredirect").setTabCompleter(lobbyCommand);
        
        getLogger().info("LobbyRedirect a été activé!");
        getLogger().info("Serveur lobby configuré: " + getConfig().getString("lobby-server", "lobby"));
    }

    @Override
    public void onDisable() {
        getLogger().info("LobbyRedirect va désactiver le plugin...");
    }

    private void loadConfig() {
        saveDefaultConfig();
        reloadConfig();
        
        initialWorlds = getConfig().getStringList("initial-worlds");
        if (initialWorlds.isEmpty()) {
            initialWorlds = new ArrayList<>();
            initialWorlds.add("world");
            initialWorlds.add("world_nether");
            initialWorlds.add("world_the_end");
        }
    }

    public void redirectToLobby(Player player) {
        if (!getConfig().getBoolean("kick-instead-of-redirect", false)) {
            String lobbyServer = getConfig().getString("lobby-server", "lobby");
            String message = getConfig().getString("redirect-message", "");
            
            if (!message.isEmpty()) {
                player.sendMessage(colorize(message));
            }
            
            // Exécuter la commande /server <lobby> pour le joueur
            Bukkit.getScheduler().runTask(this, () -> {
                player.performCommand("server " + lobbyServer);
            });
        } else {
            String kickMessage = getConfig().getString("kick-message", "Serveur en maintenance");
            player.kickPlayer(colorize(kickMessage));
        }
    }

    public void redirectAllPlayersToLobby() {
        isShuttingDown = true;
        
        // Récupérer la liste des joueurs avant de les rediriger
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        
        for (Player player : players) {
            if (hasBypassPermission(player)) {
                getLogger().info("Joueur " + player.getName() + " bypass la redirection");
                continue;
            }
            getLogger().info("Redirection de " + player.getName() + " vers " + getConfig().getString("lobby-server", "lobby"));
            redirectToLobby(player);
        }
    }

    private boolean hasBypassPermission(Player player) {
        if (!getConfig().getBoolean("bypass-permission", false)) {
            return false;
        }
        return player.hasPermission("lobbyredirect.bypass");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerCommand(ServerCommandEvent event) {
        String command = event.getCommand().toLowerCase();
        if (!isShuttingDown && command.equals("stop")) {
            getLogger().info("Détection de la commande /stop, redirection des joueurs vers le lobby...");
            
            // Annuler la commande stop pour laisser le temps à la redirection
            event.setCancelled(true);
            
            // Rediriger tous les joueurs
            redirectAllPlayersToLobby();
            
            // Programmer l'arrêt du serveur après un délai
            Bukkit.getScheduler().runTaskLater(this, () -> {
                getLogger().info("Arrêt du serveur après redirection...");
                Bukkit.shutdown();
            }, 60L); // 3 secondes de délai
        }
    }

    public boolean isFromInitialWorld(Player player) {
        if (initialWorlds == null || initialWorlds.isEmpty()) {
            return true;
        }
        String worldName = player.getWorld().getName();
        return initialWorlds.contains(worldName);
    }

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static LobbyRedirect getInstance() {
        return instance;
    }

    public List<String> getInitialWorlds() {
        return initialWorlds;
    }

    public boolean isShuttingDown() {
        return isShuttingDown;
    }
}