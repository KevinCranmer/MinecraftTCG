package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.UNDEAD_TYPES;

public class WithersBlessing implements CantripCardDefinition {
    private static final int STRENGTH_LOSS = 2;
    private static final int HEALTH_LOSS = 2;

    @Override
    public Integer cost() {
        return 2;
    }

    @Override
    public String cardName() {
        return "Withers Blessing";
    }

    @Override
    public String cardDescription() {
        return String.format("Give targeted minion -%s/-%s. If targeted minion is NOT %sUndead%s, give it -%s/-%s instead", STRENGTH_LOSS, HEALTH_LOSS, ChatColor.BOLD, ChatColor.RESET, (STRENGTH_LOSS+1), (HEALTH_LOSS+1));
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
        int strengthLoss = STRENGTH_LOSS;
        int healthLoss = HEALTH_LOSS;
        if (!UNDEAD_TYPES.contains(targetedMinion.minionInfo().entity().getType())) {
            strengthLoss++;
            healthLoss++;
        }
        targetedMinion.setStrength(targetedMinion.strength() - strengthLoss);
        targetedMinion.setMaxHealth(targetedMinion.maxHealth() - healthLoss);
        targetedMinion.setHealthNoHealTrigger(targetedMinion.health() - healthLoss);
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
