package me.crazycranberry.minecrafttcg.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import org.bukkit.Location;

import java.util.EnumSet;

public class WalkToLocationGoal extends Goal {
    private final PathfinderMob mob;
    private final double x;
    private final double y;
    private final double z;

    public WalkToLocationGoal(PathfinderMob mob, Location location) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.mob = mob;
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        return mob.isAlive();
    }

    @Override
    public void tick() {
        Path path = this.mob.getNavigation().createPath(BlockPos.containing(x, y, z), 0);
        this.mob.getNavigation().moveTo(path, 1);
    }

    @Override
    public void start() {
        Path path = this.mob.getNavigation().createPath(BlockPos.containing(x, y, z), 0);
        this.mob.getNavigation().moveTo(path, 1);
    }
}
