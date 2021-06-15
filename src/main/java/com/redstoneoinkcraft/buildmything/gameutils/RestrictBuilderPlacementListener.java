package com.redstoneoinkcraft.buildmything.gameutils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class RestrictBuilderPlacementListener implements Listener {

    GameMethods utils = GameMethods.getInstance();

    @EventHandler
    public void onBuilderPlaceEvent(BlockPlaceEvent event){
        if(playerCannotBuild(event.getPlayer(), event.getBlock().getLocation())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBuilderBreakEvent(BlockBreakEvent event){
        if(playerCannotBuild(event.getPlayer(), event.getBlock().getLocation())){
            event.setCancelled(true);
        }
    }

    // Both of the above events lead to the same messages and restrictions, so methodizing... ya!
    // Apologies for the weird double negative happening here
    public boolean playerCannotBuild(Player builder, Location blockLocation) {
        ActiveArenaObject arena = utils.getArenaByPlayer(builder);
        if(arena == null) return false;

        if(arena.getCurrentBuilder().equals(builder)){
            return !arena.isInBuildRegion(blockLocation);
            //return arena.isInBuildRegion(blockLocation);
        }

        return false;
    }

}
