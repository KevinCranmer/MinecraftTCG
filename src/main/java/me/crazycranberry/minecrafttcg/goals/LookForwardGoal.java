package me.crazycranberry.minecrafttcg.goals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Location;

import java.util.EnumSet;

public class LookForwardGoal extends Goal {
    private final PathfinderMob mob;
    private final double x;
    private final double y;
    private final double z;

    public LookForwardGoal(PathfinderMob mob, Location lookTarget) {
        this.setFlags(EnumSet.of(Flag.LOOK));
        this.mob = mob;
        this.x = lookTarget.getX();
        this.y = lookTarget.getY ();
        this.z = lookTarget.getZ();
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
        this.mob.getLookControl().setLookAt(x, y, z);
    }

    @Override
    public void start() {
        this.mob.getLookControl().setLookAt(x, y, z);
    }
}
