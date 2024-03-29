package me.crazycranberry.minecrafttcg.carddefinitions.minions;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.events.MinionDiedEvent;
import me.crazycranberry.minecrafttcg.events.MinionEnteredEvent;
import me.crazycranberry.minecrafttcg.goals.LookForwardGoal;
import me.crazycranberry.minecrafttcg.goals.ShootParticlesGoal;
import me.crazycranberry.minecrafttcg.goals.ShowTemporaryEffectParticlesGoal;
import me.crazycranberry.minecrafttcg.goals.WalkToLocationGoal;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.TurnPhase;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;

public abstract class Minion {
    private Integer strength;
    private Integer health;
    private Integer maxHealth;
    private Integer attacksPerTurn = 1;
    private Integer attacksLeft = attacksPerTurn;
    private MinionCardDefinition cardDef;
    private final MinionInfo minionInfo;
    private PathfinderMob nmsMob;
    private Integer numTurnsProtected = 0;
    private Integer temporaryBonusStrength = 0;
    private Map<Minion, Integer> staticBonusStrength = new HashMap<>(); // Multiple sources will be trying to change the static strength bonus so we have to record each source
    private Map<Minion, Integer> staticBonusMaxHealth = new HashMap<>(); // Multiple sources will be trying to change the static strength bonus so we have to record each source
    private Boolean hasOverkill = false; // Overkill stuff is handled in the MinionManager.handleOverkillDamage() method
    private Integer numTurnsOverkill = 0;
    private Boolean isFlying = false;
    private Integer numTurnsFlying = 0;
    private Boolean hasLifesteal = false;
    private Integer numTurnsLifesteal = 0;
    private final ListenerForIndividualMinion listener;

    public Minion(Card card, MinionInfo minionInfo) {
        this.cardDef = (MinionCardDefinition) card;
        this.strength = cardDef.strength();
        this.maxHealth = cardDef.maxHealth();
        this.health = cardDef.maxHealth();
        this.minionInfo = minionInfo;
        CraftMob mob = (CraftMob) minionInfo.entity();
        this.nmsMob = (PathfinderMob) mob.getHandle();
        setupGoals();
        listener = new ListenerForIndividualMinion(this);
        Bukkit.getPluginManager().registerEvents(listener, getPlugin());
    }

    public Integer baseStrength() {
        return strength;
    }

    public Integer strength() {
        return strength + temporaryBonusStrength + staticBonusStrength.values().stream().reduce(0, Integer::sum);
    }

    public Integer health() {
        return health;
    }

    public Integer maxHealth() {
        return maxHealth + staticBonusMaxHealth.values().stream().reduce(0, Integer::sum);
    }

    public void setMaxHealth(int newMaxHealth) {
        maxHealth = newMaxHealth;
        minionInfo.stadium().updateCustomName(this);
    }

    public void setHealthNoHealTrigger(int newHealth) {
        health = newHealth;
        minionInfo.stadium().updateCustomName(this);
        shouldIBeDead();
    }

    public MinionCardDefinition cardDef() {
        return cardDef;
    }

    public MinionInfo minionInfo() {
        return minionInfo;
    }

    public void setAttacksPerTurn(Integer attacksPerTurn) {
        this.attacksPerTurn = attacksPerTurn;
        this.attacksLeft = attacksPerTurn;
    }

    public void addPermanentStrength(Integer additionalStrength) {
        this.strength += additionalStrength;
        minionInfo.stadium().updateCustomName(this);
    }

    public void setStrength(Integer newStrength) {
        this.strength = newStrength;
        minionInfo.stadium().updateCustomName(this);
    }

    public void setPermanentOverkill(Boolean giveOverkill) {
        this.hasOverkill = giveOverkill;
    }

    public void setPermanentFlying(Boolean giveFlying) {
        this.isFlying = giveFlying;
        setupGoals();
    }

    public void setPermanentLifesteal(Boolean giveLifesteal) {
        this.hasLifesteal = giveLifesteal;
    }

    public Integer attacksLeft() {
        return attacksLeft;
    }

