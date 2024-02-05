package me.crazycranberry.minecrafttcg.model;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.SpellOrCantripCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.CantripCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.SpellCardDefinition;
import me.crazycranberry.minecrafttcg.config.CollectionConfigs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.CARD_NAME_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.IS_CARD_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.RANDOM_UUID_KEY;
import static me.crazycranberry.minecrafttcg.model.Collection.SortBy.COST;
import static me.crazycranberry.minecrafttcg.model.Collection.SortBy.NAME;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.DARK_PURPLE;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

public class Collection {
    public static final NamespacedKey IS_PAGING_KEY = new NamespacedKey(getPlugin(), "pagingitem");
    public static final NamespacedKey NEXT_PAGE_KEY = new NamespacedKey(getPlugin(), "nextpage");
    public static final int CARDS_PER_PAGE = 45;
    private final Map<CardEnum, Integer> collectionMap;
    private final List<List<ItemStack>> pages = new ArrayList<>();
    private int currentPage = 0;
    private final SortBy sortBy;
    private final Inventory inventory;

    private Collection(Map<CardEnum, Integer> collectionMap, SortBy sortBy) {
        this.collectionMap = collectionMap;
        this.sortBy = sortBy;
        this.inventory = collectionInventory();
    }

    public Inventory collection() {
        return inventory;
    }

    private Inventory collectionInventory() {
        Inventory collectionInv = Bukkit.createInventory(null, CARDS_PER_PAGE + 9, "My Collection");
        collectionInv.setContents(contentArrayForPage(currentPage));
        return collectionInv;
    }

    public void save(Inventory collectionInv, Player p) {
        List<ItemStack> currentPageItems = inventoryToList(collectionInv, p);
        if (currentPage == pages.size()) {
            pages.add(currentPageItems);
        } else {
            pages.set(currentPage, currentPageItems);
        }
        CollectionConfigs.saveCollection(p, pages);
    }

    private ItemStack[] contentArrayForPage(int page) {
        ItemStack[] contents = new ItemStack[CARDS_PER_PAGE + 9];
        int cards = 0;
        boolean stillHaveCards = false;
        List<CardEnum> sortedCards = collectionMap.keySet().stream()
                .sorted(
                        sortBy.comparator
                                .thenComparing(COST.comparator())
                                .thenComparing(NAME.comparator()))
                .toList();
        for (CardEnum cardEnum : sortedCards) {
            for (int i = 0; i < collectionMap.get(cardEnum); i++) {
                if (cards >= CARDS_PER_PAGE * page) {
                    ItemStack card = createCard(cardEnum);
                    contents[cards % CARDS_PER_PAGE] = card;
                }
                cards++;
                if (cards >= CARDS_PER_PAGE * (page + 1)) {
                    stillHaveCards = true;
                    break;
                }
            }
            if (cards >= CARDS_PER_PAGE * (page + 1)) {
                break;
            }
        }
        if (stillHaveCards) {
            ItemStack pagingItem = createPagingItem(true);
            contents[50] = pagingItem;
        }
        if (page > 0) {
            ItemStack pagingItem = createPagingItem(false);
            contents[48] = pagingItem;
        }
        return contents;
    }

