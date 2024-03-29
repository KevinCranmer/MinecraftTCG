package me.crazycranberry.minecrafttcg.carddefinitions.minions.lavaimp;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;

import java.util.function.Consumer;

public class LavaImpDef implements MinionCardDefinition {
    public static final int DAMAGE_TO_CONTROLLER = 3;

    @Override
    public Integer cost() {
        return 1;
    }

    @Override
    public String cardName() {
        return "Lava Imp";
    }

    @Override
    public String cardDescription() {
        return String.format("When this minion enters, it deals %s damage to its controller.", DAMAGE_TO_CONTROLLER);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public Integer strength() {
        return 3;
    }

    @Override
    public Integer maxHealth() {
        return 2;
    }

    @Override
    public EntityType minionType() {
        return EntityType.ZOMBIFIED_PIGLIN;
    }

    @Override
    public Consumer<LivingEntity> entityAdjustment() {
        return e -> ((PigZombie) e).setBaby();
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return LavaImp.class;
    }
}
