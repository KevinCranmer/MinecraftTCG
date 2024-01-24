package me.crazycranberry.minecrafttcg.carddefinitions.minions;

import me.crazycranberry.minecrafttcg.carddefinitions.CardType;
import me.crazycranberry.minecrafttcg.goals.LookForwardGoal;
import me.crazycranberry.minecrafttcg.goals.WalkToLocationGoal;
import me.crazycranberry.minecrafttcg.model.Spot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftMob;
import org.bukkit.entity.LivingEntity;

public abstract class Minion {
    private Integer strength;
    private Integer health;
    private Integer maxHealth;
    private final MinionCardDefinition cardDef;
    private final MinionInfo minionInfo;
    private final PathfinderMob nmsMob;

    public Minion(CardType cardType, MinionInfo minionInfo) {
        this.cardDef = (MinionCardDefinition) cardType.card();
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

    public void onEnter() {
        System.out.println("Yeah parent onEnter()");
        removeGoals();
        setGoalOfStayingOnSpot();
        setGoalOfLookingForward();
    }

    public abstract void onDeath();

    public abstract void onTurnStart();

    public abstract void onCombatStart();

    /** Can't let target be of type Minion because it might be the Opposing Player. */
    public abstract void onDamageDealt(LivingEntity target, Integer damageDealt);

    /** Can't let source be of type Minion because it might be the Opposing Player. */
    public abstract void onDamageReceived(LivingEntity source, Integer damageReceived);

    public abstract void onCombatEnd();

    public abstract void onTurnEnd();

    private void removeGoals() {
        nmsMob.goalSelector.getRunningGoals().peek(g -> System.out.println("Stopping " + g.getGoal().getClass())).forEach(WrappedGoal::stop);
        nmsMob.removeAllGoals(g -> true);
    }

    private void setGoalOfStayingOnSpot() {
        System.out.println("Setting goal to stay on the spot");
        nmsMob.goalSelector.addGoal(5, new WalkToLocationGoal(nmsMob, minionInfo().stadium().playerTargetLoc(minionInfo().master())));
    }

    private void setGoalOfLookingForward() {
        System.out.println("Setting goal to look forward");
        nmsMob.goalSelector.addGoal(7, new LookForwardGoal(nmsMob, minionInfo().stadium().locOfSpot(Spot.opposingFrontRankSpot(minionInfo().spot()))));
    }
}
