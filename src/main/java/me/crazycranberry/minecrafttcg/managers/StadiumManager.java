package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.events.DuelCloseEvent;
import me.crazycranberry.minecrafttcg.events.DuelStartEvent;
import me.crazycranberry.minecrafttcg.events.RegisterListenersEvent;
import me.crazycranberry.minecrafttcg.model.Deck;
import me.crazycranberry.minecrafttcg.model.Participant;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
import org.bukkit.entity.Entity;
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
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_1_BLUE_CHICKEN;
import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_1_GREEN_CHICKEN;
import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_1_OUTLOOK;
import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_1_RED_CHICKEN;
import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_2_BLUE_CHICKEN;
import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_2_GREEN_CHICKEN;
import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_2_OUTLOOK;
import static me.crazycranberry.minecrafttcg.model.Spot.PLAYER_2_RED_CHICKEN;
import static me.crazycranberry.minecrafttcg.model.Stadium.BLUE_MATERIAL;
import static me.crazycranberry.minecrafttcg.model.Stadium.GREEN_MATERIAL;
import static me.crazycranberry.minecrafttcg.model.Stadium.RED_MATERIAL;
import static me.crazycranberry.minecrafttcg.utils.StartingWorldConfigUtils.restoreStartingWorldConfig;
import static me.crazycranberry.minecrafttcg.utils.StartingWorldConfigUtils.saveStartingWorldConfig;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.BIRCH_BUTTON;
import static org.bukkit.Material.BIRCH_WALL_SIGN;
import static org.bukkit.Material.OAK_WALL_SIGN;
import static org.bukkit.Material.REDSTONE_LAMP;
import static org.bukkit.Material.STONE_BUTTON;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.SOUTH;

public class StadiumManager implements Listener {
    public static final int TOTAL_X = 26;
    public static final int TOTAL_Y = 15;
    public static final int TOTAL_Z = 13;
    public static final Vector PLAYER_1_SIGN_OFFSET = new Vector(3, 10, 4);
    public static final Vector PLAYER_1_MANA_OFFSET = new Vector(3, 13, 0);
    public static final Vector PLAYER_2_SIGN_OFFSET = new Vector(23, 10, 6);
    public static final Vector PLAYER_2_MANA_OFFSET = new Vector(23, 13, 10);
    public static final int DISTANCE_BETWEEN_STADIUMS_Z = 100;
    public static final EntityType PLAYER_PROXY_ENTITY_TYPE = EntityType.COW;
    private static final Material FILL_BLOCK = Material.WHITE_TERRACOTTA;
    private static final Map<Location, Stadium> stadiums = new HashMap<>();
    private static final Map<UUID, Scoreboard> playerOldScoreboards = new HashMap<>();

    @EventHandler
    private void onDuelClose(DuelCloseEvent event) {
        if (event.stadium().player1().getWorld().equals(event.stadium().startingCorner().getWorld()) && !event.stadium().player1().isDead()) {
            restoreStartingWorldConfig(event.stadium().player1());
        }
        if (event.stadium().player2().getWorld().equals(event.stadium().startingCorner().getWorld()) && !event.stadium().player2().isDead()) {
            restoreStartingWorldConfig(event.stadium().player2());
        }
        stadiums.remove(event.stadium().startingCorner());
        if (stadiums.isEmpty()) {
            Bukkit.getPluginManager().callEvent(new RegisterListenersEvent(false));
        }
    }

    private static void setupStadium(Location startingCorner, Player player1, Player player2, Boolean ranked) {
        clearStadiumArea(startingCorner);
        buildStadium(startingCorner);
        Stadium newStadium = new Stadium(startingCorner, player1, Deck.fromConfig(player1), player2, Deck.fromConfig(player2), ranked);
        stadiums.put(startingCorner, newStadium);
        startingCorner.getWorld().getNearbyEntities(startingCorner, 40, 40, 40).stream().filter(e -> !e.getType().equals(EntityType.PLAYER)).forEach(Entity::remove);
        newStadium.setChickens(summonChicken(PLAYER_1_RED_CHICKEN, startingCorner),
                summonChicken(PLAYER_1_BLUE_CHICKEN, startingCorner),
                summonChicken(PLAYER_1_GREEN_CHICKEN, startingCorner),
                summonChicken(PLAYER_2_RED_CHICKEN, startingCorner),
                summonChicken(PLAYER_2_BLUE_CHICKEN, startingCorner),
                summonChicken(PLAYER_2_GREEN_CHICKEN, startingCorner));
    }

    public static Optional<Scoreboard> getOriginalScoreboardAndRemoveIt(Player p) {
        return Optional.ofNullable(playerOldScoreboards.remove(p.getUniqueId()));
    }

    @EventHandler
    private void onLampTryingToTurnOff(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if (!stadiums.isEmpty() && stadiums.keySet().stream().findFirst().get().getWorld().equals(block.getWorld()) && block.getType().equals(REDSTONE_LAMP)) {
            event.setNewCurrent(1);
        }
    }

