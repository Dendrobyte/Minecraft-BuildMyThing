package com.redstoneoinkcraft.buildmything;

import com.redstoneoinkcraft.buildmything.commands.BMTCommand;
import com.redstoneoinkcraft.buildmything.gameutils.ArenaVoteMachineListeners;
import com.redstoneoinkcraft.buildmything.gameutils.GameMethods;
import com.redstoneoinkcraft.buildmything.gameutils.RestrictBuilderPlacementListener;
import com.redstoneoinkcraft.buildmything.listeners.*;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

/**
 * BuildMyThing created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class Main extends JavaPlugin {

    private static Main instance;
    private String prefix = String.format("%s[%sBuild My Thing%s]%s ", ChatColor.DARK_GRAY, ChatColor.DARK_PURPLE,
            ChatColor.DARK_GRAY, ChatColor.GRAY);

    @Override
    public void onEnable() {
        // Instantiate main instance
        instance = this;
        getLogger().log(Level.INFO, prefix + "Enabling Build My Thing v" + getDescription().getVersion() + "...");

        // Create the configuration files
        createConfig();

        // Register events
        registerAllEvents();

        // Get commands
        getCommand("buildmything").setExecutor(new BMTCommand());

        // Load all arenas
        // TODO: Loop through arena names in the config and create them. Details filled
        // in within the ActiveArenaObject.
        // This is why arenas aren't loading lol
        for (String arenaName : getConfig().getConfigurationSection("arenas").getKeys(false)) {
            getLogger().log(Level.INFO, "BMT -- Loading map " + arenaName);
            GameMethods.getInstance().createArena(arenaName);
            getLogger().log(Level.INFO, "Loaded " + arenaName + "!");
        }

        getLogger().log(Level.INFO,
                prefix + "Successfully enabled Build My Thing v" + getDescription().getVersion() + "!");
    }

    public static Main getInstance() {
        return instance;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, prefix + "Successfully disabled Build My Thing");
    }

    private void registerAllEvents() {
        // TODO: Refactor to iterate over entire directory
        Bukkit.getServer().getPluginManager().registerEvents(new CreationListeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PreventItemDropListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoiningListeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CommandListeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new VotingInvListener(), this);

        Bukkit.getServer().getPluginManager().registerEvents(new ArenaVoteMachineListeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new RestrictBuilderPlacementListener(), this);

        Bukkit.getServer().getPluginManager().registerEvents(new PlayersChatListener(), this);
    }

    private void createConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        // Generate default config.yml
        File configuration = new File(getDataFolder(), "config.yml");
        if (!configuration.exists()) {
            getLogger().log(Level.INFO,
                    "Build My Thing v" + getDescription().getVersion() + " is creating the configuration...");
            saveDefaultConfig();
            getLogger().log(Level.INFO,
                    "Build My Thing v" + getDescription().getVersion() + " configuration has been created!");
        } else {
            getLogger().log(Level.INFO,
                    "Build My Thing v" + getDescription().getVersion() + " configuration has been loaded.");
        }
    }

}
