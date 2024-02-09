package me.crazycranberry.minecrafttcg.commands;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

public class TcgCommand implements CommandExecutor, TabCompleter {
    private static final List<String> commands = List.of(
        "/autocollect - Toggle whether or not card drops should automatically go to your collection",
        "/collection - View, add to, or take from your card collection",
        "/deck - View and edit your deck",
        "/duel - Challenge someone to a duel",
        "/rankedduel - Challenge someone to a ranked duel",
        "/ranks - View the top ranked players in the server",
        "/forfeit - Forfeit your current duel"
    );
    private static final Map<String, String> infoOptions = Map.of(
        "turns", turnInfo(),
        "combat", combatInfo(),
        "cards", cardsInfo(),
        "deck", deckInfo(),
        "collection", collectionInfo(),
        "multiAttack", multiAttackInfo(),
        "ranked", rankedInfo()
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
                    sender.sendMessage(String.format("%sAvailable info subjects:%s\n%s", AQUA, GRAY, String.join("\n", infoOptions.keySet()), RESET));
                }
            } else {
                sender.sendMessage(tcgWelcomeMessage());
            }
        }
        return true;
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
        return String.format("%sCommands:%s\n%s%s", AQUA, GRAY, String.join("\n", commands), RESET);
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
            %sThere are 3 types of cards:%s
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

    private static String rankedInfo() {
        return String.format("""
            %sRanked Duels are for the truly competitive. The top ranked player in the server has an %sAqua%s colored name in chat and the Tab List.
            Use %s/ranks%s to see how you compare!%s""",
            GRAY, AQUA, GRAY,
            LIGHT_PURPLE, GRAY, RESET);
    }
}
