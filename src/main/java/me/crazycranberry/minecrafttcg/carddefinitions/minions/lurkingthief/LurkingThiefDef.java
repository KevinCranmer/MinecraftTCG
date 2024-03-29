package me.crazycranberry.minecrafttcg.carddefinitions.minions.lurkingthief;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;

import java.util.function.Consumer;

public class LurkingThiefDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 3;
    }

    @Override
    public String cardName() {
        return "Lurking Thief";
    }

    @Override
    public String cardDescription() {
        return "When this minion deals damage to a player, refresh that many mana crystals";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public Integer strength() {
        return 2;
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
    public Consumer<LivingEntity> entityAdjustment() {
        return e -> ((Villager) e).setBaby();
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return LurkingThief.class;
    }
}
