package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

import static org.bukkit.Sound.ENTITY_WANDERING_TRADER_DRINK_POTION;

public class FreshWaterBottle implements SpellCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Fresh Water Bottle";
    }

    @Override
    public String cardDescription() {
        return "Double target minions max health, then fully heal them.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        Minion minion = stadium.minionFromSpot(targets.get(0));
        if (minion != null) {
            minion.setMaxHealth(minion.maxHealth() * 2);
            minion.onHeal(minion.maxHealth());
            LivingEntity entity = minion.minionInfo().entity();
            entity.getWorld().playSound(entity.getEyeLocation(), ENTITY_WANDERING_TRADER_DRINK_POTION, 1, 1);
        }
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, false, false, false);
    }
}
