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

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.commands.DuelCommand.createDuelChallengeInventory;
import static me.crazycranberry.minecrafttcg.commands.DuelCommand.createDuelRequestsInventory;

public class RankedDuelCommand implements CommandExecutor, TabCompleter {
    /** Key: person challenged. Value: person who sent the challenge */
    private static final Map<Player, Player> playerDuelChallenges = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            return false;
        }
        if (command.getName().equalsIgnoreCase("rankedduel")) {
            if (args.length == 0) {
                p.openInventory(createDuelChallengeInventory(p, 0, true));
                return true;
            } else if ("requests".equals(args[0])) {
                p.openInventory(createDuelRequestsInventory(p, playerDuelChallenges, 0, true));
                return true;
            } else if ("accept".equals(args[0])) {
                return handleAccept((Player) sender, args, p);
            } else {
                if (handleRequestToOtherPlayer(args, p)) return false;
            }
        }
        return true;
    }

    private static boolean handleAccept(Player sender, String[] args, Player p) {
        if (playerDuelChallenges.containsValue(p)) {
            if (args.length > 1) {
                Optional<Map.Entry<Player, Player>> matchup = playerDuelChallenges.entrySet().stream().filter(e -> e.getKey().getName().equalsIgnoreCase(args[1])).findFirst();
                if (matchup.isPresent()) {
                    Bukkit.getPluginManager().callEvent(new DuelAcceptedEvent(matchup.get().getKey(), matchup.get().getValue(), true));
                    playerDuelChallenges.remove(matchup.get().getKey());
                    return true;
                } else {
                    p.sendMessage(String.format("%sNo requests found against \"%s\"", ChatColor.GRAY, args[1]));
                    return false;
                }
            } else {
                return handleAcceptFirst(sender, p);
            }
        } else {
            p.sendMessage(String.format("%sYou do not have any ranked challenges to accept", ChatColor.GRAY));
            return false;
        }
    }

    private static boolean handleAcceptFirst(Player sender, Player p) {
        Optional<Player> opponent = playerDuelChallenges.entrySet().stream().filter(e -> e.getValue().equals(p)).map(Map.Entry::getKey).findFirst();
        if (opponent.isEmpty()) {
            p.sendMessage(String.format("%sCannot accept duel for an unknown reason.", ChatColor.GRAY));
            return false;
        }
        if (!opponent.get().isOnline()) {
            p.sendMessage(String.format("%sCannot accept duel because %s is offline.", opponent.get().getName(), ChatColor.GRAY));
            return false;
        }
        if (opponent.get().isDead()) {
            p.sendMessage(String.format("%sCannot accept duel because %s is dead.", opponent.get().getName(), ChatColor.GRAY));
            return false;
        }
        if (StadiumManager.stadium(p.getLocation()) != null || StadiumManager.stadium(opponent.get().getLocation()) != null) {
            p.sendMessage(String.format("%sCannot accept duel because one of you is already in a duel.", ChatColor.GRAY));
            opponent.get().sendMessage(String.format("%s%s tried to accept your duel but one of you is already in a duel.", ChatColor.GRAY, p.getName()));
            return false;
        }
        Bukkit.getPluginManager().callEvent(new DuelAcceptedEvent(sender, opponent.get(), true));
        playerDuelChallenges.remove(opponent.get());
        return true;
    }

    private static boolean handleRequestToOtherPlayer(String[] args, Player p) {
        Optional<Player> challengedPlayer = Bukkit.getOnlinePlayers().stream().filter(player -> args[0].equals(player.getName())).map(player -> (Player) player).findFirst();
        if (challengedPlayer.isEmpty()) {
            p.sendMessage(String.format("%s%s is not the name of any online players", ChatColor.GRAY, args[0]));
            return true;
        } else if (challengedPlayer.get().equals(p)) {
            p.sendMessage(String.format("%sYou cannot challenge yourself", ChatColor.GRAY));
            return true;
        } else {
            playerDuelChallenges.put(p, challengedPlayer.get());
            p.sendMessage(String.format("%s%s has been sent the Ranked Duel Challenge. They have 60 seconds to accept it.", ChatColor.GRAY, challengedPlayer.get().getName()));
            challengedPlayer.get().sendMessage(String.format("%s%s has just sent you a ranked duel challenge. Type \"/rankedduel accept\" to accept it.", ChatColor.GRAY, p.getName()));
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                Optional<Player> key = playerDuelChallenges.entrySet().stream().filter(e -> challengedPlayer.get().equals(e.getValue()) && p.equals(e.getKey())).map(Map.Entry::getKey).findFirst();
                key.ifPresent(playerDuelChallenges::remove);
            }, 1200);
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && (command.getName().equalsIgnoreCase("rankedduel") && args.length == 1)) {
            List<String> possibleValues = new ArrayList<>(Bukkit.getOnlinePlayers().stream().filter(p -> !sender.equals(p)).map(Player::getName).toList());
            possibleValues.add("accept");
            possibleValues.add("requests");
            return possibleValues.stream().filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        } else if (sender instanceof Player && (command.getName().equalsIgnoreCase("rankedduel") && args.length == 2 && args[0].equals("accept"))) {
            return challengesMap().entrySet().stream()
                .filter(e -> e.getValue().equals(sender))
                .map(Map.Entry::getKey)
                .map(Player::getName)
                .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                .toList();
        }
        return null;
    }

    public static Map<Player, Player> challengesMap() {
        return playerDuelChallenges;
    }
}
