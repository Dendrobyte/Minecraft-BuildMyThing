package com.redstoneoinkcraft.buildmything.listeners;

import com.redstoneoinkcraft.buildmything.creationutils.CreationMethods;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;

/**
 * BuildMyThing created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class PreventItemDropListener implements Listener {

    @EventHandler
    public void playerDropsItemInBMT(EntityDropItemEvent event){
        if(event.getEntityType() == EntityType.PLAYER){
            // TODO: If player is in a game as well
            Player player = (Player)event.getEntity();
            if(CreationMethods.getInstance().getPlayerCreationState(player) != null /* || */) {
                event.setCancelled(true);
            }
        }
    }
}
