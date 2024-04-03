package me.crazycranberry.minecrafttcg.carddefinitions.minions.reaper;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class ReaperDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 5;
    }

    @Override
    public String cardName() {
        return "Reaper";
    }

    @Override
    public String cardDescription() {
        return "When Reaper contributes to killing a minion, you draw a card.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public Integer strength() {
        return 4;
    }

    @Override
    public Integer maxHealth() {
        return 4;
    }

    @Override
    public EntityType minionType() {
        return EntityType.WITHER_SKELETON;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Reaper.class;
    }
}
