package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.carddefinitions.CardType;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCard;
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

import static me.crazycranberry.minecrafttcg.MinecraftTCG.logger;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.CARD_NAME_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.Card.IS_CARD_KEY;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardType.SEWER_ZOMBIE;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

public class DeckManager implements Listener {
    @EventHandler
    private void onDeckViewRequest(DeckViewRequestEvent event) {
        Inventory deck = Bukkit.createInventory(null, 27, "My Deck");
        deck.addItem(createCard(SEWER_ZOMBIE));
        event.getPlayer().openInventory(deck);
    }

    private ItemStack createCard(CardType cardType) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.setAuthor("CrazyCranberry Mods");
        bookMeta.setTitle(cardType.card().cardName());
        String page = "";
        if (cardType.card() instanceof MinionCard minionCard) {
            page = minionCardDescription(minionCard);
        }
        bookMeta.addPage(page);
        bookMeta.setLore(List.of(String.format("%sType \"/tcg\" to learn more!%s", GOLD, RESET)));
        bookMeta.getPersistentDataContainer().set(IS_CARD_KEY, PersistentDataType.BOOLEAN, true);
        bookMeta.getPersistentDataContainer().set(CARD_NAME_KEY, PersistentDataType.STRING, cardType.name());
        book.setItemMeta(bookMeta);
        return book;
    }

    private String minionCardDescription(MinionCard card) {
        return String.format("""
            %sName:%s %s
            %sStrength:%s %s
            %sMax health:%s %s
            %sDescription:%s %s
            """,
            GOLD, RESET, card.cardName(),
            BLUE, RESET, card.strength(),
            RED, RESET, card.maxHealth(),
            GREEN, RESET, card.cardDescription()
            );
    }
}
