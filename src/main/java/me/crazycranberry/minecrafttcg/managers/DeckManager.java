package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.config.CollectionConfigs;
import me.crazycranberry.minecrafttcg.events.DeckViewRequestEvent;
import me.crazycranberry.minecrafttcg.model.Deck;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static me.crazycranberry.minecrafttcg.carddefinitions.Card.CARD_NAME_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.IS_CARD_KEY;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RESET;

public class DeckManager implements Listener {
    private static Map<Player, List<ItemStack>> playersLookingAtDecks = new HashMap<>();
    private static Set<Player> playersLookingAtCollection = new HashSet<>();

    @EventHandler
    private void onDeckViewRequest(DeckViewRequestEvent event) {
        Stadium stadium = StadiumManager.stadium(event.getPlayer().getWorld());
        Inventory deck;
        if (stadium != null && stadium.isPlayerParticipating(event.getPlayer())) {
            deck = stadium.deck(event.getPlayer()).deck();
            event.getPlayer().openInventory(deck);
        } else {
            deck = Deck.fromConfig(event.getPlayer()).deck();
            event.getPlayer().openInventory(deck);
        }
        playersLookingAtDecks.put(event.getPlayer(), List.copyOf(Arrays.asList(deck.getContents()).stream().filter(Objects::nonNull).toList()));
    }

    @EventHandler
    private void onDeckOrCollectionSave(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        if ((!playersLookingAtDecks.containsKey(p) && !playersLookingAtCollection.contains(p)) || (StadiumManager.stadium(p.getWorld()) != null && StadiumManager.stadium(p.getWorld()).isPlayerParticipating(p))) {
            return;
        }
        if (playersLookingAtDecks.containsKey(p)) {
            if (deckSaveable(event.getInventory().getContents(), (Player) event.getPlayer())) {
                CollectionConfigs.saveDeck(p, event.getInventory());
            } else {
                recoverInventoryStateBeforeDeckEdit(p, event.getInventory());
            }
            playersLookingAtDecks.remove(p);
        }
    }

    @EventHandler
    private void onTryingToDropFromDeckWithDrag(PlayerDropItemEvent event) {
        if (playersLookingAtDecks.containsKey(event.getPlayer())) {
            event.getPlayer().sendMessage(String.format("%sYou cannot drop items while viewing your deck.%s", GRAY, RESET));
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        Stadium stadium = StadiumManager.stadium(p.getWorld());
        if (playersLookingAtDecks.containsKey((Player) event.getWhoClicked()) && stadium != null && stadium.isPlayerParticipating(p)) {
            p.sendMessage(String.format("%sYou cannot edit your deck mid-duel.%s", GRAY, RESET));
            event.setCancelled(true);
        }
        if (playersLookingAtDecks.containsKey((Player) event.getWhoClicked()) && event.getClick().equals(ClickType.DROP)) {
            event.getWhoClicked().sendMessage(String.format("%sYou cannot drop items while viewing your deck.%s", GRAY, RESET));
            event.setCancelled(true);
        }
    }

    private void recoverInventoryStateBeforeDeckEdit(Player p, Inventory inventory) {
        List<ItemStack> originalItems = playersLookingAtDecks.get(p);
        for (ItemStack item : inventory.getContents()) {
            if (item != null && !originalItems.contains(item)) {
                p.getInventory().addItem(item);
            }
        }
        for (ItemStack item : originalItems) {
            if (item != null && !inventory.contains(item)) {
                for (ItemStack itemInPlayerInv : p.getInventory()) {
                    if (itemInPlayerInv != null &&
                            Boolean.TRUE.equals(itemInPlayerInv.getItemMeta().getPersistentDataContainer().get(IS_CARD_KEY, PersistentDataType.BOOLEAN)) &&
                            itemInPlayerInv.getItemMeta().getPersistentDataContainer().get(CARD_NAME_KEY, PersistentDataType.STRING).equals(item.getItemMeta().getPersistentDataContainer().get(CARD_NAME_KEY, PersistentDataType.STRING))) {
                        p.getInventory().remove(itemInPlayerInv);
                        break;
                    }
                }
            }
        }
    }

    private boolean deckSaveable(ItemStack[] contents, Player p) {
        boolean weCanSaveTheDeck = true;
        Map<CardEnum, Integer> cardCounts = new HashMap<>();
        for (ItemStack item : contents) {
            if (item == null) {
                continue; // This is not an okay thing to happen. But I'd rather count how many cards are there and then report it instead of just failing due to one missing card.
            }
            if (!Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(IS_CARD_KEY, PersistentDataType.BOOLEAN))) {
                p.sendMessage(String.format("%sCould not save deck. %s is not a valid card.%s", GRAY, item.getType(), RESET));
                weCanSaveTheDeck = false;
                break;
            }
            CardEnum card = CardEnum.fromString(item.getItemMeta().getPersistentDataContainer().get(CARD_NAME_KEY, PersistentDataType.STRING));
            Integer numCopies = cardCounts.get(card);
            numCopies = numCopies == null ? 0 : numCopies;
            if (numCopies + 1 > card.card().rarity().numAllowedPerDeck()) {
                p.sendMessage(String.format("%sCould not save deck. %s%s can only have %s copies because it's %s.%s", GRAY, item.getItemMeta().getDisplayName(), GRAY, card.card().rarity().numAllowedPerDeck(), card.card().rarity().toString(), RESET));
                weCanSaveTheDeck = false;
                break;
            }
            cardCounts.put(card, numCopies + 1);
        }
        int numCardsInDeck = cardCounts.values().stream().reduce(0, Integer::sum);
        if (weCanSaveTheDeck && numCardsInDeck != 27) {
            p.sendMessage(String.format("%sCould not save deck. Your deck needs 27 cards. Your had %s.%s", GRAY, numCardsInDeck, RESET));
            weCanSaveTheDeck = false;
        }
        return weCanSaveTheDeck;
    }
}