    public void attacksLeft(Integer attacksLeft) {
        this.attacksLeft = attacksLeft;
    }

    public void onEnter() {
        Bukkit.getPluginManager().callEvent(new MinionEnteredEvent(this));
    }

    public void onDeath() {
        HandlerList.unregisterAll(listener);
        minionInfo.stadium().updateCustomName(this);
        minionInfo.stadium().minionDied(minionInfo.spot());
        minionInfo.entity().setHealth(0);
        Bukkit.getPluginManager().callEvent(new MinionDiedEvent(this));
    }

    public void onTurnStart() {
        attacksLeft = attacksPerTurn;
        numTurnsProtected = Math.max(0, numTurnsProtected - 1);
        numTurnsOverkill = Math.max(0, numTurnsOverkill - 1);
        temporaryBonusStrength = 0;
        minionInfo.stadium().updateCustomName(this);
    }

    public void onCombatStart() {
        if ((!this.cardDef().isRanged() && this.minionInfo().stadium().hasAllyMinionInFront(this.minionInfo().spot())) || this.strength() == 0) {
            attacksLeft = 0;
        }
    }

    public void attackInFront() {
        if (attacksLeft <= 0 || strength() == 0) {
            return;
        }
        LivingEntity target = minionInfo.stadium().getTargetInFront(this);
        nmsMob.setTarget(((CraftLivingEntity) target).getHandle(), EntityTargetEvent.TargetReason.CUSTOM, true);
        if (cardDef().isRanged()) {
            DustOptions dustOptions = new DustOptions(minionInfo().master().equals(minionInfo().stadium().player1()) ? Color.GREEN : Color.ORANGE, 1);
            nmsMob.goalSelector.addGoal(1, new ShootParticlesGoal<>(this, target, Particle.REDSTONE, strength(), dustOptions));
        } else {
            nmsMob.goalSelector.addGoal(1, new MeleeAttackGoal(nmsMob, 1, true));
        }
    }

    public void onCombatEnd() {
        shouldIBeDead();
        setupGoals();
    }

    public void onTurnEnd() {

    }

    // See PackLeader for an example
    public void onAllyMinionEntered(Minion otherMinion) {

    }

    public void onEnemyMinionEntered(Minion otherMinion) {

    }

    /** Can't let target be of type Minion because it might be the Opposing Player. */
    public void onDamageDealt(LivingEntity target, Integer damageDealt, Boolean wasCombatAttack, Boolean wasProtected) {
        if (wasCombatAttack) {
            attacksLeft--;
            if (attacksLeft <= 0) {
                setupGoals();
                minionInfo.stadium().doneAttacking();
            }
        }
        if (hasLifesteal) {
            if (this.minionInfo().stadium().phase().equals(TurnPhase.COMBAT_PHASE)) {
                this.minionInfo().stadium().pendingHealForPlayer(this.minionInfo().master(), damageDealt);
            } else {
                this.minionInfo().master().setHealth(Math.max(Math.min(this.minionInfo().master().getHealth() + damageDealt, this.minionInfo().master().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()), 0));
            }
        }
    }

    /** Can't let source be of type Minion because it might be the Opposing Player. */
    public void onDamageReceived(LivingEntity source, Integer damageReceived, Boolean wasProtected) {
        if (wasProtected) {
            return;
        }
        health = Math.min(health - damageReceived, maxHealth());
        minionInfo.stadium().updateCustomName(this);
        this.minionInfo().entity().damage(0);
        if (!minionInfo.stadium().phase().equals(TurnPhase.COMBAT_PHASE)) { // Minions don't die during combat until they get their hits off
            shouldIBeDead();
        }
    }

