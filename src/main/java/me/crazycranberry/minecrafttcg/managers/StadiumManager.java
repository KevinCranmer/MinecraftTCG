package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.events.DuelCloseEvent;
import me.crazycranberry.minecrafttcg.events.DuelStartEvent;
import me.crazycranberry.minecrafttcg.events.RegisterListenersEvent;
import me.crazycranberry.minecrafttcg.managers.utils.StadiumDefinition;
import me.crazycranberry.minecrafttcg.model.Deck;
import me.crazycranberry.minecrafttcg.model.Participant;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.managers.utils.CsvLoader.loadBlocksCsv;
import static me.crazycranberry.minecrafttcg.managers.utils.CsvLoader.loadMobsCsv;
import static me.crazycranberry.minecrafttcg.managers.utils.StadiumDefinition.STADIUM_DEFINITIONS;
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
    public static final Vector PLAYER_1_SIGN_OFFSET = new Vector(3, 10, 4);
    public static final Vector PLAYER_1_MANA_OFFSET = new Vector(3, 13, 0);
    public static final Vector PLAYER_2_SIGN_OFFSET = new Vector(23, 10, 6);
    public static final Vector PLAYER_2_MANA_OFFSET = new Vector(23, 13, 10);
    public static final int DISTANCE_BETWEEN_STADIUMS_Z = 100;
    public static final EntityType PLAYER_PROXY_ENTITY_TYPE = EntityType.COW;
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
        StadiumDefinition sd = randomFromList(STADIUM_DEFINITIONS).get();
        buildStadium(startingCorner, sd);
        Stadium newStadium = new Stadium(startingCorner, player1, Deck.fromConfig(player1), player2, Deck.fromConfig(player2), ranked, sd);
        stadiums.put(startingCorner, newStadium);
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

    @EventHandler
    private void onExplode(EntityExplodeEvent event) {
        if (stadium(event.getEntity().getLocation()) != null) {
            event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            event.setCancelled(true);
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

    private static void buildStadium(Location startingCorner, StadiumDefinition sd) {
        loadBlocksCsv(startingCorner.getWorld(), sd.blocksInputStream(), (int) (startingCorner.getX() + sd.xOffset()), (int) (startingCorner.getY() + sd.yOffset()), (int) (startingCorner.getZ() + sd.zOffset()));
        loadMobsCsv(startingCorner.getWorld(), sd.mobsInputStream(), (int) (startingCorner.getX() + sd.xOffset()), (int) (startingCorner.getY() + sd.yOffset()), (int) (startingCorner.getZ() + sd.zOffset()));
        Block p1Sign = startingCorner.getBlock().getRelative((int) PLAYER_1_SIGN_OFFSET.getX(), (int) PLAYER_1_SIGN_OFFSET.getY(), (int) PLAYER_1_SIGN_OFFSET.getZ());
        Block p2Sign = startingCorner.getBlock().getRelative((int) PLAYER_2_SIGN_OFFSET.getX(), (int) PLAYER_2_SIGN_OFFSET.getY(), (int) PLAYER_2_SIGN_OFFSET.getZ());
        addDescriptionText(p1Sign);
        addDescriptionText(p2Sign);
        Block p1SignTurnPhase = startingCorner.getBlock().getRelative((int) PLAYER_1_SIGN_OFFSET.getX(), (int) PLAYER_1_SIGN_OFFSET.getY()-2, (int) PLAYER_1_SIGN_OFFSET.getZ() + 2);
        Block p2SignTurnPhase = startingCorner.getBlock().getRelative((int) PLAYER_2_SIGN_OFFSET.getX(), (int) PLAYER_2_SIGN_OFFSET.getY()-2, (int) PLAYER_2_SIGN_OFFSET.getZ() - 2);
        addNextPhaseText(p1SignTurnPhase);
        addNextPhaseText(p2SignTurnPhase);
    }

    private static void addDescriptionText(Block sign) {
        Sign signState = (Sign) sign.getState();
        signState.getSide(Side.FRONT).setLine(0, "Left click things");
        signState.getSide(Side.FRONT).setLine(1, "for a description");
        signState.getSide(Side.FRONT).setLine(2, "|");
        signState.getSide(Side.FRONT).setLine(3, "V");
        signState.update();
    }

    private static void addNextPhaseText(Block sign) {
        Sign signState = (Sign) sign.getState();
        signState.getSide(Side.FRONT).setLine(0, "/\\");
        signState.getSide(Side.FRONT).setLine(1, "|");
        signState.getSide(Side.FRONT).setLine(2, "Go to the next");
        signState.getSide(Side.FRONT).setLine(3, "turn Phase");
        signState.update();
    }

    private static void clearStadiumArea(Location startingCorner) {
        for (int x = -20; x < 60; x++) {
            for (int y = -30; y < 20; y++) {
                for (int z = -20; z < 50; z++) {
                    startingCorner.getBlock().getRelative(x, y, z).setType(AIR);
                }
            }
        }
        startingCorner.getWorld().getNearbyEntities(startingCorner, 60, 40, 50).stream().filter(e -> !e.getType().equals(EntityType.PLAYER)).forEach(Entity::remove);
    }
}
