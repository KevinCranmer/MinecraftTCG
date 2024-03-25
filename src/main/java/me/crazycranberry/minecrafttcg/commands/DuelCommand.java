package me.crazycranberry.minecrafttcg.commands;

import me.crazycranberry.minecrafttcg.events.DuelAcceptedEvent;
import me.crazycranberry.minecrafttcg.managers.StadiumManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.commands.TcgCommand.MENU_KEY;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

public class DuelCommand implements CommandExecutor, TabCompleter {
    /** Key: person challenged. Value: person who sent the challenge */
    private static final Map<Player, Player> playerDuelChallenges = new HashMap<>();
    public static final String DUEL_MENU_NAME = "Choose an opponent";
    public static final NamespacedKey DUEL_NEW_PAGE_KEY = new NamespacedKey(getPlugin(), "duel_load_page");
    public static final NamespacedKey IS_RANKED_KEY = new NamespacedKey(getPlugin(), "is_ranked");
    private static final int numPlayersPerPage = 45;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            return false;
        }
        if (command.getName().equalsIgnoreCase("duel")) {
            if (args.length == 0) {
                p.openInventory(createDuelChallengeInventory(p, 0, false));
                return true;
            } else if ("accept".equals(args[0])) {
                if (playerDuelChallenges.containsKey(p)) {
                    Player opponent = playerDuelChallenges.get(p);
                    if (StadiumManager.stadium(p.getLocation()) != null || StadiumManager.stadium(opponent.getLocation()) != null) {
                        p.sendMessage(String.format("%sCannot accept duel because one of you is already in a duel.", ChatColor.GRAY));
                        opponent.sendMessage(String.format("%s%s tried to accept your duel but one of you is already in a duel.", ChatColor.GRAY, p.getName()));
                        return true;
                    }
                    Bukkit.getPluginManager().callEvent(new DuelAcceptedEvent((Player) sender, opponent));
                    Stream<Player> keys = playerDuelChallenges.entrySet().stream()
                        .filter(e -> p.equals(e.getKey()) || p.equals(e.getValue()) || opponent.equals(e.getValue()) || opponent.equals(e.getKey()))
                        .map(Map.Entry::getKey);
                    for (Player key : keys.toList()) {
                        playerDuelChallenges.remove(key);
                    }
                    return true;
                } else {
                    p.sendMessage(String.format("%sYou do not have any challenges to accept", ChatColor.GRAY));
                    return false;
                }
            } else if("decline".equals(args[0])) {
                if (playerDuelChallenges.containsKey(p)) {
                    Player opponent = playerDuelChallenges.get(p);
                    playerDuelChallenges.remove(p);
                    opponent.sendMessage(String.format("%s%s has declined your duel.", ChatColor.GRAY, p.getName()));
                    p.sendMessage(String.format("%sSuccessfully declined.", ChatColor.GRAY, p.getName()));
                } else {
                    p.sendMessage(String.format("%sYou do not have any challenges to decline", ChatColor.GRAY));
                }
                return true;
            } else {
                Optional<Player> challengedPlayer = Bukkit.getOnlinePlayers().stream().filter(player -> args[0].equals(player.getName())).map(player -> (Player) player).findFirst();
                if (challengedPlayer.isEmpty()) {
                    p.sendMessage(String.format("%s%s is not the name of any online players", ChatColor.GRAY, args[0]));
                    return false;
                } else if (challengedPlayer.get().equals(p)) {
                    p.sendMessage(String.format("%sYou cannot challenge yourself", ChatColor.GRAY));
                    return false;
                } else {
                    playerDuelChallenges.put(challengedPlayer.get(), p);
                    p.sendMessage(String.format("%s%s has been sent the Duel Challenge. They have 60 seconds to accept it.", ChatColor.GRAY, challengedPlayer.get().getName()));
                    challengedPlayer.get().sendMessage(String.format("%s%s has just sent you a duel challenge. Type \"/duel <accept/decline>\" to respond to it.", ChatColor.GRAY, p.getName()));
                    Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                        Optional<Player> key = playerDuelChallenges.entrySet().stream().filter(e -> challengedPlayer.get().equals(e.getKey()) && p.equals(e.getValue())).map(Map.Entry::getKey).findFirst();
                        key.ifPresent(player -> playerDuelChallenges.remove(player));
                    }, 1200);
                }
            }
        }
        return true;
    }

    public static Optional<Player> getOpponentFromChallengeRequest(Player p) {
        return Optional.ofNullable(playerDuelChallenges.get(p));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && (command.getName().equalsIgnoreCase("duel") && args.length == 1)) {
            List<String> possibleValues = new ArrayList<>(Bukkit.getOnlinePlayers().stream().filter(p -> !sender.equals(p)).map(Player::getName).toList());
            possibleValues.add("accept");
            possibleValues.add("decline");
            return possibleValues.stream().filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        }
        return null;
    }

    public static Inventory createDuelChallengeInventory(Player requester, int pageNumber, boolean isRanked) {
        Inventory inv = Bukkit.createInventory(null, numPlayersPerPage + 9, DUEL_MENU_NAME);
        List<Player> onlinePlayers = Bukkit.getOnlinePlayers().stream()
            .filter(p -> !p.equals(requester))
            .map(p -> (Player)p)
            .toList();
        int startIndex = pageNumber * numPlayersPerPage;
        int endIndex = (pageNumber * numPlayersPerPage) + numPlayersPerPage;
        if (startIndex >= onlinePlayers.size()) {
            return inv;
        }
        for (Player p : onlinePlayers.subList(startIndex, Math.min(endIndex, onlinePlayers.size()))) {
            inv.addItem(playerChallengeSkull(p, isRanked));
        }
        if (pageNumber > 0) {
            inv.setItem(numPlayersPerPage + 3, createPagingItem(pageNumber, false, isRanked));
        }
        if (onlinePlayers.size() > endIndex) {
            inv.setItem(numPlayersPerPage + 5, createPagingItem(pageNumber, true, isRanked));
        }
        return inv;
    }

    private static ItemStack playerChallengeSkull(Player p, boolean isRanked) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        String command = String.format("%s %s", isRanked ? "/rankedduel" : "/duel", p.getName());
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(String.format("%s%s", isRanked ? AQUA : GREEN, command));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setOwningPlayer(p);
        meta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, command.replace("/", ""));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createPagingItem(int currentPageNumber, boolean isNextPage, boolean isRanked) {
        ItemStack pagingItem = new ItemStack(Material.POTATO);
        ItemMeta meta = pagingItem.getItemMeta();
        meta.setDisplayName(String.format("%s%s%s", RED, isNextPage ? "Next Page" : "Previous Page", RESET));
        meta.getPersistentDataContainer().set(DUEL_NEW_PAGE_KEY, PersistentDataType.INTEGER, isNextPage ? currentPageNumber + 1 : currentPageNumber - 1);
        meta.getPersistentDataContainer().set(IS_RANKED_KEY, PersistentDataType.BOOLEAN, isRanked);
        pagingItem.setItemMeta(meta);
        return pagingItem;
    }


}
