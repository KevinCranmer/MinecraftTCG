package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.managers.StadiumManager;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;

import java.util.List;

public class PlantCrops implements SpellCardDefinition {
    @Override
    public Integer cost() {
        return 2;
    }

    @Override
    public String cardName() {
        return "Plant Crops";
    }

    @Override
    public String cardDescription() {
        return "Gain an empty mana lamp";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        stadium.addMaxMana(caster, 1);
        StadiumManager.updateManaLampsForPlayer(stadium, caster);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, false, false, false, false);
    }
}
