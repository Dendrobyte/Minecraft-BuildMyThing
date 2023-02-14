package com.redstoneoinkcraft.buildmything.gameutils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * BuildMyThing created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class ArenaTimer extends BukkitRunnable {

    int timeUntilStart = 60; // When it hits zero, the game starts
    int roundTimer; // Voted on by players
    ActiveArenaObject arena; // The arena this timer belongs to
    boolean gameStarted = false;

    // Custom constructor so that we can modify the arena this timer is a part of
    ArenaTimer(ActiveArenaObject arena){
        this.arena = arena;
    }

    // Overloaded constructor, mainly for testing
    ArenaTimer(ActiveArenaObject arena, int waitTime){
        this.arena = arena;
        this.timeUntilStart = waitTime;
    }

    @Override
    public void run() {
        // Do things based on the fact the game has not started
        if(!gameStarted){
            timeUntilStart--;
            if (roundTimer == 0) {
                cancel();
                arena.initGame();
            }
        } else {

            // Timers for within the game, i.e. rounds
            roundTimer--;
            if (roundTimer == 0) {
                cancel();
                // TODO: Start new round 'n stuff
            } else {
                // TODO: Do the round stuff (create a method in the arena class to make this one remain simple
            }
        }
    }
}
