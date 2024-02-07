package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.events.DuelAcceptedEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;

public class WorldManager implements Listener {
    private static final String WORLD_NAME = "tcgworld";
    private static World tcgWorld;
    @EventHandler
    private void onDuelAccepted(DuelAcceptedEvent event) {
        if (tcgWorld != null) {
            StadiumManager.sendPlayersToDuel(tcgWorld, event.requester(), event.accepter(), event.isRanked());
            return;
        }
        File file = new File(Bukkit.getServer().getWorldContainer(), WORLD_NAME);
        WorldCreator God;
        if (file.exists()) {
            God = new WorldCreator(WORLD_NAME);
        } else {
            God = new WorldCreator(WORLD_NAME).type(WorldType.FLAT).generatorSettings("""
                    {
                        "layers": [
                            {
                                "block": "air",
                                "height": 4
                            }
                        ],
                        "biome":"void"
                    }
                    """).generateStructures(false);
        }
        tcgWorld = God.createWorld();
        tcgWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        tcgWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        StadiumManager.sendPlayersToDuel(tcgWorld, event.requester(), event.accepter(), event.isRanked());
    }
}
