package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.SpellOrCantripCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.CantripCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.SpellCardDefinition;
import me.crazycranberry.minecrafttcg.events.CastCardEvent;
import me.crazycranberry.minecrafttcg.model.Stadium;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.logger;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.CARD_NAME_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.IS_CARD_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.RANDOM_UUID_KEY;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.ITALIC;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.event.block.Action.LEFT_CLICK_AIR;
import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;
import static org.bukkit.event.block.Action.RIGHT_CLICK_AIR;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;

public class DuelActionsManager implements Listener {
    Set<String> cardsBeingDescriptionChecked = new HashSet<>();

    @EventHandler
    private void onAction(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Stadium stadium = StadiumManager.stadium(p.getLocation());
        if (readingBookDetails(event, stadium)) {
            // It appears there's a bug in Spigot/Minecraft. When you right click in minecraft, a RIGHT_CLICK_ACTION is sent and then a LEFT_CLICK_ACTION is also sent.
            // I'm going to manually track when that happens to make it so players can read the description of their cards without casting them.
            String UUID = event.getItem().getItemMeta().getPersistentDataContainer().get(RANDOM_UUID_KEY, PersistentDataType.STRING);
            cardsBeingDescriptionChecked.add(UUID);
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> cardsBeingDescriptionChecked.remove(UUID), 1);
        } else if (isValidCastAttempt(event, stadium)) {
            BookMeta bookMeta = (BookMeta) event.getItem().getItemMeta();
            CardEnum cardEnum = CardEnum.valueOf(bookMeta.getPersistentDataContainer().get(CARD_NAME_KEY, PersistentDataType.STRING));
            Card card = cardEnum.card();
            if (castable(p, stadium, card)) {
                Bukkit.getPluginManager().callEvent(new CastCardEvent(card, stadium, p, event.getItem()));
            }
        } else if (isValidDescCheck(event, stadium)) {
            stadium.displayDescription(p);
        } else if (isNextPhaseRequested(event, stadium)) {
            requestNextPhase(stadium, p);
            if (event.getItem() != null && event.getItem().getItemMeta() != null && Boolean.TRUE.equals(event.getItem().getItemMeta().getPersistentDataContainer().get(IS_CARD_KEY, PersistentDataType.BOOLEAN))) {
                // The bug can happen here too...
                String UUID = event.getItem().getItemMeta().getPersistentDataContainer().get(RANDOM_UUID_KEY, PersistentDataType.STRING);
                cardsBeingDescriptionChecked.add(UUID);
                Bukkit.getScheduler().runTaskLater(getPlugin(), () -> cardsBeingDescriptionChecked.remove(UUID), 1);
            }
        }
    }

    private void sendCastMessage(Player p, Stadium stadium, Card card) {
        ChatColor color = p.equals(stadium.player1()) ? GREEN : GOLD;
        TextComponent castText = new TextComponent(String.format("%s%s%s has cast %s[%s]%s", color, p.getName(), RESET, card.rarity().color(), card.cardName(), RESET));
        String description = "";
        if (card instanceof MinionCardDefinition minionCard) {
            description += String.format("%s%s%s:%s %s❤%s:%s/%s\n",
                    DARK_GREEN, minionCard.isRanged() ? "\uD83C\uDFF9" : "🗡", RESET, minionCard.strength(), RED, RESET, minionCard.maxHealth(), minionCard.maxHealth());
        }
        description += card.cardDescription();
        castText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(description)));
        stadium.player1().spigot().sendMessage(castText);
        stadium.player2().spigot().sendMessage(castText);
    }

    @EventHandler
    private void onCardCast(CastCardEvent event) {
        cast(event.caster(), event.stadium(), event.card());
        event.caster().getInventory().remove(event.cardItem());
        sendCastMessage(event.caster(), event.stadium(), event.card());
        event.stadium().reduceMana(event.caster(), event.card().cost());
    }

    private void cast(Player player, Stadium stadium, Card card) {
        if (card instanceof MinionCardDefinition minionCardDefinition) {
            castMinion(player, stadium, minionCardDefinition);
        } else if (card instanceof CantripCardDefinition cantripCardDefinition) {
            castCantrip(player, stadium, cantripCardDefinition);
        } else if (card instanceof SpellCardDefinition spellCardDefinition) {
            castSpell(player, stadium, spellCardDefinition);
        }
    }

    private void castMinion(Player player, Stadium stadium, MinionCardDefinition minionCard) {
        minionCard.onCast(stadium, player);
    }

    private void castCantrip(Player player, Stadium stadium, CantripCardDefinition cantripCard) {
        cantripCard.onCast(stadium, player);
    }

    private void castSpell(Player player, Stadium stadium, SpellCardDefinition spellCard) {
        spellCard.onCast(stadium, player);
    }

    private boolean castable(Player p, Stadium stadium, Card card) {
        if (card.cost() > stadium.playerMana(p)) {
            p.sendMessage(String.format("%s%sYou only have %s mana, this card costs %s.%s", GRAY, ITALIC, stadium.playerMana(p), card.cost(), RESET));
            return false;
        } else if ((!(card instanceof CantripCardDefinition)) && !stadium.isPlayersTurn(p)) {
            p.sendMessage(String.format("%s%sYou cannot cast this card while it's not your turn.%s", GRAY, ITALIC, RESET));
            return false;
        } else if (card instanceof SpellOrCantripCardDefinition spellOrCantripCardDef) {
            if (!validTarget(p, stadium, spellOrCantripCardDef)) {
                p.sendMessage(String.format("%s%sThat is not a valid target for this spell.%s", GRAY, ITALIC, RESET));
                return false;
            }
        } else if (card instanceof MinionCardDefinition) {
            return summonable(p, stadium);
        }
        return true;
    }

    private boolean validTarget(Player p, Stadium stadium, SpellOrCantripCardDefinition spellOrCantripCardDef) {
        if (!spellOrCantripCardDef.targetsEmptySpots() && !spellOrCantripCardDef.targetsMinion() && !spellOrCantripCardDef.targetsPlayer()) {
            return true;
        }
        Minion targetedMinion = stadium.targetedMinion(p);
        return switch (stadium.playerTargetSpot(p)) {
            case RED_1_BACK, RED_2_BACK, RED_1_FRONT, RED_2_FRONT, BLUE_1_BACK, BLUE_2_BACK, BLUE_1_FRONT, BLUE_2_FRONT, GREEN_1_BACK, GREEN_2_BACK, GREEN_1_FRONT, GREEN_2_FRONT ->
                    (spellOrCantripCardDef.targetsMinion() && targetedMinion != null) || (spellOrCantripCardDef.targetsEmptySpots() && targetedMinion == null);
            case PLAYER_1_OUTLOOK, PLAYER_2_OUTLOOK -> spellOrCantripCardDef.targetsPlayer();
            default -> false;
        };
    }

    private boolean summonable(Player p, Stadium stadium) {
        if (!stadium.isPlayerTargetingSummonableSpot(p)) {
            p.sendMessage(String.format("%s%sYou cannot summon a minion on that spot.%s", GRAY, ITALIC, RESET));
            return false;
        } else if (!stadium.isPlayerTargetingTheirOwnSpots(p)) {
            p.sendMessage(String.format("%s%sYou cannot summon a minion on your opponents side of the field.%s", GRAY, ITALIC, RESET));
            return false;
        } else if (!stadium.isPlayersTargetAvailable(p)) {
            p.sendMessage(String.format("%s%sYou cannot summon a minion because a minion already exists there.%s", GRAY, ITALIC, RESET));
            return false;
        }
        return true;
    }

    private boolean readingBookDetails(PlayerInteractEvent event, Stadium stadium) {
        return EquipmentSlot.HAND.equals(event.getHand()) &&
            event.getItem() != null &&
            event.getItem().getType().equals(Material.WRITTEN_BOOK) &&
            event.getItem().getItemMeta() != null &&
            Boolean.TRUE.equals(event.getItem().getItemMeta().getPersistentDataContainer().get(IS_CARD_KEY, PersistentDataType.BOOLEAN)) &&
            (event.getAction().equals(RIGHT_CLICK_BLOCK) || event.getAction().equals(RIGHT_CLICK_AIR)) &&
            (event.getClickedBlock() == null || !event.getClickedBlock().getType().name().endsWith("_BUTTON")) &&
            stadium != null &&
            stadium.isPlayerParticipating(event.getPlayer());
    }

    private boolean isValidCastAttempt(PlayerInteractEvent event, Stadium stadium) {
        return EquipmentSlot.HAND.equals(event.getHand()) &&
                event.getItem() != null &&
                event.getItem().getType().equals(Material.WRITTEN_BOOK) &&
                event.getItem().getItemMeta() != null &&
                Boolean.TRUE.equals(event.getItem().getItemMeta().getPersistentDataContainer().get(IS_CARD_KEY, PersistentDataType.BOOLEAN)) &&
                !cardsBeingDescriptionChecked.contains(event.getItem().getItemMeta().getPersistentDataContainer().get(RANDOM_UUID_KEY, PersistentDataType.STRING)) &&
                (event.getAction().equals(LEFT_CLICK_BLOCK) || event.getAction().equals(LEFT_CLICK_AIR)) &&
                stadium != null &&
                stadium.isPlayerParticipating(event.getPlayer());
    }

    private boolean isValidDescCheck(PlayerInteractEvent event, Stadium stadium) {
        return EquipmentSlot.HAND.equals(event.getHand()) &&
                (event.getAction().equals(LEFT_CLICK_BLOCK) || event.getAction().equals(LEFT_CLICK_AIR)) &&
                stadium != null &&
                stadium.isPlayerParticipating(event.getPlayer()) &&
                stadium.playerTargetSpot(event.getPlayer()) != null;
    }

    private boolean isNextPhaseRequested(PlayerInteractEvent event, Stadium stadium) {
        return event.getAction().equals(RIGHT_CLICK_BLOCK) &&
                event.getClickedBlock() != null &&
                event.getClickedBlock().getType().name().endsWith("_BUTTON") &&
                stadium != null &&
                stadium.isPlayerParticipating(event.getPlayer());
    }

    public void requestNextPhase(Stadium stadium, Player player) {
        if (stadium.phase() == null) {
            return;
        }
        if (stadium.isPlayersTurn(player)) {
            try {
                Constructor<? extends Event> c = stadium.phase().nextPhaseRequestEventClass().getConstructor(Stadium.class);
                c.setAccessible(true);
                Event nextPhaseEvent = c.newInstance(stadium);
                Bukkit.getPluginManager().callEvent(nextPhaseEvent);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                logger().severe("Exception trying to create the next phases event: " + e.getMessage());
            }
        } else {
            player.sendMessage(String.format("%sYou cannot change the turn phase, it's not your turn.%s", GRAY, RESET));
        }
    }
}
