package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.SpellOrCantripCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.CantripCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.events.DeckViewRequestEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.crazycranberry.minecrafttcg.carddefinitions.Card.CARD_NAME_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.IS_CARD_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.RANDOM_UUID_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.ADRENALINE;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.AGGRESSIVE_BANDIT;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.DINGY_SKELETON;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.HEAL;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.HEAL_WITCH;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.HUNGRY_ZOMBIE;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.PROTECT;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.SEWER_ZOMBIE;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.TOXIC_SPIKES;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.DARK_PURPLE;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.ChatColor.YELLOW;

public class DeckManager implements Listener {
    @EventHandler
    private void onDeckViewRequest(DeckViewRequestEvent event) {
        Inventory deck = Bukkit.createInventory(null, 27, "My Deck");
        deck.addItem(createCard(SEWER_ZOMBIE));
        deck.addItem(createCard(DINGY_SKELETON));
        deck.addItem(createCard(PROTECT));
        deck.addItem(createCard(TOXIC_SPIKES));
        deck.addItem(createCard(HEAL));
        deck.addItem(createCard(ADRENALINE));
        deck.addItem(createCard(AGGRESSIVE_BANDIT));
        deck.addItem(createCard(HUNGRY_ZOMBIE));
        deck.addItem(createCard(HEAL_WITCH));
        event.getPlayer().openInventory(deck);
    }

    private ItemStack createCard(CardEnum cardEnum) {
        Card cardDef = cardEnum.card();
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.setDisplayName(String.format("%s%s%s", cardDef.rarity().color(), cardDef.cardName(), RESET));
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

    private String spellOrCantripCardDescription(SpellOrCantripCardDefinition card) {
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
            %s%sName:%s %s
            %sRarity:%s %s
            %sCard Type:%s %s
            %sTargets:%s %s
            %sCard Cost:%s %s
            %sDescription:%s %s
            """,
            RESET, GOLD, RESET, card.cardName(),
            YELLOW, RESET, card.rarity().toString(),
            AQUA, RESET, card instanceof CantripCardDefinition ? "Cantrip" : "Spell",
            DARK_PURPLE, RESET, String.join(", ", targets),
            LIGHT_PURPLE, RESET, card.cost(),
            BLUE, RESET, card.cardDescription()
        );
    }

    private String minionCardDescription(MinionCardDefinition card) {
        return String.format("""
            %s%sName:%s %s
            %sRarity:%s %s
            %sCard Type:%s Minion
            %sMinion Type:%s %s
            %sCard Cost:%s %s
            %sIs Ranged:%s %s
            %sStrength:%s %s
            %sMax health:%s %s
            %sDescription:%s %s
            """,
            RESET, GOLD, RESET, card.cardName(),
            YELLOW, RESET, card.rarity().toString(),
            AQUA, RESET,
            DARK_PURPLE, RESET, card.minionType(),
            LIGHT_PURPLE, RESET, card.cost(),
            DARK_GREEN, RESET, card.isRanged(),
            GREEN, RESET, card.strength(),
            RED, RESET, card.maxHealth(),
            BLUE, RESET, card.cardDescription()
            );
    }
}
