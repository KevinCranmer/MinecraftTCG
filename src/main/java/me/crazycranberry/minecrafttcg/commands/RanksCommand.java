package me.crazycranberry.minecrafttcg.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static net.md_5.bungee.api.ChatColor.AQUA;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RESET;

public class RanksCommand implements CommandExecutor {
    private static final int numRanksShown = 5;
    public static final int MAX_RANK_GAIN_PER_MATCH = 50;
    public static final int MIN_RANK_GAIN_PER_MATCH = 5;
    public static final int MAX_RANK_GAIN_PER_TIE = 10;
    public static final int MIN_RANK_GAIN_PER_TIE = 0;
    public static final int UPPER_LIMIT_OF_RANK_DIFFERENTIAL_WEIGHTING = 300;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("ranks")) {
            sender.sendMessage(rankMessage((Player) sender, getPlugin().config().playerRanks()));
        }
        return true;
    }

    private String rankMessage(Player sender, YamlConfiguration playerRanks) {
        return String.format("""
            %sTop ranks:%s
            %s
            %sYour rank:%s
            %s%s
            """,
            LIGHT_PURPLE, GRAY,
            topRanks(playerRanks),
            LIGHT_PURPLE, GRAY,
            yourRank(sender, playerRanks), RESET
            );
    }

    private String yourRank(Player sender, YamlConfiguration playerRanks) {
        Map<String, Integer> playerRankMap = new HashMap<>();
        for (String playerName : playerRanks.getKeys(false)) {
            playerRankMap.put(playerName, playerRanks.getInt(playerName));
        }
        List<Map.Entry<String, Integer>> ranksOrdered = playerRankMap.entrySet().stream().sorted((e1, e2) -> e2.getValue() - e1.getValue()).toList();
        for (int i = 1; i <= ranksOrdered.size(); i++) {
            if (!ranksOrdered.get(i - 1).getKey().equals(sender.getName())) {
                continue;
            }
            if (i == 1) {
                return String.format("1. %s%s%s - %s", AQUA, ranksOrdered.get(0).getKey(), GRAY, ranksOrdered.get(0).getValue());
            } else {
                return String.format("%s. %s - %s", i, ranksOrdered.get(i - 1).getKey(), ranksOrdered.get(i - 1).getValue());
            }
        }
        return "";
    }

    private String topRanks(YamlConfiguration playerRanks) {
        Map<String, Integer> playerRankMap = new HashMap<>();
        for (String playerName : playerRanks.getKeys(false)) {
            playerRankMap.put(playerName, playerRanks.getInt(playerName));
        }
        List<Map.Entry<String, Integer>> ranksOrdered = playerRankMap.entrySet().stream().sorted((e1, e2) -> e2.getValue() - e1.getValue()).toList();
        StringBuilder topRanks = new StringBuilder(String.valueOf(GRAY));
        for (int i = 1; i <= Math.min(numRanksShown, ranksOrdered.size()); i++) {
            if (i == 1) {
                topRanks.append(String.format("1. %s%s%s - %s", AQUA, ranksOrdered.get(0).getKey(), GRAY, ranksOrdered.get(0).getValue()));
            } else {
                topRanks.append(String.format("\n%s. %s - %s", i, ranksOrdered.get(i - 1).getKey(), ranksOrdered.get(i - 1).getValue()));
            }
        }
        return topRanks.toString();
    }
}
