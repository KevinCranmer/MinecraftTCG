package me.crazycranberry.minecrafttcg.carddefinitions.minions.sewerzombie;


import me.crazycranberry.minecrafttcg.carddefinitions.CardType;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import org.bukkit.entity.LivingEntity;

public class SewerZombie extends Minion {
    public SewerZombie(MinionInfo minionInfo) {
        super(CardType.SEWER_ZOMBIE, minionInfo);
    }

    @Override
    public void onEnter() {
        super.onEnter();
    }

    @Override
    public void onDeath() {

    }

    @Override
    public void onTurnStart() {

    }

    @Override
    public void onCombatStart() {

    }

    @Override
    public void onDamageDealt(LivingEntity target, Integer damageDealt) {

    }

    @Override
    public void onDamageReceived(LivingEntity source, Integer damageReceived) {

    }

    @Override
    public void onCombatEnd() {

    }

    @Override
    public void onTurnEnd() {

    }
}
