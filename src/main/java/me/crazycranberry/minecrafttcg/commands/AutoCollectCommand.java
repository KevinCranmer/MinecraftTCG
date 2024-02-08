package me.crazycranberry.minecrafttcg.commands;

import me.crazycranberry.minecrafttcg.events.CollectionViewRequestEvent;
import me.crazycranberry.minecrafttcg.model.Collection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;

public class AutoCollectCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("autocollect")) {
            boolean isCurrentlyAutoCollecting = getPlugin().config().shouldAutoCollect(sender.getName());
            if ((args.length > 0 && args[0].equalsIgnoreCase("on")) || (args.length == 0 && !isCurrentlyAutoCollecting)) {
                getPlugin().config().updateAutoCollect(sender.getName(), true);
                sender.sendMessage(String.format("%sDropped cards will now automatically go to your collection.%s", ChatColor.GRAY, ChatColor.RESET));
            } else if (args.length > 0 && args[0].equalsIgnoreCase("off") || args.length == 0) {
                getPlugin().config().updateAutoCollect(sender.getName(), false);
                sender.sendMessage(String.format("%sDropped cards will now drop as actual items.%s", ChatColor.GRAY, ChatColor.RESET));
            } else {
                sender.sendMessage(String.format("%sUnknown param \"%s\", please use \"on\" or \"off\".%s", ChatColor.GRAY, args[0], ChatColor.RESET));
            }
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && (command.getName().equalsIgnoreCase("autocollect") && args.length == 1)) {
            return Stream.of("on", "off").filter(s -> s.startsWith(args[0].toLowerCase())).toList();
        }
        return null;
    }
}
