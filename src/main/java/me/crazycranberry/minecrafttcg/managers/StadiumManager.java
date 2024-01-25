package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.events.BuildStadiumEvent;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_1_BLUE_CHICKEN;
import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_1_GREEN_CHICKEN;
import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_1_RED_CHICKEN;
import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_2_BLUE_CHICKEN;
import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_2_GREEN_CHICKEN;
import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_2_RED_CHICKEN;
import static me.crazycranberry.minecrafttcg.model.Stadium.BLUE_MATERIAL;
import static me.crazycranberry.minecrafttcg.model.Stadium.GREEN_MATERIAL;
import static me.crazycranberry.minecrafttcg.model.Stadium.RED_MATERIAL;
import static org.bukkit.Material.BIRCH_BUTTON;
import static org.bukkit.Material.BIRCH_WALL_SIGN;
import static org.bukkit.Material.OAK_WALL_SIGN;
import static org.bukkit.Material.REDSTONE_LAMP;
import static org.bukkit.Material.STONE_BUTTON;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.SOUTH;

public class StadiumManager implements Listener {
    public static final Vector PLAYER_1_SIGN_OFFSET = new Vector(3, 10, 4);
    public static final Vector PLAYER_1_MANA_OFFSET = new Vector(3, 13, 0);
    public static final Vector PLAYER_2_SIGN_OFFSET = new Vector(23, 10, 6);
    public static final Vector PLAYER_2_MANA_OFFSET = new Vector(23, 13, 10);
    private static final Material FILL_BLOCK = Material.WHITE_TERRACOTTA;
    private static final Map<World, Stadium> stadiums = new HashMap<>();

    @EventHandler
    private void onBuildRequested(BuildStadiumEvent event) {
        ScoreboardManager sm = Bukkit.getScoreboardManager();
        Scoreboard s = sm.getNewScoreboard();
        Objective h = s.registerNewObjective("showhealth", Criteria.HEALTH, ChatColor.RED + "❤");
        h.setDisplaySlot(DisplaySlot.BELOW_NAME);
        event.player1().setScoreboard(s);
        event.player2().setScoreboard(s);
        buildStadium(event.getStartingCorner());
        stadiums.put(event.getStartingCorner().getWorld(), new Stadium(event.getStartingCorner(),
                event.player1(),
                event.player2(),
                summonChicken(PLAYER_1_RED_CHICKEN, event.getStartingCorner()),
                summonChicken(PLAYER_1_BLUE_CHICKEN, event.getStartingCorner()),
                summonChicken(PLAYER_1_GREEN_CHICKEN, event.getStartingCorner()),
                summonChicken(PLAYER_2_RED_CHICKEN, event.getStartingCorner()),
                summonChicken(PLAYER_2_BLUE_CHICKEN, event.getStartingCorner()),
                summonChicken(PLAYER_2_GREEN_CHICKEN, event.getStartingCorner()))
            );
    }

    @EventHandler
    private void onLampTryingToTurnOff(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if (stadiums.containsKey(block.getWorld()) && block.getType().equals(REDSTONE_LAMP)) {
            event.setNewCurrent(1);
        }
    }

    public static void updateManaForANewTurn(Stadium stadium, int turn) {
        Block startingCorner = stadium.startingCorner().getBlock();
        int x1 = (int) PLAYER_1_MANA_OFFSET.getX(), y1 = (int) PLAYER_1_MANA_OFFSET.getY(), z1 = (int) PLAYER_1_MANA_OFFSET.getZ();
        int x2 = (int) PLAYER_2_MANA_OFFSET.getX(), y2 = (int) PLAYER_2_MANA_OFFSET.getY(), z2 = (int) PLAYER_2_MANA_OFFSET.getZ();
        for (int i = 0; i < Math.min(turn, 10); i++) {
            int passedHalfway = i / 5;
            Block lamp1 = startingCorner.getRelative(x1, y1, z1+passedHalfway+i);
            lamp1.setType(REDSTONE_LAMP);
            Lightable lightable1 = (Lightable) lamp1.getBlockData();
            lightable1.setLit(true);
            lamp1.setBlockData(lightable1);
            Block lamp2 = startingCorner.getRelative(x2, y2, z2-passedHalfway-i);
            lamp2.setType(REDSTONE_LAMP);
            Lightable lightable2 = (Lightable) lamp2.getBlockData();
            lightable2.setLit(true);
            lamp2.setBlockData(lightable2);
        }
    }