    public static void updateManaLamps(Stadium stadium) {
        updateManaLampsForPlayer(stadium, stadium.player1());
        updateManaLampsForPlayer(stadium, stadium.player2());
    }

    public static void updateManaLampsForPlayer(Stadium stadium, Player player) {
        boolean isPlayer1 = stadium.player1().equals(player);
        int x, y, z;
        if (isPlayer1) {
            x = (int) PLAYER_1_MANA_OFFSET.getX();
            y = (int) PLAYER_1_MANA_OFFSET.getY();
            z = (int) PLAYER_1_MANA_OFFSET.getZ();
        } else {
            x = (int) PLAYER_2_MANA_OFFSET.getX();
            y = (int) PLAYER_2_MANA_OFFSET.getY();
            z = (int) PLAYER_2_MANA_OFFSET.getZ();
        }
        Block startingCorner = stadium.startingCorner().getBlock();
        for (int i = 0; i < Math.min(stadium.playerMaxMana(player), 10); i++) {
            int passedHalfway = i / 5;
            Block lamp1 = startingCorner.getRelative(x, y, stadium.player1().equals(player) ? z+passedHalfway+i : z-passedHalfway-i);
            lamp1.setType(REDSTONE_LAMP);
            Lightable lightable1 = (Lightable) lamp1.getBlockData();
            lightable1.setLit(stadium.playerMana(player) > i);
            lamp1.setBlockData(lightable1);
        }
    }

