package me.crazycranberry.minecrafttcg.carddefinitions.minions;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.goals.LookForwardGoal;
import me.crazycranberry.minecrafttcg.goals.WalkToLocationGoal;
import me.crazycranberry.minecrafttcg.model.Spot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftMob;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftSkeleton;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetEvent;

public abstract class Minion {
    private Integer strength;
    private Integer health;
    private Integer maxHealth;
    private Integer attacksLeft = 1;
    private final MinionCardDefinition cardDef;
    private final MinionInfo minionInfo;
    private final PathfinderMob nmsMob;

    public Minion(CardEnum cardEnum, MinionInfo minionInfo) {
        this.cardDef = (MinionCardDefinition) cardEnum.card();
        this.strength = cardDef.strength();
        this.maxHealth = cardDef.maxHealth();
        this.health = cardDef.maxHealth();
        this.minionInfo = minionInfo;
        CraftMob mob = (CraftMob) minionInfo.entity();
        this.nmsMob = (PathfinderMob) mob.getHandle();
    }

    public Integer strength() {
        return strength;
    }

    public Integer health() {
        return health;
    }

    public Integer maxHealth() {
        return maxHealth;
    }

    public MinionCardDefinition cardDef() {
        return cardDef;
    }

    public MinionInfo minionInfo() {
        return minionInfo;
    }

    public Integer attacksLeft() {
        return attacksLeft;
    }

    public void onEnter() {
        removeGoals();
        setGoalOfStayingOnSpot();
        setGoalOfLookingForward();
    }

    public abstract void onDeath();

    public void onTurnStart() {
        attacksLeft = 1;
    }

    public abstract void onCombatStart();

    /** Can't let target be of type Minion because it might be the Opposing Player. */
    public void onDamageDealt(LivingEntity target, Integer damageDealt) {
        attacksLeft--;
        if (attacksLeft <= 0) {
            nmsMob.goalSelector.getRunningGoals().filter(g -> g.getGoal() instanceof MeleeAttackGoal).forEach(g -> System.out.println("Removing " + g.getGoal() + " goal"));
            nmsMob.goalSelector.getRunningGoals().filter(g -> g.getGoal() instanceof MeleeAttackGoal).forEach(WrappedGoal::stop);
            nmsMob.removeAllGoals(g -> g instanceof MeleeAttackGoal);
            minionInfo.stadium().doneAttacking();
        }
    }

    /** Can't let source be of type Minion because it might be the Opposing Player. */
    public abstract void onDamageReceived(LivingEntity source, Integer damageReceived);

    public abstract void onCombatEnd();

    public abstract void onTurnEnd();

    public void attackInFront() {
        System.out.println("Making dudes attack");
        LivingEntity target = minionInfo.stadium().getTargetInFront(this);
        nmsMob.setTarget(((CraftLivingEntity) target).getHandle(), EntityTargetEvent.TargetReason.CUSTOM, true);
        System.out.println("Targeting " + target.getType());
        nmsMob.goalSelector.addGoal(1, new MeleeAttackGoal(nmsMob, 1, true));
    }

    private void removeGoals() {
        nmsMob.goalSelector.getRunningGoals().forEach(WrappedGoal::stop);
        nmsMob.removeAllGoals(g -> true);
    }

    private void setGoalOfStayingOnSpot() {
        nmsMob.goalSelector.addGoal(5, new WalkToLocationGoal(nmsMob, minionInfo().stadium().playerTargetLoc(minionInfo().master())));
    }

    private void setGoalOfLookingForward() {
        nmsMob.goalSelector.addGoal(7, new LookForwardGoal(nmsMob, minionInfo().stadium().locOfSpot(Spot.opposingFrontRankSpot(minionInfo().spot()))));
    }
}
