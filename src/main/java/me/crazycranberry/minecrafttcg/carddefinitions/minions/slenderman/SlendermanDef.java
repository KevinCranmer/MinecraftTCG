package me.crazycranberry.minecrafttcg.carddefinitions.minions.slenderman;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class SlendermanDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Slenderman";
    }

    @Override
    public String cardDescription() {
        return String.format("%sPacifist%s. At the start of each turn, this minion attacks an enemy minion - prioritizing minions in front of it, otherwise a random minion.", ChatColor.BOLD, ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public Integer strength() {
        return 3;
    }

    @Override
    public Integer maxHealth() {
        return 2;
    }

    @Override
    public EntityType minionType() {
        return EntityType.CREAKING;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Slenderman.class;
    }
}
