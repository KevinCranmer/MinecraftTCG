package me.crazycranberry.minecrafttcg.carddefinitions.minions.fluffynecromancer;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.MultiTargetCard;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.DINGY_SKELETON;

public class FluffyNecromancerDef implements MinionCardDefinition, MultiTargetCard {
    @Override
    public Integer cost() {
        return 4;
    }

    @Override
    public String cardName() {
        return "Fluffy Necromancer";
    }

    @Override
    public String cardDescription() {
        return String.format("Summons a %s%s%s at target Spot.", DINGY_SKELETON.card().rarity().color(), DINGY_SKELETON.card().cardName(), ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
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
        return EntityType.LLAMA;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return FluffyNecromancer.class;
    }

    @Override
    public String signDescription() {
        return "";
    }

    @Override
    public List<TargetRules> targetRulesForExtraTargets() {
        return List.of(new TargetRules(false, false, false, true, false));
    }

    @Override
    public boolean isValidAdditionalTarget(Player p, Stadium stadium, Card card, List<Spot> targets, Spot newTarget) {
        return !targets.get(0).equals(newTarget);
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets, Map<EquipmentSlot, ItemStack> equipment) {
        MinionCardDefinition.summonMinion(targets.get(0), stadium, caster, minionClass(), minionType(), null);
        MinionCardDefinition.summonMinion(targets.get(1), stadium, caster, ((MinionCardDefinition)DINGY_SKELETON.card()).minionClass(), ((MinionCardDefinition)DINGY_SKELETON.card()).minionType(), null);
    }
}
