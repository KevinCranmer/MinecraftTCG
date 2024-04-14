package me.crazycranberry.minecrafttcg.config;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.logger;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.CARD_NAME_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.IS_CARD_KEY;

public class CollectionConfigs {
    public static final String COLLECTION_FOLDER = "collections";
    public static final int DECK_SIZE = 36;

    public static YamlConfiguration collectionConfigOrCreateDefault(Player p) {
        File playersCollectionFile = collectionFile(p);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playersCollectionFile);
        ConfigurationSection deckCs = config.getConfigurationSection("deck");
        ConfigurationSection collectionCs = config.getConfigurationSection("collection");
        if (!playersCollectionFile.exists() || deckCs == null) {
            deckCs = config.createSection("deck");
            deckCs.set(CardEnum.ADRENALINE.name(), 3);
            deckCs.set(CardEnum.AGGRESSIVE_BANDIT.name(), 3);
            deckCs.set(CardEnum.BACON.name(), 3);
            deckCs.set(CardEnum.BUILD_WALLS.name(), 3);
            deckCs.set(CardEnum.DINGY_SKELETON.name(), 3);
            deckCs.set(CardEnum.DIG_DEEPER.name(), 3);
            deckCs.set(CardEnum.HEAL.name(), 3);
            deckCs.set(CardEnum.HEAL_WITCH.name(), 3);
            deckCs.set(CardEnum.HUNGRY_ZOMBIE.name(), 3);
            deckCs.set(CardEnum.PROTECT.name(), 3);
            deckCs.set(CardEnum.SEWER_ZOMBIE.name(), 3);
            deckCs.set(CardEnum.TOXIC_SPIKES.name(), 3);
        }
        if (!playersCollectionFile.exists() || collectionCs == null) {
            collectionCs = config.createSection("collection");
        }
        cleanDeck(deckCs, collectionCs, p.getName());
        try {
            config.save(playersCollectionFile);
        } catch (IOException e) {
            logger().severe("Error saving collection file for " + p.getName() + "\n" + e.getMessage());
        }
        return config;
    }

    public static void cleanDeck(ConfigurationSection deckCs, ConfigurationSection collectionCs, String playersName) {
        // Remove any cards that aren't cards or have zero/negative values
        for (String key : deckCs.getKeys(false)) {
            if (CardEnum.fromString(key) == null) {
                deckCs.set(key, null);
                logger().warning("Don't know the card: " + key + " removing it from " + playersName + "'s deck.");
            }
            if (deckCs.getInt(key) < 1) {
                deckCs.set(key, null);
                logger().warning("The card: " + key + " had a count below 1. Removing it from " + playersName + "'s deck.");
            }
        }

        // Move any cards that have more than the allowed copies to their collection
        for (String key : deckCs.getKeys(false)) {
            int numAllowedPerDeck = CardEnum.fromString(key).card().rarity().numAllowedPerDeck();
            int numCopies = deckCs.getInt(key);
            if (numCopies > numAllowedPerDeck) {
                deckCs.set(key, numAllowedPerDeck);
                int collectionCopies = collectionCs.contains(key) ? collectionCs.getInt(key) : 0;
                collectionCs.set(key, collectionCopies + numCopies - numAllowedPerDeck);
                logger().warning("Too many copies of: " + key + " in " + playersName + "'s deck reducing copy count to " + numAllowedPerDeck);
            }
        }

        // Move excess cards to the collection
        int totalCards = 0;
        for (String key : deckCs.getKeys(false)) {
            int numCopies = deckCs.getInt(key);
            if (totalCards + numCopies > DECK_SIZE) {
                int numCopiesToSendToCollection = DECK_SIZE - totalCards;
                deckCs.set(key, numCopies - numCopiesToSendToCollection);
                int collectionCopies = collectionCs.contains(key) ? collectionCs.getInt(key) : 0;
                collectionCs.set(key, collectionCopies + numCopiesToSendToCollection);
                logger().warning(String.format("Deck too big. Sending %s copies of %s to %s's collection.", numCopiesToSendToCollection, key, playersName));
                totalCards += numCopies - numCopiesToSendToCollection;
            } else {
                totalCards += numCopies;
            }
        }

        // Fill in cards if below DECK_SIZE
        for (CardEnum cardEnum : CardEnum.values()) {
            if (totalCards == DECK_SIZE) {
                break;
            } else if (totalCards > DECK_SIZE) {
                logger().severe("My Deck Cleaning algorithm is broke. Somehow I filled in more than 36 cards. Please contact Crazy_Cranberry to resolve this.");
                break;
            }
            if (!cardEnum.card().rarity().equals(CardRarity.COMMON)) {
                continue;
            }
            if (deckCs.contains(cardEnum.name())) {
                int existingCopies = deckCs.getInt(cardEnum.name());
                int numAllowedPerDeck = cardEnum.card().rarity().numAllowedPerDeck();
                int amountToAdd = numAllowedPerDeck - existingCopies + totalCards > DECK_SIZE ? DECK_SIZE - totalCards : numAllowedPerDeck - existingCopies;
                deckCs.set(cardEnum.name(), existingCopies + amountToAdd);
                totalCards += amountToAdd;
            } else {
                int numAllowedPerDeck = cardEnum.card().rarity().numAllowedPerDeck();
                int amountToAdd = numAllowedPerDeck + totalCards > DECK_SIZE ? DECK_SIZE - totalCards : numAllowedPerDeck;
                deckCs.set(cardEnum.name(), amountToAdd);
                totalCards += amountToAdd;
            }
        }
    }

    public static File collectionFile(Player p) {
        return new File(getPlugin().getDataFolder().getAbsolutePath() + File.separatorChar + COLLECTION_FOLDER, p.getName() + ".yml");
    }

    public static void saveDeck(Player p, Inventory inventory) {
        File playersCollectionFile = collectionFile(p);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playersCollectionFile);
        ConfigurationSection deckCs = config.getConfigurationSection("deck");
        ConfigurationSection collectionCs = config.getConfigurationSection("collection");
        for (String key : deckCs.getKeys(false)) {
            deckCs.set(key, null);
        }
        for (Map.Entry<CardEnum, Integer> cardWithCount : cardConfigMapFromInventory(inventory).entrySet()) {
            deckCs.set(cardWithCount.getKey().name(), cardWithCount.getValue());
        }
        cleanDeck(deckCs, collectionCs, p.getName());
        try {
            config.save(playersCollectionFile);
        } catch (IOException e) {
            logger().severe("Error saving collection file for " + p.getName() + "\n" + e.getMessage());
        }
    }

    public static void saveCollection(Player p, List<List<ItemStack>> pages) {
        File playersCollectionFile = collectionFile(p);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playersCollectionFile);
        ConfigurationSection collectionCs = config.getConfigurationSection("collection");
        clear(collectionCs);
        for (List<ItemStack> page : pages) {
            for (ItemStack item : page) {
                if (item != null && item.getItemMeta() != null && Boolean.TRUE.equals(item.getItemMeta().getPersistentDataContainer().get(IS_CARD_KEY, PersistentDataType.BOOLEAN))) {
                    String cardEnum = item.getItemMeta().getPersistentDataContainer().get(CARD_NAME_KEY, PersistentDataType.STRING);
                    collectionCs.set(cardEnum, collectionCs.getInt(cardEnum, 0) + 1);
                }
            }
        }
        try {
            config.save(playersCollectionFile);
        } catch (IOException e) {
            logger().severe("Error saving collection file for " + p.getName() + "\n" + e.getMessage());
        }
    }

    private static void clear(ConfigurationSection cs) {
        for (String key : cs.getKeys(false)) {
            cs.set(key, null);
        }
    }

    private static Map<CardEnum, Integer> cardConfigMapFromInventory(Inventory inventory) {
        Map<CardEnum, Integer> cardConfigMap = new HashMap<>();
        for (ItemStack item : inventory.getContents()) {
            CardEnum cardEnum = CardEnum.fromString(item.getItemMeta().getPersistentDataContainer().get(CARD_NAME_KEY, PersistentDataType.STRING));
            Integer numCopies = cardConfigMap.get(cardEnum);
            numCopies = numCopies == null ? 0 : numCopies;
            cardConfigMap.put(cardEnum, numCopies + 1);
        }
        return cardConfigMap;
    }

    public static void addCardToCollection(Player p, CardEnum cardEnum) {
        File playersCollectionFile = collectionFile(p);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playersCollectionFile);
        ConfigurationSection collectionCs = config.getConfigurationSection("collection");
        if (!playersCollectionFile.exists() || collectionCs == null) {
            collectionCs = config.createSection("collection");
        }
        collectionCs.set(cardEnum.name(), collectionCs.getInt(cardEnum.name(), 0) + 1);
        try {
            config.save(playersCollectionFile);
        } catch (IOException e) {
            logger().severe("Error saving collection file for " + p.getName() + "\n" + e.getMessage());
        }
    }
}
