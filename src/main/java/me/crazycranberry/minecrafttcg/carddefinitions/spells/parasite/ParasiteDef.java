package me.crazycranberry.minecrafttcg.carddefinitions.spells.parasite;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.SUMMON_PARASITE;

public class ParasiteDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return SUMMON_PARASITE.card().cost();
    }

    @Override
    public String cardName() {
        return "Parasite";
    }

    @Override
    public String cardDescription() {
        return SUMMON_PARASITE.card().cardDescription();
    }

    @Override
    public CardRarity rarity() {
        return SUMMON_PARASITE.card().rarity();
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
        return EntityType.SILVERFISH;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Parasite.class;
    }
}