    public void onHeal(Integer healFor) {
        if (maxHealth() > health) {
            health = Math.min(maxHealth(), health + healFor);
            minionInfo.entity().getWorld().spawnParticle(Particle.HEART, minionInfo().entity().getEyeLocation(), 7, 0.5, 0.75, 0.5);
            minionInfo.stadium().updateCustomName(this);
            minionInfo.entity().getWorld().playSound(minionInfo.entity(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 2, 1);
        }
    }

    public void unstuckify() {
        if (attacksLeft > 0 && !minionInfo.stadium().isWalled(this) && nmsMob.getTarget() != null && nmsMob.getTarget().isAlive()) {
            minionInfo.entity().teleport(nmsMob.getTarget().getBukkitEntity());
        }
    }

    public void giveTemporaryStrength(Integer bonusStrength) {
        temporaryBonusStrength += bonusStrength;
        minionInfo.entity().getWorld().playSound(minionInfo.entity(), Sound.BLOCK_ANVIL_USE, 1, 1);
        this.minionInfo().stadium().updateCustomName(this);
    }

    public void setStaticStrengthBonus(Minion source, Integer staticStrength) {
        staticBonusStrength.put(source, staticStrength);
        this.minionInfo().stadium().updateCustomName(this);
    }

    public void setStaticMaxHealthBonus(Minion source, Integer staticMaxHealth) {
        Integer currentMaxHealthBonus = staticBonusMaxHealth.get(source);
        int healthChange;
        if (currentMaxHealthBonus != null) {
            healthChange = staticMaxHealth - currentMaxHealthBonus;
        } else {
            healthChange = staticMaxHealth;
        }
        staticBonusMaxHealth.put(source, staticMaxHealth);
        this.setHealthNoHealTrigger(this.health() + healthChange);
        this.minionInfo().stadium().updateCustomName(this);
    }

    public void setProtected(int numTurns) {
        numTurnsProtected = numTurns;
        minionInfo.entity().getWorld().playSound(minionInfo.entity(), Sound.ITEM_SHIELD_BLOCK, 2, 1);
    }

    public void setTemporaryOverkill(int numTurns) {
        numTurnsOverkill = numTurns;
    }

    public Boolean isProtected() {
        return numTurnsProtected > 0;
    }

    public boolean hasBonusStrength() {
        return temporaryBonusStrength > 0;
    }

    public void shouldIBeDead() {
        if (health <= 0) {
            this.onDeath();
        }
    }

    public void setupGoals() {
        removeGoals();
        setGoalOfStayingOnSpot();
        setGoalOfLookingForward();
        setAlterationParticlesGoal();
    }

    private void removeGoals() {
        nmsMob.targetSelector.getRunningGoals().forEach(WrappedGoal::stop);
        nmsMob.goalSelector.getRunningGoals().forEach(WrappedGoal::stop);
        nmsMob.removeAllGoals(g -> true);
    }

    public void setGoalOfStayingOnSpot() {
        Vector flyingModifier = this.cardDef().isFlying() ? new Vector(0, 3, 0) : new Vector(0, 0, 0);
        nmsMob.goalSelector.addGoal(5, new WalkToLocationGoal(nmsMob, minionInfo().stadium().locOfSpot(minionInfo().spot()).add(flyingModifier)));
    }

    private void setGoalOfLookingForward() {
        nmsMob.goalSelector.addGoal(7, new LookForwardGoal(nmsMob, minionInfo().stadium().locOfSpot(Spot.opposingFrontRankSpot(minionInfo().spot()))));
    }

    private void setAlterationParticlesGoal() {
        nmsMob.goalSelector.addGoal(7, new ShowTemporaryEffectParticlesGoal<>(this));
    }

    public Boolean hasOverkill() {
        return numTurnsOverkill > 0 || hasOverkill;
    }

    public Boolean hasFlying() {
        return numTurnsFlying > 0 || isFlying;
    }

    public Boolean hasLifesteal() {
        return numTurnsLifesteal > 0 || hasLifesteal;
    }

    public void loadTemporaryEffects(Minion minion) {
        this.numTurnsLifesteal = minion.numTurnsLifesteal;
        this.numTurnsFlying = minion.numTurnsFlying;
        this.numTurnsOverkill = minion.numTurnsOverkill;
        this.numTurnsProtected = minion.numTurnsProtected;
        this.temporaryBonusStrength = minion.temporaryBonusStrength;
    }

    public PathfinderMob nmsMob() {
        return nmsMob;
    }
}
