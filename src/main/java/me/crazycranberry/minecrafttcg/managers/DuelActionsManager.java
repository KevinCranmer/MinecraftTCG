package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardType;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.events.CombatStartEvent;
import me.crazycranberry.minecrafttcg.events.SecondPostCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.SecondPreCombatPhaseStartedEvent;
import me.crazycranberry.minecrafttcg.events.TurnEndEvent;
import me.crazycranberry.minecrafttcg.model.Stadium;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import static me.crazycranberry.minecrafttcg.carddefinitions.Card.CARD_NAME_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.IS_CARD_KEY;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.COMBAT_PHASE;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.FIRST_POSTCOMBAT_PHASE;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.FIRST_PRECOMBAT_PHASE;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.POST_COMBAT_CLEANUP;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.SECOND_POSTCOMBAT_PHASE;
import static me.crazycranberry.minecrafttcg.model.TurnPhase.SECOND_PRECOMBAT_PHASE;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.ITALIC;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.event.block.Action.LEFT_CLICK_AIR;
import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class DuelActionsManager implements Listener {
    @EventHandler
    private void onCardCast(PlayerInteractEvent event) {
        Stadium stadium = StadiumManager.stadium(event.getPlayer().getWorld());
        if (isValidCastAttempt(event, stadium)) {
            BookMeta bookMeta = (BookMeta) event.getItem().getItemMeta();
            CardType cardType = CardType.valueOf(bookMeta.getPersistentDataContainer().get(CARD_NAME_KEY, PersistentDataType.STRING));
            Card card = cardType.card();
            // TODO: CHECK MANA COST
            Boolean successfullyCast = false;
            if (card instanceof MinionCardDefinition minionCardDefinition) {
                successfullyCast = summonMinion(event.getPlayer(), stadium, minionCardDefinition);
            }

            if (successfullyCast) {
                // TODO: LOWER MANA
                event.getPlayer().getInventory().remove(event.getItem());
            }
        } else if (isValidDescCheck(event, stadium)) {
            stadium.displayDescription(event.getPlayer());
        } else if (isNextPhaseRequested(event, stadium)) {
            requestNextPhase(stadium, event.getPlayer());
        }
    }

    private Boolean summonMinion(Player caster, Stadium stadium, MinionCardDefinition minionCardDefinition) {
        if (summonable(caster, stadium)) {
            stadium.minionSummoned(caster, minionCardDefinition);
            return true;
        }
        return false;
    }

    private boolean summonable(Player caster, Stadium stadium) {
        // TODO: MAKE SURE IT's THEIR TURN
        if (!stadium.isPlayerTargetingSummonableSpot(caster)) {
            caster.sendMessage(String.format("%s%sYou cannot summon a minion on that spot.%s", GRAY, ITALIC, RESET));
            return false;
        } else if (!stadium.isPlayerTargetingTheirOwnSpots(caster)) {
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

    private boolean isValidDescCheck(PlayerInteractEvent event, Stadium stadium) {
        return EquipmentSlot.HAND.equals(event.getHand()) &&
                (event.getAction().equals(LEFT_CLICK_BLOCK) || event.getAction().equals(LEFT_CLICK_AIR)) &&
                stadium != null &&
                stadium.isPlayerParticipating(event.getPlayer());
    }

    private boolean isNextPhaseRequested(PlayerInteractEvent event, Stadium stadium) {
        return event.getAction().equals(RIGHT_CLICK_BLOCK) &&
                event.getClickedBlock() != null &&
                event.getClickedBlock().getType().name().endsWith("_BUTTON") &&
                stadium != null &&
                stadium.isPlayerParticipating(event.getPlayer());
    }

    public void requestNextPhase(Stadium stadium, Player player) {
        if (player.equals(stadium.player1()) && stadium.turn() % 2 == 1) {
            switch (stadium.phase()) {
                case FIRST_PRECOMBAT_PHASE:
                    Bukkit.getPluginManager().callEvent(new SecondPreCombatPhaseStartedEvent(stadium));
                    break;
                case FIRST_POSTCOMBAT_PHASE:
                case SECOND_PRECOMBAT_PHASE:
                    player.sendMessage(String.format("%sYou cannot change the turn phase, it's not your turn.%s", GRAY, RESET));
                    break;
                case SECOND_POSTCOMBAT_PHASE:
                    Bukkit.getPluginManager().callEvent(new TurnEndEvent(stadium));
                    break;
                case POST_COMBAT_CLEANUP:
                case COMBAT_PHASE:
                    player.sendMessage(String.format("%sYou cannot change the turn phase during combat.%s", GRAY, RESET));
                    break;
            }
        } else {
            switch (stadium.phase()) {
                case SECOND_PRECOMBAT_PHASE:
                    Bukkit.getPluginManager().callEvent(new CombatStartEvent(stadium));
                    break;
                case SECOND_POSTCOMBAT_PHASE:
                case FIRST_PRECOMBAT_PHASE:
                    player.sendMessage(String.format("%sYou cannot change the turn phase, it's not your turn.%s", GRAY, RESET));
                    break;
                case FIRST_POSTCOMBAT_PHASE:
                    Bukkit.getPluginManager().callEvent(new SecondPostCombatPhaseStartedEvent(stadium));
                    break;
                case POST_COMBAT_CLEANUP:
                case COMBAT_PHASE:
                    player.sendMessage(String.format("%sYou cannot change the turn phase during combat.%s", GRAY, RESET));
                    break;
            }
        }
    }
}