    public void otherPage(Inventory collectionInv, Player p, Boolean isNextPage) {
        if (collectionInv != inventory) {
            System.out.println("Trying to get next page, but it isn't even the right inventory...");
            return;
        }
        List<ItemStack> currentPageItems = inventoryToList(collectionInv, p);
        if (currentPage == pages.size()) {
            pages.add(currentPageItems);
        } else {
            pages.set(currentPage, currentPageItems);
        }
        collectionInv.clear(); // Give the illusion that it's "loading" the next page. Otherwise there is no visual indicator that the next page button worked.
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            fillInPage(collectionInv, isNextPage);
        }, 3);
    }

    private void fillInPage(Inventory collectionInv, Boolean isNextPage) {
        currentPage = isNextPage ? currentPage + 1 : currentPage - 1;
        if (currentPage >= 0 && currentPage < pages.size()) {
            ItemStack[] contents = new ItemStack[CARDS_PER_PAGE + 9];
            collectionInv.setContents(pages.get(currentPage).toArray(contents));
        } else {
            collectionInv.setContents(contentArrayForPage(currentPage));
        }
    }

    private List<ItemStack> inventoryToList(Inventory collectionInv, Player p) {
        List<ItemStack> inventoryItems = new ArrayList<>();
        for (ItemStack item : collectionInv.getContents()) {
            if (item != null) {
                ItemMeta meta = item.getItemMeta();
                if (meta == null ||
                        !(Boolean.TRUE.equals(meta.getPersistentDataContainer().get(IS_CARD_KEY, PersistentDataType.BOOLEAN)) ||
                        Boolean.TRUE.equals(meta.getPersistentDataContainer().get(IS_PAGING_KEY, PersistentDataType.BOOLEAN)))) {
                    p.sendMessage(String.format("%SYou cannot save a %s to your collection.%s", GRAY, item.getType(), RESET));
                    p.getInventory().addItem(item);
                    inventoryItems.add(null);
                } else {
                    inventoryItems.add(item);
                }
            } else {
                inventoryItems.add(null);
            }
        }
        return inventoryItems;
    }

    private ItemStack createPagingItem(boolean isNextPage) {
        ItemStack pagingItem = new ItemStack(Material.POTATO);
        ItemMeta meta = pagingItem.getItemMeta();
        meta.setDisplayName(String.format("%s%s%s", RED, isNextPage ? "Next Page" : "Previous Page", RESET));
        meta.getPersistentDataContainer().set(IS_PAGING_KEY, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(NEXT_PAGE_KEY, PersistentDataType.BOOLEAN, isNextPage);
        pagingItem.setItemMeta(meta);
        return pagingItem;
    }

    public static Collection fromConfig(Player p, SortBy sortBy) {
        Map<CardEnum, Integer> collection = new HashMap<>();
        YamlConfiguration collectionConfig = CollectionConfigs.collectionConfigOrCreateDefault(p);
        ConfigurationSection collectionCs = collectionConfig.getConfigurationSection("collection");
        for (String key : collectionCs.getKeys(false)) {
            collection.put(CardEnum.fromString(key), collectionCs.getInt(key));
        }
        return new Collection(collection, sortBy);
    }

    public static ItemStack createCard(CardEnum cardEnum) {
        Card cardDef = cardEnum.card();
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        String cardType = "MINION";
        if (cardDef instanceof CantripCardDefinition) {
            cardType = "CANTRIP";
        } else if (cardDef instanceof SpellCardDefinition) {
            cardType = "SPELL";
        }
        bookMeta.setDisplayName(String.format("%s%s [%s] Cost:%s%s", cardDef.rarity().color(), cardDef.cardName(), cardType, cardDef.cost(), RESET));
        bookMeta.setAuthor("CrazyCranberry Mods");
        bookMeta.setTitle(cardDef.cardName());
        String page = "";
        if (cardDef instanceof MinionCardDefinition minionCardDefinition) {
            page = minionCardDescription(minionCardDefinition);
        } else if (cardDef instanceof SpellOrCantripCardDefinition spellOrCantripCardDef) {
            page = spellOrCantripCardDescription(spellOrCantripCardDef);
        }
        bookMeta.addPage(page);
        bookMeta.setLore(List.of(String.format("%sType \"/tcg\" to learn more!%s", GOLD, RESET)));
        bookMeta.getPersistentDataContainer().set(IS_CARD_KEY, PersistentDataType.BOOLEAN, true);
        bookMeta.getPersistentDataContainer().set(CARD_NAME_KEY, PersistentDataType.STRING, cardEnum.name());
        bookMeta.getPersistentDataContainer().set(RANDOM_UUID_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());
        book.setItemMeta(bookMeta);
        return book;
    }

    public static String spellOrCantripCardDescription(SpellOrCantripCardDefinition card) {
        List<String> targets = new ArrayList<>();
        if (card.targetsMinion()) {
            targets.add("Minions");
        }
        if (card.targetsPlayer()) {
            targets.add("Players");
        }
        if (card.targetsEmptySpots()) {
            targets.add("Spots");
        }
        return String.format("""
            %s%s%s%s [%s] Cost: %s
            %sTargets:%s %s
            %sDescription:%s %s
            """,
                RESET, card.rarity().color(), card.cardName(), RESET, card instanceof CantripCardDefinition ? "CANTRIP" : "SPELL", card.cost(),
                DARK_PURPLE, RESET, String.join(", ", targets),
                BLUE, RESET, card.cardDescription()
        );
    }

    public static String minionCardDescription(MinionCardDefinition card) {
        return String.format("""
            %s%s%s%s [MINION] Cost: %s
            %s%s%s:%s %s❤%s:%s/%s
            %sMinion Type:%s %s
            %sDescription:%s %s
            """,
                RESET, card.rarity().color(), card.cardName(), RESET, card.cost(),
                DARK_GREEN, card.isRanged() ? "\uD83C\uDFF9" : "🗡", RESET, card.strength(), RED, RESET, card.maxHealth(), card.maxHealth(),
                DARK_PURPLE, RESET, card.minionType(),
                BLUE, RESET, card.cardDescription()
        );
    }

    public enum SortBy {
        NAME(compareName()),
        COST(compareCost()),
        RARITY(compareRarity());

        private final Comparator<CardEnum> comparator;

        private SortBy(Comparator<CardEnum> comparator) {
            this.comparator = comparator;
        }

        public Comparator<CardEnum> comparator() {
            return comparator;
        }

        public static Comparator<CardEnum> compareName() {
            return Comparator.comparing(Enum::name);
        }

        private static Comparator<CardEnum> compareCost() {
            return Comparator.comparing((CardEnum card) -> card.card().cost());
        }

        private static Comparator<CardEnum> compareRarity() {
            return Comparator.comparing((CardEnum card) -> card.card().rarity()).reversed();
        }

        public static SortBy fromString(String s) {
            for (SortBy sortBy : values()) {
                if (s.toUpperCase().equals(sortBy.name())) {
                    return sortBy;
                }
            }
            return null;
        }
    }
}
