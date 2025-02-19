package me.crazycranberry.minecrafttcg.carddefinitions.spells.parasite;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.SpellCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.awakenthealpha.TheAlpha;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;

import java.util.List;

import static me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition.summonMinion;

public class SummonParasite implements SpellCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Parasite";
    }

    @Override
    public String cardDescription() {
        return "Deals 1 damage to controller whenever an ally minion enters or dies";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        summonMinion(targets.get(0), stadium, stadium.opponent(caster), Parasite.class, new ParasiteDef());
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, false, false, false, true);
    }
}
