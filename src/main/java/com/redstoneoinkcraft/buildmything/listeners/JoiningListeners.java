package com.redstoneoinkcraft.buildmything.listeners;

import com.redstoneoinkcraft.buildmything.Main;
import com.redstoneoinkcraft.buildmything.gameutils.ActiveArenaObject;
import com.redstoneoinkcraft.buildmything.gameutils.ArenaStates;
import com.redstoneoinkcraft.buildmything.gameutils.GameMethods;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * BuildMyThing created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class JoiningListeners implements Listener {

    String prefix = Main.getInstance().getPrefix();
    GameMethods gameMethods = GameMethods.getInstance();

    @EventHandler
    public void onPlayerJoinGame(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) return;

        if(event.getClickedBlock().getType().toString().contains("SIGN")){
            Sign joinSign = (Sign) event.getClickedBlock().getState();
            if(!ChatColor.stripColor(joinSign.getLine(0)).equalsIgnoreCase(ChatColor.stripColor(prefix.substring(0, prefix.length()-1)))) return;

            // Go ahead and grab the arena so we don't rely on the sign info
            String arenaName = joinSign.getLine(1);
            ActiveArenaObject currArena = gameMethods.getArenaByName(arenaName);

            // Check if the arena exists, just in case
            if(currArena == null){
                player.sendMessage(prefix + "This arena appears not to exist... Please contact an admin.");
            }

            // Check if the arena is running
            else if(currArena.getCurrentState() == ArenaStates.ACTIVE){
                // TODO: Allow players to join ongoing games (just add them to the queue)
                player.sendMessage(prefix + ChatColor.RED + "Sorry! That game is currently ongoing.");
            }
            else {
                // Otherwise, add the player to the game
                gameMethods.addPlayerToGame(player, currArena);
                // Update the sign
                String pCountString = joinSign.getLine(3).split("/")[0];
                int currPCount = Integer.parseInt(pCountString); // It's pretty reliable this will always be a number
                joinSign.setLine(3, currPCount + 1 + "/" + gameMethods.getMaxPlayersPergame());
                joinSign.update();
            }
        }
    }
}