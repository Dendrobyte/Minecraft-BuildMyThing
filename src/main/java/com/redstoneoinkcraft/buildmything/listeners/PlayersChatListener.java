package com.redstoneoinkcraft.buildmything.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.redstoneoinkcraft.buildmything.Main;
import com.redstoneoinkcraft.buildmything.gameutils.ActiveArenaObject;
import com.redstoneoinkcraft.buildmything.gameutils.GameMethods;

import net.md_5.bungee.api.ChatColor;

public class PlayersChatListener implements Listener {

    @EventHandler
    public void playerSendsMessage(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!GameMethods.getInstance().isPlayerInGame(player))
            return;

        // Handle a new builder choosing a word
        ActiveArenaObject arena = GameMethods.getInstance().getArenaByPlayer(player); // should never be null
        if (arena.getCurrentBuilder().equals(player)) {
            if (arena.getCurrentWord() == null) {
                String chosenWord = event.getMessage().split(" ")[0]; // We can always assume at least one word, right?
                for (String possibleChoice : arena.getWordChoices()) { // oof
                    if (chosenWord.toLowerCase().equals(possibleChoice.toLowerCase())) {
                        arena.setBuildersWord(chosenWord);
                        player.sendMessage(Main.getInstance().getPrefix() + "Alright! Start building " + ChatColor.GREEN
                                + ChatColor.BOLD
                                + chosenWord + "!");
                        return;
                    }
                }
                // TODO: To avoid this possible fiasco, show an inventory with three choices.
                // Or click on the message, could be fun
                player.sendMessage(
                        Main.getInstance().getPrefix()
                                + "Doesn't seem like that was a possible word choice... [BETA MOMENT]");
                return;
            } else {
                // Don't let them send chat messages
                // TODO: Send the chat messages in the builder/guessed channel... although not
                // chatting incentivizes building?
                event.setCancelled(true);
            }
        }

        // TODO: Handle players guessing word if spectator or builder
        // Should be pretty straight forward, the heavy work will be scoring and keeping
        // track of whether or not they guessed, are correct, etc.\
        // If arena.currentWord is null, don't do anything
    }

}
