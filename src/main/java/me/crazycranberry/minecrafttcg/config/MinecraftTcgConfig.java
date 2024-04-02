package me.crazycranberry.minecrafttcg.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.logger;
import static me.crazycranberry.minecrafttcg.utils.FileUtils.loadOriginalConfig;

public class MinecraftTcgConfig {
    private final YamlConfiguration originalConfig;
    private final YamlConfiguration dropOddsConfig;
    private final YamlConfiguration cardDropRulesConfig;
    private final YamlConfiguration playerRanks;
    private final YamlConfiguration config;
    private int duelSecondsPerRound;
    private boolean duelShowAllMinionNames;
    private boolean duelAutoSkipPhasesWithoutAvailableActions;
    private boolean setHighestRankedPlayersNameToBlue;
    private boolean rankedDuelHasHappened;
    private List<String> autoCollectPlayerNames;

    public MinecraftTcgConfig(YamlConfiguration config, YamlConfiguration dropOddsConfig, YamlConfiguration cardDropRulesConfig, YamlConfiguration playerRanks) {
        this.originalConfig = loadOriginalConfig("minecraft_tcg.yml");
        this.dropOddsConfig = dropOddsConfig;
        this.cardDropRulesConfig = cardDropRulesConfig;
        this.playerRanks = playerRanks;
        updateOutOfDateConfig(config);
        loadConfig(config);
        this.config = config;
    }

    private void updateOutOfDateConfig(YamlConfiguration config) {
        boolean madeAChange = false;
        for (String key : originalConfig.getKeys(true)) {
            if (!config.isString(key) && !config.isConfigurationSection(key) && !config.isBoolean(key) && !config.isDouble(key) && !config.isInt(key) && !config.isList(key)) {
                logger().info("The " + key + " is missing from minecraft_tcg.yml, adding it now.");
                config.set(key, originalConfig.get(key));
                madeAChange = true;
            }
        }

        if (madeAChange) {
            try {
                config.save(getPlugin().getDataFolder() + "" + File.separatorChar + "minecraft_tcg.yml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadConfig(YamlConfiguration config) {
        duelSecondsPerRound = config.getInt("duel.seconds_per_round", originalConfig.getInt("duel.seconds_per_round"));
        duelShowAllMinionNames = config.getBoolean("duel.show_all_minion_names", originalConfig.getBoolean("duel.show_all_minion_names"));
        duelAutoSkipPhasesWithoutAvailableActions = config.getBoolean("duel.auto_skip_phases_with_no_available_actions", originalConfig.getBoolean("duel.auto_skip_phases_with_no_available_actions"));
        setHighestRankedPlayersNameToBlue = config.getBoolean("set_highest_ranked_players_name_to_blue", originalConfig.getBoolean("set_highest_ranked_players_name_to_blue"));
        rankedDuelHasHappened = config.getBoolean("ranked_duel_has_happened", originalConfig.getBoolean("ranked_duel_has_happened"));
        autoCollectPlayerNames = new ArrayList<>((List<String>) config.getList("auto_collect", List.of()));
    }

    public int duelSecondsPerRound() {
        return duelSecondsPerRound;
    }

    public boolean duelShowAllMinionNames() {
        return duelShowAllMinionNames;
    }

    public boolean duelAutoSkipPhasesWithoutAvailableActions() {
        return duelAutoSkipPhasesWithoutAvailableActions;
    }

    public boolean setHighestRankedPlayersNameToBlue() {
        return setHighestRankedPlayersNameToBlue;
    }

    public boolean rankedDuelHasHappened() {
        return rankedDuelHasHappened;
    }

    public YamlConfiguration dropOddsConfig() {
        return dropOddsConfig;
    }

    public YamlConfiguration cardDropRulesConfig() {
        return cardDropRulesConfig;
    }

    public YamlConfiguration playerRanks() {
        return playerRanks;
    }

    public boolean shouldAutoCollect(String playersName) {
        return autoCollectPlayerNames.contains(playersName);
    }

    public void updateAutoCollect(String playersName, boolean shouldAutoCollect) {
        try {
            if (shouldAutoCollect && !autoCollectPlayerNames.contains(playersName)) {
                autoCollectPlayerNames.add(playersName);
                config.set("auto_collect", autoCollectPlayerNames);
                config.save(new File(getPlugin().getDataFolder() + "" + File.separatorChar + "minecraft_tcg.yml"));
            } else if (!shouldAutoCollect && autoCollectPlayerNames.contains(playersName)) {
                autoCollectPlayerNames.remove(playersName);
                config.set("auto_collect", autoCollectPlayerNames);
                config.save(new File(getPlugin().getDataFolder() + "" + File.separatorChar + "minecraft_tcg.yml"));
            }
        } catch (IOException e) {
            logger().severe("Error trying to save to minecraft_tcg.yml: " + e.getMessage());
        }
    }

    public Integer playerRank(Player p) {
        if (!playerRanks.contains(p.getName())) {
            playerRanks.set(p.getName(), 1000);
            try {
                playerRanks.save(new File(getPlugin().getDataFolder() + "" + File.separatorChar + "player_ranks.yml"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return playerRanks.getInt(p.getName());
    }

    public void setPlayerRank(Player p, Integer rank) {
        playerRanks.set(p.getName(), rank);
        try {
            playerRanks.save(new File(getPlugin().getDataFolder() + "" + File.separatorChar + "player_ranks.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String topRankedPlayerName() {
        Map<String, Integer> playerRankMap = new HashMap<>();
        for (String playerName : playerRanks.getKeys(false)) {
            playerRankMap.put(playerName, playerRanks.getInt(playerName));
        }
        return playerRankMap.entrySet().stream().sorted((e1, e2) -> e2.getValue() - e1.getValue()).map(Map.Entry::getKey).findFirst().orElse(null);
    }

    public void playerRankChanged() throws IOException {
        if (!setHighestRankedPlayersNameToBlue()) {
            return;
        }
        String currentKingName = config.getString("current_highest_ranked_player_name");
        String newKingName = topRankedPlayerName();
        if (currentKingName == null) {
            Player newKing = Bukkit.getPlayer(newKingName);
            if (newKing != null) {
                newKing.setDisplayName(ChatColor.AQUA + newKingName);
                newKing.setPlayerListName(ChatColor.AQUA + newKingName);
            }
            config.set("current_highest_ranked_player_name", newKingName);
            config.save(new File(getPlugin().getDataFolder() + "" + File.separatorChar + "minecraft_tcg.yml"));
        } else if (!currentKingName.equals(newKingName)) {
            Player currentKing = Bukkit.getPlayer(currentKingName);
            Player newKing = Bukkit.getPlayer(newKingName);
            if (currentKing != null) {
                currentKing.setDisplayName(currentKingName);
                currentKing.setPlayerListName(currentKingName);
            }
            if (newKing != null) {
                newKing.setDisplayName(ChatColor.AQUA + newKingName);
                newKing.setPlayerListName(ChatColor.AQUA + newKingName);
            }
            config.set("current_highest_ranked_player_name", newKingName);
            config.save(new File(getPlugin().getDataFolder() + "" + File.separatorChar + "minecraft_tcg.yml"));
        }
        if (!rankedDuelHasHappened()) {
            config.set("ranked_duel_has_happened", true);
            config.save(new File(getPlugin().getDataFolder() + "" + File.separatorChar + "minecraft_tcg.yml"));
            rankedDuelHasHappened = true;
        }
    }
}
