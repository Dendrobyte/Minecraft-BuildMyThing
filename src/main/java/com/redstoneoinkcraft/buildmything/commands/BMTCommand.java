package com.redstoneoinkcraft.buildmything.commands;

import com.redstoneoinkcraft.buildmything.CreationStates;
import com.redstoneoinkcraft.buildmything.Main;
import com.redstoneoinkcraft.buildmything.creationutils.CreationArenaObject;
import com.redstoneoinkcraft.buildmything.creationutils.CreationMethods;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * BuildMyThing created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class BMTCommand implements CommandExecutor {

    String prefix = Main.getInstance().getPrefix();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage(prefix + "You must be a player to use this command.");
            return true;
        }

        Player player = (Player) sender;
        if(args.length == 0){
            player.sendMessage("" + ChatColor.GOLD + "---- " + ChatColor.DARK_PURPLE + "BuildMyThing Commands" + ChatColor.GOLD + " ----");
            player.sendMessage("" + ChatColor.DARK_PURPLE + "/bmt leave" + ChatColor.GOLD + " - Leave the game you are in");
            player.sendMessage("" + ChatColor.DARK_PURPLE + "/bmt vote" + ChatColor.GOLD + " - Vote for round time and length");
        }
        if(player.hasPermission("buildmything.create")){
            CreationMethods creationMethods = CreationMethods.getInstance();
            if (args.length == 0){
                player.sendMessage("" + ChatColor.DARK_GREEN + "---- " + ChatColor.DARK_PURPLE + "Arena Creation Commands" + ChatColor.DARK_GREEN + " ----");
                player.sendMessage("" + ChatColor.DARK_PURPLE + "/bmt create" + ChatColor.DARK_GREEN + " - Begin arena creation (or leave it)");
                player.sendMessage("" + ChatColor.DARK_PURPLE + "/bmt finalize <name>" + ChatColor.DARK_GREEN + " - Finish arena creation for arena");
            }

            // Create an arena
            if(args.length > 0) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (creationMethods.getPlayerCreationState(player) != null) {
                        creationMethods.removePlayerFromCreation(player);
                        player.sendMessage(prefix + ChatColor.RED + "You have been removed from arena creation.");
                        return true;
                    }
                    creationMethods.beginCreation(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("finalize")) {
                    if (args.length == 1) {
                        player.sendMessage(prefix + "Please provide an arena name!");
                        return true;
                    }
                    if (creationMethods.getPlayerCreationState(player) != CreationStates.FINISH) {
                        player.sendMessage(prefix + "You still have fields to set before finalizing!");
                        return true;
                    }
                    String name = args[1];
                    creationMethods.getPlayerCreationArena(player).setName(name);
                    creationMethods.finalizeCreation(player);
                }
            }

        }
        if(player.hasPermission("buildmything.events")){
            if(args.length == 0){
                player.sendMessage("" + ChatColor.DARK_AQUA + "---- " + ChatColor.DARK_PURPLE + "Event Management Commands" + ChatColor.DARK_AQUA + " ----");
                player.sendMessage("" + ChatColor.DARK_PURPLE + "/bmt forcestart" + ChatColor.DARK_AQUA + " - Force start the game you are in");
                // TODO (see below)
                player.sendMessage("" + ChatColor.DARK_PURPLE + "/bmt set <round_number> <round_time>" + ChatColor.DARK_AQUA + " - Force/outvote the game parameters");
            }
            if (args.length > 0) {
                return true;
            }
        }
        return true;
    }

}
