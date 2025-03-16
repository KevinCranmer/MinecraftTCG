package me.crazycranberry.minecrafttcg.carddefinitions.minions.pumpernickel;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class PumpernickelDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Pumpernickel";
    }

    @Override
    public String cardDescription() {
        return String.format("%sBlock 3%s", ChatColor.BOLD, ChatColor.RESET);
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
        return 1;
    }

    @Override
    public Integer block() {
        return 3;
    }

    @Override
    public EntityType minionType() {
        return EntityType.ARMADILLO;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Pumpernickel.class;
    }
}
