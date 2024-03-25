package me.crazycranberry.minecrafttcg.carddefinitions.minions.emovitro;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;

public class EmoVitroDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 2;
    }

    @Override
    public String cardName() {
        return "Emo Vitro";
    }

    @Override
    public String cardDescription() {
        return "Gets +2 strength and +2 max health when there are 3 other ally minions.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public Integer strength() {
        return 2;
    }

    @Override
    public Integer maxHealth() {
        return 2;
    }

    @Override
    public EntityType minionType() {
        return EntityType.VILLAGER;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return EmoVitro.class;
    }

    @Override
    public String signDescription() {
        return "Gets +2 str\nand +2 health\nwhen it has\n3 allies";
    }
}
