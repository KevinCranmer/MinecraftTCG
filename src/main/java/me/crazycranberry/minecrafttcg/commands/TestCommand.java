package me.crazycranberry.minecrafttcg.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.OP_PERMISSION_TITLE;
import static me.crazycranberry.minecrafttcg.TestCommands.executeTestCommand;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(OP_PERMISSION_TITLE)) {
            sender.sendMessage("You do not have permission for this command");
            return true;
        }
        if (command.getName().equalsIgnoreCase("tcgtc")) {
            executeTestCommand(args, (Player) sender);
        }
        return true;
    }
}
