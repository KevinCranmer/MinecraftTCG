package me.crazycranberry.minecrafttcg.carddefinitions.minions.happynarwhale;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class HappyNarwhaleDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Happy Narwhale";
    }

    @Override
    public String cardDescription() {
        return "This minion can jump and hit flying minions.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public Integer strength() {
        return 4;
    }

    @Override
    public Integer maxHealth() {
        return 3;
    }

    @Override
    public EntityType minionType() {
        return EntityType.DOLPHIN;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return HappyNarwhale.class;
    }

    @Override
    public String signDescription() {
        return "Can hit Flying";
    }
}
