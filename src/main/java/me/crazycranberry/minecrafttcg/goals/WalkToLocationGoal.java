package me.crazycranberry.minecrafttcg.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;

public class WalkToLocationGoal extends Goal {
    protected final PathfinderMob mob;
    protected final double x;
    protected final double y;
    protected final double z;

    public WalkToLocationGoal(PathfinderMob mob, double x, double y, double z) {
        this.mob = mob;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    public void start() {
        Path path = this.mob.getNavigation().createPath(BlockPos.containing(x, y, z), 0);
        this.mob.getNavigation().moveTo(path, 1);
    }
}
