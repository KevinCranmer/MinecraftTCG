package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.MultiTargetCard;
import me.crazycranberry.minecrafttcg.carddefinitions.SpellOrCantripCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.CantripCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.SpellCardDefinition;
import me.crazycranberry.minecrafttcg.events.CardAnimationFinishedEvent;
import me.crazycranberry.minecrafttcg.events.CardAnimationStartedEvent;
import me.crazycranberry.minecrafttcg.events.CastCardEvent;
import me.crazycranberry.minecrafttcg.events.CastCardTargetEvent;
import me.crazycranberry.minecrafttcg.managers.utils.CastInProgress;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import me.crazycranberry.minecrafttcg.model.TurnPhase;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static me.crazycranberry.minecrafttcg.CommonFunctions.nthSuffix;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.logger;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.CARD_NAME_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.IS_CARD_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.RANDOM_UUID_KEY;
import static me.crazycranberry.minecrafttcg.managers.TurnManager.MULLIGAN_INV_NAME;
import static me.crazycranberry.minecrafttcg.model.Collection.targetsString;
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
    Map<UUID, CastInProgress> castsInProgressMap = new HashMap<>();

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
        } else if (isValidTargetAttempt(event, stadium)) {
            BookMeta bookMeta = (BookMeta) event.getItem().getItemMeta();
            CardEnum cardEnum = CardEnum.valueOf(bookMeta.getPersistentDataContainer().get(CARD_NAME_KEY, PersistentDataType.STRING));
            Card card = cardEnum.card();
            if (castable(p, stadium, card)) {
                Bukkit.getPluginManager().callEvent(new CastCardTargetEvent(card, stadium, p, event.getItem()));
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
                    DARK_GREEN, minionCard.isFlying() ? "☁" : minionCard.isRanged() ? "\uD83C\uDFF9" : "🗡", RESET, minionCard.strength(), RED, RESET, minionCard.maxHealth(), minionCard.maxHealth());
        }
        description += card.cardDescription();
        castText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(description)));
        stadium.player1().spigot().sendMessage(castText);
        stadium.player2().spigot().sendMessage(castText);
    }

    @EventHandler
    private void onTarget(CastCardTargetEvent event) {
        CastInProgress cast = castsInProgressMap.get(event.caster().getUniqueId());
        String cardUuid = event.cardItem().getItemMeta().getPersistentDataContainer().get(RANDOM_UUID_KEY, PersistentDataType.STRING);
        Spot targetedSpot = event.stadium().playerTargetSpot(event.caster());
        if (cast != null && !cast.cardUuid().equals(cardUuid)) {
            castsInProgressMap.remove(event.caster().getUniqueId());
        } else if (event.card() instanceof MultiTargetCard multiTargetCard) {
            if (cast == null) {
                cast = new CastInProgress(cardUuid);
                castsInProgressMap.put(event.caster().getUniqueId(), cast);
            }
            cast.addTarget(targetedSpot);
            if (cast.targets().size() >= multiTargetCard.targetRulesForExtraTargets().size() + 1) { // + 1 because it's "Extra" targets beyond the initial first target which is required for all cards
                Bukkit.getPluginManager().callEvent(new CastCardEvent(event.card(), event.stadium(), event.caster(), event.cardItem(), cast.targets()));
            } else {
                event.caster().sendMessage(String.format("%s%sNow select your %s target for this spell.%s", GRAY, ITALIC, nthSuffix(cast.targets().size()), RESET));
            }
        } else {
            Bukkit.getPluginManager().callEvent(new CastCardEvent(event.card(), event.stadium(), event.caster(), event.cardItem(), List.of(targetedSpot)));
        }
    }

    @EventHandler
    private void onCardFinishedCasting(CastCardEvent event) {
        cast(event.caster(), event.stadium(), event.card(), event.targets());
        event.caster().getInventory().remove(event.cardItem());
        sendCastMessage(event.caster(), event.stadium(), event.card());
        event.stadium().reduceMana(event.caster(), event.card().cost());
        castsInProgressMap.remove(event.caster().getUniqueId());
    }

    @EventHandler
    private void onCardAnimationStarted(CardAnimationStartedEvent event) {
        event.stadium().cardAnimationStarted();
    }

    @EventHandler
    private void onCardAnimationFinished(CardAnimationFinishedEvent event) {
        event.stadium().cardAnimationFinished();
    }

    @EventHandler
    private void onTryingToDropCard(PlayerDropItemEvent event) {
        if (StadiumManager.stadium(event.getPlayer().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onFinishMulliganing(InventoryCloseEvent event) {
        Stadium stadium = StadiumManager.stadium(event.getPlayer().getLocation());
        if (event.getView().getTitle().equals(MULLIGAN_INV_NAME) && stadium != null) {
            stadium.playerFinishedMulliganing((Player) event.getPlayer(), event.getInventory());
        }
    }

    private void cast(Player player, Stadium stadium, Card card, List<Spot> targets) {
        if (card instanceof MinionCardDefinition minionCardDefinition) {
            castMinion(player, stadium, minionCardDefinition, targets);
        } else if (card instanceof CantripCardDefinition cantripCardDefinition) {
            castCantrip(player, stadium, cantripCardDefinition, targets);
        } else if (card instanceof SpellCardDefinition spellCardDefinition) {
            castSpell(player, stadium, spellCardDefinition, targets);
        }
    }

    private void castMinion(Player player, Stadium stadium, MinionCardDefinition minionCard, List<Spot> targets) {
        minionCard.onCast(stadium, player, targets, minionCard);
    }

    private void castCantrip(Player player, Stadium stadium, CantripCardDefinition cantripCard, List<Spot> targets) {
        cantripCard.onCast(stadium, player, targets, cantripCard);
    }

    private void castSpell(Player player, Stadium stadium, SpellCardDefinition spellCard, List<Spot> targets) {
        spellCard.onCast(stadium, player, targets, spellCard);
    }

    private boolean castable(Player p, Stadium stadium, Card card) {
        if (stadium.isCardAnimationInProgress()) {
            p.sendMessage(String.format("%s%sCannot cast cards while other card animations are playing.%s", GRAY, ITALIC, RESET));
            return false;
        } else if (card.cost() > stadium.playerMana(p)) {
            p.sendMessage(String.format("%s%sYou only have %s mana, this card costs %s.%s", GRAY, ITALIC, stadium.playerMana(p), card.cost(), RESET));
            return false;
        } else if ((!(card instanceof CantripCardDefinition)) && !stadium.isPlayersTurn(p)) {
            p.sendMessage(String.format("%s%sYou cannot cast this card while it's not your turn.%s", GRAY, ITALIC, RESET));
            return false;
        }  else if (card instanceof CantripCardDefinition cantripCardDefinition && ((stadium.phase().equals(TurnPhase.COMBAT_PHASE) || stadium.phase().equals(TurnPhase.POST_COMBAT_CLEANUP)) && !cantripCardDefinition.canCastDuringCombat())) {
            p.sendMessage(String.format("%s%sThis card cannot be cast during combat.%s", GRAY, ITALIC, RESET));
            return false;
        } else if (card instanceof MultiTargetCard multiTargetCard && castsInProgressMap.containsKey(p.getUniqueId())) {
            CastInProgress cast = castsInProgressMap.get(p.getUniqueId());
            TargetRules targetRules = multiTargetCard.targetRulesForExtraTargets().get(cast.targets().size()-1);
            if (!validTarget(p, stadium, targetRules) || !multiTargetCard.isValidAdditionalTarget(p, stadium, card, cast.targets(), stadium.playerTargetSpot(p))) {
                String targetNumberString = nthSuffix(cast.targets().size());
                p.sendMessage(String.format("%s%sValid %s targets for this card are: [%s]%s", GRAY, ITALIC, targetNumberString, targetsString(targetRules), RESET));
                return false;
            }
        } else if (card instanceof SpellOrCantripCardDefinition spellOrCantripCardDef) {
            if (!validTarget(p, stadium, spellOrCantripCardDef.targetRules()) || !spellOrCantripCardDef.isValidInitialTarget(p, stadium, card, stadium.playerTargetSpot(p))) {
                p.sendMessage(String.format("%s%sThat is not a valid target for this spell.%s", GRAY, ITALIC, RESET));
                return false;
            }
        } else if (card instanceof MinionCardDefinition) {
            return summonable(p, stadium);
        }
        return true;
    }

    private boolean validTarget(Player p, Stadium stadium, TargetRules targetRules) {
        if (!targetRules.targetsEmptyAllySpots() && !targetRules.targetsEmptyEnemySpots() && !targetRules.targetsAllyMinions() && !targetRules.targetsEnemyMinions() && !targetRules.targetsPlayers()) {
            return true;
        }
        Minion targetedMinion = stadium.targetedMinion(p);
        Spot spot = stadium.playerTargetSpot(p);
        if (stadium.allyMinionSpots(p).contains(spot)) {
            return (targetRules.targetsAllyMinions() && targetedMinion != null) || (targetRules.targetsEmptyAllySpots() && targetedMinion == null);
        } else if (stadium.enemyMinionSpots(p).contains(spot)) {
            return (targetRules.targetsEnemyMinions() && targetedMinion != null) || (targetRules.targetsEmptyEnemySpots() && targetedMinion == null);
        } else if (spot.equals(Spot.PLAYER_1_OUTLOOK) || spot.equals(Spot.PLAYER_2_OUTLOOK)) {
            return targetRules.targetsPlayers();
        }
        return false;
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

    private boolean isValidTargetAttempt(PlayerInteractEvent event, Stadium stadium) {
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
