package me.crazycranberry.minecrafttcg.managers.utils;

import org.bukkit.Material;

import java.io.InputStream;
import java.util.List;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static org.bukkit.Material.BARRIER;
import static org.bukkit.Material.NETHER_BRICKS;
import static org.bukkit.Material.POLISHED_BLACKSTONE_BRICKS;

public record StadiumDefinition(int xOffset, int yOffset, int zOffset, String name, Material player1OutlookBlock,
                                Material player2OutlookBlock) {
    public static final List<StadiumDefinition> STADIUM_DEFINITIONS = List.of(
        new StadiumDefinition(-6, -12, -10, "plains", BARRIER, BARRIER),
        new StadiumDefinition(-6, -13, -10, "nether", POLISHED_BLACKSTONE_BRICKS, NETHER_BRICKS),
        new StadiumDefinition(-6, -14, -10, "ocean", BARRIER, BARRIER)
    );

    public InputStream blocksInputStream() {
        return getPlugin().getResource(String.format("arenas/%s/blocks.csv", name));
    }

    public InputStream mobsInputStream() {
        return getPlugin().getResource(String.format("arenas/%s/mobs.csv", name));
    }
}
