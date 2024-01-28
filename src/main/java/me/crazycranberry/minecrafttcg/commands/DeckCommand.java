package me.crazycranberry.minecrafttcg.commands;

import me.crazycranberry.minecrafttcg.events.DeckViewRequestEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeckCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("deck")) {
            Bukkit.getPluginManager().callEvent(new DeckViewRequestEvent((Player) sender));
        }
        return true;
    }
}
