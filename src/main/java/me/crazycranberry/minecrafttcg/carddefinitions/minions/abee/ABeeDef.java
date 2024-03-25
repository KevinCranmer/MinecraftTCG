package me.crazycranberry.minecrafttcg.carddefinitions.minions.abee;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class ABeeDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "A Bee";
    }

    @Override
    public String cardDescription() {
        return String.format("%sLifesteal%s", ChatColor.BOLD, ChatColor.RESET);
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
        return EntityType.BEE;
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return ABee.class;
    }

    @Override
    public String signDescription() {
        return "Lifesteal";
    }
}
