package me.crazycranberry.minecrafttcg.managers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

import static me.crazycranberry.minecrafttcg.commands.DuelCommand.DUEL_MENU_NAME;
import static me.crazycranberry.minecrafttcg.commands.DuelCommand.DUEL_NEW_PAGE_KEY;
import static me.crazycranberry.minecrafttcg.commands.DuelCommand.IS_RANKED_KEY;
import static me.crazycranberry.minecrafttcg.commands.DuelCommand.createDuelChallengeInventory;
import static me.crazycranberry.minecrafttcg.commands.TcgCommand.MENU_KEY;
import static me.crazycranberry.minecrafttcg.commands.TcgCommand.TCG_INFO_MENU_NAME;
import static me.crazycranberry.minecrafttcg.commands.TcgCommand.TCG_MENU_NAME;

public class MenuManager implements Listener {
    @EventHandler
    private void onMenuInteract(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(TCG_MENU_NAME) || event.getView().getTitle().equals(TCG_INFO_MENU_NAME)) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            Optional.ofNullable(event.getCurrentItem())
                .map(ItemStack::getItemMeta)
                .map(ItemMeta::getPersistentDataContainer)
                .map(c -> c.get(MENU_KEY, PersistentDataType.STRING))
                .ifPresent(c -> ((Player) event.getWhoClicked()).performCommand(c));
        } else if (event.getView().getTitle().equals(DUEL_MENU_NAME)) {
            event.setCancelled(true);
            Optional<PersistentDataContainer> maybePdc = Optional.ofNullable(event.getCurrentItem())
                .map(ItemStack::getItemMeta)
                .map(ItemMeta::getPersistentDataContainer);
            if (maybePdc.isPresent()) {
                Integer newPage = maybePdc.get().get(DUEL_NEW_PAGE_KEY, PersistentDataType.INTEGER);
                if (newPage != null) {
                    event.getWhoClicked().openInventory(createDuelChallengeInventory((Player) event.getWhoClicked(), newPage, Boolean.TRUE.equals(maybePdc.get().get(IS_RANKED_KEY, PersistentDataType.BOOLEAN))));
                } else {
                    maybePdc
                        .map(c -> c.get(MENU_KEY, PersistentDataType.STRING))
                        .ifPresent(c -> ((Player) event.getWhoClicked()).performCommand(c));
                    event.getWhoClicked().closeInventory();
                }
            }
        }
    }
}
