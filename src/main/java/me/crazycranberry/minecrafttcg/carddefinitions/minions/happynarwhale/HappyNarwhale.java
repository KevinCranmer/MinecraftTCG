package me.crazycranberry.minecrafttcg.carddefinitions.minions.happynarwhale;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.goals.DolphinAttackTargetGoal;
import me.crazycranberry.minecrafttcg.goals.ShootParticlesGoal;
import me.crazycranberry.minecrafttcg.goals.WalkToLocationGoal;
import net.minecraft.world.entity.animal.Dolphin;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.Vector;

import java.util.List;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.model.Spot.BLUE_1_BACK;
import static me.crazycranberry.minecrafttcg.model.Spot.BLUE_2_BACK;
import static me.crazycranberry.minecrafttcg.model.Spot.GREEN_1_BACK;
import static me.crazycranberry.minecrafttcg.model.Spot.GREEN_2_BACK;
import static me.crazycranberry.minecrafttcg.model.Spot.RED_1_BACK;
import static me.crazycranberry.minecrafttcg.model.Spot.RED_2_BACK;

public class HappyNarwhale extends Minion {
    private int taskId;
    private Block waterBlock;
    private final double dolphinRestingY;

    public HappyNarwhale(MinionInfo minionInfo) {
        super(CardEnum.HAPPY_NARWHALE.card(), minionInfo);
        this.minionInfo().entity().teleport(this.minionInfo().entity().getLocation().add(0, 1, 0));
        dolphinRestingY = this.minionInfo().stadium().locOfSpot(RED_1_BACK).getY();
        Location loc = this.minionInfo().entity().getLocation().clone();
        loc.setY(dolphinRestingY);
        waterBlock = loc.getBlock();
        set5x3(waterBlock, Material.WATER);
        startWaterTracker();
    }

    @Override
    public void setGoalOfStayingOnSpot() {
        Vector positionVector = new Vector(0, 1, 0);
        if (List.of(GREEN_1_BACK, GREEN_2_BACK, BLUE_1_BACK, BLUE_2_BACK, RED_1_BACK, RED_2_BACK).contains(this.minionInfo().spot())) {
            positionVector = new Vector(0, 0, 0);
        }
        Vector offset = this.hasFlying() ? new Vector(0, 3, 0) : positionVector;
        nmsMob().goalSelector.addGoal(5, new WalkToLocationGoal(nmsMob(), minionInfo().stadium().locOfSpot(minionInfo().spot()).add(offset)));
    }

    @Override
    public void attackInFront() {
        if (attacksLeft() <= 0) {
            return;
        }
        LivingEntity target = minionInfo().stadium().getTargetInFront(this, true);
        nmsMob().setTarget(((CraftLivingEntity) target).getHandle(), EntityTargetEvent.TargetReason.CUSTOM, true);
        if (hasRanged()) {
            Particle.DustOptions dustOptions = new Particle.DustOptions(minionInfo().master().equals(minionInfo().stadium().player1()) ? Color.GREEN : Color.ORANGE, 1);
            nmsMob().goalSelector.addGoal(1, new ShootParticlesGoal<>(this, target, Particle.REDSTONE, strength(), dustOptions));
        } else {
            nmsMob().goalSelector.addGoal(0, new DolphinAttackTargetGoal((Dolphin) nmsMob(), nmsMob().getTarget(), this.minionInfo().stadium().minionFromEntity(target).map(Minion::hasFlying).orElse(false)));
        }
    }

    private void startWaterTracker() {
        taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            if (this.minionInfo().entity().isDead()) {
                if (waterBlock.getType().equals(Material.WATER)) {
                    set5x3(waterBlock, Material.AIR);
                }
                Bukkit.getScheduler().cancelTask(taskId);
                return;
            }
            Location currentLoc = this.minionInfo().entity().getLocation().clone();
            currentLoc.setY(dolphinRestingY);
            Block currentBlock = currentLoc.getBlock();
            if (!currentBlock.equals(waterBlock)) {
                set5x3(waterBlock, Material.AIR);
                set5x3(currentBlock, Material.WATER);
                waterBlock = currentBlock;
            }
        }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
    }

    private void set5x3(Block block, Material mat) {
        for (int y = -1; y <= 0; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -1; z <= 1; z++) {
                    setBlock(block.getRelative(x, y, z), mat);
                }
            }
        }
    }

    private void setBlock(Block b, Material mat) {
        if (b.getType().equals(Material.WATER) || b.getType().equals(Material.AIR)) {
            b.setType(mat);
        }
    }
}
