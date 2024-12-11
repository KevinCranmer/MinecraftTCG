package me.crazycranberry.minecrafttcg.carddefinitions.minions.farmer;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;

import java.util.function.Consumer;

import static org.bukkit.entity.Villager.Type.SAVANNA;

public class FarmerDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 1;
    }

    @Override
    public String cardName() {
        return "Farmer";
    }

    @Override
    public String cardDescription() {
        return String.format("Draws a card whenever a friendly %sAnimal%s enters the battlefield.", ChatColor.BOLD, ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public Integer strength() {
        return 1;
    }

    @Override
    public Integer maxHealth() {
        return 3;
    }

    @Override
    public EntityType minionType() {
        return EntityType.VILLAGER;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Farmer.class;
    }

    @Override
    public Consumer<LivingEntity> entityAdjustment() {
        return e -> {
            ((Villager) e).setProfession(Villager.Profession.FARMER);
            ((Villager) e).setVillagerType(SAVANNA);
        };
    }
}
