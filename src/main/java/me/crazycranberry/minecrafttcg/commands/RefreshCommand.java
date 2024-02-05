package me.crazycranberry.minecrafttcg.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;

public class RefreshCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("refresh")) {
            String response = getPlugin().refreshConfigs();
            sender.sendMessage(response);
        }
        return true;
    }
}
