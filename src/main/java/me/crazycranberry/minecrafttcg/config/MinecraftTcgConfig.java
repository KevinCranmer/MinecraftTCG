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
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.logger;
import static me.crazycranberry.minecrafttcg.utils.FileUtils.loadOriginalConfig;

public class MinecraftTcgConfig {
    private final YamlConfiguration originalConfig;
    private YamlConfiguration dropOddsConfig;
    private YamlConfiguration cardDropRulesConfig;
    private YamlConfiguration playerRanks;
    private YamlConfiguration config;
    private int duelSecondsPerRound;
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
        autoCollectPlayerNames = new ArrayList<>((List<String>) config.getList("auto_collect", List.of()));
    }

    public int duelSecondsPerRound() {
        return duelSecondsPerRound;
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
        String currentKingName = config.getString("current_highest_ranked_player_name");
        String newKingName = topRankedPlayerName();
        if (currentKingName == null) {
            Bukkit.getPlayer(newKingName).setDisplayName(ChatColor.AQUA + newKingName);
            Bukkit.getPlayer(newKingName).setPlayerListName(ChatColor.AQUA + newKingName);
            config.set("current_highest_ranked_player_name", newKingName);
            config.save(new File(getPlugin().getDataFolder() + "" + File.separatorChar + "minecraft_tcg.yml"));
        } else if (!currentKingName.equals(newKingName)) {
            Bukkit.getPlayer(currentKingName).setDisplayName(currentKingName);
            Bukkit.getPlayer(currentKingName).setPlayerListName(currentKingName);
            Bukkit.getPlayer(newKingName).setDisplayName(ChatColor.AQUA + newKingName);
            Bukkit.getPlayer(newKingName).setPlayerListName(ChatColor.AQUA + newKingName);
            config.set("current_highest_ranked_player_name", newKingName);
            config.save(new File(getPlugin().getDataFolder() + "" + File.separatorChar + "minecraft_tcg.yml"));
        }
    }
}