    public static void reduceMana(Stadium stadium, int mana, int turn, boolean isPlayer1) {
        Block startingCorner = stadium.startingCorner().getBlock();
        Vector offset = isPlayer1 ? PLAYER_1_MANA_OFFSET : PLAYER_2_MANA_OFFSET;
        int x = (int) offset.getX(), y = (int) offset.getY(), z = (int) offset.getZ();
        for (int i = 0; i < turn; i++) {
            int passedHalfway = i / 5;
            int direction = isPlayer1 ? 1 : -1;
            int increment = (i + passedHalfway) * direction;
            Block lamp = startingCorner.getRelative(x, y, z+increment);
            lamp.setType(REDSTONE_LAMP);
            Lightable lightable1 = (Lightable) lamp.getBlockData();
            lightable1.setLit(i < mana);
            lamp.setBlockData(lightable1);
        }
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

    private LivingEntity summonChicken(Spot spot, Location startingCorner) {
        return (LivingEntity) startingCorner.getWorld().spawnEntity(startingCorner.clone().add(spot.offset()), EntityType.CHICKEN);
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
        // Player towers
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
        // Sides of rows
        for (int i = 2; i < 25; i++) {
            startingCornerBlock.getRelative(i, 1, -1).setType(Material.BARRIER);
            startingCornerBlock.getRelative(i, 1, 3).setType(Material.BARRIER);
            startingCornerBlock.getRelative(i, 1, 7).setType(Material.BARRIER);
            startingCornerBlock.getRelative(i, 1, 11).setType(Material.BARRIER);
            startingCornerBlock.getRelative(i, 2, -1).setType(Material.BARRIER);
            startingCornerBlock.getRelative(i, 2, 3).setType(Material.BARRIER);
            startingCornerBlock.getRelative(i, 2, 7).setType(Material.BARRIER);
            startingCornerBlock.getRelative(i, 2, 11).setType(Material.BARRIER);
            startingCornerBlock.getRelative(i, 3, -1).setType(Material.BARRIER);
            startingCornerBlock.getRelative(i, 3, 3).setType(Material.BARRIER);
            startingCornerBlock.getRelative(i, 3, 7).setType(Material.BARRIER);
            startingCornerBlock.getRelative(i, 3, 11).setType(Material.BARRIER);
        }
        // Ends of rows
        for (int i = 0; i < 3; i++) {
            for (int j = 1; j < 4; j++) {
                int zOffset = i * 4;
                startingCornerBlock.getRelative(25, j, 0 + zOffset).setType(Material.BARRIER);
                startingCornerBlock.getRelative(25, j, 1 + zOffset).setType(Material.BARRIER);
                startingCornerBlock.getRelative(25, j, 2 + zOffset).setType(Material.BARRIER);
            }
        }
    }

    private void buildPlayer1Tower(Block startingCornerBlock) {
        // Sign posts:
        int signX = (int) PLAYER_1_SIGN_OFFSET.getX();
        int signY = (int) PLAYER_1_SIGN_OFFSET.getY();
        int signZ = (int) PLAYER_1_SIGN_OFFSET.getZ();
        startingCornerBlock.getRelative(3, 8, 3).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(3, 9, 3).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(3, 10, 3).setType(Material.BIRCH_PLANKS);
        makeSign(startingCornerBlock, signX, signY, signZ, BIRCH_WALL_SIGN, SOUTH, true, false);
        makeSign(startingCornerBlock, signX, signY-1, signZ, BIRCH_WALL_SIGN, SOUTH, false, false);
        makeSign(startingCornerBlock, signX, signY-2, signZ, BIRCH_WALL_SIGN, SOUTH, false, false);

        // Phase button:
        startingCornerBlock.getRelative(3, 8, 7).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(3, 9, 7).setType(Material.BIRCH_PLANKS);
        makeButton(startingCornerBlock, 3, 9, 6, NORTH, STONE_BUTTON);
        makeSign(startingCornerBlock, 3, 8, 6, BIRCH_WALL_SIGN, NORTH, false, true);

        // Building:
        startingCornerBlock.getRelative(1, 7, 4).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(1, 7, 5).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(1, 7, 6).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(2, 7, 4).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(2, 7, 5).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(2, 7, 6).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(3, 7, 4).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(3, 7, 5).setType(Material.BIRCH_PLANKS);
        startingCornerBlock.getRelative(3, 7, 6).setType(Material.BIRCH_PLANKS);

        // Mana blocks
        int manaX = (int) PLAYER_1_MANA_OFFSET.getX();
        int manaY = (int) PLAYER_1_MANA_OFFSET.getY();
        int manaZ = (int) PLAYER_1_MANA_OFFSET.getZ();
        startingCornerBlock.getRelative(manaX, manaY, manaZ).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ + 1).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ + 2).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ + 3).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ + 4).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ + 6).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ + 7).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ + 8).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ + 9).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ + 10).setType(Material.BEDROCK);
    }

    private void buildPlayer2Tower(Block startingCornerBlock) {
        // Sign posts:
        int signX = (int) PLAYER_2_SIGN_OFFSET.getX();
        int signY = (int) PLAYER_2_SIGN_OFFSET.getY();
        int signZ = (int) PLAYER_2_SIGN_OFFSET.getZ();
        startingCornerBlock.getRelative(23, 8, 7).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(23, 9, 7).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(23, 10, 7).setType(Material.COBBLESTONE);
        makeSign(startingCornerBlock, signX, signY, signZ, OAK_WALL_SIGN, NORTH, true, false);
        makeSign(startingCornerBlock, signX, signY-1, signZ, OAK_WALL_SIGN, NORTH, false, false);
        makeSign(startingCornerBlock, signX, signY-2, signZ, OAK_WALL_SIGN, NORTH, false, false);

        // Phase button:
        startingCornerBlock.getRelative(23, 8, 3).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(23, 9, 3).setType(Material.COBBLESTONE);
        makeButton(startingCornerBlock, 23, 9, 4, SOUTH, BIRCH_BUTTON);
        makeSign(startingCornerBlock, 23, 8, 4, OAK_WALL_SIGN, SOUTH, false, true);

        //Building:
        startingCornerBlock.getRelative(23, 7, 4).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(23, 7, 5).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(23, 7, 6).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(24, 7, 4).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(24, 7, 5).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(24, 7, 6).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(25, 7, 4).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(25, 7, 5).setType(Material.COBBLESTONE);
        startingCornerBlock.getRelative(25, 7, 6).setType(Material.COBBLESTONE);

        // Mana blocks
        int manaX = (int) PLAYER_2_MANA_OFFSET.getX();
        int manaY = (int) PLAYER_2_MANA_OFFSET.getY();
        int manaZ = (int) PLAYER_2_MANA_OFFSET.getZ();
        startingCornerBlock.getRelative(manaX, manaY, manaZ).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ - 1).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ - 2).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ - 3).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ - 4).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ - 6).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ - 7).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ - 8).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ - 9).setType(Material.BEDROCK);
        startingCornerBlock.getRelative(manaX, manaY, manaZ - 10).setType(Material.BEDROCK);
    }

    private void makeButton(Block startingCornerBlock, int x, int y, int z, BlockFace direction, Material material) {
        Block buttonBlock = startingCornerBlock.getRelative(x, y, z);
        buttonBlock.setType(material);
        BlockState state = buttonBlock.getState();
        Directional directional = (Directional) state.getBlockData();
        directional.setFacing(direction);
        state.setBlockData(directional);
        state.update();
    }

    private void makeSign(Block startingCornerBlock, int x, int y, int z, Material material, BlockFace blockFace, boolean addDescText, boolean addPhaseText) {
        Block signBlock = startingCornerBlock.getRelative(x, y, z);
        signBlock.setType(material);
        Sign signState = (Sign) signBlock.getState();
        Directional directional = (Directional) signState.getBlockData();
        directional.setFacing(blockFace);
        signState.setBlockData(directional);
        if (addDescText) {
            signState.getSide(Side.FRONT).setLine(0, "Left Click a Minion");
            signState.getSide(Side.FRONT).setLine(1, "for a description");
            signState.getSide(Side.FRONT).setLine(2, "|");
            signState.getSide(Side.FRONT).setLine(3, "V");
        } else if (addPhaseText) {
            signState.getSide(Side.FRONT).setLine(0, "/\\");
            signState.getSide(Side.FRONT).setLine(1, "|");
            signState.getSide(Side.FRONT).setLine(2, "Go to the next");
            signState.getSide(Side.FRONT).setLine(3, "turn Phase");
        } else {
            signState.getSide(Side.FRONT).setLine(0, "");
            signState.getSide(Side.FRONT).setLine(1, "");
            signState.getSide(Side.FRONT).setLine(2, "");
            signState.getSide(Side.FRONT).setLine(3, "");
        }
        signState.update();
    }

    private void buildRow(Block startingCornerBlock, int zOffset, Material light, Material medium, Material dark) {
        startingCornerBlock.getRelative(2, 0, zOffset).setType(dark);
        startingCornerBlock.getRelative(2, 0, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(2, 0, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(3, 0, zOffset).setType(dark);
        startingCornerBlock.getRelative(3, 0, zOffset+1).setType(medium);
        startingCornerBlock.getRelative(3, 0, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(4, 0, zOffset).setType(dark);
        startingCornerBlock.getRelative(4, 0, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(4, 0, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(2, 1, zOffset).setType(dark);
        startingCornerBlock.getRelative(2, 1, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(2, 1, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(3, 1, zOffset).setType(dark);
        startingCornerBlock.getRelative(3, 1, zOffset+1).setType(medium);
        startingCornerBlock.getRelative(3, 1, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(4, 1, zOffset).setType(dark);
        startingCornerBlock.getRelative(4, 1, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(4, 1, zOffset+2).setType(dark);
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
        startingCornerBlock.getRelative(22, 0, zOffset).setType(dark);
        startingCornerBlock.getRelative(22, 0, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(22, 0, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(23, 0, zOffset).setType(dark);
        startingCornerBlock.getRelative(23, 0, zOffset+1).setType(medium);
        startingCornerBlock.getRelative(23, 0, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(24, 0, zOffset).setType(dark);
        startingCornerBlock.getRelative(24, 0, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(24, 0, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(22, 1, zOffset).setType(dark);
        startingCornerBlock.getRelative(22, 1, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(22, 1, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(23, 1, zOffset).setType(dark);
        startingCornerBlock.getRelative(23, 1, zOffset+1).setType(medium);
        startingCornerBlock.getRelative(23, 1, zOffset+2).setType(dark);
        startingCornerBlock.getRelative(24, 1, zOffset).setType(dark);
        startingCornerBlock.getRelative(24, 1, zOffset+1).setType(dark);
        startingCornerBlock.getRelative(24, 1, zOffset+2).setType(dark);
    }
}
