package me.crazycranberry.minecrafttcg.carddefinitions.minions.brutalsniper;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.MultiTargetCard;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.DINGY_SKELETON;

public class BrutalSniperDef implements MinionCardDefinition, MultiTargetCard {
    @Override
    public Integer cost() {
        return 6;
    }

    @Override
    public String cardName() {
        return "Brutal Sniper";
    }

    @Override
    public String cardDescription() {
        return "When this minion enters, destroy target minion.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public List<TargetRules> targetRulesForExtraTargets() {
        return List.of(new TargetRules(false, true, false, false, true));
    }

    @Override
    public boolean isValidAdditionalTarget(Player p, Stadium stadium, Card card, List<Spot> targets, Spot newTarget) {
        return true;
    }

    @Override
    public Integer strength() {
        return 2;
    }

    @Override
    public Integer maxHealth() {
        return 2;
    }

    @Override
    public EntityType minionType() {
        return EntityType.SKELETON;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return BrutalSniper.class;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets, Map<EquipmentSlot, ItemStack> equipment) {
        MinionCardDefinition.summonMinion(targets.get(0), stadium, caster, minionClass(), this);
        Optional.ofNullable(stadium.minionFromSpot(targets.get(1))).ifPresent(Minion::onDeath);
    }
}
