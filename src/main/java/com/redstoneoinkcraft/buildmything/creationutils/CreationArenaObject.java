package com.redstoneoinkcraft.buildmything.creationutils;

import com.redstoneoinkcraft.buildmything.Main;
import org.bukkit.Location;

/**
 * BuildMyThing created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class CreationArenaObject {

    // Variables
    Location lobbySpawnLocation, joinSignLocation, buildRegionCornerOne, buildRegionCornerTwo;
    String name;

    // Constructor for creation methods to then load to config
    public CreationArenaObject(Location lobbySpawnLocation){
        this.lobbySpawnLocation = lobbySpawnLocation;
    }

    // Overloaded constructor for loading from config
    public CreationArenaObject(String name, Location lobbySpawnLocation, Location joinSignLocation, Location buildRegionCornerOne, Location buildRegionCornerTwo){
        this.name = name;
        this.lobbySpawnLocation = lobbySpawnLocation;
        this.joinSignLocation = joinSignLocation;
        this.buildRegionCornerOne = buildRegionCornerOne;
        this.buildRegionCornerTwo = buildRegionCornerTwo;
    }

    // Getters and setters
    public Location getLobbySpawnLocation() {
        return lobbySpawnLocation;
    }

    public void setLobbySpawnLocation(Location lobbySpawnLocation) {
        this.lobbySpawnLocation = lobbySpawnLocation;
    }

    public Location getJoinSignLocation() {
        return joinSignLocation;
    }

    public void setJoinSignLocation(Location joinSignLocation) {
        this.joinSignLocation = joinSignLocation;
    }

    public Location getBuildRegionCornerOne() {
        return buildRegionCornerOne;
    }

    public void setBuildRegionCornerOne(Location buildRegionCornerOne) {
        this.buildRegionCornerOne = new Location(buildRegionCornerOne.getWorld(), buildRegionCornerOne.getBlockX(), buildRegionCornerOne.getBlockY()+1, buildRegionCornerOne.getBlockZ());
    }

    public Location getBuildRegionCornerTwo() {
        return buildRegionCornerTwo;
    }

    public void setBuildRegionCornerTwo(Location buildRegionCornerTwo) {
        this.buildRegionCornerTwo = new Location(buildRegionCornerTwo.getWorld(), buildRegionCornerTwo.getBlockX(), buildRegionCornerTwo.getBlockY()+1, buildRegionCornerTwo.getBlockZ());
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    // Misc methods
    public void writeArenaToConfig(){
        String basePath = "arenas." + name;
        Main.getInstance().getConfig().set(basePath + ".lobbyspawnloc", lobbySpawnLocation);
        Main.getInstance().getConfig().set(basePath + ".joinsignloc", joinSignLocation);
        Main.getInstance().getConfig().set(basePath + ".corneroneloc", buildRegionCornerOne);
        Main.getInstance().getConfig().set(basePath + ".cornertwoloc", buildRegionCornerTwo);
        Main.getInstance().saveConfig();
    }

}
