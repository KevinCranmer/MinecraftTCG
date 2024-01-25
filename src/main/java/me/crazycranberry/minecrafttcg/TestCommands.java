package me.crazycranberry.minecrafttcg;

import me.crazycranberry.minecrafttcg.events.BuildStadiumEvent;
import me.crazycranberry.minecrafttcg.events.CombatEndEvent;
import me.crazycranberry.minecrafttcg.events.CombatStartEvent;
import me.crazycranberry.minecrafttcg.events.DeckViewRequestEvent;
import me.crazycranberry.minecrafttcg.events.DuelStartEvent;
import me.crazycranberry.minecrafttcg.goals.WalkToLocationGoal;
import me.crazycranberry.minecrafttcg.managers.DuelActionsManager;
import me.crazycranberry.minecrafttcg.managers.LookAndHighlightManager;
import me.crazycranberry.minecrafttcg.managers.StadiumManager;
import me.crazycranberry.minecrafttcg.managers.TurnManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftSkeleton;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.Optional;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;

public class TestCommands {
    static Skeleton skeleton;
    static Zombie zombie;

    static LookAndHighlightManager lookAndHighlightManager;
    static DuelActionsManager duelActionsManager;

    static RangedBowAttackGoal<net.minecraft.world.entity.monster.Skeleton> shootingGoal;

    public static void executeTestCommand(String[] command, Player p) {
        switch (command[0]) {
            case "spawn":
                spawn(p);
                break;
            case "attack":
                attack(p);
                break;
            case "shoot":
                shoot(p);
                break;
            case "stopShooting":
                stopShooting(p);
                break;
            case "walk":
                walk(p);
                break;
            case "move":
                move(p, Double.parseDouble(command[1]), Double.parseDouble(command[2]), Double.parseDouble(command[3]));
                break;
            case "removeGoals":
                removeGoals(p);
                break;
            case "spawnAndRemove":
                spawnAndRemove(p);
                break;
            case "aion":
                aion(p);
                break;
            case "aioff":
                aioff(p);
                break;
            case "particle":
                spawnParticle(p);
                break;
            case "buildStadium":
                buildStadium(p, command[1], command[2], p.getLocation());
                break;
            case "setup":
                setup(p);
                break;
            case "trackVision":
                trackVision(p);
                break;
            case "stopTrackingVision":
                stopTrackingVision(p);
                break;
            case "trackDuel":
                trackDuel(p);
                break;
            case "stopDuel":
                stopDuel(p);
                break;
            case "startCombat":
                startCombat(p);
                break;
            case "endCombat":
                endCombat(p);
                break;
            case "deck":
                deck(p);
                break;
            default:
                System.out.println("Unknown command: " + command[0]);
        }
    }

    private static void endCombat(Player p) {
        Bukkit.getPluginManager().callEvent(new CombatEndEvent(StadiumManager.stadium(p.getWorld())));
    }

    private static void startCombat(Player p) {
        Bukkit.getPluginManager().callEvent(new CombatStartEvent(StadiumManager.stadium(p.getWorld())));
    }

    private static void setup(Player p) {
        Player me = Bukkit.getOnlinePlayers().stream().filter(player -> player.getName().equals("Crazy_Cranberry")).findFirst().get();
        me.getNearbyEntities(40, 40, 40).stream().filter(e -> !e.getType().equals(EntityType.PLAYER)).forEach(Entity::remove);
        buildStadium(p, "Crazy_Cranberry", "GoofyCranberry", new Location(p.getWorld(), 22.5, 96, -39.5));
        trackVision(p);
        trackDuel(p);
        Bukkit.getServer().getPluginManager().registerEvents(new TurnManager(), getPlugin());
        Bukkit.getPluginManager().callEvent(new DuelStartEvent(StadiumManager.stadium(p.getWorld())));
    }

    private static void trackDuel(Player p) {
        if (duelActionsManager == null) {
            duelActionsManager = new DuelActionsManager();
        }
        Bukkit.getServer().getPluginManager().registerEvents(duelActionsManager, getPlugin());
    }

    private static void stopDuel(Player p) {
        if (duelActionsManager == null) {
            System.out.println("Not tracking anyway");
        }
        HandlerList.unregisterAll(duelActionsManager);
    }

    private static void trackVision(Player p) {
        if (lookAndHighlightManager == null) {
            lookAndHighlightManager = new LookAndHighlightManager();
        }
        Bukkit.getServer().getPluginManager().registerEvents(lookAndHighlightManager, getPlugin());
    }

    private static void stopTrackingVision(Player p) {
        if (lookAndHighlightManager == null) {
            System.out.println("Not tracking anyway");
        }
        HandlerList.unregisterAll(lookAndHighlightManager);
    }

    private static void deck(Player p) {
        Bukkit.getPluginManager().callEvent(new DeckViewRequestEvent(p));
    }

