package fr.lobbyredirect.listeners;

import fr.lobbyredirect.LobbyRedirect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final LobbyRedirect plugin;

    public PlayerListener(LobbyRedirect plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Optionnel: Si le joueur est dans un monde initial et qu'il n'a pas de permission,
        // on pourrait le rediriger automatiquement vers le lobby
        // Décommenter si souhaité
        /*
        if (!player.hasPermission("lobbyredirect.bypass") && plugin.isFromInitialWorld(player)) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    plugin.redirectToLobby(player);
                }
            }, 20L); // 1 seconde de délai
        }
        */
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Nettoyage si nécessaire
    }
}