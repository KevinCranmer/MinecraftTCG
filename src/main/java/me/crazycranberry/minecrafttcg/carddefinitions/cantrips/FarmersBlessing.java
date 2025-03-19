package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.ANIMAL_TYPES;

public class FarmersBlessing implements CantripCardDefinition {
    private static final int BONUS_STRENGTH = 2;
    private static final int BONUS_HEALTH = 2;

    @Override
    public Integer cost() {
        return 2;
    }

    @Override
    public String cardName() {
        return "Farmers Blessing";
    }

    @Override
    public String cardDescription() {
        return String.format("Give targeted minion +%s/+%s. If targeted minion is an %sAnimal%s, give it +%s/+%s instead", BONUS_STRENGTH, BONUS_HEALTH, ChatColor.BOLD, ChatColor.RESET, (BONUS_STRENGTH+1), (BONUS_HEALTH+1));
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        Minion targetedMinion = stadium.minionFromSpot(targets.get(0));
        if (targetedMinion == null) {
            return;
        }
        int bonusStrength = BONUS_STRENGTH;
        int bonusHealth = BONUS_HEALTH;
        if (ANIMAL_TYPES.contains(targetedMinion.minionInfo().entity().getType())) {
            bonusStrength++;
            bonusHealth++;
        }
        targetedMinion.addPermanentStrength(bonusStrength);
        targetedMinion.addPermanentMaxHealth(bonusHealth);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, false, false, false);
    }

    @Override
    public Boolean canCastDuringCombat() {
        return true;
    }
}
