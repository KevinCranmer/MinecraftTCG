package me.crazycranberry.minecrafttcg.carddefinitions.spells.awakenthealpha;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

import static me.crazycranberry.minecrafttcg.carddefinitions.spells.awakenthealpha.AwakenTheAlpha.COST;
import static me.crazycranberry.minecrafttcg.carddefinitions.spells.awakenthealpha.AwakenTheAlpha.HEALTH;
import static me.crazycranberry.minecrafttcg.carddefinitions.spells.awakenthealpha.AwakenTheAlpha.RARITY;
import static me.crazycranberry.minecrafttcg.carddefinitions.spells.awakenthealpha.AwakenTheAlpha.STRENGTH;

public class TheAlphaDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return COST;
    }

    @Override
    public String cardName() {
        return "The Alpha";
    }

    @Override
    public String cardDescription() {
        return "Gobbles up all ally minions when entering and adds their stats to his own";
    }

    @Override
    public CardRarity rarity() {
        return RARITY;
    }

    @Override
    public Integer strength() {
        return STRENGTH;
    }

    @Override
    public Integer maxHealth() {
        return HEALTH;
    }

    @Override
    public EntityType minionType() {
        return EntityType.RAVAGER;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return TheAlpha.class;
    }
}
