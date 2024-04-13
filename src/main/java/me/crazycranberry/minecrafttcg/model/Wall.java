package me.crazycranberry.minecrafttcg.model;

import me.crazycranberry.minecrafttcg.events.TurnEndEvent;
import me.crazycranberry.minecrafttcg.events.WallBuiltEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.model.Spot.MIDDLE_X;

public class Wall implements Listener {
    private final Column column;
    private final Material material;
    private final Stadium stadium;
    private int turnsLeft;
    private final List<Block> wallBlocks;
    private int deconstructTaskId;

    public Wall(Stadium stadium, Column column, Material wallMaterial, int numTurnsStanding) {
        this.column = column;
        this.material = wallMaterial;
        this.stadium = stadium;
        this.turnsLeft = numTurnsStanding;
        this.wallBlocks = new ArrayList<>();
        Block startingBlock = stadium().startingCorner().getBlock();
        for (int y = 1; y < 6; y++) {
            for (int z = this.column().zOffset(); z < this.column().zOffset() + 3; z++) {
                Block block = startingBlock.getRelative(MIDDLE_X, y, z);
                block.setType(this.material());
                wallBlocks.add(block);
            }
        }
        Bukkit.getPluginManager().callEvent(new WallBuiltEvent(this));
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    private void onTurnEnd(TurnEndEvent event) {
        this.turnsLeft--;
        if (turnsLeft <= 0 ) {
            deconstruct();
        }
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    private void deconstruct() {
        this.stadium().setWall(this.column(), null);
        deconstructTaskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            Optional<Block> blockToRemove = randomFromList(wallBlocks);
            if (blockToRemove.isEmpty()) {
                Bukkit.getScheduler().cancelTask(deconstructTaskId);
                return;
            }
            Block block = blockToRemove.get();
            wallBlocks.remove(block);
            if (block.getType().equals(this.material())) {
                block.setType(Material.AIR);
            }
        }, 0, 1).getTaskId();
    }

    public Column column() {
        return column;
    }

    public Material material() {
        return material;
    }

    public Stadium stadium() {
        return stadium;
    }
}
