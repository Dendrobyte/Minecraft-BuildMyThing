package com.redstoneoinkcraft.buildmything;

import com.redstoneoinkcraft.buildmything.commands.BMTCommand;
import com.redstoneoinkcraft.buildmything.listeners.CreationListeners;
import com.redstoneoinkcraft.buildmything.listeners.JoiningListeners;
import com.redstoneoinkcraft.buildmything.listeners.PreventItemDropListener;
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
    private String prefix = "§8[§5Build My Thing§8]§7 ";

    @Override
    public void onEnable(){
        // Instantiate main instance
        instance = this;
        System.out.println(prefix + "Enabling Build My Thing v" + getDescription().getVersion() + "...");

        // Create the configuration files
        createConfig();

        // Register events
        Bukkit.getServer().getPluginManager().registerEvents(new CreationListeners(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PreventItemDropListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoiningListeners(), this);

        // Get commands
        getCommand("buildmything").setExecutor(new BMTCommand());

        System.out.println(prefix + "Successfully enabled Build My Thing v" + getDescription().getVersion() + "!");
    }


    public static Main getInstance(){
        return instance;
    }

    public String getPrefix(){
        return prefix;
    }

    @Override
    public void onDisable(){
        System.out.println();
    }

    private void createConfig(){
        if(!getDataFolder().exists()){
            getDataFolder().mkdirs();
        }
        // Generate default config.yml
        File configuration = new File(getDataFolder(), "config.yml");
        if(!configuration.exists()){
            getLogger().log(Level.INFO, "Build My Thing v" + getDescription().getVersion() + " is creating the configuration...");
            saveDefaultConfig();
            getLogger().log(Level.INFO, "Build My Thing v" + getDescription().getVersion() + " configuration has been created!");
        } else {
            getLogger().log(Level.INFO, "Build My Thing v" + getDescription().getVersion() + " configuration has been loaded.");
        }
    }

}