    private static void buildStadium(Player p, String player1Name, String player2Name, Location startingCorner) {
        Optional<Player> player1 = Bukkit.getOnlinePlayers().stream().filter(onlinePlayer -> onlinePlayer.getName().equals(player1Name)).map(OfflinePlayer::getPlayer).findFirst();
        Optional<Player> player2 = Bukkit.getOnlinePlayers().stream().filter(onlinePlayer -> onlinePlayer.getName().equals(player2Name)).map(OfflinePlayer::getPlayer).findFirst();
        if (player1.isEmpty() || player2.isEmpty()) {
            System.out.println("NO");
            return;
        }
        Bukkit.getPluginManager().callEvent(new BuildStadiumEvent(startingCorner, player1.get(), player2.get()));
    }

    private static void stopShooting(Player p) {
        CraftSkeleton craftSkeleton = (CraftSkeleton) skeleton;
        net.minecraft.world.entity.monster.Skeleton nmsSkeleton = craftSkeleton.getHandle();
        nmsSkeleton.goalSelector.removeGoal(shootingGoal);
    }

    private static void spawnParticle(Player p) {
        Location location = p.getLocation().clone();
        double x = location.getX() + 1;
        double y = location.getY();
        double z = location.getZ();

        p.getWorld().spawnParticle(Particle.FLAME, location.add(x, y, z), 50);
    }

    private static void aioff(Player p) {
        skeleton.setAI(false);
        zombie.setAI(false);
    }

    private static void aion(Player p) {
        skeleton.setAI(true);
        zombie.setAI(true);
    }

    private static void attack(Player p) {
        CraftZombie craftZombie = (CraftZombie) zombie;
        net.minecraft.world.entity.monster.Zombie nmsZombie = craftZombie.getHandle();
        nmsZombie.setTarget(((CraftSkeleton) skeleton).getHandle(), EntityTargetEvent.TargetReason.CUSTOM, true);
        nmsZombie.goalSelector.addGoal(1, new MeleeAttackGoal(nmsZombie, 1, true));
    }

    private static void shoot(Player p) {
        CraftSkeleton craftSkeleton = (CraftSkeleton) skeleton;
        net.minecraft.world.entity.monster.Skeleton nmsSkeleton = craftSkeleton.getHandle();
        nmsSkeleton.setTarget(((CraftZombie) zombie).getHandle(), EntityTargetEvent.TargetReason.CUSTOM, true);
        shootingGoal = new RangedBowAttackGoal<>(nmsSkeleton, 1, 1, 10);
        nmsSkeleton.goalSelector.addGoal(1, shootingGoal);
    }

    private static void spawnAndRemove(Player p) {
        spawn(p);
        removeGoals(p);
    }

    private static void removeGoals(Player p) {
        CraftZombie craftZombie = (CraftZombie) zombie;
        net.minecraft.world.entity.monster.Zombie nmsZombie = craftZombie.getHandle();
        nmsZombie.goalSelector.getRunningGoals().peek(g -> System.out.println("Stopping " + g.getGoal().getClass())).forEach(WrappedGoal::stop);
        nmsZombie.removeAllGoals(g -> true);
        CraftSkeleton craftSkeleton = (CraftSkeleton) skeleton;
        net.minecraft.world.entity.monster.Skeleton nmsSkeleton = craftSkeleton.getHandle();
        nmsSkeleton.goalSelector.getRunningGoals().forEach(WrappedGoal::stop);
        nmsSkeleton.removeAllGoals(g -> true);
    }

    private static void move(Player p, double x, double y, double z) {
        CraftZombie craftZombie = (CraftZombie) zombie;
        net.minecraft.world.entity.monster.Zombie nmsZombie = craftZombie.getHandle();
        nmsZombie.goalSelector.addGoal(1, new WalkToLocationGoal(nmsZombie, new Location(p.getWorld(), x, y, z)));

    }

    private static void walk(Player p) {
        CraftZombie craftZombie = (CraftZombie) zombie;
        net.minecraft.world.entity.monster.Zombie nmsZombie = craftZombie.getHandle();
        nmsZombie.setTarget((LivingEntity) skeleton);
        nmsZombie.goalSelector.addGoal(1, new MoveTowardsTargetGoal(nmsZombie, 1.0, 100));
    }

    private static void spawn(Player p) {
        zombie = (Zombie) p.getWorld().spawnEntity(p.getLocation().add(1, 0, 0), EntityType.ZOMBIE);
        zombie.setAI(false);
        skeleton = (Skeleton) p.getWorld().spawnEntity(p.getLocation().add(5, 0, 0), EntityType.SKELETON);
        skeleton.setAI(false);
    }

    private static void printGoals() {
        if (zombie == null || skeleton == null) {
            return;
        }
        CraftSkeleton craftSkeleton = (CraftSkeleton) skeleton;
        net.minecraft.world.entity.monster.Skeleton nmsSkeleton = craftSkeleton.getHandle();
        System.out.println("\nSkele goals:");
        nmsSkeleton.goalSelector.getAvailableGoals()
                .forEach(g -> System.out.println(String.format("%s %s", g.getGoal().getClass(), g.getGoal().toString())));

        CraftZombie craftZombie = (CraftZombie) zombie;
        net.minecraft.world.entity.monster.Zombie nmsZombie = craftZombie.getHandle();
        System.out.println("\nZombie goals:");
        nmsZombie.goalSelector.getAvailableGoals()
                .forEach(g -> System.out.println(String.format("%s %s", g.getGoal().getClass(), g.getGoal().toString())));
    }
}
