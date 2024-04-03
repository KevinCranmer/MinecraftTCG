package me.crazycranberry.minecrafttcg.commands;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.ANIMAL_TYPES;
import static me.crazycranberry.minecrafttcg.carddefinitions.CardUtils.UNDEAD_TYPES;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.Material.BOOK;
import static org.bukkit.Material.BOOKSHELF;
import static org.bukkit.Material.CYAN_DYE;
import static org.bukkit.Material.DIAMOND;
import static org.bukkit.Material.DIAMOND_AXE;
import static org.bukkit.Material.IRON_AXE;
import static org.bukkit.Material.LIME_DYE;
import static org.bukkit.Material.PAPER;
import static org.bukkit.Material.RED_DYE;
import static org.bukkit.Material.WHITE_DYE;

public class TcgCommand implements CommandExecutor, TabCompleter {
    public static final NamespacedKey MENU_KEY = new NamespacedKey(getPlugin(), "menu_item");
    public static final String TCG_MENU_NAME = "MinecraftTCG";
    public static final String TCG_INFO_MENU_NAME = "MinecraftTCG Information";

    private static final Map<String, String> commands = Map.ofEntries(
        Map.entry("/autocollect", "Toggle whether or not card drops should automatically go to your collection"),
        Map.entry("/collection", "View, add to, or take from your card collection"),
        Map.entry("/deck", "View and edit your deck"),
        Map.entry("/duel", "Challenge someone to a duel"),
        Map.entry("/rankedduel", "Challenge someone to a ranked duel"),
        Map.entry("/ranks", "View the top ranked players in the server"),
        Map.entry("/forfeit", "Forfeit your current duel"),
        Map.entry("/tcg info", "Get information about a particular piece of the TCG"),
        Map.entry("/duel requests", "View current requests of people that want to duel you"),
        Map.entry("/rankedduel requests", "View current requests of people that want to ranked duel you")
    );
    private static final Map<String, String> infoOptions = Map.ofEntries(
        Map.entry("turns", turnInfo()),
        Map.entry("combat", combatInfo()),
        Map.entry("cards", cardsInfo()),
        Map.entry("deck", deckInfo()),
        Map.entry("collection", collectionInfo()),
        Map.entry("multiAttack", multiAttackInfo()),
        Map.entry("overkill", overkillInfo()),
        Map.entry("pacifist", pacifistInfo()),
        Map.entry("lifesteal", lifestealInfo()),
        Map.entry("ranked", rankedInfo()),
        Map.entry("ranged", rangedInfo()),
        Map.entry("flying", flyingInfo()),
        Map.entry("rally", rallyInfo()),
        Map.entry("animal", animalInfo()),
        Map.entry("undead", undeadInfo())
    );

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("tcg")) {
            if (args.length > 0 && args[0].equals("commands")) {
                sender.sendMessage(commandsMessage());
            } else if (args.length > 0 && args[0].equals("info")) {
                if (args.length > 1 && infoOptions.containsKey(args[1])) {
                    sender.sendMessage(infoOptions.get(args[1]));
                } else {
                    if (sender instanceof Player p) {
                        p.openInventory(createInfoInventory());
                    } else {
                        sender.sendMessage(String.format("%sAvailable info subjects:%s\n%s", GRAY, String.join("\n", infoOptions.keySet()), RESET));
                    }
                }
            } else {
                if (sender instanceof Player p) {
                    p.openInventory(createTcgInventory(p));
                } else {
                    sender.sendMessage(tcgWelcomeMessage());
                }
            }
        }
        return true;
    }

    private Inventory createInfoInventory() {
        Inventory infoInv = Bukkit.createInventory(null, 54, TCG_INFO_MENU_NAME);
        for (Map.Entry<String, String> e : infoOptions.entrySet()) {
            infoInv.addItem(createMenuItem(PAPER, "/tcg info " + e.getKey(), WHITE, e.getKey(), false));
        }
        return infoInv;
    }

    private Inventory createTcgInventory(Player p) {
        Inventory tcgInv = Bukkit.createInventory(null, 36, TCG_MENU_NAME);
        tcgInv.setItem(2, createMenuItem(IRON_AXE, "/duel", GREEN));
        tcgInv.setItem(4, createMenuItem(DIAMOND_AXE, "/rankedduel", AQUA));
        tcgInv.setItem(6, createMenuItem(DIAMOND, "/ranks", AQUA));
        tcgInv.setItem(3, createDuelRequestsItem(p, false));
        tcgInv.setItem(5, createDuelRequestsItem(p, true));
        tcgInv.setItem(21, createMenuItem(BOOK, "/deck", GOLD));
        tcgInv.setItem(22, createMenuItem(BOOKSHELF, "/collection", LIGHT_PURPLE));
        tcgInv.setItem(23, createAutoCollectItem(p));
        tcgInv.setItem(31, createMenuItem(PAPER, "/tcg info", GRAY));
        return tcgInv;
    }

    private ItemStack createDuelRequestsItem(Player p, boolean isRanked) {
        Map<Player, Player> challengesMap = isRanked ? RankedDuelCommand.challengesMap() : DuelCommand.challengesMap();
        if (!challengesMap.containsValue(p)) {
            return null;
        }
        Material mat = WHITE_DYE;
        ChatColor color = GREEN;
        String command = "/duel requests";
        if (isRanked) {
            mat = CYAN_DYE;
            color = AQUA;
            command = "/rankedduel requests";
        }
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color + command);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, command.replace("/", ""));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createAutoCollectItem(Player p) {
        boolean hasAutoCollect = getPlugin().config().shouldAutoCollect(p.getName());
        Material mat = RED_DYE;
        ChatColor color = RED;
        String onOrOff = "OFF";
        if (hasAutoCollect) {
            mat = LIME_DYE;
            color = GREEN;
            onOrOff = "ON";
        }
        return createMenuItem(mat, "/autocollect", color, "Toggle /autocollect - Currently: " + onOrOff, true);
    }

    private ItemStack createMenuItem(Material material, String command, ChatColor color, String display, boolean displayLore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color + display);
        if (displayLore) {
            List<String> lore = new ArrayList<>();
            String commandDesc = commands.get(command);
            while(true) {
                if (commandDesc.length() < 25) {
                    lore.add(GRAY + commandDesc);
                    break;
                }
                String laterChunk = commandDesc.substring(24);
                commandDesc = commandDesc.replace(laterChunk, laterChunk.replaceFirst(" ", "\n"));
                String[] commandPieces = commandDesc.split("\n");
                lore.add(GRAY + commandPieces[0]);
                commandDesc = commandPieces.length > 1 ? commandPieces[1] : "";
            }
            meta.setLore(lore);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(MENU_KEY, PersistentDataType.STRING, command.replace("/", ""));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createMenuItem(Material material, String command, ChatColor color) {
        return createMenuItem(material, command, color, command, true);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && (command.getName().equalsIgnoreCase("tcg") && args.length == 1)) {
            return Stream.of("commands", "info").filter(s -> s.startsWith(args[0].toLowerCase())).toList();
        } else if (sender instanceof Player && (command.getName().equalsIgnoreCase("tcg") && "info".equals(args[0]) && args.length == 2)) {
            return infoOptions.keySet().stream().filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        }
        return null;
    }

    private static String tcgWelcomeMessage() {
        return String.format("""
            %sWelcome to the Minecraft Trading Card Plugin!
            %sThis plugin gives your regular Minecraft actions
            a chance to drop %sTCG Cards%s that you can use to
            duel your friends!
            Type:
            %s/tcg commands%s
            or
            %s/tcg info%s
            to learn more!%s""",
            AQUA,
            GRAY,
            AQUA, GRAY,
            LIGHT_PURPLE, GRAY,
            LIGHT_PURPLE, GRAY,
            RESET);
    }

    private static String commandsMessage() {
        return String.format("%sCommands:%s\n%s%s", AQUA, GRAY, String.join("\n", commands.entrySet().stream().map(e -> String.format("%s - %s", e.getKey(), e.getValue())).toList()), RESET);
    }

    private static String turnInfo() {
        return String.format("""
            %sAt the start of every turn, you draw a card, gain an additional mana lamp, and refresh your mana.
            Turn order looks like the following:
            Turn 1:
            %sPlayer 1 %s-> %sPlayer 2 %s-> %sCombat %s-> %sPlayer 2 %s-> %sPlayer 1%s
            Turn 2:
            %sPlayer 2 %s-> %sPlayer 1 %s-> %sCombat %s-> %sPlayer 1 %s-> %sPlayer 2%s
            ...%s""",
            GRAY,
            GREEN, GRAY, GOLD, GRAY, RED, GRAY, GOLD, GRAY, GREEN, GRAY,
            GOLD, GRAY, GREEN, GRAY, RED, GRAY, GREEN, GRAY, GOLD, GRAY,
            RESET);
    }

    private static String combatInfo() {
        return String.format("""
            %sDuring combat phase, minions will try to attack the enemy minion in front of them.
            Only ranged minions can attack through ally minion.
            If there is no enemy minion in front of your minion, it will attack the enemy's cow.
            Damage done to the enemy cow is redirected to your enemy player.
            Once all minions are done attacking, combat ends and any minions with 0 health die.%s""",
            GRAY,
            RESET);
    }

    private static String cardsInfo() {
        return String.format("""
            %sThere are 3 types of cards:
            1. Minions - Playable mobs that attack the enemy for you.
            2. Spells - Cards that interact with the match or minions in some way.
            3. Cantrips - Similar to spells, except they can be cast at any time.
            There are also different rarities of cards: %sCommon%s, %sUncommon%s, %sRare%s, %sLegendary%s""",
            GRAY,
            CardRarity.COMMON.color(), GRAY,
            CardRarity.UNCOMMON.color(), GRAY,
            CardRarity.RARE.color(), GRAY,
            CardRarity.LEGENDARY.color(), RESET);
    }

    private static String deckInfo() {
        return String.format("""
            %sYour deck is what you will use to battle your enemies.
            With %s/deck%s you can edit your deck using cards you've collected while playing Minecraft.
            Card rarities have limits in your deck. Decks can have 3 copies of Common and Uncommon cards,
            but only 2 copies of Rares and 1 copy of Legendary cards.%s""",
            GRAY,
            LIGHT_PURPLE, GRAY,
            RESET);
    }

    private static String collectionInfo() {
        return String.format("""
            %sYour collection will hold cards you've collected that aren't currently in your deck.
            With %s/collection%s you can add cards to or take cards from your collection. You can also use %s/autocollect%s to make cards automatically get sent to your collection when dropped.%s""",
            GRAY,
            LIGHT_PURPLE, GRAY, LIGHT_PURPLE, GRAY, RESET);
    }

    private static String multiAttackInfo() {
        return String.format("""
            %sMinions with Multi-Attack will attack however many times their Multi-Attack level is.
            A minion with Multi-Attack 3, will attack 3 times each combat. Giving these minions bonus strength is a great idea because it'd be bonus damage for each attack.%s""",
            GRAY,
            RESET);
    }

    private static String overkillInfo() {
        return String.format("""
            %sMinions with Overkill will deal excess damage to the Entities behind.
            For example, an Overkill minion with 5 strength attacking a minion with 3 health will do 3 damage to that minion, and then 2 damage to the enemy player.%s""",
            GRAY,
            RESET);
    }

    private static String pacifistInfo() {
        return String.format("""
            %sMinions with Pacifist do not attack during combat.%s""",
            GRAY,
            RESET);
    }

    private static String animalInfo() {
        return String.format("""
            %sThe following entities are considered animals: [%s]
            (It's very likely some of these don't have cards for them yet).%s""",
            GRAY,
            String.join(", ", ANIMAL_TYPES.stream().map(EntityType::name).toList()),
            RESET);
    }

    private static String undeadInfo() {
        return String.format("""
            %sThe following entities are considered undead: [%s]
            (It's very likely some of these don't have cards for them yet).%s""",
            GRAY,
            String.join(", ", UNDEAD_TYPES.stream().map(EntityType::name).toList()),
            RESET);
    }

    private static String lifestealInfo() {
        return String.format("""
            %sMinions with Lifesteal heal their controller for any damage they deal.%s""",
            GRAY,
            RESET);
    }

    private static String rankedInfo() {
        return String.format("""
            %sRanked Duels are for the truly competitive. The top ranked player in the server has an %sAqua%s colored name in chat and the Tab List.
            Use %s/ranks%s to see how you compare!%s""",
            GRAY, AQUA, GRAY,
            LIGHT_PURPLE, GRAY, RESET);
    }

    private static String rangedInfo() {
        return String.format("""
            %sMinions with ranged can hit flying minions.%s""",
            GRAY, RESET);
    }

    private static String flyingInfo() {
        return String.format("""
            %sMinions with flying cannot be hit my other minions unless they have ranged or flying.%s""",
            GRAY, RESET);
    }

    private static String rallyInfo() {
        return String.format("""
            %sMinions with rally can attack even if there is an ally minion in front of them.%s""",
            GRAY, RESET);
    }
}
