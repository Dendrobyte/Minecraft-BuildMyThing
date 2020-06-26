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

    private GameMethods instance;
    String prefix = Main.getInstance().getPrefix();

    public GameMethods getInstance() {
        if(instance == null){
            instance = new GameMethods();
        }
        return instance;
    }

    private ArrayList<ActiveArenaObject> loadedArenas = new ArrayList<>(2);
    public void createArena(String name){
        ActiveArenaObject currentArena = new ActiveArenaObject(name, 3, 60); // Create arena with default values
        loadedArenas.add(currentArena);
    }

    public ActiveArenaObject getArenaByName(String name){
        for(ActiveArenaObject arena : loadedArenas){
            if(arena.getName().equalsIgnoreCase(name)){
                return arena;
            }
        }
        return null;
    }

    public boolean addPlayerToGame(Player player, String arenaName){
        ActiveArenaObject arena = getArenaByName(arenaName);
        if(arena == null){
            player.sendMessage(prefix + ChatColor.RED + "This arena doesn't seem to be set up properly. Please contact a staff member!");
            return false;
        } else {
            arena.addPlayerToArena(player);
            return true;
        }
    }
}
