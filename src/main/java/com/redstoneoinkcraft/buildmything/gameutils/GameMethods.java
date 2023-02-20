package com.redstoneoinkcraft.buildmything.gameutils;

import com.redstoneoinkcraft.buildmything.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * BuildMyThing created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class GameMethods {

    private static GameMethods instance;
    String prefix = Main.getInstance().getPrefix();

    public static GameMethods getInstance() {
        if (instance == null) {
            instance = new GameMethods();
        }
        return instance;
    }

    private ArrayList<ActiveArenaObject> loadedArenas = new ArrayList<>(2);

    public void createArena(String name) {
        ActiveArenaObject currentArena = new ActiveArenaObject(name, 3, 60); // Create arena with default values
        loadedArenas.add(currentArena);
    }

    public ActiveArenaObject getArenaByName(String name) {
        for (ActiveArenaObject arena : loadedArenas) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }

    public ActiveArenaObject getArenaByPlayer(Player player) {
        if (!playersInGames.contains(player))
            return null;

        for (ActiveArenaObject arena : loadedArenas) {
            if (arena.isPlayerInArena(player))
                return arena;
        }

        return null; // We should never hit this line, but we do need it
    }

    // Little wrapper methods of sorts
    ArrayList<Player> playersInGames = new ArrayList<>(2);

    public boolean addPlayerToGame(Player player, ActiveArenaObject arena) {
        if (playersInGames.contains(player)) {
            player.sendMessage(
                    prefix + "You appear to already be in a game (contact an admin if you believe this is a problem).");
            return false;
        }
        if (playersInGames.size() >= getMaxPlayersPergame()) {
            player.sendMessage(prefix + "Sorry, that game is currently full!");
            return false;
        }
        if (arena == null) {
            player.sendMessage(prefix + ChatColor.RED
                    + "This arena doesn't seem to be set up properly. Please contact a staff member!");
            return false;
        } else {
            arena.addPlayerToArena(player);
            playersInGames.add(player);
            return true;
        }
    }

    public void removePlayerFromGame(Player player, ActiveArenaObject arena) {
        if (playersInGames.contains(player))
            playersInGames.remove(player);
        player.teleport(player.getWorld().getSpawnLocation()); // Should suffice
    }

    private int maxPlayersPergame = 12;

    public int getMaxPlayersPergame() {
        return maxPlayersPergame;
    }
}
