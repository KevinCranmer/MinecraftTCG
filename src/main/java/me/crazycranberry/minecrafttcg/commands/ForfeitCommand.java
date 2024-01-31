package me.crazycranberry.minecrafttcg.commands;

import me.crazycranberry.minecrafttcg.events.DuelEndEvent;
import me.crazycranberry.minecrafttcg.managers.StadiumManager;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ForfeitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("forfeit") || command.getName().equalsIgnoreCase("ff") || command.getName().equalsIgnoreCase("surrender")) {
            Stadium stadium = StadiumManager.stadium(((Player) sender).getLocation());
            if (stadium == null) {
                sender.sendMessage(String.format("%sYou cannot forfeit when you aren't in a duel...%s", ChatColor.GRAY, ChatColor.RESET));
                return false;
            } else if (DuelEndEvent.isEndable(stadium)) {
                Bukkit.getPluginManager().callEvent(new DuelEndEvent((Player) sender, false));
            }
        }
        return true;
    }
}
