package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class Fatigue implements CantripCardDefinition {
    private final int MINUS_STRENGTH = 3;
    private final int MINUS_HEALTH = 3;

    @Override
    public Integer cost() {
        return 6;
    }

    @Override
    public String cardName() {
        return "Fatigue";
    }

    @Override
    public String cardDescription() {
        return String.format("Give all enemy minions -%s/-%s until end of turn.", MINUS_STRENGTH, MINUS_HEALTH);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        stadium.enemyMinionSpots(caster).stream()
            .map(stadium::minionFromSpot)
            .filter(Objects::nonNull)
            .forEach(m -> {
                m.giveTemporaryStrength(-MINUS_HEALTH);
                m.giveTemporaryHealth(-MINUS_STRENGTH);
                m.shouldIBeDead();
            });
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, false, false, false, false);
    }

    @Override
    public Boolean canCastDuringCombat() {
        return true;
    }
}
