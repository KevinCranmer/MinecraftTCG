package me.crazycranberry.minecrafttcg.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.logger;
import static me.crazycranberry.minecrafttcg.utils.FileUtils.loadOriginalConfig;

public class MinecraftTcgConfig {
    private final YamlConfiguration originalConfig;
    private YamlConfiguration dropOddsConfig;
    private YamlConfiguration cardDropRulesConfig;
    private int duelSecondsPerRound;

    public MinecraftTcgConfig(YamlConfiguration config, YamlConfiguration dropOddsConfig, YamlConfiguration cardDropRulesConfig) {
        this.originalConfig = loadOriginalConfig("minecraft_tcg.yml");
        this.dropOddsConfig = dropOddsConfig;
        this.cardDropRulesConfig = cardDropRulesConfig;
        updateOutOfDateConfig(config);
        loadConfig(config);
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
}
