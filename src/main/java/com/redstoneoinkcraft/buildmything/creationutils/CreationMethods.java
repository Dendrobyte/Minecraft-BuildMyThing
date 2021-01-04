package com.redstoneoinkcraft.buildmything.creationutils;

import com.redstoneoinkcraft.buildmything.Main;
import com.redstoneoinkcraft.buildmything.gameutils.GameMethods;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;


/**
 * BuildMyThing created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class CreationMethods {

    private static CreationMethods instance;
    private String prefix = Main.getInstance().getPrefix();

    public static CreationMethods getInstance(){
        if(instance == null){
            instance = new CreationMethods();
        }
        return instance;
    }

    // The selection tool
    public ItemStack getCreationWand(){
        ItemStack creationWand = new ItemStack(Material.DIAMOND_HOE, 1);
        ItemMeta creationWandMeta = creationWand.getItemMeta();
        creationWandMeta.setDisplayName("" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "BuildMyThing Creation Brush");
        creationWandMeta.setUnbreakable(true);
        creationWand.setItemMeta(creationWandMeta);
        return creationWand;
    }

    // Initialize creation for a player
    public void beginCreation(Player player){
        changeCreationState(player, CreationStates.ARENA_SPAWN);
        player.getInventory().setItemInMainHand(getCreationWand());
        player.sendMessage(prefix + ChatColor.GREEN + ChatColor.BOLD + "STARTING CREATION! " + ChatColor.getLastColors(prefix) + ChatColor.ITALIC + "Use creation brush for ALL selections.");
        player.sendMessage(prefix + "Right click the arena's " + ChatColor.GOLD + ChatColor.BOLD + "LOBBY SPAWN LOCATION");
        player.sendMessage(prefix + ChatColor.RED + ChatColor.ITALIC + "Please Note: " + ChatColor.GRAY + ChatColor.ITALIC + "This will also be where someone respawns after finishing their buildphase.");
    }

    private HashMap<Player, CreationStates> playersCreationStates = new HashMap<Player, CreationStates>(1);

    // Change a player's creation state
    public void changeCreationState(Player player, CreationStates state){
        playersCreationStates.put(player, state);
    }

    // Get a player's current creation state
    public CreationStates getPlayerCreationState(Player player){
        return playersCreationStates.get(player);
    }

    private HashMap<Player, CreationArenaObject> playerCreationArenas = new HashMap<Player, CreationArenaObject>(1);

    // Get a players creation arena
    public CreationArenaObject getPlayerCreationArena(Player player){
        return playerCreationArenas.get(player);
    }

    // Set a players creation arena
    public void setPlayerCreationArena(Player player, CreationArenaObject arena){
        playerCreationArenas.put(player, arena);
    }

    // Edit the join sign
    public void writeJoinSign(Location signLoc, String name){
        Sign sign = (Sign) signLoc.getBlock().getState();
        sign.setLine(0, prefix.substring(0, prefix.length()-1));
        sign.setLine(1, name);
        sign.setLine(2, "" + ChatColor.GRAY + ChatColor.ITALIC + "WAITING");
        sign.setLine(3, "0/" + GameMethods.getInstance().getMaxPlayersPergame());
        sign.update();
    }

    // Wrap up creation
    public void finalizeCreation(Player player){
        getPlayerCreationArena(player).writeArenaToConfig();
        CreationArenaObject arena = playerCreationArenas.get(player);
        String name = arena.getName();
        writeJoinSign(arena.getJoinSignLocation(), playerCreationArenas.get(player).getName());
        playerCreationArenas.remove(player);
        playersCreationStates.remove(player);
        GameMethods.getInstance().createArena(name); // Loads the arena
        player.sendMessage(prefix + "Successfully created the arena " + name + "!");
    }

    // Remove player from arena creation
    public void removePlayerFromCreation(Player player){
        playerCreationArenas.remove(player);
        playersCreationStates.remove(player);
        player.getInventory().remove(CreationMethods.getInstance().getCreationWand());
    }

}
