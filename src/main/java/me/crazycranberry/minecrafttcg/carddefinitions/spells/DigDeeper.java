package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;

public class DigDeeper implements SpellCardDefinition {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Dig Deeper";
    }

    @Override
    public String cardDescription() {
        return "Draw 2 cards";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster) {
        stadium.draw(caster);
        stadium.draw(caster);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, false, false, false);
    }
}
