# 🎮 LobbyRedirect - Plugin Minecraft BungeeCord

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21-blue)](https://minecraft.net)
[![Spigot/Paper](https://img.shields.io/badge/Spigot-Paper-green)](https://www.spigotmc.org)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

## Description

Plugin Minecraft 1.21 compatible **BungeeCord** qui redirige automatiquement les joueurs vers le lobby lors de la fermeture du serveur. 

Plus de déconnexions surprises ! Vos joueurs seront automatiquement redirigés vers le serveur lobby avant l'arrêt.

## ✨ Fonctionnalités

- ✅ **Redirection automatique** vers le lobby lors du `/stop`
- ✅ **Compatible Minecraft 1.21** (Spigot / Paper / Purpur)
- ✅ **Compatible BungeeCord** / Waterfall / Velocity
- ✅ **Configuration simple** via `config.yml`
- ✅ **Messages personnalisables** avec codes couleur
- ✅ **Commandes admin** pour la gestion
- ✅ **Système de permissions** pour le bypass

## 📥 Installation

1. Téléchargez la dernière release : **[LobbyRedirect-1.0.0.jar](https://github.com/abelliardadresse-alt/Bungeecord-lobby-Redirection/releases/latest)**
2. Placez le fichier `LobbyRedirect-1.0.0.jar` dans le dossier `plugins/` de votre serveur
3. Redémarrez le serveur
4. Modifiez la configuration dans `plugins/LobbyRedirect/config.yml`

## ⚙️ Configuration

```yaml
# ============================================
#        LobbyRedirect - Configuration
# ============================================

# Le nom du serveur lobby dans BungeeCord
# (doit correspondre au nom dans config.yml de BungeeCord)
lobby-server: "lobby"

# Message envoyé aux joueurs avant la redirection
# Supports color codes: &a, &b, &c, etc.
redirect-message: "&6[Server] &eLe serveur va fermer, redirection vers le lobby..."

# Délai en ticks avant la redirection (20 ticks = 1 seconde)
redirect-delay: 20

# Si true, kick les joueurs avec un message au lieu de les rediriger
kick-instead-of-redirect: false

# Message de kick (si kick-instead-of-redirect est true)
kick-message: "&6[Server] &eLe serveur est actuellement en maintenance."
```

## 🎮 Commandes

| Commande | Description |
|----------|-------------|
| `/lobbyredirect reload` | Recharge la configuration |
| `/lobbyredirect lobby [joueur]` | Redirige un joueur vers le lobby |
| `/lobbyredirect status` | Affiche le statut du plugin |

**Permission requise:** `lobbyredirect.admin` (OP par défaut)

## 🔑 Permissions

| Permission | Description | Défaut |
|------------|-------------|--------|
| `lobbyredirect.admin` | Accès aux commandes d'administration | OP |
| `lobbyredirect.bypass` | Ne pas être redirigé à la fermeture | Désactivé |

## 📋 Configuration BungeeCord

Assurez-vous que le nom du serveur dans votre `config.yml` de BungeeCord correspond :

```yaml
servers:
  lobby:
    address: localhost:25565
    motd: "&aMon Serveur Lobby"
    restricted: false
```

## 📝 Changelog

### v1.0.0
- ✅ Version initiale
- ✅ Redirection vers le lobby à la fermeture
- ✅ Configuration complète
- ✅ Commandes d'administration
- ✅ Support des codes couleur

---

*Développé avec ❤️ pour la communauté Minecraft française*
