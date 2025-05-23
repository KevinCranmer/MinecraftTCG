package me.crazycranberry.minecrafttcg.commands;

import me.crazycranberry.minecrafttcg.events.CardsViewRequestEvent;
import me.crazycranberry.minecrafttcg.model.Collection;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CardsCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("cards")) {
            Collection.SortBy sortBy = null;
            if (args.length > 0) {
                sortBy = Collection.SortBy.fromString(args[0].replace("sortby=", ""));
            }
            if (sortBy == null) {
                sortBy = Collection.SortBy.COST;
            }
            Bukkit.getPluginManager().callEvent(new CardsViewRequestEvent((Player) sender, sortBy));
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && (command.getName().equalsIgnoreCase("cards") && args.length == 1)) {
            return Arrays.stream(Collection.SortBy.values()).map(s -> s.name().toLowerCase())
                .filter(s -> s.startsWith(args[0].toLowerCase()) || ("sortby=" + s).startsWith(args[0].toLowerCase()))
                .map(s -> "sortby=" + s)
                .toList();
        }
        return null;
    }
}
