package com.redstoneoinkcraft.buildmything.gameutils;

import com.redstoneoinkcraft.buildmything.Main;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * BuildMyThing created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class ActiveArenaObject {

    // Variables
    private Location lobbySpawnLocation, joinSignLocation, buildRegionCornerOne, buildRegionCornerTwo,
            buildRegionCenter;
    private String name;
    private int maxRound;
    private ArenaTimer timer;
    private int maxPlayers = GameMethods.getInstance().getMaxPlayersPergame();
    String prefix = Main.getInstance().getPrefix();

    private HashMap<Player, PlayerStates> activePlayers = new HashMap<>(2); // Stores all players and if they are
                                                                            // builders or spectators
    private LinkedList<Player> playerQueue = new LinkedList<>(); // Queue of players who have yet to be builders
    private int currentRound = 0;
    boolean inUse = false;
    private ArenaStates currentState = ArenaStates.WAITING;
    private ArenaVoteMachine voteMachine = new ArenaVoteMachine();

    private String currentWord;
    private String[] wordChoices; // Feels like there's a better way than to just save these on the arena

    /* Object construction */
    // Constructor
    public ActiveArenaObject(String name, int maxRound, int roundTime) {
        this.name = name;
        String basePath = "arenas." + name;
        lobbySpawnLocation = Main.getInstance().getConfig().getLocation(basePath + ".lobbyspawnloc");
        joinSignLocation = Main.getInstance().getConfig().getLocation(basePath + ".joinsignloc");
        buildRegionCornerOne = Main.getInstance().getConfig().getLocation(basePath + ".corneroneloc");
        buildRegionCornerTwo = Main.getInstance().getConfig().getLocation(basePath + ".cornertwoloc");

        buildRegionCenter = new Location(buildRegionCornerOne.getWorld(),
                calcMean(buildRegionCornerOne.getBlockX(), buildRegionCornerTwo.getBlockX()),
                buildRegionCornerTwo.getBlockY() + 2,
                calcMean(buildRegionCornerOne.getBlockZ(), buildRegionCornerTwo.getBlockZ()));

        this.maxRound = maxRound;

        // Two constructors exist for the timer class. Without a wait time, the wait
        // time is set to 60 by default. Overloaded mainly for testing purposes.
        this.timer = new ArenaTimer(this, 5);
    }

    /* Getters specifically for fields we'll need to access in other classes */

    // Get all the active players and their states
    public HashMap<Player, PlayerStates> getActivePlayers() {
        return activePlayers;
    }

    // Return whoever is the current builder
    public Player getCurrentBuilder() { // If all is done properly, there will only be one builder :)
        for (Player player : activePlayers.keySet()) {
            if (activePlayers.get(player) == PlayerStates.BUILDING) {
                return player;
            }
        }
        return null; // Shouldn't happen :(
    }

    // Return a list of all the spectators
    public ArrayList<Player> getCurrentSpectators() {
        ArrayList<Player> spectators = new ArrayList<>(activePlayers.size());
        spectators.addAll(activePlayers.keySet());
        spectators.remove(getCurrentBuilder());
        return spectators;
    }

    // Return whether or not a player is in an arena
    public boolean isPlayerInArena(Player player) {
        return activePlayers.keySet().contains(player);
    }

    // Return the arena's current state
    public ArenaStates getCurrentState() {
        return currentState;
    }

    // Set the arena's current state
    public void setCurrentState(ArenaStates newState) {
        this.currentState = newState;
    }

    private int calcMean(int a, int b) {
        return (a + b) / 2;
    }

    // Getters
    public String getName() {
        return name;
    }

    public Location getLobbyLoc() {
        return lobbySpawnLocation;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void incrCurrentRound() {
        currentRound += 1;
    }

    public Location getBuildRegionCenter() {
        return buildRegionCenter;
    }

    public int getMaxRound() {
        return maxRound;
    }

    public void setMaxRound(int maxRound) {
        this.maxRound = maxRound;
    }

    public int getRoundTime() {
        return timer.roundTimer;
    }

    public void setRoundTime(int roundTime) {
        this.timer.roundTimer = roundTime;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public Location getJoinSignLocation() {
        return joinSignLocation;
    }

    public ArenaVoteMachine getVoteMachine() {
        return voteMachine;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public String[] getWordChoices() { // returns a list of words the new builder can build
        return this.wordChoices;
    }

    // Methods for the game timer (before first round starts)
    private int adjustTimeUntilStart(int seconds) {
        return timer.timeUntilStart = seconds;
    }

    // Add a new player to an arena when they join
    public void addPlayerToArena(Player player) {

        // Initiate queues
        playerQueue.add(player);
        activePlayers.put(player, PlayerStates.WAITING); // TODO: Allow players to join as spectators if the game is
                                                         // already running (this would go in below if block I guess)
        player.teleport(getLobbyLoc());
        voteMachine.addPlayerToVoteStorage(player);

        // Calc things to start the game
        if (currentState == ArenaStates.WAITING) {
            // Set initial round and times per round depending on player count
            // These are not announced, as a vote will override them at the very end of the
            // waiting phase
            if (activePlayers.size() == 2) {
                voteMachine.setRoundNumber(5);
                voteMachine.setTimePerRound(60);
                // Start the game timer
                // When this hits 0 to start, that's when the game starts'
                timer.runTaskTimer(Main.getInstance(), 0, 20);
            } else if (activePlayers.size() == 5) {
                voteMachine.setRoundNumber(3);
                voteMachine.setTimePerRound(30);
            }
            // Give the player at least a few seconds to vote
            else if (activePlayers.size() > 5) {
                if (timer.timeUntilStart < 10 && timer.timeUntilStart > 3) {
                    adjustTimeUntilStart(10);
                }
                // TODO: Announce that timer has reset to 10 seconds, also add announcement for
                // countdown from 5
            }

            // Send some messages
            player.sendMessage(prefix + "Welcome to Build My Thing!");
            player.sendMessage(prefix + "Your arena: " + ChatColor.DARK_PURPLE + ChatColor.BOLD + getName());
            // Change based on first or secondary players
            if (activePlayers.size() == 1)
                player.sendMessage(prefix + "You're first! Waiting for other players...");
            else
                player.sendMessage(prefix + "Time left 'til game start: " + ChatColor.GOLD + ChatColor.BOLD
                        + timer.timeUntilStart);

            // Automatically open voting inventory
            voteMachine.openInventory(player);
        }

    }

    // Remove player from game, whether they be kicked, leaving, or game ending
    public void removePlayerFromArena(Player playerToRemove) {
        // TODO: :)
        // Clearing their vote count properly (make a new method for this in the vote
        // machine class)
        // Rest of the normal stuff

        // Reset timer if game is empty
        if (playerQueue.size() == 0) {
            endGame();
        } // TODO: Ensure that if it goes from X players -> 1, we reset the timer
    }

    // Initiate the game. This is called when the corresponding ArenaTimer hits 0
    // before the game has started
    public void initGame() {

        // Set up proper arena data
        currentState = ArenaStates.ACTIVE;

        // Change the join sign
        Sign joinSign = (Sign) getJoinSignLocation().getBlock().getState();
        joinSign.setLine(2, ChatColor.GREEN + "ACTIVE");
        joinSign.update();

        // Set final values of round information
        setMaxRound(voteMachine.getRoundNumber());
        setRoundTime(voteMachine.getTimePerRound());

        // Set up player details and whatnot
        for (Player player : getActivePlayers().keySet()) {
            getActivePlayers().put(player, PlayerStates.SPECTATING); // Set to spectator
        }
        broadcastMessage("Build My Thing is about to start... get building, and get guessing!");
        broadcastMessage("" + ChatColor.RED + ChatColor.ITALIC + "The game is currently in ALPHA. " + ChatColor.WHITE
                + "If you notice any bugs, please report them. " +
                "Send screenshots, recordings if you can, and steps on how to re-create that bug to the best of your ability."
                +
                ChatColor.GREEN + " We really appreciate your help in making this game a bit better.");
        currentRound = 0; // Start as zero since it increments in the method
        startNextRound();

    }

    // Get the next player to make into a builder for the first go
    public void startQueue() {
        Player firstBuilder = playerQueue.getFirst();
        activePlayers.put(firstBuilder, PlayerStates.BUILDING);
        setSpectatorToBuilder(firstBuilder);
    }

    public void nextBuilder(Player currentBuilder) {
        activePlayers.put(currentBuilder, PlayerStates.SPECTATING); // Put the last person to build back as a spectator

        // Go ahead and shift down the queue and reset the builder to a spectator
        Player nextPlayer = playerQueue.get(playerQueue.indexOf(currentBuilder) + 1);
        resetBuilderToSpectator(currentBuilder);
        setSpectatorToBuilder(nextPlayer);
    }

    // Set a spectator to a builder... used at start of queue and when next
    // spectator is made a builder
    public void setSpectatorToBuilder(Player nextBuilder) {
        activePlayers.put(nextBuilder, PlayerStates.BUILDING);
        nextBuilder.teleport(buildRegionCenter);
        nextBuilder.setGameMode(GameMode.CREATIVE);

        // Fill inventory with basic wool color blocks
        nextBuilder.getInventory().setItem(0, new ItemStack(Material.WHITE_WOOL));
        nextBuilder.getInventory().setItem(1, new ItemStack(Material.RED_WOOL));
        nextBuilder.getInventory().setItem(2, new ItemStack(Material.ORANGE_WOOL));
        nextBuilder.getInventory().setItem(3, new ItemStack(Material.GREEN_WOOL));
        nextBuilder.getInventory().setItem(4, new ItemStack(Material.LIME_WOOL));
        nextBuilder.getInventory().setItem(5, new ItemStack(Material.CYAN_WOOL));
        nextBuilder.getInventory().setItem(6, new ItemStack(Material.PURPLE_WOOL));
        nextBuilder.getInventory().setItem(7, new ItemStack(Material.MAGENTA_WOOL));
        nextBuilder.getInventory().setItem(8, new ItemStack(Material.BLACK_WOOL));

        // Word choice inventory
        // TODO: This is going to need its own inventory. Additionally, keep in mind
        // that this selection should have a countdown of its own and it starts the
        // round timer.
        nextBuilder.sendMessage(prefix + "Time to choose a word! Your options are...");
        String[] words = { "lab", "brain", "apple" }; // TODO: Randomize/pick these from file
        this.wordChoices = words;
        nextBuilder.sendMessage(prefix + words); // Stylize?
        nextBuilder.sendMessage(prefix + ChatColor.GRAY + ChatColor.ITALIC + "Type it out! [BETA MOMENT]");
        // TODO: Start a timer that, if it hits zero, forces a player to end the turn

    }

    // Reset a builder to a spectator
    public void resetBuilderToSpectator(Player currentBuilder) {
        activePlayers.put(currentBuilder, PlayerStates.SPECTATING);
        currentBuilder.teleport(lobbySpawnLocation);
        currentBuilder.setGameMode(GameMode.ADVENTURE);
        currentBuilder.getInventory().clear();
    }

    // Send a message to all users in the arena
    public void broadcastMessage(String msg) {
        for (Player playerInGame : activePlayers.keySet()) {
            playerInGame.sendMessage(prefix + ChatColor.GRAY + msg);
        }
    }

    // Go ahead and start the next round (called when all spectators have built)
    public void startNextRound() {
        incrCurrentRound();
        if (currentRound == maxRound) {
            endGame();
            return;
        }
        broadcastMessage("Starting round " + ChatColor.GOLD + ChatColor.BOLD + currentRound
                + ChatColor.getLastColors(prefix) + "...");
        if (currentRound == maxRound - 1) {
            broadcastMessage("" + ChatColor.AQUA + ChatColor.ITALIC + "This is the final round- make it count!");
        }

        // Restart the queue and builder stuff (more or less totally irrelevant to other
        // things and can operate on its own)
        startQueue();
    }

    public void setBuildersWord(String newWord) {
        this.currentWord = newWord;
        timer.run();
        // TODO: Check to make sure the roundTimer thing updated properly :)
    }

    // End a current round, which occurs when either everyone guesses or the timer
    // has hit 0
    public void endCurrentTurn() {
        // Send respective messages
        broadcastMessage("The word was...");
        getCurrentBuilder().sendMessage("n people guessed...");
        // TODO: Save builder stats. Spectator stats should show and save when they
        // guess

        // If the builder is the last one in the queue, then we've gone through all the
        // players so go to next round
        // NOTE: I sure do hope order is maintained here!
        if (getCurrentBuilder().getName().equals(playerQueue.getLast().getName())) {
            resetBuilderToSpectator(getCurrentBuilder());
            startNextRound();
            return;
        }

        // Just go to next builder if that wasn't it'
        nextBuilder(getCurrentBuilder());

        /*
         * Note to self: So pretty much, a round is defined as all players have had a
         * turn. Interesting choice of my past self...
         * We just cycle through builders and don't consider one person builder a
         * "round". Weird thing, but do consider that. We'll call that a "turn"
         */

        // TODO: There's probably other stuff that needs to be done
    }

    // Wrap up the game
    public void endGame() {
        // TODO: Calc stats for winner

        // TODO: Announce winner and unique stats to each player

        // Remove each player from the game and bring them to minigame spawn
        for (Player player : playerQueue) {
            removePlayerFromArena(player);
            GameMethods.getInstance().removePlayerFromGame(player, this);
            player.sendMessage(prefix + ChatColor.GRAY + ChatColor.ITALIC + "You have been removed from the game.");
        }

        // Clear up data
        currentState = ArenaStates.WAITING;
        Sign joinSign = (Sign) getJoinSignLocation().getBlock().getState();
        joinSign.setLine(2, "" + ChatColor.GRAY + ChatColor.ITALIC + "WAITING");
        joinSign.setLine(3, "0/" + maxPlayers);
        joinSign.update();
        activePlayers.clear();
        playerQueue.clear();
        currentRound = 0;
        voteMachine = new ArenaVoteMachine();
        // TODO: Uncomment below. Commented out while testing so I could easily
        // forcestop the arena.
        // timer.cancel();
        // timer = new ArenaTimer(this, 5);
    }

    // Check to see if a placed block is within the build region
    public boolean isInBuildRegion(Location loc) {
        // Store the build region bounds in an array of sorts
        int[] bounds = {
                // X coords
                Math.min(buildRegionCornerOne.getBlockX(), buildRegionCornerTwo.getBlockX()),
                Math.max(buildRegionCornerOne.getBlockX(), buildRegionCornerTwo.getBlockX()),

                // Y coords
                Math.min(buildRegionCornerOne.getBlockY(), buildRegionCornerTwo.getBlockY()),
                Math.max(buildRegionCornerOne.getBlockY(), buildRegionCornerTwo.getBlockY()),

                // Z coords
                Math.min(buildRegionCornerOne.getBlockZ(), buildRegionCornerTwo.getBlockZ()),
                Math.max(buildRegionCornerOne.getBlockZ(), buildRegionCornerTwo.getBlockZ()),

        };

        // Check if the attempted build location is within the bounds
        if (loc.getBlockX() >= bounds[0] && loc.getBlockX() <= bounds[1]) {
            if (loc.getBlockY() >= bounds[2] && loc.getBlockY() <= bounds[3]) {
                return loc.getBlockZ() >= bounds[4] && loc.getBlockZ() <= bounds[5];
            }
        }
        return false;
    }

}
