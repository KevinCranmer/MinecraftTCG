package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
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

import java.util.List;

import static me.crazycranberry.minecrafttcg.carddefinitions.Card.CARD_NAME_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.IS_CARD_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.SEWER_ZOMBIE;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.DARK_PURPLE;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

public class DeckManager implements Listener {
    @EventHandler
    private void onDeckViewRequest(DeckViewRequestEvent event) {
        Inventory deck = Bukkit.createInventory(null, 27, "My Deck");
        deck.addItem(createCard(SEWER_ZOMBIE));
        event.getPlayer().openInventory(deck);
    }

    private ItemStack createCard(CardEnum cardEnum) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.setAuthor("CrazyCranberry Mods");
        bookMeta.setTitle(cardEnum.card().cardName());
        String page = "";
        if (cardEnum.card() instanceof MinionCardDefinition minionCardDefinition) {
            page = minionCardDescription(minionCardDefinition);
        }
        bookMeta.addPage(page);
        bookMeta.setLore(List.of(String.format("%sType \"/tcg\" to learn more!%s", GOLD, RESET)));
        bookMeta.getPersistentDataContainer().set(IS_CARD_KEY, PersistentDataType.BOOLEAN, true);
        bookMeta.getPersistentDataContainer().set(CARD_NAME_KEY, PersistentDataType.STRING, cardEnum.name());
        book.setItemMeta(bookMeta);
        return book;
    }

    private String minionCardDescription(MinionCardDefinition card) {
        return String.format("""
            %s%sName:%s %s
            %sCard Type:%s Minion
            %sMinion Type:%s %s
            %sCard Cost:%s %s
            %sStrength:%s %s
            %sMax health:%s %s
            %sDescription:%s %s
            """,
            RESET, GOLD, RESET, card.cardName(),
            AQUA, RESET,
            DARK_PURPLE, RESET, card.minionType(),
            LIGHT_PURPLE, RESET, card.cost(),
            RED, RESET, card.strength(),
            BLUE, RESET, card.maxHealth(),
            GREEN, RESET, card.cardDescription().replace("$", "")
            );
    }
}
