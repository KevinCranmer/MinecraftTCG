package me.crazycranberry.minecrafttcg.carddefinitions.minions.liljim;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Zombie;

import java.util.function.Consumer;

public class LilJimDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 5;
    }

    @Override
    public String cardName() {
        return "Lil Jim";
    }

    @Override
    public String cardDescription() {
        return "";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.LEGENDARY;
    }

    @Override
    public Integer strength() {
        return 9;
    }

    @Override
    public Integer maxHealth() {
        return 1;
    }

    @Override
    public EntityType minionType() {
        return EntityType.ZOMBIE;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return LilJim.class;
    }

    @Override
    public String signDescription() {
        return "";
    }

    @Override
    public Consumer<LivingEntity> entityAdjustment() {
        return e -> ((Zombie) e).setBaby();
    }
}
