package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;

public class Grief implements SpellCardDefinition {
    @Override
    public Integer cost() {
        return 1;
    }

    @Override
    public String cardName() {
        return "Grief";
    }

    @Override
    public String cardDescription() {
        return "Randomly remove 1 card from your opponents hand";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        List<ItemStack> cards = new ArrayList<>();
        var opponent = stadium.opponent(caster);
        for (int i = 0; i < opponent.getInventory().getContents().length; i++) {
            ItemStack item = opponent.getInventory().getContents()[i];
            if (item == null) {
                continue;
            }
            if (Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(IS_CARD_KEY, PersistentDataType.BOOLEAN))) {
                cards.add(item);
            }
        }
        var cardToRemove = randomFromList(cards);
        if (cards.isEmpty() || cardToRemove.isEmpty()) {
            caster.sendMessage(String.format("%sOpponent did not have any cards to remove.%s", ChatColor.GRAY, ChatColor.RESET));
            return;
        }
        opponent.getInventory().remove(cardToRemove.get());
        opponent.getWorld().spawnParticle(Particle.SQUID_INK, opponent.getEyeLocation(), 5, 0.25, 0.5, 0.25);
        opponent.getWorld().playSound(opponent.getLocation(), Sound.ENTITY_CREAKING_ATTACK, 0.8f, 1);
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(false, false, false, false, false);
    }
}
