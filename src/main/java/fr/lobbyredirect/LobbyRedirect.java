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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (DataOutputStream dos = new DataOutputStream(baos)) {
                dos.writeUTF("Connect");
                dos.writeUTF(lobbyServer);
            } catch (java.io.IOException e) {
                getLogger().warning("Erreur lors de la redirection: " + e.getMessage());
                return;
            }
            
            player.sendPluginMessage(this, "BungeeCord", baos.toByteArray());
        } else {
            String kickMessage = getConfig().getString("kick-message", "Serveur en maintenance");
            player.kickPlayer(colorize(kickMessage));
        }
    }

    public void redirectAllPlayersToLobby() {
        isShuttingDown = true;
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (hasBypassPermission(player)) {
                continue;
            }
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
        if (!isShuttingDown && (command.equals("stop") || command.startsWith("stop "))) {
            getLogger().info("Détection de la commande stop, redirection des joueurs...");
            redirectAllPlayersToLobby();
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