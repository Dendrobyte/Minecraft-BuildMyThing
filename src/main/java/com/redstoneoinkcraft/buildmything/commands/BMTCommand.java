package com.redstoneoinkcraft.buildmything.commands;

import com.redstoneoinkcraft.buildmything.creationutils.CreationStates;
import com.redstoneoinkcraft.buildmything.Main;
import com.redstoneoinkcraft.buildmything.creationutils.CreationMethods;
import com.redstoneoinkcraft.buildmything.gameutils.ActiveArenaObject;
import com.redstoneoinkcraft.buildmything.gameutils.ArenaStates;
import com.redstoneoinkcraft.buildmything.gameutils.GameMethods;
import com.redstoneoinkcraft.buildmything.gameutils.PlayerStates;
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
    GameMethods utils = GameMethods.getInstance();

    // TODO: Refactor all permissions from a permissions class since they may be
    // used in multiple locations
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + "You must be a player to use this command.");
            return true;
        }

        Player player = (Player) sender;
        ActiveArenaObject arena = utils.getArenaByPlayer(player);
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("leave")) {
                arena.removePlayerFromArena(player);
            }
            if (args[0].equalsIgnoreCase("vote")) {
                if (arena == null) {
                    player.sendMessage(prefix + ChatColor.RED + "You're not in an arena...");
                } else {
                    if (arena.getCurrentState() != ArenaStates.WAITING) {
                        player.sendMessage(prefix + "You can only vote when a game has not yet started.");
                    } else {
                        arena.getVoteMachine().openInventory(player);
                        player.sendMessage(prefix + "Opening voting inventory...");
                    }
                }
            }
            // TODO: DEV COMMAND ONLY. DELETE WHEN BETA.
            if (args[0].equalsIgnoreCase("changemode")) {
                if (arena == null) {
                    player.sendMessage(ChatColor.ITALIC + "Someone doesn't know how to use their own dev commands...");
                    return true;
                }
                if (arena.getCurrentState() == ArenaStates.WAITING) {
                    arena.initGame();
                    player.sendMessage(prefix + ChatColor.RED + "DEV COMMAND: Arena state changed to active.");
                    return true;
                }
                if (arena.getCurrentState() == ArenaStates.ACTIVE) {
                    if (arena.getActivePlayers().get(player) == PlayerStates.SPECTATING) {
                        arena.setSpectatorToBuilder(player);
                        player.sendMessage(
                                prefix + ChatColor.RED
                                        + "DEV COMMAND: Your state has gone from SPECTATING to BUILDING");
                    } else if (arena.getActivePlayers().get(player) == PlayerStates.BUILDING) {
                        arena.resetBuilderToSpectator(player);
                        player.sendMessage(
                                prefix + ChatColor.RED
                                        + "DEV COMMAND: Your state has gone from BUILDING to SPECTATING");
                    }
                }
            }
        }

        /* Stuff for the help menu(s) */
        if (args.length == 0) {
            player.sendMessage("" + ChatColor.GOLD + "---- " + ChatColor.DARK_PURPLE + "BuildMyThing Commands"
                    + ChatColor.GOLD + " ----");
            player.sendMessage(
                    "" + ChatColor.DARK_PURPLE + "/bmt leave" + ChatColor.GOLD + " - Leave the game you are in");
            player.sendMessage(
                    "" + ChatColor.DARK_PURPLE + "/bmt vote" + ChatColor.GOLD + " - Vote for round time and length");
        }

        if (player.hasPermission("buildmything.create")) {
            CreationMethods creationMethods = CreationMethods.getInstance();
            if (args.length == 0) {
                player.sendMessage("" + ChatColor.DARK_GREEN + "---- " + ChatColor.DARK_PURPLE
                        + "Arena Creation Commands" + ChatColor.DARK_GREEN + " ----");
                player.sendMessage("" + ChatColor.DARK_PURPLE + "/bmt create" + ChatColor.DARK_GREEN
                        + " - Begin arena creation (or leave it)");
                player.sendMessage("" + ChatColor.DARK_PURPLE + "/bmt finalize <name>" + ChatColor.DARK_GREEN
                        + " - Finish arena creation for arena");
            }

            // Create an arena
            if (args.length > 0) {
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
        if (player.hasPermission("buildmything.events")) {
            if (args.length == 0) {
                player.sendMessage("" + ChatColor.DARK_AQUA + "---- " + ChatColor.DARK_PURPLE
                        + "Event Management Commands" + ChatColor.DARK_AQUA + " ----");
                player.sendMessage("" + ChatColor.DARK_PURPLE + "/bmt forcestart" + ChatColor.DARK_AQUA
                        + " - Force start the game you are in");
                // Should be an admin command, but could be useful so whatever
                player.sendMessage("" + ChatColor.DARK_PURPLE + "/bmt forcestop" + ChatColor.DARK_AQUA
                        + " - Force stop the game you are in");
                // TODO: The reason we have this is so that event managers can put spins on the
                // rounds and whatnot, like challenges or speed rounds
                player.sendMessage("" + ChatColor.DARK_PURPLE + "/bmt set <round_number> <round_time>"
                        + ChatColor.DARK_AQUA + " - Force/outvote the game parameters");
            }
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("forcestart")) {
                    // TODO: Force start the arena
                } else if (args[0].equalsIgnoreCase("forcestop")) {
                    // Useful for testing purposes mainly
                    utils.getArenaByPlayer(player)
                            .broadcastMessage("The game is being force stopped by " + player.getName() + "!");
                    utils.getArenaByPlayer(player).endGame();

                } else if (args[0].equalsIgnoreCase("debug")) {
                    // For any issues that come up, shoulder simply iterate over a given game state.
                    // Screenshots of this would be nice.
                    // TODO: Implement
                }
                return true;
            }
        }
        return true;
    }

}
