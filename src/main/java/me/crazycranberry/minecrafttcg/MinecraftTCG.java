package me.crazycranberry.minecrafttcg;

import me.crazycranberry.minecrafttcg.commands.DeckCommand;
import me.crazycranberry.minecrafttcg.commands.DuelCommand;
import me.crazycranberry.minecrafttcg.commands.TestCommand;
import me.crazycranberry.minecrafttcg.events.DuelStartEvent;
import me.crazycranberry.minecrafttcg.events.RegisterListenersEvent;
import me.crazycranberry.minecrafttcg.managers.DeckManager;
import me.crazycranberry.minecrafttcg.managers.DuelActionsManager;
import me.crazycranberry.minecrafttcg.managers.LookAndHighlightManager;
import me.crazycranberry.minecrafttcg.managers.MinionManager;
import me.crazycranberry.minecrafttcg.managers.StadiumManager;
import me.crazycranberry.minecrafttcg.managers.TurnManager;
import me.crazycranberry.minecrafttcg.managers.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

public final class MinecraftTCG extends JavaPlugin implements Listener {
    private static Logger logger;
    private static MinecraftTCG plugin;
    private static boolean managersRegistered = false;
    private static final List<Listener> managers = List.of(
            new MinionManager(),
            new DuelActionsManager(),
            new LookAndHighlightManager()
    );

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = this.getLogger();
        registerManagers();
        registerCommands();
    }

    private void registerManagers() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new DeckManager(), this);
        getServer().getPluginManager().registerEvents(new StadiumManager(), this);
        getServer().getPluginManager().registerEvents(new TurnManager(), this);
        getServer().getPluginManager().registerEvents(new WorldManager(), this);
    }

    private void registerCommands() {
        setCommandManager("deck", new DeckCommand());
        setCommandManager("duel", new DuelCommand());
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

    @EventHandler
    public void onRegisterListenersEvent(RegisterListenersEvent event) {
        if (!managersRegistered) {
            for (Listener listener : managers) {
                Bukkit.getPluginManager().registerEvents(listener, this);
            }
        }
        managersRegistered = true;
    }

    public static Logger logger() {
        return logger;
    }

    public static MinecraftTCG getPlugin() {
        return plugin;
    }
}
