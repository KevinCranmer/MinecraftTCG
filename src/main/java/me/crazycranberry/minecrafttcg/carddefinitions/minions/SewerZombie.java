package me.crazycranberry.minecrafttcg.carddefinitions.minions;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class SewerZombie implements MinionCard {
    @Override
    public Integer cost() {
        return 1;
    }

    @Override
    public String cardName() {
        return "Sewer Zombie";
    }

    @Override
    public String cardDescription() {
        return "";
    }

    @Override
    public Integer strength() {
        return 1;
    }

    @Override
    public Integer maxHealth() {
        return 1;
    }

    @Override
    public EntityType minionType() {
        return EntityType.ZOMBIE;
    }

    @Override
    public void onEnter() {
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
    public void onDamageDealt(Entity target, Integer damageDealt) {
    }

    @Override
    public void onDamageReceived(Entity source, Integer damageReceived) {
    }

    @Override
    public void onCombatEnd() {
    }

    @Override
    public void onTurnEnd() {
    }
}
