package me.crazycranberry.minecrafttcg;

import me.crazycranberry.minecrafttcg.commands.DeckCommand;
import me.crazycranberry.minecrafttcg.commands.TestCommand;
import me.crazycranberry.minecrafttcg.managers.DeckManager;
import me.crazycranberry.minecrafttcg.managers.MinionManager;
import me.crazycranberry.minecrafttcg.managers.StadiumManager;
import org.bukkit.Particle;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class MinecraftTCG extends JavaPlugin {
    private static Logger logger;
    private static MinecraftTCG plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = this.getLogger();
        registerManagers();
        registerCommands();
    }

    private void registerManagers() {
        getServer().getPluginManager().registerEvents(new DeckManager(), this);
        getServer().getPluginManager().registerEvents(new MinionManager(), this);
        getServer().getPluginManager().registerEvents(new StadiumManager(), this);
    }

    private void registerCommands() {
        setCommandManager("deck", new DeckCommand());
        setCommandManager("tc", new TestCommand());
    }

    private void setCommandManager(String command, CommandExecutor commandManager) {
        PluginCommand pc = getCommand(command);
        if (pc == null) {
            logger().warning(String.format("[ ERROR ] - Error loading the %s command", command));
        } else {
            pc.setExecutor(commandManager);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Logger logger() {
        return logger;
    }

    public static MinecraftTCG getPlugin() {
        return plugin;
    }
}
