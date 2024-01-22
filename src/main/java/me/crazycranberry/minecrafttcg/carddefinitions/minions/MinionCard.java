package me.crazycranberry.minecrafttcg.carddefinitions.minions;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public interface MinionCard extends Card {
    public Integer strength();
    public Integer maxHealth();
    public EntityType minionType();
    public void onEnter();
    public void onDeath();
    public void onTurnStart();
    public void onCombatStart();
    public void onDamageDealt(Entity target, Integer damageDealt);
    public void onDamageReceived(Entity source, Integer damageReceived);
    public void onCombatEnd();
    public void onTurnEnd();
}
