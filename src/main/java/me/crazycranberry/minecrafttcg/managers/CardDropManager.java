package me.crazycranberry.minecrafttcg.managers;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.config.CollectionConfigs;
import me.crazycranberry.minecrafttcg.model.Collection;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.CommonFunctions.randomFromList;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.MinecraftTCG.logger;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

public class CardDropManager implements Listener {
    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        String name = event.getBlockPlaced().getType().name().toLowerCase();
        double roll = Math.random();
        double dropRate = getDropRate("block_place", name);
        if (roll < dropRate) {
            Optional<CardEnum> card = getCardToDrop("block_place." + name);
            if (card.isPresent()) {
                sendDropMessage(event.getPlayer(), card.get().card());
                if (getPlugin().config().shouldAutoCollect(event.getPlayer().getName())) {
                    CollectionConfigs.addCardToCollection(event.getPlayer(), card.get());
                } else {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), Collection.createCard(card.get()));
                }
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
        if (roll < dropRate) {
            Optional<CardEnum> card = getCardToDrop("block_break." + name);
            if (card.isPresent()) {
                sendDropMessage(event.getPlayer(), card.get().card());
                if (getPlugin().config().shouldAutoCollect(event.getPlayer().getName())) {
                    CollectionConfigs.addCardToCollection(event.getPlayer(), card.get());
                } else {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), Collection.createCard(card.get()));
                }
            }
        }
    }

    @EventHandler
    private void onCraftItem(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player p) {
            String name = event.getRecipe().getResult().getType().name().toLowerCase();
            double roll = Math.random();
            double dropRate = getDropRate("craft_item", name);
            if (roll < dropRate) {
                Optional<CardEnum> card = getCardToDrop("craft_item." + name);
                if (card.isPresent()) {
                    sendDropMessage(p, card.get().card());
                    if (getPlugin().config().shouldAutoCollect(p.getName())) {
                        CollectionConfigs.addCardToCollection(p, card.get());
                    } else {
                        p.getWorld().dropItemNaturally(p.getLocation(), Collection.createCard(card.get()));
                    }
                }
            }
        }
    }

    @EventHandler
    private void onSmelt(FurnaceSmeltEvent event) {
        String name = event.getResult().getType().name().toLowerCase();
        double roll = Math.random();
        double dropRate = getDropRate("smelt_item", name);
        if (roll < dropRate) {
            Optional<CardEnum> card = getCardToDrop("smelt_item." + name);
            card.ifPresent(c -> event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), Collection.createCard(c)));
        }
    }

    @EventHandler
    private void onEnchant(EnchantItemEvent event) {
        String name = String.format("level_%s", event.getExpLevelCost());
        double roll = Math.random();
        double dropRate = getDropRate("enchant_item", name);
        if (roll < dropRate) {
            Optional<CardEnum> card = getCardToDrop("enchant_item." + name);
            if (card.isPresent()) {
                sendDropMessage(event.getEnchanter(), card.get().card());
                if (getPlugin().config().shouldAutoCollect(event.getEnchanter().getName())) {
                    CollectionConfigs.addCardToCollection(event.getEnchanter(), card.get());
                } else {
                    event.getEnchantBlock().getWorld().dropItemNaturally(event.getEnchantBlock().getLocation(), Collection.createCard(card.get()));
                }
            }
        }
    }

    @EventHandler
    private void onFish(PlayerFishEvent event) {
        if (event.getCaught() == null || !event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            return;
        }
        String name = event.getCaught().getType().name().toLowerCase();
        double roll = Math.random();
        double dropRate = getDropRate("fishing", name);
        if (roll < dropRate) {
            Optional<CardEnum> card = getCardToDrop("fishing." + name);
            if (card.isPresent()) {
                sendDropMessage(event.getPlayer(), card.get().card());
                if (getPlugin().config().shouldAutoCollect(event.getPlayer().getName())) {
                    CollectionConfigs.addCardToCollection(event.getPlayer(), card.get());
                } else {
                    Item book = event.getPlayer().getWorld().dropItemNaturally(event.getCaught().getLocation(), Collection.createCard(card.get()));
                    Vector differenceVector = event.getPlayer().getLocation().toVector().subtract(book.getLocation().toVector()).normalize().multiply(1.5);
                    book.setVelocity(differenceVector);
                }
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
            if (roll < dropRate) {
                Optional<CardEnum> card = getCardToDrop("mob_kill." + name);
                if (card.isPresent()) {
                    sendDropMessage(entity.getKiller(), card.get().card());
                    if (getPlugin().config().shouldAutoCollect(entity.getKiller().getName())) {
                        CollectionConfigs.addCardToCollection(entity.getKiller(), card.get());
                    } else {
                        entity.getWorld().dropItemNaturally(entity.getLocation(), Collection.createCard(card.get()));
                    }
                }
            }
        }
    }

    private void sendDropMessage(Player p, Card card) {
        TextComponent dropText = new TextComponent(String.format("%sA %s[%s]%s card has dropped for you.%s", GRAY, card.rarity().color(), card.cardName(), GRAY, RESET));
        String description = "";
        if (card instanceof MinionCardDefinition minionCard) {
            description += String.format("%s%s%s:%s %s❤%s:%s/%s\n",
                DARK_GREEN, minionCard.isFlying() ? "☁" : minionCard.isRanged() ? "\uD83C\uDFF9" : "🗡", RESET, minionCard.strength(), RED, RESET, minionCard.maxHealth(), minionCard.maxHealth());
        }
        description += card.cardDescription();
        dropText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(description)));
        p.spigot().sendMessage(dropText);
    }

    private Optional<CardEnum> getCardToDrop(String event) {
        List<CardEnum> possibleCardDrops = getEventExclusiveCards(event);
        if (possibleCardDrops.isEmpty()) {
            possibleCardDrops = getAllDroppableCards(event);
        }
        if (possibleCardDrops.isEmpty()) {
            return Optional.empty();
        }
        return rollForCardBasedOnRarity(possibleCardDrops);
    }

    private Optional<CardEnum> rollForCardBasedOnRarity(List<CardEnum> possibleCardDrops) {
        List<CardRarity> raritiesInDroppableCards = possibleCardDrops.stream()
                .map(c -> c.card().rarity())
                .distinct()
                .toList();
        double totalRarityOdds = raritiesInDroppableCards.stream()
                .map(r -> getPlugin().config().dropOddsConfig().getDouble(r.name().toLowerCase(), 0))
                .reduce(0.0, Double::sum);
        double roll = Math.random() * totalRarityOdds;
        int rarityIndex = 0;
        while (roll > 0) {
            double rarityOdds = getPlugin().config().dropOddsConfig().getDouble(raritiesInDroppableCards.get(rarityIndex).name().toLowerCase());
            if (rarityOdds != 0.0 && roll < rarityOdds) {
                break;
            }
            roll -= rarityOdds;
            rarityIndex++;
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
        return droppableCards;
    }

    private boolean isDroppableFromEvent(CardEnum cardEnum, String event) {
        List<String> eventsThatCanDropTheCard = (List<String>) getPlugin().config().cardDropRulesConfig().getList("only_dropped_by." + cardEnum.name());
        if (eventsThatCanDropTheCard == null || eventsThatCanDropTheCard.isEmpty()) {
            return true;
        }
        return eventsThatCanDropTheCard.contains(event);
    }

    private List<CardEnum> getEventExclusiveCards(String event) {
        List<String> eventExclusiveCardNames = (List<String>) getPlugin().config().cardDropRulesConfig().getList("event_exclusively_drops." + event);
        List<CardEnum> eventExclusiveCards = new ArrayList<>();
        if (eventExclusiveCardNames == null) {
            return eventExclusiveCards;
        }
        for (String cardName : eventExclusiveCardNames) {
            CardEnum cardEnum = CardEnum.fromString(cardName);
            if (cardEnum == null) {
                logger().warning(String.format("%s is not a valid card name in the card_drop_rules.yml file.", cardName));
            }
        }
        return eventExclusiveCards;
    }

    private double getDropRate(String prefix, String name) {
        String yamlMobName = prefix + "." + name.toLowerCase();
        double dropRate = getPlugin().config().dropOddsConfig().getDouble(yamlMobName, -1);
        if (dropRate < 0) {
            dropRate = getPlugin().config().dropOddsConfig().getDouble(prefix + ".default", 0);
        }
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
