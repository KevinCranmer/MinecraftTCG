package me.crazycranberry.minecrafttcg.carddefinitions.minions.sewerzombie;


import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import org.bukkit.entity.LivingEntity;

public class SewerZombie extends Minion {
    public SewerZombie(MinionInfo minionInfo) {
        super(CardEnum.SEWER_ZOMBIE, minionInfo);
    }

    @Override
    public void onDeath() {

    }

    @Override
    public void onCombatStart() {

    }

    @Override
    public void onDamageDealt(LivingEntity target, Integer damageDealt) {
        super.onDamageDealt(target, damageDealt);
        System.out.println("Sewer zombie just did " + damageDealt + " damage to a " + target.getType());
    }

    @Override
    public void onDamageReceived(LivingEntity source, Integer damageReceived) {
        System.out.println("Sewer zombie just received " + damageReceived + " damage from a " + source.getType());
    }

    @Override
    public void onCombatEnd() {

    }

    @Override
    public void onTurnEnd() {

    }
}
