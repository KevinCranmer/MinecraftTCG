package me.crazycranberry.minecrafttcg.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class DolphinAttackTargetGoal extends Goal {
    private int attackCooldown = 0;
    private final Dolphin mob;
    private final LivingEntity target;
    private boolean targetIsFlying = false;
    private boolean jumpStarted = false;
    private boolean breached;

    public DolphinAttackTargetGoal(Dolphin mob, LivingEntity target, boolean isTargetFlying) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.mob = mob;
        this.target = target;
        this.targetIsFlying = isTargetFlying;
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
        if ((mob.distanceTo(target) < 1.2 || mob.blockPosition().below(1).distManhattan(target.blockPosition()) < 1) && attackCooldown == 0) {
            mob.doHurtTarget(target);
            attackCooldown = 20;
        }
        if (targetIsFlying && !jumpStarted && mob.distanceTo(target) < 6.5) {
            jumpStarted = true;
            Direction var0 = this.mob.getMotionDirection();
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add((double)var0.getStepX() * 0.6, 0.7, (double)var0.getStepZ() * 0.6));
            this.mob.getNavigation().stop();
        } else if (jumpStarted) {
            boolean var0 = this.breached;
            if (!var0) {
                FluidState var1 = this.mob.level().getFluidState(this.mob.blockPosition());
                this.breached = var1.is(FluidTags.WATER);
            }

            if (this.breached && !var0) {
                this.mob.playSound(SoundEvents.DOLPHIN_JUMP, 1.0F, 1.0F);
            }

            Vec3 var1 = this.mob.getDeltaMovement();
            if (var1.y * var1.y < 0.029999999329447746 && this.mob.getXRot() != 0.0F) {
                this.mob.setXRot(Mth.rotLerp(0.2F, this.mob.getXRot(), 0.0F));
            } else if (var1.length() > 9.999999747378752E-6) {
                double var2 = var1.horizontalDistance();
                double var4 = Math.atan2(-var1.y, var2) * 57.2957763671875;
                this.mob.setXRot((float)var4);
            }
        } else {
            Path path = this.mob.getNavigation().createPath(target.getX(), target.getY(), target.getZ(), 0);
            this.mob.getNavigation().moveTo(path, 1);
        }
        attackCooldown = Math.max(0, attackCooldown - 1);
    }

    @Override
    public void start() {
        Path path = this.mob.getNavigation().createPath(BlockPos.containing(target.getX(), target.getY(), target.getZ()), 0);
        this.mob.getNavigation().moveTo(path, 1);
    }

    public float distanceTo(Entity target) {
        float f = (float)(this.mob.getX() - target.getX());
        float f1 = (float)(this.mob.getY() - target.getY());
        float f2 = (float)(this.mob.getZ() - target.getZ());
        return f * f + f1 * f1 + f2 * f2;
    }
}
