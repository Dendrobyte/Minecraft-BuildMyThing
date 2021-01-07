package com.redstoneoinkcraft.buildmything.gameutils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * BuildMyThing created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class IngameVoteInventory implements Listener {

    private static IngameVoteInventory instance;

    public static IngameVoteInventory getInstance(){
        if(instance == null){
            instance = new IngameVoteInventory();
        }

        return instance;
    }

    private Inventory voteInventory = Bukkit.createInventory(null, 9, "Build My Thing - Voting");
    {
        // Clock to vote for length of each round
        ItemStack clock = new ItemStack(Material.CLOCK); // TODO: Give this name and meta
        voteInventory.setItem(2, clock);
    }
    {
        // Button to vote for number of rounds
        ItemStack button = new ItemStack(Material.STONE_BUTTON); // TODO: Give this name and meta
        voteInventory.setItem(6, button);
    }

    public void openInventory(Player player){
        player.openInventory(voteInventory);
    }

    /* Inventory click events because who needs a second class (: */

    // TODO: On closing the inventory, tell the player that they can open it with /vote

}
