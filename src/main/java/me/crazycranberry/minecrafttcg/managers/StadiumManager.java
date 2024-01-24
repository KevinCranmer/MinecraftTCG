package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.events.BuildStadiumEvent;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

import static me.crazycranberry.minecrafttcg.model.Stadium.BLUE_MATERIAL;
import static me.crazycranberry.minecrafttcg.model.Stadium.GREEN_MATERIAL;
import static me.crazycranberry.minecrafttcg.model.Stadium.RED_MATERIAL;
import static org.bukkit.Material.BIRCH_WALL_SIGN;
import static org.bukkit.Material.OAK_WALL_SIGN;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.SOUTH;

public class StadiumManager implements Listener {
    public static final Vector PLAYER_1_SIGN_OFFSET = new Vector(3, 10, 4);
    public static final Vector PLAYER_2_SIGN_OFFSET = new Vector(23, 10, 6);
    private static final Material FILL_BLOCK = Material.WHITE_TERRACOTTA;
    private static final Map<World, Stadium> stadiums = new HashMap<>();

    @EventHandler
    private void onBuildRequested(BuildStadiumEvent event) {
        stadiums.put(event.getStartingCorner().getWorld(), new Stadium(event.getStartingCorner(), event.player1(), event.player2()));
        buildStadium(event.getStartingCorner());
    }

    public static void playerLookingAt(Player p, Spot spot) {
        Stadium stadium = stadium(p.getWorld());
        if (stadium != null) {
            stadium.playerTargeting(p, spot);
        }
    }

    public static Location locOfSpot(World w, Spot spot) {
        Stadium stadium = stadium(w);
        return stadium == null ? null : stadium.locOfSpot(spot);
    }

    public static Stadium stadium(World w) {
        return stadiums.get(w);
    }

    private void buildStadium(Location location) {
        buildRow(location.getBlock(), 0, Material.PURPLE_TERRACOTTA, RED_MATERIAL, Material.PINK_TERRACOTTA);
        buildRow(location.getBlock(), 4, Material.BLUE_TERRACOTTA, BLUE_MATERIAL, Material.CYAN_TERRACOTTA);
        buildRow(location.getBlock(), 8, Material.YELLOW_TERRACOTTA, GREEN_MATERIAL, Material.GREEN_TERRACOTTA);
        buildBarriers(location.getBlock());
        buildPlayer1Tower(location.getBlock());
        buildPlayer2Tower(location.getBlock());
    }

    private void buildBarriers(Block startingCornerBlock) {
        for (int i = 0; i <= 22; i = i + 22) {
            for (int j = 0; j < 2; j++) {
                startingCornerBlock.getRelative(i, j+8, 4).setType(Material.BARRIER);
                startingCornerBlock.getRelative(i, j+8, 5).setType(Material.BARRIER);
                startingCornerBlock.getRelative(i, j+8, 6).setType(Material.BARRIER);
                startingCornerBlock.getRelative(i+1, j+8, 3).setType(Material.BARRIER);
                startingCornerBlock.getRelative(i+1, j+8, 7).setType(Material.BARRIER);
                startingCornerBlock.getRelative(i+2, j+8, 3).setType(Material.BARRIER);
                startingCornerBlock.getRelative(i+2, j+8, 7).setType(Material.BARRIER);
                startingCornerBlock.getRelative(i+3, j+8, 3).setType(Material.BARRIER);
                startingCornerBlock.getRelative(i+3, j+8, 7).setType(Material.BARRIER);
                startingCornerBlock.getRelative(i+4, j+8, 4).setType(Material.BARRIER);
                startingCornerBlock.getRelative(i+4, j+8, 5).setType(Material.BARRIER);
                startingCornerBlock.getRelative(i+4, j+8, 6).setType(Material.BARRIER);
            }
        }
        for (int i = 5; i < 22; i++) {
            startingCornerBlock.getRelative(i, 2, -1).setType(Material.BARRIER);
            startingCornerBlock.getRelative(i, 2, 3).setType(Material.BARRIER);
            startingCornerBlock.getRelative(i, 2, 7).setType(Material.BARRIER);
            startingCornerBlock.getRelative(i, 2, 11).setType(Material.BARRIER);
        }
    }

