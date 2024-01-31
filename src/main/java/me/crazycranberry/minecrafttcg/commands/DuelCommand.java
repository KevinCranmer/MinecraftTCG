package me.crazycranberry.minecrafttcg.commands;

import me.crazycranberry.minecrafttcg.events.DuelAcceptedEvent;
import me.crazycranberry.minecrafttcg.managers.StadiumManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;

public class DuelCommand implements CommandExecutor, TabCompleter {
    /** Key: person challenged. Value: person who sent the challenge */
    private Map<Player, Player> playerDuelChallenges = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player p = (Player) sender;
        if (command.getName().equalsIgnoreCase("duel")) {
            if (args.length == 0) {
                sender.sendMessage(String.format("%sType \"/duel <player_name>\" to challenge someone to a duel.", ChatColor.GRAY));
                return false;
            } else if ("accept".equals(args[0])) {
                if (playerDuelChallenges.containsKey(p)) {
                    Player opponent = playerDuelChallenges.get(p);
                    if (StadiumManager.stadium(p.getLocation()) != null || StadiumManager.stadium(opponent.getLocation()) != null) {
                        p.sendMessage(String.format("%sCannot accept duel because one of you is already in a duel.", ChatColor.GRAY));
                        opponent.sendMessage(String.format("%s%s tried to accept your duel but one of you is already in a duel.", ChatColor.GRAY, p.getName()));
                        return true;
                    }
                    Bukkit.getPluginManager().callEvent(new DuelAcceptedEvent((Player) sender, opponent));
                    Stream<Player> keys = playerDuelChallenges.entrySet().stream()
                        .filter(e -> p.equals(e.getKey()) || p.equals(e.getValue()) || opponent.equals(e.getValue()) || opponent.equals(e.getKey()))
                        .map(Map.Entry::getKey);
                    for (Player key : keys.toList()) {
                        playerDuelChallenges.remove(key);
                    }
                    return true;
                } else {
                    p.sendMessage(String.format("%sYou do not have any challenges to accept", ChatColor.GRAY));
                    return false;
                }
            } else if("decline".equals(args[0])) {
                if (playerDuelChallenges.containsKey(p)) {
                    Player opponent = playerDuelChallenges.get(p);
                    playerDuelChallenges.remove(p);
                    opponent.sendMessage(String.format("%s%s has declined your duel.", ChatColor.GRAY, p.getName()));
                    p.sendMessage(String.format("%sSuccessfully declined.", ChatColor.GRAY, p.getName()));
                } else {
                    p.sendMessage(String.format("%sYou do not have any challenges to decline", ChatColor.GRAY));
                }
                return true;
            } else {
                Optional<Player> challengedPlayer = Bukkit.getOnlinePlayers().stream().filter(player -> args[0].equals(player.getName())).map(player -> (Player) player).findFirst();
                if (challengedPlayer.isEmpty()) {
                    p.sendMessage(String.format("%s%s is not the name of any online players", ChatColor.GRAY, args[0]));
                    return false;
                } else if (challengedPlayer.get().equals(p)) {
                    p.sendMessage(String.format("%sYou cannot challenge yourself", ChatColor.GRAY));
                    return false;
                } else {
                    playerDuelChallenges.put(challengedPlayer.get(), p);
                    p.sendMessage(String.format("%s%s has been sent the Duel Challenge. They have 60 seconds to accept it.", ChatColor.GRAY, challengedPlayer.get().getName()));
                    challengedPlayer.get().sendMessage(String.format("%s%s has just sent you a duel challenge. Type \"/duel <accept/decline>\" to respond to it.", ChatColor.GRAY, p.getName()));
                    Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                        Optional<Player> key = playerDuelChallenges.entrySet().stream().filter(e -> challengedPlayer.get().equals(e.getKey()) && p.equals(e.getValue())).map(Map.Entry::getKey).findFirst();
                        key.ifPresent(player -> playerDuelChallenges.remove(player));
                    }, 1200);
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && (command.getName().equalsIgnoreCase("duel") && args.length == 1)) {
            List<String> possibleValues = new ArrayList<>(Bukkit.getOnlinePlayers().stream().filter(p -> !((Player) sender).equals(p)).map(Player::getName).toList());
            possibleValues.add("accept");
            possibleValues.add("decline");
            return possibleValues.stream().filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        }
        return null;
    }
}
