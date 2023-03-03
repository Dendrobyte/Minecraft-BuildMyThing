package com.redstoneoinkcraft.buildmything.gameutils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * BuildMyThing created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class ArenaVoteMachine implements Listener {

    private Inventory voteInventory;
    private int roundNumber;
    private int timePerRound;

    public Inventory getVoteInventory() {
        return voteInventory;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getTimePerRound() {
        return timePerRound;
    }

    public void setTimePerRound(int timePerRound) {
        this.timePerRound = timePerRound;
    }

    // List of inventory items so we can easily access it in the listener class
    public static Material[] invItems = new Material[] { Material.CLOCK, Material.BOOK, Material.NETHER_STAR,
            Material.BARRIER };

    public ArenaVoteMachine() {
        // Create the inventory itself
        // TODO: Voting options (time, rounds) should reflect config change.
        // Save for beta probably, not mvp-esque
        voteInventory = Bukkit.createInventory(null, 9,
                "" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Build My Thing - Voting");
        {
            ItemStack temp = new ItemStack(Material.FIRE);
            ItemMeta tempMeta = temp.getItemMeta();
            tempMeta.setDisplayName("" + ChatColor.RED + ChatColor.BOLD + "None of this works yet :)");
            temp.setItemMeta(tempMeta);
            voteInventory.setItem(1, temp);
        }
        {
            // Clock to vote for length of each round
            ItemStack clock = new ItemStack(invItems[0]);
            ItemMeta clockMeta = clock.getItemMeta();
            clockMeta.setDisplayName("" + ChatColor.GOLD + "Vote for Time Per Round");
            clock.setItemMeta(clockMeta);
            voteInventory.setItem(1, clock);
        }
        {
            // Button to vote for number of rounds
            ItemStack button = new ItemStack(invItems[1]);
            ItemMeta buttonMeta = button.getItemMeta();
            buttonMeta.setDisplayName("" + ChatColor.GOLD + "Vote for Number of Rounds");
            button.setItemMeta(buttonMeta);
            voteInventory.setItem(3, button);
        }
        {
            // Netherstar for extra items, which doesn't actually have anything yet
            ItemStack netherstar = new ItemStack(invItems[2]);
            ItemMeta netherstarMeta = netherstar.getItemMeta();
            netherstarMeta.setDisplayName("" + ChatColor.DARK_PURPLE + "Vote for Special Round Features");
            ArrayList<String> netherStarLore = new ArrayList<>(1);
            netherStarLore.add("" + ChatColor.RED + ChatColor.ITALIC + "No enhancements here yet. Wait for beta!");
            netherstarMeta.setLore(netherStarLore);
            netherstar.setItemMeta(netherstarMeta);
            voteInventory.setItem(5, netherstar);
        }
        {
            // Barrier to exit... I guess, why not
            ItemStack barrier = new ItemStack(invItems[3]);
            ItemMeta barrierMeta = barrier.getItemMeta();
            barrierMeta.setDisplayName("" + ChatColor.RED + ChatColor.BOLD + "Leave Lobby");
            barrier.setItemMeta(barrierMeta);
            voteInventory.setItem(7, barrier);
        }

        // Initialize vote values to defaults -- pull from config, remove upstream?
        roundNumber = 3; // getRoundNumber();
        timePerRound = 60; // getTimePerRound();

    }

    public void openInventory(Player player) {
        player.openInventory(voteInventory);
    }

    /* Vote count storage and whatnot. */

    /*
     * This HashMap stores the votes a user has put in. The integer array is as
     * follows: [roundNumberVotes, timePerRoundVotes].
     * Please update this comment as enhancements are added as this simplistic way
     * of storing votes feels the most straightforward as of right now!
     * If a user votes, and they are in this list, we decr based on what they
     * previous voted on then incr their new change.
     * If they aren't in the list, do nothing. If they are, any values set to -1
     * mean they haven't voted on that thing.
     */
    private HashMap<Player, int[]> userVoteStorage = new HashMap<Player, int[]>();

    public void addPlayerToVoteStorage(Player player) {
        userVoteStorage.put(player, new int[] { -1, -1 }); // TODO: No need for whole player to be the key, just have
                                                           // their username or something
    }

    public void removePlayerFromVoteStorage(Player player) {
        // This is done to completely eradicate a player's vote
        int[] userVotes = userVoteStorage.get(player);
        if (userVotes != null) {
            for (int i = 0; i < userVotes.length; i++) {
                if (userVotes[i] != -1) { // thus user has voted
                    decrRoundNumberVotes(userVotes[i]); // i.e. if vote is '4', they voted for the '4th' option for the
                                                        // 0th userVotes choice (round number)
                }
            }
        }
        userVoteStorage.remove(player);
    }

    // Method to handle player voting (is there a better way?)
    // Future note: Overload method with non-int if voting differs
    public void doPlayerVote(Player voter, int voteStorageIndex, int voteValue) {
        boolean hasUserVoted = userVoteStorage.get(voter)[voteStorageIndex] != -1; // True if user has voted

        for (int i = 0; i < userVoteStorage.get(voter).length; i++) {
            if (i == voteStorageIndex && hasUserVoted) {
                decrRoundNumberVotes(userVoteStorage.get(voter)[voteStorageIndex]); // decr prev vote
                incrRoundNumberVotes(voteValue); // incr new vote
                break;
            }
        }

        // Set the user's new vote value
        userVoteStorage.get(voter)[voteStorageIndex] = voteValue;
    }

    // TODO: Make configurable
    private HashMap<Integer, Integer> roundNumberVotes = new HashMap<Integer, Integer>() {
        {
            put(2, 0);
            put(4, 0);
            put(5, 0);
            put(7, 0);
            put(9, 0);
        }
    };

    // TODO: Getting errors here. Either try voting twice, or voting then leaving.
    public void incrRoundNumberVotes(int voteToIncr) {
        roundNumberVotes.put(voteToIncr, roundNumberVotes.get(voteToIncr) + 1);
        roundNumberInventory = generateRoundNumberInventory();
    }

    public void decrRoundNumberVotes(int voteToIncr) {
        roundNumberVotes.put(voteToIncr, roundNumberVotes.get(voteToIncr) - 1);
        roundNumberInventory = generateRoundNumberInventory();
    }

    private HashMap<Integer, Integer> timePerRoundVotes = new HashMap<Integer, Integer>() {
        {
            put(60, 0);
            put(90, 0);
            put(120, 0);
            put(150, 0);
            put(180, 0);
        }
    };

    public void incrTimePerRoundVotes(int voteToIncr) {
        timePerRoundVotes.put(voteToIncr, timePerRoundVotes.get(voteToIncr) + 1);
        timePerRoundInventory = generateTimePerRoundInventory();
    }

    public void decrTimePerRoundVotes(int voteToIncr) {
        timePerRoundVotes.put(voteToIncr, timePerRoundVotes.get(voteToIncr) - 1);
        timePerRoundInventory = generateTimePerRoundInventory();
    }

    /*
     * All event listeners are in ArenaVoteMachineListeners. These are the extra
     * inventories for that.
     */

    // Generation methods could probably be made more concise.
    // They are needed as votes update and need to be refreshed. Quite frankly, I'm
    // not sure if that will work.
    private Inventory roundNumberInventory = generateRoundNumberInventory();

    private Inventory generateRoundNumberInventory() {
        Inventory resultInv = Bukkit.createInventory(null, 9,
                "" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Build My Thing - Voting");

        {
            int spaceInInv = 0;
            for (Integer roundNumber : roundNumberVotes.keySet()) {
                ItemStack paper = new ItemStack(Material.PAPER);
                ItemMeta paperMeta = paper.getItemMeta();
                paperMeta.setDisplayName(
                        "" + ChatColor.GOLD + ChatColor.BOLD + roundNumber + ChatColor.DARK_GREEN + " Rounds");
                ArrayList<String> paperLore = new ArrayList<>(1);
                paperLore.add(
                        "" + ChatColor.GRAY + "Current votes: " + ChatColor.GREEN + roundNumberVotes.get(roundNumber));
                paperLore.add("" + ChatColor.DARK_GRAY + "VM-RN"); // For later tabulation
                paperMeta.setLore(paperLore);
                paper.setItemMeta(paperMeta);

                // Add to the inventory
                resultInv.setItem(spaceInInv, paper);
                spaceInInv += 2;
            }
        }

        return resultInv;
    }

    public Inventory getRoundNumberInventory() {
        return roundNumberInventory;
    }

    private Inventory timePerRoundInventory = generateTimePerRoundInventory();

    private Inventory generateTimePerRoundInventory() {
        Inventory resultInv = Bukkit.createInventory(null, 9,
                "" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Build My Thing - Voting");

        {
            int spaceInInv = 0;
            for (Integer timePerRound : timePerRoundVotes.keySet()) {
                ItemStack paper = new ItemStack(Material.PAPER);
                ItemMeta paperMeta = paper.getItemMeta();
                paperMeta.setDisplayName("" + ChatColor.GOLD + ChatColor.BOLD + timePerRound + ChatColor.DARK_GREEN
                        + " Seconds Per Round");
                ArrayList<String> paperLore = new ArrayList<>(1);
                paperLore.add("" + ChatColor.GRAY + "Current votes: " + ChatColor.GREEN
                        + timePerRoundVotes.get(timePerRound));
                paperLore.add("" + ChatColor.DARK_GRAY + "VM-TPR"); // For later tabulation
                paperMeta.setLore(paperLore);
                paper.setItemMeta(paperMeta);

                // Add to the inventory
                resultInv.setItem(spaceInInv, paper);
                spaceInInv += 2;
            }
        }
        return resultInv;
    }

    public Inventory getTimePerRoundInventory() {
        return timePerRoundInventory;
    }

}
