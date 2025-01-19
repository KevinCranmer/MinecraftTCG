package me.crazycranberry.minecrafttcg.carddefinitions.minions.summoning;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

import static me.crazycranberry.minecrafttcg.carddefinitions.minions.cultist.CultistDef.CULTIST_SUMMONING_HEALTH;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.cultist.CultistDef.CULTIST_SUMMONING_STRENGTH;

public class SummoningDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 6;
    }

    @Override
    public String cardName() {
        return "Summoning";
    }

    @Override
    public String cardDescription() {
        return "";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
    }

    @Override
    public Integer strength() {
        return CULTIST_SUMMONING_STRENGTH;
    }

    @Override
    public Integer maxHealth() {
        return CULTIST_SUMMONING_HEALTH;
    }

    @Override
    public EntityType minionType() {
        return EntityType.ZOMBIE_HORSE;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Summoning.class;
    }
}
