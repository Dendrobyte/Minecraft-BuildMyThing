package com.redstoneoinkcraft.buildmything.gameutils;

import com.redstoneoinkcraft.buildmything.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ArenaVoteMachineListeners implements Listener {

    private static Material[] invItems = ArenaVoteMachine.invItems;
    private GameMethods utils = GameMethods.getInstance();
    private String prefix = Main.getInstance().getPrefix();

    @EventHandler
    public void onVoteMachineInventoryClick(InventoryClickEvent event){
        if(!event.getView().getTitle().equalsIgnoreCase("" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Build My Thing - Voting")) return;

        // Create a variable for the actual inventory and cancel click
        Inventory eventInventory = event.getClickedInventory();
        event.setCancelled(true);

        // Get the player
        Player player = (Player) event.getView().getPlayer();

        // Get the arena of that player (to modify the values and get the proper ArenaVoteMachine)
        ActiveArenaObject arena = utils.getArenaByPlayer(player);
        if(arena == null) return; // Shouldn't happen, but just in case

        // Handle all item clicks
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null) return;

        if(clickedItem.getType().equals(invItems[0])) { // Voting for time per round
            player.openInventory(arena.getVoteMachine().getTimePerRoundInventory());
        }

        else if(clickedItem.getType().equals(invItems[1])) { // Voting for number of rounds
            player.openInventory(arena.getVoteMachine().getRoundNumberInventory());
        }

        else if(clickedItem.getType().equals(invItems[2])) { // Voting for extra enhancements for the game
            // TODO: Future enhancements and stuff
        }

        else if(clickedItem.getType().equals(invItems[3])) { // Exit the menu
            player.getOpenInventory().close();
        }

        // Handle clicking of paper
        else if(clickedItem.getType().equals(Material.PAPER)){
            // TODO: Voting stuff goes here. All information in lore. See ArenaVoteMachine class for more info, maybe just make methods in there.
        }
    }

    @EventHandler
    public void onVoteInventoryClose(InventoryCloseEvent event){
        if(event.getView().getTitle().equalsIgnoreCase("" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Build My Thing - Voting")) {
            event.getPlayer().sendMessage(prefix + "" + ChatColor.GRAY + ChatColor.ITALIC + "You can change your vote with " + ChatColor.GOLD + "/bmt vote");
        }
    }

}
