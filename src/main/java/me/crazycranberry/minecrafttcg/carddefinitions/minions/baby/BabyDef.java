package me.crazycranberry.minecrafttcg.carddefinitions.minions.baby;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;

import java.util.function.Consumer;

public class BabyDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 0;
    }

    @Override
    public String cardName() {
        return "Baby";
    }

    @Override
    public String cardDescription() {
        return "";
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
        return 1;
    }

    @Override
    public EntityType minionType() {
        return EntityType.VILLAGER;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return Baby.class;
    }

    @Override
    public Consumer<LivingEntity> entityAdjustment() {
        return e -> {
            ((Villager) e).setBaby();
            ((Breedable) e).setAgeLock(true);
        };
    }
}
