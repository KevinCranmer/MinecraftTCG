package me.crazycranberry.minecrafttcg.carddefinitions.minions.skiturtle;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class SkiTurtleDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Ski Turtle";
    }

    @Override
    public String cardDescription() {
        return "At the end of combat, fully heal the ally minion in front of this one.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public Integer strength() {
        return 1;
    }

    @Override
    public Integer maxHealth() {
        return 3;
    }

    @Override
    public EntityType minionType() {
        return EntityType.TURTLE;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return SkiTurtle.class;
    }

    @Override
    public boolean isRanged() {
        return true;
    }
}
