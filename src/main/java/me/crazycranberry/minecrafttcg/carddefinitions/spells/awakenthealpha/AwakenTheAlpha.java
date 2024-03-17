package me.crazycranberry.minecrafttcg.carddefinitions.spells.awakenthealpha;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.ParticleBeamInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.ParticleBeamTracker;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.FlingSmallPoops;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.SpellCardDefinition;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

public class AwakenTheAlpha implements SpellCardDefinition {
    public static final int COST = 3;
    public static final int STRENGTH = 2;
    public static final int HEALTH = 2;
    public static final CardRarity RARITY = CardRarity.RARE;

    @Override
    public Integer cost() {
        return COST;
    }

    @Override
    public String cardName() {
        return "Awaken The Alpha";
    }

    @Override
    public String cardDescription() {
        return String.format("Sacrifice all ally minions. Then summon a %s/%s \"The Alpha\" minion with %sOverkill%s that gains the strength and health of all sacrificed minions.", STRENGTH, HEALTH, ChatColor.BOLD, ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return RARITY;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        int totalStr = 0;
        int totalHealth = 0;
        for (Spot spot : stadium.allyMinionSpots(caster)) {
            Minion allyMinion = spot.minionRef().apply(stadium);
            if (allyMinion != null) {
                totalStr += allyMinion.strength();
                totalHealth += allyMinion.health();
                allyMinion.onDeath();
            }
        }
        TheAlphaDef alphaDef = new TheAlphaDef();
        Minion theAlpha = MinionCardDefinition.summonMinion(targets.get(0), stadium, caster, TheAlpha.class, alphaDef.minionType(), null, alphaDef.entityAdjustment());
        theAlpha.addPermanentStrength(totalStr);
        theAlpha.setMaxHealth(theAlpha.maxHealth() + totalHealth);
        theAlpha.setHealthNoHealTrigger(alphaDef.maxHealth());
        theAlpha.setPermanentOverkill(true);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, false, false, true, false);
    }
}
