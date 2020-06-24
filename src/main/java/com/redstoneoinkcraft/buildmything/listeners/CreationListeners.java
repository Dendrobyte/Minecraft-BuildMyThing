package com.redstoneoinkcraft.buildmything.listeners;

import com.redstoneoinkcraft.buildmything.CreationStates;
import com.redstoneoinkcraft.buildmything.Main;
import com.redstoneoinkcraft.buildmything.creationutils.CreationArenaObject;
import com.redstoneoinkcraft.buildmything.creationutils.CreationMethods;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * BuildMyThing created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class CreationListeners implements Listener {

    CreationMethods creationMethods = CreationMethods.getInstance();
    String prefix = Main.getInstance().getPrefix();

    @EventHandler
    public void playerClicksWithCreationWand(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getHand() == EquipmentSlot.OFF_HAND) return;
        if(creationMethods.getPlayerCreationState(player) == null) return;
        ItemMeta itemInMainHand = player.getInventory().getItemInMainHand().getItemMeta();
        if(itemInMainHand == null) return;
        if(itemInMainHand.getDisplayName().equalsIgnoreCase(creationMethods.getCreationWand().getItemMeta().getDisplayName())
                && !itemInMainHand.isUnbreakable()) return;

        event.setCancelled(true);
        if(creationMethods.getPlayerCreationState(player) == CreationStates.ARENA_SPAWN){
            // Instantiate an arena object with a spawn location
            CreationArenaObject newArena = new CreationArenaObject(event.getClickedBlock().getLocation());
            creationMethods.setPlayerCreationArena(player, newArena);

            creationMethods.changeCreationState(player, CreationStates.BUILD_REGION);
            player.sendMessage(prefix + "Please select the corners of the " + ChatColor.GOLD + ChatColor.BOLD + "BUILD REGION");
            player.sendMessage(prefix + ChatColor.DARK_PURPLE + "LEFT CLICK" + ChatColor.GRAY + ChatColor.ITALIC + " selects one corner, " + ChatColor.DARK_PURPLE +
                    "RIGHT CLICK" + ChatColor.GRAY + ChatColor.ITALIC + " selects the other corner.");
            player.sendMessage(prefix + ChatColor.DARK_PURPLE + "SNEAK + LEFT CLICK " + ChatColor.GRAY + ChatColor.ITALIC + "when you're done");

        }
        else if (creationMethods.getPlayerCreationState(player) == CreationStates.BUILD_REGION){
            CreationArenaObject arena = creationMethods.getPlayerCreationArena(player);
            if (event.getAction() == Action.LEFT_CLICK_BLOCK){
                arena.setBuildRegionCornerOne(event.getClickedBlock().getLocation());
                player.sendMessage(prefix + ChatColor.GREEN + "Corner one set!");
            }
            else if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
                arena.setBuildRegionCornerTwo(event.getClickedBlock().getLocation());
                player.sendMessage(prefix + ChatColor.GREEN + "Corner two set!");
            }
            else if (event.getAction() == Action.LEFT_CLICK_AIR && player.isSneaking()) {
                if(arena.getBuildRegionCornerOne() != null && arena.getBuildRegionCornerTwo() != null){
                    creationMethods.changeCreationState(player, CreationStates.JOIN_SIGN);
                    player.sendMessage(prefix + "Please click the " + ChatColor.GOLD + ChatColor.BOLD + "JOIN SIGN");
                    player.sendMessage(prefix + ChatColor.GRAY + ChatColor.ITALIC + "(Nothing needs to be written on it)");
                    return;
                }
                if(arena.getBuildRegionCornerOne() == null){
                    player.sendMessage(prefix + ChatColor.RED + ChatColor.ITALIC + "Build region corner one is not set!");
                }
                if(arena.getBuildRegionCornerTwo() == null){
                    player.sendMessage(prefix + ChatColor.RED + ChatColor.ITALIC + "Build region corner two is not set!");
                }
            }

        }
        else if (creationMethods.getPlayerCreationState(player) == CreationStates.JOIN_SIGN){
            CreationArenaObject arena = creationMethods.getPlayerCreationArena(player);
            if(event.getClickedBlock().getType().toString().contains("SIGN")){
                arena.setJoinSignLocation(event.getClickedBlock().getLocation()); // We modify the sign later
                creationMethods.changeCreationState(player, CreationStates.FINISH);
                player.getInventory().remove(creationMethods.getCreationWand());
                player.sendMessage(prefix + "Use" + ChatColor.GOLD + " /bmt finalize <name> " + ChatColor.getLastColors(prefix) + "to create your arena!");
            } else {
                player.sendMessage(prefix + ChatColor.RED + ChatColor.ITALIC + "Please select a sign!");
            }
        }
    }

}