    private void buildPlayer1Tower(Block startingCornerBlock) {
        int signX = (int) PLAYER_1_SIGN_OFFSET.getX();
        int signY = (int) PLAYER_1_SIGN_OFFSET.getY();
        int signZ = (int) PLAYER_1_SIGN_OFFSET.getZ();
        startingCornerBlock.getRelative(3, 8, 3).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(3, 9, 3).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(3, 10, 3).setType(Material.BIRCH_PLANKS);
        makeSign(startingCornerBlock, signX, signY, signZ, BIRCH_WALL_SIGN, SOUTH, true);
        makeSign(startingCornerBlock, signX, signY-1, signZ, BIRCH_WALL_SIGN, SOUTH, false);
        makeSign(startingCornerBlock, signX, signY-2, signZ, BIRCH_WALL_SIGN, SOUTH, false);
        startingCornerBlock.getRelative(1, 7, 4).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(1, 7, 5).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(1, 7, 6).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(2, 7, 4).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(2, 7, 5).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(2, 7, 6).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(3, 7, 4).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(3, 7, 5).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(3, 7, 6).setType(Material.BIRCH_PLANKS);
    }

    private void buildPlayer2Tower(Block startingCornerBlock) {
        int signX = (int) PLAYER_2_SIGN_OFFSET.getX();
        int signY = (int) PLAYER_2_SIGN_OFFSET.getY();
        int signZ = (int) PLAYER_2_SIGN_OFFSET.getZ();
        startingCornerBlock.getRelative(23, 8, 7).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(23, 9, 7).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(23, 10, 7).setType(Material.COBBLESTONE);
        makeSign(startingCornerBlock, signX, signY, signZ, OAK_WALL_SIGN, NORTH, true);
        makeSign(startingCornerBlock, signX, signY-1, signZ, OAK_WALL_SIGN, NORTH, false);
        makeSign(startingCornerBlock, signX, signY-2, signZ, OAK_WALL_SIGN, NORTH, false);
        startingCornerBlock.getRelative(23, 7, 4).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(23, 7, 5).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(23, 7, 6).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(24, 7, 4).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(24, 7, 5).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(24, 7, 6).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(25, 7, 4).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(25, 7, 5).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(25, 7, 6).setType(Material.COBBLESTONE);
    }

    private void makeSign(Block startingCornerBlock, int x, int y, int z, Material material, BlockFace blockFace, boolean addText) {
        Block signBlock = startingCornerBlock.getRelative(x, y, z);
        signBlock.setType(material);
        Sign signState = (Sign) signBlock.getState();
        Directional directional = (Directional) signState.getBlockData();
        directional.setFacing(blockFace);
        signState.setBlockData(directional);
        if (addText) {
            signState.getSide(Side.FRONT).setLine(0, "Left Click a Minion");
            signState.getSide(Side.FRONT).setLine(1, "for its description");
            signState.getSide(Side.FRONT).setLine(2, "|");
            signState.getSide(Side.FRONT).setLine(3, "V");
        } else {
            signState.getSide(Side.FRONT).setLine(0, "");
            signState.getSide(Side.FRONT).setLine(1, "");
            signState.getSide(Side.FRONT).setLine(2, "");
            signState.getSide(Side.FRONT).setLine(3, "");
        }
        signState.update();
    }

