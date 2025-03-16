package me.crazycranberry.minecrafttcg.model;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.MultiTargetCard;
import me.crazycranberry.minecrafttcg.carddefinitions.SpellOrCantripCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
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
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.crazycranberry.minecrafttcg.CommonFunctions.nthSuffix;
import static me.crazycranberry.minecrafttcg.CommonFunctions.textToLines;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.CARD_COST_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.CARD_NAME_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.IS_CARD_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.RANDOM_UUID_KEY;
import static me.crazycranberry.minecrafttcg.model.Collection.SortBy.COST;
import static me.crazycranberry.minecrafttcg.model.Collection.SortBy.NAME;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.DARK_PURPLE;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP;

public class Collection {
    public static final NamespacedKey IS_PAGING_KEY = new NamespacedKey(getPlugin(), "pagingitem");
    public static final NamespacedKey NEXT_PAGE_KEY = new NamespacedKey(getPlugin(), "nextpage");
    public static final String COLLECTION_INV_NAME = "My Collection";
    public static final String ALL_CARDS_INV_NAME = "All Cards";
    public static final int CARDS_PER_PAGE = 45;
    private static final int previousPageIndex = 48;
    private static final int nextPageIndex = 50;
    private final Map<CardEnum, Integer> collectionMap;
    private final List<List<ItemStack>> pages = new ArrayList<>();
    private int currentPage = 0;
    private final SortBy sortBy;
    private final Inventory inventory;

    private Collection(Map<CardEnum, Integer> collectionMap, SortBy sortBy, String inventoryName) {
        this.collectionMap = collectionMap;
        this.sortBy = sortBy;
        this.inventory = collectionInventory(inventoryName);
    }

    public Inventory collection() {
        return inventory;
    }

    private Inventory collectionInventory(String inventoryName) {
        Inventory collectionInv = Bukkit.createInventory(null, CARDS_PER_PAGE + 9, inventoryName);
        int page = 0;
        List<ItemStack> pageContents = Arrays.asList(contentArrayForPage(page));
        pages.add(pageContents);
        while (pageContents.get(nextPageIndex) != null) {
            page++;
            pageContents = Arrays.asList(contentArrayForPage(page));
            pages.add(pageContents);
        }
        fillInPage(collectionInv);
        return collectionInv;
    }

