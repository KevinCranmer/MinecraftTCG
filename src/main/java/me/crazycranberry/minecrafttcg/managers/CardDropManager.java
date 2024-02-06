package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.model.Collection;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.TraderLlama;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.logger;

public class CardDropManager implements Listener {
    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        String name = event.getBlockPlaced().getType().name().toLowerCase();
        double roll = Math.random();
        double dropRate = getDropRate("block_place", name);
        System.out.println("The main roll for block_place." + name + ": " + roll + " / " + dropRate);
        if (roll < dropRate) {
            Optional<ItemStack> card = getCardToDrop("block_place." + name);
            if (card.isPresent()) {
                event.getPlayer().sendMessage(String.format("%sA TCG card just dropped for you.%s", ChatColor.GRAY, ChatColor.RESET));
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), card.get());
            }
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        String name = event.getBlock().getType().name().toLowerCase();
        double roll = Math.random();
        if (event.getPlayer().getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
            name = "default";
        }
        double dropRate = getDropRate("block_break", name);
        System.out.println("The main roll for block_break." + name + ": " + roll + " / " + dropRate);
        if (roll < dropRate) {
            Optional<ItemStack> card = getCardToDrop("block_break." + name);
            if (card.isPresent()) {
                event.getPlayer().sendMessage(String.format("%sA TCG card just dropped for you.%s", ChatColor.GRAY, ChatColor.RESET));
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), card.get());
            }
        }
    }

    @EventHandler
    private void onCraftItem(CraftItemEvent event) {
        String name = event.getRecipe().getResult().getType().name().toLowerCase();
        double roll = Math.random();
        double dropRate = getDropRate("craft_item", name);
        System.out.println("The main roll for craft_item." + name + ": " + roll + " / " + dropRate);
        if (roll < dropRate) {
            Optional<ItemStack> card = getCardToDrop("craft_item." + name);
            if (card.isPresent()) {
                event.getWhoClicked().sendMessage(String.format("%sA TCG card just dropped for you.%s", ChatColor.GRAY, ChatColor.RESET));
                event.getWhoClicked().getWorld().dropItemNaturally(event.getWhoClicked().getLocation(), card.get());
            }
        }
    }

    @EventHandler
    private void onSmelt(FurnaceSmeltEvent event) {
        String name = event.getResult().getType().name().toLowerCase();
        double roll = Math.random();
        double dropRate = getDropRate("smelt_item", name);
        System.out.println("The main roll for smelt_item." + name + ": " + roll + " / " + dropRate);
        if (roll < dropRate) {
            Optional<ItemStack> card = getCardToDrop("smelt_item." + name);
            card.ifPresent(itemStack -> event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), itemStack));
        }
    }

    @EventHandler
    private void onEnchant(EnchantItemEvent event) {
        String name = String.format("level_%s", event.getExpLevelCost());
        double roll = Math.random();
        double dropRate = getDropRate("enchant_item", name);
        System.out.println("The main roll for enchant_item." + name + ": " + roll + " / " + dropRate);
        if (roll < dropRate) {
            Optional<ItemStack> card = getCardToDrop("enchant_item." + name);
            if (card.isPresent()) {
                event.getEnchanter().sendMessage(String.format("%sA TCG card just dropped for you.%s", ChatColor.GRAY, ChatColor.RESET));
                event.getEnchantBlock().getWorld().dropItemNaturally(event.getEnchantBlock().getLocation(), card.get());
            }
        }
    }

    @EventHandler
    private void onFish(PlayerFishEvent event) {
        System.out.println("Fishing state: " + event.getState());
        System.out.println("exp dropped: " + event.getExpToDrop());
        if (event.getCaught() == null || !event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            return;
        }
        String name = event.getCaught().getType().name().toLowerCase();
        double roll = Math.random();
        double dropRate = getDropRate("fishing", name);
        System.out.println("The main roll for fishing." + name + ": " + roll + " / " + dropRate);
        if (roll < dropRate) {
            Optional<ItemStack> card = getCardToDrop("fishing." + name);
            if (card.isPresent()) {
                event.getPlayer().sendMessage(String.format("%sA TCG card just dropped for you.%s", ChatColor.GRAY, ChatColor.RESET));
                Item book = event.getPlayer().getWorld().dropItemNaturally(event.getCaught().getLocation(), card.get());
                Vector differenceVector = event.getPlayer().getLocation().toVector().subtract(book.getLocation().toVector()).normalize().multiply(1.5);
                book.setVelocity(differenceVector);
            }
        }
    }

    @EventHandler
    private void onMobKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getKiller() != null) {
            String name = getTrueVictimName(event);
            double roll = Math.random();
            double dropRate = getDropRate("mob_kill", name);
            System.out.println("The main roll for mob_kill." + name + ": " + roll + " / " + dropRate);
            if (roll < dropRate) {
                Optional<ItemStack> card = getCardToDrop("mob_kill." + name);
                if (card.isPresent()) {
                    entity.getKiller().sendMessage(String.format("%sA TCG card just dropped for you.%s", ChatColor.GRAY, ChatColor.RESET));
                    entity.getWorld().dropItemNaturally(entity.getLocation(), card.get());
                }
            }
        }
    }

    private Optional<ItemStack> getCardToDrop(String event) {
        List<CardEnum> possibleCardDrops = getEventExclusiveCards(event);
        if (possibleCardDrops.isEmpty()) {
            possibleCardDrops = getAllDroppableCards(event);
        }
        if (possibleCardDrops.isEmpty()) {
            return Optional.empty();
        }
        Optional<CardEnum> rolledCard = rollForCardBasedOnRarity(possibleCardDrops);
        return rolledCard.map(Collection::createCard);
    }

    private Optional<CardEnum> rollForCardBasedOnRarity(List<CardEnum> possibleCardDrops) {
        List<CardRarity> raritiesInDroppableCards = possibleCardDrops.stream()
                .map(c -> c.card().rarity())
                .distinct()
                .toList();
        System.out.println("Rarity in droppable cards: " + raritiesInDroppableCards);
        double totalRarityOdds = raritiesInDroppableCards.stream()
                .map(r -> getPlugin().config().dropOddsConfig().getDouble(r.name().toLowerCase(), 0))
                .reduce(0.0, Double::sum);
        System.out.println("Total rarity odds: " + totalRarityOdds);
        double roll = Math.random() * totalRarityOdds;
        System.out.println("The rarity roll: " + roll);
        int rarityIndex = 0;
        while (roll > 0) {
            Double rarityOdds = getPlugin().config().dropOddsConfig().getDouble(raritiesInDroppableCards.get(rarityIndex).name().toLowerCase());
            System.out.printf("RarityOdds for %s is %s%n", raritiesInDroppableCards.get(rarityIndex).name().toLowerCase(), rarityOdds);
            if (rarityOdds != 0.0 && roll < rarityOdds) {
                break;
            }
            roll -= rarityOdds;
            rarityIndex++;
            System.out.printf("Now the roll is %s and the index is %s%n", roll, rarityIndex);
        }
        int finalRarityIndex = rarityIndex;
        return randomFromList(
            possibleCardDrops.stream()
                .filter(c -> c.card().rarity().equals(raritiesInDroppableCards.get(finalRarityIndex)))
                .toList()
        );
    }

    private List<CardEnum> getAllDroppableCards(String event) {
        List<CardEnum> droppableCards = Arrays.stream(CardEnum.values())
                .filter(c -> !c.card().rarity().equals(CardRarity.COMMON))
                .filter(c -> isDroppableFromEvent(c, event))
                .toList();
        System.out.println("Droppable cards: " + droppableCards);
        return droppableCards;
    }

    private boolean isDroppableFromEvent(CardEnum cardEnum, String event) {
        List<String> eventsThatCanDropTheCard = (List<String>) getPlugin().config().cardDropRulesConfig().getList("only_dropped_by." + cardEnum.name());
        if (eventsThatCanDropTheCard == null || eventsThatCanDropTheCard.isEmpty()) {
            System.out.println(String.format("%s is droppable because it wasn't found in the only_dropped_by", cardEnum.name()));
            return true;
        }
        System.out.println(String.format("Is %s an allowed event for %s? %s", event, cardEnum.name(), eventsThatCanDropTheCard.contains(event)));
        return eventsThatCanDropTheCard.contains(event);
    }

    private List<CardEnum> getEventExclusiveCards(String event) {
        List<String> eventExclusiveCardNames = (List<String>) getPlugin().config().cardDropRulesConfig().getList("event_exclusively_drops." + event);
        List<CardEnum> eventExclusiveCards = new ArrayList<>();
        if (eventExclusiveCardNames == null) {
            System.out.println("No event exclusive cards for " + event);
            return eventExclusiveCards;
        }
        for (String cardName : eventExclusiveCardNames) {
            CardEnum cardEnum = CardEnum.fromString(cardName);
            if (cardEnum == null) {
                logger().warning(String.format("%s is not a valid card name in the card_drop_rules.yml file.", cardName));
            }
        }
        System.out.println("Event exclusive cards: " + eventExclusiveCards);
        return eventExclusiveCards;
    }

    private double getDropRate(String prefix, String name) {
        String yamlMobName = prefix + "." + name.toLowerCase();
        double dropRate = getPlugin().config().dropOddsConfig().getDouble(yamlMobName, -1);
        if (dropRate < 0) {
            dropRate = getPlugin().config().dropOddsConfig().getDouble(prefix + ".default", 0);
        }
        System.out.println("The drop rate: " + dropRate);
        return dropRate;
    }

    private String getTrueVictimName(EntityDeathEvent event) {
        String name = event.getEntityType().name().replaceAll(" ", "_");
        switch(name) {
            case "AXOLOTL":
                return "AXOLOTL." + ((Axolotl) event.getEntity()).getVariant();
            case "CAT":
                return "CAT." + ((Cat) event.getEntity()).getCatType();
            case "FOX":
                return "FOX." + ((Fox) event.getEntity()).getFoxType();
            case "SHEEP":
                return "SHEEP." + ((Sheep) event.getEntity()).getColor();
            case "TRADER_LLAMA":
                return "TRADER_LLAMA." + ((TraderLlama) event.getEntity()).getColor();
            case "HORSE":
                return "HORSE." + ((Horse) event.getEntity()).getColor();
            case "LLAMA":
                return "LLAMA." + ((Llama) event.getEntity()).getColor();
            case "MUSHROOM_COW":
                return "MUSHROOM_COW." + ((MushroomCow) event.getEntity()).getVariant();
            case "PANDA":
                return "PANDA." + ((Panda) event.getEntity()).getMainGene();
            case "PARROT":
                return "PARROT." + ((Parrot) event.getEntity()).getVariant();
            case "RABBIT":
                return "RABBIT." + ((Rabbit) event.getEntity()).getRabbitType();
            case "FROG":
                return "FROG." + ((Frog) event.getEntity()).getVariant();
            default:
                return name;
        }
    }
}