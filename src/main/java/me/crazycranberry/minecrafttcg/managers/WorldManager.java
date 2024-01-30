package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.events.DuelAcceptedEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;

public class WorldManager implements Listener {
    private static final String WORLD_NAME = "tcgworld";
    private static World tcgWorld;
    @EventHandler
    private void onDuelAccepted(DuelAcceptedEvent event) {
        if (tcgWorld != null) {
            StadiumManager.sendPlayersToDuel(tcgWorld, event.requester(), event.accepter());
            return;
        }
        File file = new File(Bukkit.getServer().getWorldContainer(), WORLD_NAME);
        WorldCreator God;
        if (file.exists()) {
            System.out.println("World already exists, lets just load it");
            God = new WorldCreator(WORLD_NAME);
        } else {
            System.out.println("Generating new world");
            God = new WorldCreator(WORLD_NAME).generator(new EmptyWorldGenerator());
        }
        tcgWorld = God.createWorld();
        tcgWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        StadiumManager.sendPlayersToDuel(tcgWorld, event.requester(), event.accepter());
    }

    public static class EmptyWorldGenerator extends ChunkGenerator {
        @Override
        public boolean shouldGenerateNoise() {
            return false;
        }

        @Override
        public boolean shouldGenerateSurface() {
            return false;
        }

        @Override
        public boolean shouldGenerateBedrock() {
            return false;
        }

        @Override
        public boolean shouldGenerateCaves() {
            return false;
        }

        @Override
        public boolean shouldGenerateDecorations() {
            return false;
        }

        @Override
        public boolean shouldGenerateMobs() {
            return false;
        }

        @Override
        public boolean shouldGenerateStructures() {
            return false;
        }
    }
}
