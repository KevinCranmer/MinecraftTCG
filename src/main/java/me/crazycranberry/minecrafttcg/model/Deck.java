package me.crazycranberry.minecrafttcg.model;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.config.CollectionConfigs;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;
import static me.crazycranberry.minecrafttcg.config.CollectionConfigs.DECK_SIZE;
import static me.crazycranberry.minecrafttcg.model.Collection.SortBy.COST;
import static me.crazycranberry.minecrafttcg.model.Collection.SortBy.NAME;
import static me.crazycranberry.minecrafttcg.model.Collection.SortBy.RARITY;
import static me.crazycranberry.minecrafttcg.model.Collection.createCard;

public class Deck {
    private final Inventory deck;

    private Deck(Inventory deck) {
        this.deck = deck;
    }

    public Inventory deck() {
        return deck;
    }

    private List<ItemStack> remainingCards() {
        List<ItemStack> remainingCards = new ArrayList<>();
        for (ItemStack itemStack : deck.getContents()) {
            if (itemStack != null) {
                remainingCards.add(itemStack);
            }
        }
        return remainingCards;
    }

    /** Randomly removes a card from the deck inventory and returns it. */
    public Optional<ItemStack> draw() {
        Optional<ItemStack> drawnCard = randomFromList(remainingCards());
        drawnCard.ifPresent(deck::remove);
        return drawnCard;
    }

    public void addCard(ItemStack card) {
        deck.addItem(card);
    }

    public static Deck fromConfig(Player p) {
        Inventory deckInv = Bukkit.createInventory(null, DECK_SIZE, "My Deck");
        YamlConfiguration collectionConfig = CollectionConfigs.collectionConfigOrCreateDefault(p);
        ConfigurationSection deckCs = collectionConfig.getConfigurationSection("deck");
        for (CardEnum cardEnum : deckCs.getKeys(false)
                .stream()
                .map(CardEnum::fromString)
                .sorted(
                    RARITY.comparator()
                        .thenComparing(COST.comparator())
                        .thenComparing(NAME.comparator())).toList()) {
            for (int i = 0; i < deckCs.getInt(cardEnum.name()); i++) {
                deckInv.addItem(createCard(cardEnum));
            }
        }
        return new Deck(deckInv);
    }
}
