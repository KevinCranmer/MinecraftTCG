package me.crazycranberry.minecrafttcg.carddefinitions.minions.supportivezombie;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.MultiTargetCard;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.armor;

public class SupportiveZombieDef implements MinionCardDefinition, MultiTargetCard {
    public static final int BONUS_STRENGTH = 2;

    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Support Zombie";
    }

    @Override
    public String cardDescription() {
        return String.format("When this minion enters, give another target minion +%s strength", BONUS_STRENGTH);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public Integer strength() {
        return 4;
    }

    @Override
    public Integer maxHealth() {
        return 4;
    }

    @Override
    public EntityType minionType() {
        return EntityType.ZOMBIE;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return SupportiveZombie.class;
    }

    @Override
    public List<TargetRules> targetRulesForExtraTargets() {
        return List.of(new TargetRules(true, false, false, true, false));
    }

    @Override
    public boolean isValidAdditionalTarget(Player p, Stadium stadium, Card card, List<Spot> targets, Spot newTarget) {
        return true;
    }

    @Override
    public Map<EquipmentSlot, ItemStack> equipment() {
        return Map.ofEntries(
            armor(EquipmentSlot.HEAD, Material.GOLD_INGOT, null, null),
            armor(EquipmentSlot.LEGS, Material.LEATHER, null, null)
        );
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        MinionCardDefinition.super.onCast(stadium, caster, targets);
        if (targets.size() > 1) {
            Minion minion = stadium.minionFromSpot(targets.get(1));
            if (minion != null) {
                minion.giveTemporaryStrength(BONUS_STRENGTH);
            }
        }
    }
}