    public void save(Inventory collectionInv, Player p) {
        List<ItemStack> currentPageItems = inventoryToList(collectionInv, p);
        pages.set(currentPage, currentPageItems);
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
            contents[nextPageIndex] = pagingItem;
        }
        if (page > 0) {
            ItemStack pagingItem = createPagingItem(false);
            contents[previousPageIndex] = pagingItem;
        }
        return contents;
    }

    public void otherPage(Inventory collectionInv, Player p, Boolean isNextPage) {
        if (collectionInv != inventory) {
            System.out.println("Trying to get next page, but it isn't even the right inventory...");
            return;
        }
        List<ItemStack> currentPageItems = inventoryToList(collectionInv, p);
        pages.set(currentPage, currentPageItems);
        collectionInv.clear(); // Give the illusion that it's "loading" the next page. Otherwise there is no visual indicator that the next page button worked.
        currentPage = isNextPage ? currentPage + 1 : currentPage - 1;
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> fillInPage(collectionInv), 3);
    }

    private void fillInPage(Inventory collectionInv) {
        ItemStack[] contents = new ItemStack[CARDS_PER_PAGE + 9];
        collectionInv.setContents(pages.get(currentPage).toArray(contents));
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
        return new Collection(collection, sortBy, COLLECTION_INV_NAME);
    }

    public static Collection allCards(SortBy sortBy) {
        Map<CardEnum, Integer> allCards = new HashMap<>();
        for (CardEnum card : CardEnum.values()) {
            allCards.put(card, 1);
        }
        return new Collection(allCards, sortBy, ALL_CARDS_INV_NAME);
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
        bookMeta.setLore(cardLore(cardDef));
        bookMeta.addItemFlags(HIDE_ADDITIONAL_TOOLTIP);
        bookMeta.getPersistentDataContainer().set(IS_CARD_KEY, PersistentDataType.BOOLEAN, true);
        bookMeta.getPersistentDataContainer().set(CARD_NAME_KEY, PersistentDataType.STRING, cardEnum.name());
        bookMeta.getPersistentDataContainer().set(RANDOM_UUID_KEY, PersistentDataType.STRING, UUID.randomUUID().toString());
        bookMeta.getPersistentDataContainer().set(CARD_COST_KEY, PersistentDataType.INTEGER, cardDef.cost());
        book.setItemMeta(bookMeta);
        return book;
    }

    private static List<String> cardLore(Card cardDef) {
        List<String> lore = new ArrayList<>();
        if (cardDef instanceof MinionCardDefinition minionCardDefinition) {
            lore.add(minionCardStats(minionCardDefinition));
        }
        lore.addAll(textToLines(cardDef.cardDescription(), 35, GRAY));
        lore.add(String.format("%sType \"/tcg\" to learn more!%s", GOLD, RESET));
        return lore;
    }

    public static String targetsString(TargetRules targetRules) {
        List<String> targets = new ArrayList<>();
        if (targetRules.targetsAllyMinions()) {
            targets.add("Allies");
        }
        if (targetRules.targetsEnemyMinions()) {
            targets.add("Enemies");
        }
        if (targetRules.targetsPlayers()) {
            targets.add("Players");
        }
        if (targetRules.targetsEmptyAllySpots()) {
            targets.add("Ally Spots");
        }
        if (targetRules.targetsEmptyEnemySpots()) {
            targets.add("Enemy Spots");
        }
        return targets.isEmpty() ? "" : String.join(", ", targets);
    }

    private static String targetsDescription(Card card) {
        StringBuilder description = new StringBuilder();
        if (card instanceof MultiTargetCard multiTargetCard) {
            if (card instanceof MinionCardDefinition) {
                description.append(String.format("%s1st Target:%s Empty Spot%n", DARK_PURPLE, RESET));
            } else if (card instanceof SpellOrCantripCardDefinition spellOrCantripCardDefinition) {
                description.append(String.format("%s1st Target:%s %s%n", DARK_PURPLE, RESET, targetsString(spellOrCantripCardDefinition.targetRules())));
            }
            for (int i = 0; i < multiTargetCard.targetRulesForExtraTargets().size(); i++) {
                String targetNumberString = nthSuffix(i + 1);
                description.append(String.format("%s%s Target:%s %s%n", DARK_PURPLE, targetNumberString, RESET, targetsString(multiTargetCard.targetRulesForExtraTargets().get(i))));
            }
        } else if (card instanceof SpellOrCantripCardDefinition spellOrCantripCardDefinition) {
                description.append(String.format("%sTarget:%s %s%n", DARK_PURPLE, RESET, targetsString(spellOrCantripCardDefinition.targetRules())));
        }
        return description.toString();
    }

    public static String spellOrCantripCardDescription(SpellOrCantripCardDefinition card) {
        return String.format("""
            %s%s%s%s [%s] Cost: %s
            %s%s%sDescription:%s %s
            """,
            RESET, card.rarity().color(), card.cardName(), RESET, card instanceof CantripCardDefinition ? "CANTRIP" : "SPELL", card.cost(),
            targetsDescription(card), card instanceof CantripCardDefinition cantrip && !cantrip.canCastDuringCombat() ? " [Cannot be cast during combat]\n" : "",
            BLUE, RESET, card.cardDescription()
        );
    }

    public static String minionCardDescription(MinionCardDefinition card) {
        return String.format("""
            %s%s%s%s [MINION] Cost: %s
            %s
            %sMinion Type:%s %s
            %s%sDescription:%s %s
            """,
                RESET, card.rarity().color(), card.cardName(), RESET, card.cost(),
                minionCardStats(card),
                LIGHT_PURPLE, RESET, card.minionType(),
                String.format("%s", targetsDescription(card)),
                BLUE, RESET, card.cardDescription()
        );
    }

    public static String minionCardStats(MinionCardDefinition card) {
        return String.format("%s%s%s:%s %s❤%s:%s/%s", DARK_GREEN, card.isFlying() ? "☁" : card.isRanged() ? "\uD83C\uDFF9" : "🗡", RESET, card.strength(), RED, RESET, card.maxHealth(), card.maxHealth());
    }

    public enum SortBy {
        NAME(compareName()),
        COST(compareCost()),
        RARITY(compareRarity());

        private final Comparator<CardEnum> comparator;

        SortBy(Comparator<CardEnum> comparator) {
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
            return Comparator.comparing((CardEnum card) -> card.card().rarity());
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