    private void buildRow(Block startingCornerBlock, int zOffset, Material light, Material medium, Material dark) {
        startingCornerBlock.getRelative(5, 0, zOffset).setType(dark);
        startingCornerBlock.getRelative(5, 0, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(5, 0, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(6, 0, zOffset).setType(dark);
        startingCornerBlock.getRelative(6, 0, zOffset+1).setType(medium);
        startingCornerBlock.getRelative(6, 0, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(7, 0, zOffset).setType(dark);
        startingCornerBlock.getRelative(7, 0, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(7, 0, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(5, 1, zOffset).setType(dark);
        startingCornerBlock.getRelative(5, 1, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(5, 1, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(6, 1, zOffset).setType(dark);
        startingCornerBlock.getRelative(6, 1, zOffset+1).setType(medium);
        startingCornerBlock.getRelative(6, 1, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(7, 1, zOffset).setType(dark);
        startingCornerBlock.getRelative(7, 1, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(7, 1, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(8, 0, zOffset).setType(light);
        startingCornerBlock.getRelative(8, 0, zOffset+1).setType(light);
        startingCornerBlock.getRelative(8, 0, zOffset+2).setType(light);
        startingCornerBlock.getRelative(9, 0, zOffset).setType(light);
        startingCornerBlock.getRelative(9, 0, zOffset+1).setType(medium);
        startingCornerBlock.getRelative(9, 0, zOffset+2).setType(light);
        startingCornerBlock.getRelative(10, 0, zOffset).setType(light);
        startingCornerBlock.getRelative(10, 0, zOffset+1).setType(light);
        startingCornerBlock.getRelative(10, 0, zOffset+2).setType(light);
        startingCornerBlock.getRelative(11, 0, zOffset).setType(FILL_BLOCK);
        startingCornerBlock.getRelative(11, 0, zOffset+1).setType(medium);
        startingCornerBlock.getRelative(11, 0, zOffset+2).setType(FILL_BLOCK);
        startingCornerBlock.getRelative(12, 0, zOffset).setType(FILL_BLOCK);
        startingCornerBlock.getRelative(12, 0, zOffset+1).setType(medium);
        startingCornerBlock.getRelative(12, 0, zOffset+2).setType(FILL_BLOCK);
        startingCornerBlock.getRelative(13, 0, zOffset).setType(FILL_BLOCK);
        startingCornerBlock.getRelative(13, 0, zOffset+1).setType(medium);
        startingCornerBlock.getRelative(13, 0, zOffset+2).setType(FILL_BLOCK);
        startingCornerBlock.getRelative(14, 0, zOffset).setType(FILL_BLOCK);
        startingCornerBlock.getRelative(14, 0, zOffset+1).setType(medium);
        startingCornerBlock.getRelative(14, 0, zOffset+2).setType(FILL_BLOCK);
        startingCornerBlock.getRelative(15, 0, zOffset).setType(FILL_BLOCK);
        startingCornerBlock.getRelative(15, 0, zOffset+1).setType(medium);
        startingCornerBlock.getRelative(15, 0, zOffset+2).setType(FILL_BLOCK);
        startingCornerBlock.getRelative(16, 0, zOffset).setType(light);
        startingCornerBlock.getRelative(16, 0, zOffset+1).setType(light);
        startingCornerBlock.getRelative(16, 0, zOffset+2).setType(light);
        startingCornerBlock.getRelative(17, 0, zOffset).setType(light);
        startingCornerBlock.getRelative(17, 0, zOffset+1).setType(medium);
        startingCornerBlock.getRelative(17, 0, zOffset+2).setType(light);
        startingCornerBlock.getRelative(18, 0, zOffset).setType(light);
        startingCornerBlock.getRelative(18, 0, zOffset+1).setType(light);
        startingCornerBlock.getRelative(18, 0, zOffset+2).setType(light);
        startingCornerBlock.getRelative(19, 0, zOffset).setType(dark);
        startingCornerBlock.getRelative(19, 0, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(19, 0, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(20, 0, zOffset).setType(dark);
        startingCornerBlock.getRelative(20, 0, zOffset+1).setType(medium);
        startingCornerBlock.getRelative(20, 0, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(21, 0, zOffset).setType(dark);
        startingCornerBlock.getRelative(21, 0, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(21, 0, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(19, 1, zOffset).setType(dark);
        startingCornerBlock.getRelative(19, 1, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(19, 1, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(20, 1, zOffset).setType(dark);
        startingCornerBlock.getRelative(20, 1, zOffset+1).setType(medium);
        startingCornerBlock.getRelative(20, 1, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(21, 1, zOffset).setType(dark);
        startingCornerBlock.getRelative(21, 1, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(21, 1, zOffset+2).setType(dark);
    }
}