    public static void reduceMana(Stadium stadium, int mana, int turn, boolean isPlayer1) {
        Block startingCorner = stadium.startingCorner().getBlock();
        Vector offset = isPlayer1 ? PLAYER_1_MANA_OFFSET : PLAYER_2_MANA_OFFSET;
        int x = (int) offset.getX(), y = (int) offset.getY(), z = (int) offset.getZ();
        for (int i = 0; i < Math.min(10, turn); i++) {
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
        Stadium stadium = stadium(p.getLocation());
        if (stadium != null) {
            stadium.playerTargeting(p, spot);
        }
    }

    public static Location locOfSpot(Location playerLoc, Spot spot) {
        Stadium stadium = stadium(playerLoc);
        return stadium == null ? null : stadium.locOfSpot(spot);
    }

    /** This will find the stadium that's closest to the location provided. */
    public static Stadium stadium(Location locOfEntityInTheStadium) {
        if (stadiums.isEmpty() || !stadiums.keySet().stream().allMatch(k -> k.getWorld().equals(locOfEntityInTheStadium.getWorld()))) {
            return null;
        }
        return stadiums.entrySet().stream()
                .min((e1, e2) -> (int) (e1.getKey().distanceSquared(locOfEntityInTheStadium) - e2.getKey().distanceSquared(locOfEntityInTheStadium)))
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    public static Location getNextAvailableStartingCorner(World w) {
        if (stadiums.isEmpty()) {
            return new Location(w, 0.5, 100, 0.5);
        }
        Location prevLoc = null;
        for (Location l : stadiums.keySet().stream().sorted((k1, k2) -> (int) (k1.getZ() - k2.getZ())).toList()) {
            if (prevLoc == null && l.getZ() != 0.5) {
                return new Location(w, 0.5, 100, 0.5);
            }
            if (prevLoc != null && l.getZ() > prevLoc.getZ() + DISTANCE_BETWEEN_STADIUMS_Z) {
                return new Location(w, 0.5, 100, prevLoc.getZ() + DISTANCE_BETWEEN_STADIUMS_Z);
            }
            prevLoc = l;
        }
        return new Location(w, 0.5, 100, prevLoc.getZ() + DISTANCE_BETWEEN_STADIUMS_Z);
    }

    public static void sendPlayersToDuel(World w, Player requester, Player accepter, Boolean ranked) {
        Bukkit.getPluginManager().callEvent(new RegisterListenersEvent(true));
        Location nextStartingCorner = getNextAvailableStartingCorner(w);
        saveStartingWorldConfig(new Participant(requester));
        playerOldScoreboards.put(requester.getUniqueId(), requester.getScoreboard());
        saveStartingWorldConfig(new Participant(accepter));
        playerOldScoreboards.put(accepter.getUniqueId(), accepter.getScoreboard());
        Stadium stadium;
        if (Math.random() < 0.5) {
            setupStadium(nextStartingCorner, requester, accepter, ranked);
            stadium = stadiums.get(nextStartingCorner);
            requester.teleport(stadium.locOfSpot(PLAYER_1_OUTLOOK));
            accepter.teleport(stadium.locOfSpot(PLAYER_2_OUTLOOK));
        } else {
            setupStadium(nextStartingCorner, accepter, requester, ranked);
            stadium = stadiums.get(nextStartingCorner);
            accepter.teleport(stadium.locOfSpot(PLAYER_1_OUTLOOK));
            requester.teleport(stadium.locOfSpot(PLAYER_2_OUTLOOK));
        }
        cleanUpPlayerBeforeDuel(requester);
        cleanUpPlayerBeforeDuel(accepter);
        Bukkit.getPluginManager().callEvent(new DuelStartEvent(stadium));
    }

    private static void cleanUpPlayerBeforeDuel(Player p) {
        p.setHealth(20);
        p.setFoodLevel(10);
        p.setGameMode(GameMode.ADVENTURE);
        p.getInventory().clear();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(p.getName() + "senemyhealth", Criteria.DUMMY, "Enemy's Health");
        objective.setDisplayName("Enemy's Health");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        p.setScoreboard(scoreboard);
    }

    private static LivingEntity summonChicken(Spot spot, Location startingCorner) {
        return (LivingEntity) startingCorner.getWorld().spawnEntity(startingCorner.clone().add(spot.offset()), PLAYER_PROXY_ENTITY_TYPE);
    }

    private static void buildStadium(Location location) {
        buildRow(location.getBlock(), 0, Material.PURPLE_TERRACOTTA, RED_MATERIAL, Material.PINK_TERRACOTTA);
        buildRow(location.getBlock(), 4, Material.BLUE_TERRACOTTA, BLUE_MATERIAL, Material.CYAN_TERRACOTTA);
        buildRow(location.getBlock(), 8, Material.YELLOW_TERRACOTTA, GREEN_MATERIAL, Material.GREEN_TERRACOTTA);
        buildBarriers(location.getBlock());
        buildPlayer1Tower(location.getBlock());
        buildPlayer2Tower(location.getBlock());
    }

    private static void clearStadiumArea(Location startingCorner) {
        for (int x = 0; x < TOTAL_X; x++) {
            for (int y = 0; y < TOTAL_Y; y++) {
                for (int z = 0; z < TOTAL_Z; z++) {
                    startingCorner.getBlock().getRelative(x, y, z).setType(AIR);
                }
            }
        }
    }

    private static void buildBarriers(Block startingCornerBlock) {
        // Walls
        for (int y = 0; y < 12; y++) {
            for (int x = 0; x <= 26; x++) {
                startingCornerBlock.getRelative(x, y, -1).setType(Material.BARRIER);
                startingCornerBlock.getRelative(x, y, 11).setType(Material.BARRIER);
            }
            for (int z = -1; z <= 11; z++) {
                startingCornerBlock.getRelative(0, y, z).setType(Material.BARRIER);
                startingCornerBlock.getRelative(26, y, z).setType(Material.BARRIER);
            }
        }
        // Floor
        for (int x = 0; x <= 26; x++) {
            for (int z = -1; z <= 11; z++) {
                startingCornerBlock.getRelative(x, 6, z).setType(Material.BARRIER);
            }
        }
        // Sides of rows
        for (int i = 2; i < 25; i++) {
            for (int y = 0; y <= 6; y++) {
                startingCornerBlock.getRelative(i, y, -1).setType(Material.BARRIER);
                startingCornerBlock.getRelative(i, y, 3).setType(Material.BARRIER);
                startingCornerBlock.getRelative(i, y, 7).setType(Material.BARRIER);
                startingCornerBlock.getRelative(i, y, 11).setType(Material.BARRIER);
            }
        }
        // Ends of rows
        for (int z = 0; z <= 11; z++) {
            for (int y = 0; y <= 6; y++) {
                startingCornerBlock.getRelative(25, y, z).setType(Material.BARRIER);
                startingCornerBlock.getRelative(1, y, z).setType(Material.BARRIER);
            }
        }
    }

    private static void buildPlayer1Tower(Block startingCornerBlock) {
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

    private static void buildPlayer2Tower(Block startingCornerBlock) {
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

    private static void makeButton(Block startingCornerBlock, int x, int y, int z, BlockFace direction, Material material) {
        Block buttonBlock = startingCornerBlock.getRelative(x, y, z);
        buttonBlock.setType(material);
        BlockState state = buttonBlock.getState();
        Directional directional = (Directional) state.getBlockData();
        directional.setFacing(direction);
        state.setBlockData(directional);
        state.update();
    }

    private static void makeSign(Block startingCornerBlock, int x, int y, int z, Material material, BlockFace blockFace, boolean addDescText, boolean addPhaseText) {
        Block signBlock = startingCornerBlock.getRelative(x, y, z);
        signBlock.setType(material);
        Sign signState = (Sign) signBlock.getState();
        Directional directional = (Directional) signState.getBlockData();
        directional.setFacing(blockFace);
        signState.setBlockData(directional);
        if (addDescText) {
            signState.getSide(Side.FRONT).setLine(0, "Left click things");
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

    private static void buildRow(Block startingCornerBlock, int zOffset, Material light, Material medium, Material dark) {
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
