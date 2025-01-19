package me.crazycranberry.minecrafttcg.carddefinitions.minions.cultist;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class CultistDef implements MinionCardDefinition {
    public static int CULTIST_SUMMONING_STRENGTH = 6;
    public static int CULTIST_SUMMONING_HEALTH = 7;

    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Cultist";
    }

    @Override
    public String cardDescription() {
        return String.format("After combat, two Cultist's on opposite sides in the same row will summon a %s/%s minion in the middle.", CULTIST_SUMMONING_STRENGTH, CULTIST_SUMMONING_HEALTH);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
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
        return EntityType.VILLAGER;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Cultist.class;
    }
}
