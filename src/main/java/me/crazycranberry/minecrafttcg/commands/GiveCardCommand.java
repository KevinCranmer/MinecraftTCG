package me.crazycranberry.minecrafttcg.commands;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;
import static me.crazycranberry.minecrafttcg.model.Collection.createCard;

public class GiveCardCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("givecard")) {
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(String.format("%sYou must provide a card name", ChatColor.GRAY));
            return false;
        }
        int endOfCardNameIndex = args.length - 1;
        String potentialPlayersName = args[endOfCardNameIndex];
        Optional<? extends Player> playerReceivingCard = Bukkit.getOnlinePlayers().stream().filter(p -> p.getName().equalsIgnoreCase(potentialPlayersName)).findFirst();
        if (playerReceivingCard.isEmpty()) {
            // Assuming if the player isn't found that they're trying to give the card to themselves
            // and the entirety of the args can be considered the cardName
            endOfCardNameIndex++;
        }
        String cardName = String.join(" ", Arrays.copyOfRange(args, 0, endOfCardNameIndex));
        Optional<CardEnum> cardToGive = "random".equalsIgnoreCase(cardName) ? randomFromList(Arrays.asList(CardEnum.values())) : CardEnum.byName(cardName);
        if (cardToGive.isEmpty()) {
            sender.sendMessage(String.format("%sCould not find card \"%s\"", ChatColor.GRAY, cardName));
            return true;
        }
        if (playerReceivingCard.isEmpty()) {
            if (sender instanceof Player p) {
                p.getInventory().addItem(createCard(cardToGive.get()));
            } else {
                sender.sendMessage(String.format("%sNeed players name since this command sender is not a player", ChatColor.GRAY));
                return false;
            }
        } else {
            playerReceivingCard.get().getInventory().addItem(createCard(cardToGive.get()));
        }
        return true;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0) {
            List<String> cardNames = Arrays.stream(CardEnum.values()).map(c -> c.card().cardName())
                .filter(cardName -> cardName.toLowerCase().startsWith(String.join(" ", args).toLowerCase()))
                .map(cardName -> cardName.split(" "))
                .filter(cardNameArray -> cardNameArray.length >= args.length)
                .map(cardNameArray -> Arrays.copyOfRange(cardNameArray, args.length-1, cardNameArray.length))
                .map(cardNameArray -> String.join(" ", cardNameArray))
                .toList();
            List<String> playerNames = Bukkit.getOnlinePlayers().stream().map(Player::getName)
                .filter(playerName -> args.length > 1 && playerName.toLowerCase().startsWith(args[args.length-1].toLowerCase()))
                .toList();
            return Stream.of(cardNames, playerNames)
                .flatMap(Collection::stream)
                .toList();
        }
        return null;
    }
}
