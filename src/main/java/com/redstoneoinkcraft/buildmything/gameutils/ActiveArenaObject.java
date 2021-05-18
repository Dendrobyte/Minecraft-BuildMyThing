package com.redstoneoinkcraft.buildmything.gameutils;

import com.redstoneoinkcraft.buildmything.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * BuildMyThing created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class ActiveArenaObject {

    // Variables
    private Location lobbySpawnLocation, joinSignLocation, buildRegionCornerOne, buildRegionCornerTwo, buildRegionCenter;
    private String name;
    private int maxRound;
    private ArenaTimer timer;
    private int maxPlayers = GameMethods.getInstance().getMaxPlayersPergame();
    String prefix = Main.getInstance().getPrefix();

    private HashMap<Player, PlayerStates> activePlayers = new HashMap<>(2); // Stores all players and if they are builders or spectators
    private LinkedList<Player> playerQueue = new LinkedList<>(); // Queue of players who have yet to be builders
    private int currentRound = 0;
    boolean inUse = false;
    private ArenaStates currentState = ArenaStates.WAITING;

    /* Getters specifically for fields we'll need to access in other classes */

    // Return whoever is the current builder
    public Player getCurrentBuilder(){ // If all is done properly, there will only be one builder :)
        for(Player player : activePlayers.keySet()){
            if(activePlayers.get(player) == PlayerStates.BUILDING){
                return player;
            }
        }
        return null; // Shouldn't happen :(
    }

    // Return a list of all the spectators
    public ArrayList<Player> getCurrentSpectators(){
        ArrayList<Player> spectators = new ArrayList<>(activePlayers.size());
        spectators.addAll(activePlayers.keySet());
        spectators.remove(getCurrentBuilder());
        return spectators;
    }

    // Return whether or not a player is in an arena
    public boolean isPlayerInArena(Player player){
        return activePlayers.keySet().contains(player);
    }

    // Return the arena's current state
    public ArenaStates getCurrentState(){
        return currentState;
    }

    /* Object construction */
    // Constructor
    public ActiveArenaObject(String name, int maxRound, int roundTime){
        this.name = name;
        String basePath = "arenas." + name;
        lobbySpawnLocation = Main.getInstance().getConfig().getLocation(basePath + ".lobbyspawnloc");
        joinSignLocation = Main.getInstance().getConfig().getLocation(basePath + ".joinsignloc");
        buildRegionCornerOne = Main.getInstance().getConfig().getLocation(basePath + ".corneroneloc");
        buildRegionCornerTwo = Main.getInstance().getConfig().getLocation(basePath + ".cornertwoloc");

        buildRegionCenter = new Location(buildRegionCornerOne.getWorld(), calcMean(buildRegionCornerOne.getBlockX(), buildRegionCornerTwo.getBlockX()), buildRegionCornerTwo.getBlockY()+2, calcMean(buildRegionCornerOne.getBlockZ(), buildRegionCornerTwo.getBlockZ()));

        this.maxRound = maxRound;
        this.timer = new ArenaTimer(this);
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

    public void setMaxRound(int maxRound) {
        this.maxRound = maxRound;
    }

    public int getRoundTime(){
        return timer.roundTimer;
    }

    public void setRoundTime(int roundTime){
        this.timer.roundTimer = roundTime;
    }

    public boolean isInUse(){
        return inUse;
    }

    public void setInUse(boolean inUse){
        this.inUse = inUse;
    }

    public Sign getJoinSign(){
        return (Sign)joinSignLocation.getBlock().getState();
    }

    public void updateJoinSign(){
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> getJoinSign().update());
    }

    // Methods for the game timer (before first round starts)
    private int adjustTimeUntilStart(int seconds){
        return timer.timeUntilStart = seconds;
    }

    // Add a new player to an arena when they join
    public void addPlayerToArena(Player player){

        // Initiate queues
        playerQueue.add(player);
        activePlayers.put(player, PlayerStates.WAITING);
        player.teleport(getLobbyLoc());

        // Calc things to start the game
        if(currentState == ArenaStates.WAITING){
            // These are not announced, as a vote will override them at the very end of the waiting phase
            if(activePlayers.size() == 2){
                setMaxRound(5);
                setRoundTime(60);
                // Start the game timer
                timer.runTaskTimer(Main.getInstance(), 0, 20);
            }
            else if(activePlayers.size() == 5){
                setMaxRound(3);
                setRoundTime(30);
            }
            // Give the player at least a few seconds to vote
            else if(activePlayers.size() > 5){
                if(timer.timeUntilStart < 10 && timer.timeUntilStart > 3){
                    adjustTimeUntilStart(10);
                }
                // TODO: Announce that timer has reset to 10 seconds, also add announcement for countdown from 5
            }

            // Send some messages
            player.sendMessage(prefix + "Welcome to Build My Thing!");
            player.sendMessage(prefix + "Your arena: " + ChatColor.DARK_PURPLE + ChatColor.BOLD + getName());
            // Change based on first or secondary players
            if(activePlayers.size() == 1)
                    player.sendMessage(prefix + "You're first! Waiting for other players...");
            else
                    player.sendMessage(prefix + "Time left 'til game start: " + ChatColor.GOLD + ChatColor.BOLD + timer.timeUntilStart);

            // Automatically open voting inventory
            IngameVoteInventory.getInstance().openInventory(player);
        }

    }

    // Remove player from game, whether they be kicked, leaving, or game ending
    public void removePlayerFromArena(Player playerToRemove){
        // TODO: :)

        // Reset timer if game is empty
        if(playerQueue.size() == 0){
            endGame();
        }
    }

    // Initiate the game
    public void initGame(){

        // Change the join sign
        getJoinSign().setLine(2, ChatColor.GREEN + "ACTIVE");
        updateJoinSign();

        // Set up proper data
        currentState = ArenaStates.ACTIVE;
        timer.gameStarted = true;
        timer.runTaskTimer(Main.getInstance(), 0, 20);
    }

    // Get the next player to make into a builder for the first go
    public void startQueue(){
        Player firstBuilder = playerQueue.getFirst();
        activePlayers.put(firstBuilder, PlayerStates.BUILDING);
        setSpectatorToBuilder(firstBuilder);
    }

    public void nextBuilder(Player currentBuilder){
        activePlayers.put(currentBuilder, PlayerStates.SPECTATING); // Put the last person to build back as a spectator

        // If the builder is the last one in the queue, then we've gone through all the players so go to next round
        if(currentBuilder.getName().equals(playerQueue.getLast().getName())){
            resetBuilderToSpectator(currentBuilder);
            startNextRound();
            return;
        }

        // Go ahead and shift down the queue and reset the builder to a spectator
        Player nextPlayer = playerQueue.get(playerQueue.indexOf(currentBuilder)+1);
        activePlayers.put(nextPlayer, PlayerStates.BUILDING);
        resetBuilderToSpectator(currentBuilder);
        setSpectatorToBuilder(nextPlayer);
    }

    // Set a spectator to a builder... used at start of queue and when next spectator is made a builder
    public void setSpectatorToBuilder(Player nextBuilder){
        nextBuilder.teleport(buildRegionCenter);
        nextBuilder.setGameMode(GameMode.CREATIVE);
    }

    // Reset a builder to a spectator
    public void resetBuilderToSpectator(Player currentBuilder){
        currentBuilder.teleport(lobbySpawnLocation);
        currentBuilder.setGameMode(GameMode.ADVENTURE);
    }

    // Go ahead and start the next round (called when all spectators have built)
    public void startNextRound(){
        if(currentRound == maxRound){
            endGame();
            return;
        }
        // TODO: Announce to players the next round

        // Restart the queue and builder stuff (more or less totally irrelevant to other things and can operate on its own)
        startQueue();
        incrCurrentRound();
    }

    // Wrap up the game
    public void endGame(){
        // TODO: Calc stats for winner

        // TODO: Announce winner and unique stats to each player

        // Remove each player from the game and bring them to minigame spawn
        for(Player player : playerQueue){
            removePlayerFromArena(player);
        }

        // Clear up data
        currentState = ArenaStates.WAITING;
        getJoinSign().setLine(2, ChatColor.WHITE + "WAITING");
        getJoinSign().setLine(3, "0/" + maxPlayers);
        getJoinSign().update();
        activePlayers.clear();
        playerQueue.clear();
        currentRound = 0;
        timer.cancel();
        timer = new ArenaTimer(this);

    }

}
