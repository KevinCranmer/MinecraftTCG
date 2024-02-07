package me.crazycranberry.minecrafttcg;

import me.crazycranberry.minecrafttcg.commands.CollectionCommand;
import me.crazycranberry.minecrafttcg.commands.DeckCommand;
import me.crazycranberry.minecrafttcg.commands.DuelCommand;
import me.crazycranberry.minecrafttcg.commands.ForfeitCommand;
import me.crazycranberry.minecrafttcg.commands.RankedDuelCommand;
import me.crazycranberry.minecrafttcg.commands.RanksCommand;
import me.crazycranberry.minecrafttcg.commands.RefreshCommand;
import me.crazycranberry.minecrafttcg.commands.TestCommand;
import me.crazycranberry.minecrafttcg.config.MinecraftTcgConfig;
import me.crazycranberry.minecrafttcg.events.RegisterListenersEvent;
import me.crazycranberry.minecrafttcg.managers.CardDropManager;
import me.crazycranberry.minecrafttcg.managers.DeckManager;
import me.crazycranberry.minecrafttcg.managers.DuelActionsManager;
import me.crazycranberry.minecrafttcg.managers.PlayerManager;
import me.crazycranberry.minecrafttcg.managers.MinionManager;
import me.crazycranberry.minecrafttcg.managers.StadiumManager;
import me.crazycranberry.minecrafttcg.managers.TurnManager;
import me.crazycranberry.minecrafttcg.managers.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

import static me.crazycranberry.minecrafttcg.utils.FileUtils.loadConfig;
import static me.crazycranberry.minecrafttcg.utils.StartingWorldConfigUtils.restoreStartingWorldConfig;
import static me.crazycranberry.minecrafttcg.utils.StartingWorldConfigUtils.startingWorldConfigExists;

public final class MinecraftTCG extends JavaPlugin implements Listener {
    private static Logger logger;
    private static MinecraftTCG plugin;
    private static boolean managersRegistered = false;
    private MinecraftTcgConfig config;
    private static final List<Listener> managers = List.of(
            new MinionManager(),
            new DuelActionsManager(),
            new PlayerManager()
    );

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = this.getLogger();
        refreshConfigs();
        registerManagers();
        registerCommands();
    }

    private void registerManagers() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new CardDropManager(), this);
        getServer().getPluginManager().registerEvents(new DeckManager(), this);
        getServer().getPluginManager().registerEvents(new StadiumManager(), this);
        getServer().getPluginManager().registerEvents(new TurnManager(), this);
        getServer().getPluginManager().registerEvents(new WorldManager(), this);
    }

    private void registerCommands() {
        setCommandManager("collection", new CollectionCommand());
        setCommandManager("deck", new DeckCommand());
        setCommandManager("duel", new DuelCommand());
        setCommandManager("forfeit", new ForfeitCommand());
        setCommandManager("rankedduel", new RankedDuelCommand());
        setCommandManager("ranks", new RanksCommand());
        setCommandManager("refresh", new RefreshCommand());
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
    private void onRegisterListenersEvent(RegisterListenersEvent event) {
        if (event.shouldRegister() && !managersRegistered) {
            for (Listener listener : managers) {
                Bukkit.getPluginManager().registerEvents(listener, this);
            }
            managersRegistered = true;
        } else if (!event.shouldRegister() && managersRegistered) {
            for (Listener listener : managers) {
                HandlerList.unregisterAll(listener);
            }
            managersRegistered = false;
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        if (startingWorldConfigExists(event.getPlayer())) {
            //Server crashed mid-duel, or they left during duel, and they're startingWorldConfig still exists, gotta load it up for them
            logger().info("Duel crash recovery initiated for " + event.getPlayer().getName());
            restoreStartingWorldConfig(event.getPlayer());
        }
        config().playerRank(event.getPlayer()); // Just to make sure everyone that's logged in has a rank
    }

    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        if (startingWorldConfigExists(event.getPlayer())) {
            restoreStartingWorldConfig(event.getPlayer());
        }
    }

    public static Logger logger() {
        return logger;
    }

    public static MinecraftTCG getPlugin() {
        return plugin;
    }

    public MinecraftTcgConfig config() {
        return config;
    }

    public String refreshConfigs() {
        try {
            config = new MinecraftTcgConfig(loadConfig("minecraft_tcg.yml"),
                loadConfig("drop_odds.yml"),
                loadConfig("card_drop_rules.yml"),
                loadConfig("player_ranks.yml")
            );
            return "Successfully loaded configs.";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
