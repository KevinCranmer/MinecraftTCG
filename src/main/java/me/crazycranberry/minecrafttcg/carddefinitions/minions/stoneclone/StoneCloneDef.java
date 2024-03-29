package me.crazycranberry.minecrafttcg.carddefinitions.minions.stoneclone;

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

public class StoneCloneDef implements MinionCardDefinition, MultiTargetCard {

    Minion target = null;

    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Stone Clone";
    }

    @Override
    public String cardDescription() {
        return "When casted can target any minion and copy it's health and power";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public List<TargetRules> targetRulesForExtraTargets() {
        return List.of(new TargetRules(true, true, false, false,false));
    }

    @Override
    public boolean isValidAdditionalTarget(Player p, Stadium stadium, Card card, List<Spot> targets, Spot newTarget) {
        return true;
    }

    @Override
    public Integer strength() {
        return 0;
    }

    @Override
    public Integer maxHealth() {
        return 0;
    }

    @Override
    public EntityType minionType() {
        return EntityType.HUSK;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return StoneClone.class;
    }

    @Override
    public String signDescription() {
        return "Player targets\n minion and will\n copy target\n card";
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets, Map<EquipmentSlot, ItemStack> equipment) {
        if (!targets.isEmpty()) {
            Spot targetSpot = targets.get(0);
            if (targetSpot != null) {
                Minion targetMinion = targetSpot.minionRef().apply(stadium);
                if (targetMinion != null) {
                    MinionCardDefinition.summonMinion(targets.get(0), stadium, caster, minionClass(), minionType(), null, entityAdjustment());
                }
            }
        }
    }
}
