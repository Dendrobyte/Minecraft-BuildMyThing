package com.redstoneoinkcraft.buildmything.gameutils;

import com.redstoneoinkcraft.buildmything.Main;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.*;

/**
 * BuildMyThing created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class ActiveArenaObject {

    // Variables
    Location lobbySpawnLocation, joinSignLocation, buildRegionCornerOne, buildRegionCornerTwo, buildRegionCenter;
    String name;
    int maxRound;
    int roundTime;

    private HashMap<Player, PlayerStates> activePlayers = new HashMap<>(2);
    private LinkedList<Player> playerQueue = new LinkedList<>();
    private int currentRound = 0;

    // Constructor
    public ActiveArenaObject(String name, int maxRound, int roundTime){
        this.name = name;
        String basePath = "arenas." + name;
        lobbySpawnLocation = Main.getInstance().getConfig().getLocation(basePath + ".lobbyspawnloc");
        joinSignLocation = Main.getInstance().getConfig().getLocation(basePath + ".joinsignloc");
        buildRegionCornerOne = Main.getInstance().getConfig().getLocation(basePath + ".buildRegionCornerOne");
        buildRegionCornerTwo = Main.getInstance().getConfig().getLocation(basePath + ".buildRegionCornerTwo");

        buildRegionCenter = new Location(buildRegionCornerOne.getWorld(), calcMean(buildRegionCornerOne.getBlockX(), buildRegionCornerTwo.getBlockX()), buildRegionCornerTwo.getBlockY()+2, calcMean(buildRegionCornerOne.getBlockZ(), buildRegionCornerTwo.getBlockZ()));

        this.maxRound = maxRound;
        this.roundTime = roundTime;
    }

    private int calcMean(int a, int b){
        return (a + b)/2;
    }

    // Getters
    public String getName(){
        return name;
    }

    public Location getLobbyLoc(){
        return lobbySpawnLocation;
    }

    public Sign getJoinSign(){
        return (Sign)joinSignLocation.getBlock().getState();
    }

    public int getCurrentRound(){
        return currentRound;
    }

    public void incrCurrentRound(){
        currentRound += 1;
    }

    public Location getBuildRegionCenter(){
        return buildRegionCenter;
    }

    public int getMaxRound(){
        return maxRound;
    }

    public int getRoundTime(){
        return getRoundTime();
    }

    // Misc methods
    public void addPlayerToArena(Player player){
        playerQueue.add(player);
        activePlayers.put(player, PlayerStates.WAITING);
    }

    public void startQueue(){
        Player firstBuilder = playerQueue.getFirst();
        activePlayers.put(firstBuilder, PlayerStates.BUILDING);
        firstBuilder.teleport(buildRegionCenter);
        firstBuilder.setGameMode(GameMode.CREATIVE);
    }

    public void nextBuilder(Player currentBuilder){
        activePlayers.put(currentBuilder, PlayerStates.SPECTATING);
        if(currentBuilder.getName().equals(playerQueue.getLast().getName())){
            currentBuilder.teleport(lobbySpawnLocation);
            currentBuilder.setGameMode(GameMode.ADVENTURE);
            startNextRound();
            return;
        }
        Player nextPlayer = playerQueue.get(playerQueue.indexOf(currentBuilder)+1);
        activePlayers.put(nextPlayer, PlayerStates.BUILDING);
        currentBuilder.teleport(lobbySpawnLocation);
        currentBuilder.setGameMode(GameMode.ADVENTURE);
        nextPlayer.teleport(buildRegionCenter);
        nextPlayer.setGameMode(GameMode.CREATIVE);
    }

    public void startNextRound(){
        if(currentRound == maxRound){
            // TODO: Finish game
            return;
        }
        // TODO: Announce to players the next round
        startQueue();
        incrCurrentRound();
    }

}
