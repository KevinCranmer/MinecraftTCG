package me.crazycranberry.minecrafttcg.carddefinitions.minions.bulldozer;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class BullDozerDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 7;
    }

    @Override
    public String cardName() {
        return "Bulldozer";
    }

    @Override
    public String cardDescription() {
        return String.format("%sOverkill%s", ChatColor.BOLD, ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public Integer strength() {
        return 6;
    }

    @Override
    public Integer maxHealth() {
        return 9;
    }

    @Override
    public EntityType minionType() {
        return EntityType.RAVAGER;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return BullDozer.class;
    }

    @Override
    public String signDescription() {
        return "Overkill";
    }
}
