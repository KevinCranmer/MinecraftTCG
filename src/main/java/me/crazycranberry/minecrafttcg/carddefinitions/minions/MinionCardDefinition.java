package me.crazycranberry.minecrafttcg.carddefinitions.minions;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import org.bukkit.entity.EntityType;

public interface MinionCardDefinition extends Card {
    public Integer strength();
    public Integer maxHealth();
    public EntityType minionType();
    public Class<? extends Minion> minionClass();
}
