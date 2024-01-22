package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardType;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCard;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import static me.crazycranberry.minecrafttcg.carddefinitions.Card.CARD_NAME_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.IS_CARD_KEY;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.ITALIC;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.event.block.Action.LEFT_CLICK_AIR;
import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;

public class DuelManager implements Listener {
    @EventHandler
    private void onCardCast(PlayerInteractEvent event) {
        Stadium stadium = StadiumManager.stadium(event.getPlayer().getWorld());
        if (isValidCastAttempt(event, stadium)) {
            BookMeta bookMeta = (BookMeta) event.getItem().getItemMeta();
            CardType cardType = CardType.valueOf(bookMeta.getPersistentDataContainer().get(CARD_NAME_KEY, PersistentDataType.STRING));
            Card card = cardType.card();
            // TODO: CHECK MANA COST
            Boolean successfullyCast = false;
            if (card instanceof MinionCard minionCard) {
                successfullyCast = summonMinion(event.getPlayer(), stadium, minionCard);
            }

            if (successfullyCast) {
                // TODO: LOWER MANA
                event.getPlayer().getInventory().remove(event.getItem());
            }
        }
    }

    private Boolean summonMinion(Player caster, Stadium stadium, MinionCard minionCard) {
        if (summonable(caster, stadium)) {
            LivingEntity minion = (LivingEntity) caster.getWorld().spawnEntity(stadium.playerTargetLoc(caster), minionCard.minionType(), false);
            minion.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(minionCard.maxHealth());
            stadium.minionSummoned(caster, minion);
            minionCard.onEnter();
            return true;
        }
        return false;
    }

    private boolean summonable(Player caster, Stadium stadium) {
        if (!stadium.isPlayerTargetingSummonableSpot(caster)) {
            caster.sendMessage(String.format("%s%sYou cannot summon a minion on that spot.%s", GRAY, ITALIC, RESET));
            return false;
        }else if (!stadium.isPlayerTargetingTheirOwnSpots(caster)) {
            caster.sendMessage(String.format("%s%sYou cannot summon a minion on your opponents side of the field.%s", GRAY, ITALIC, RESET));
            return false;
        } else if (!stadium.isPlayersTargetAvailable(caster)) {
            caster.sendMessage(String.format("%s%sYou cannot summon a minion because a minion already exists there.%s", GRAY, ITALIC, RESET));
            return false;
        }
        return true;
    }

    private boolean isValidCastAttempt(PlayerInteractEvent event, Stadium stadium) {
        return EquipmentSlot.HAND.equals(event.getHand()) &&
                event.getItem() != null &&
                event.getItem().getType().equals(Material.WRITTEN_BOOK) &&
                event.getItem().getItemMeta() != null &&
                Boolean.TRUE.equals(event.getItem().getItemMeta().getPersistentDataContainer().get(IS_CARD_KEY, PersistentDataType.BOOLEAN)) &&
                (event.getAction().equals(LEFT_CLICK_BLOCK) || event.getAction().equals(LEFT_CLICK_AIR)) &&
                stadium != null &&
                stadium.isPlayerParticipating(event.getPlayer());
    }
}
