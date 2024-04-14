package me.crazycranberry.minecrafttcg.carddefinitions.minions.glassjaw;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class GlassJawDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Glass Jaw";
    }

    @Override
    public String cardDescription() {
        return String.format("%sOverkill, Rush%s. (Make sure this minions health is buffed before it enters)", ChatColor.BOLD, ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public Integer strength() {
        return 7;
    }

    @Override
    public Integer maxHealth() {
        return 0;
    }

    @Override
    public EntityType minionType() {
        return EntityType.RAVAGER;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return GlassJaw.class;
    }

    @Override
    public boolean hasRush() {
        return true;
    }
}
