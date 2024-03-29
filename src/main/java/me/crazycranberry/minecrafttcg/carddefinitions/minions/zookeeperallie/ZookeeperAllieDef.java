package me.crazycranberry.minecrafttcg.carddefinitions.minions.zookeeperallie;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class ZookeeperAllieDef implements MinionCardDefinition {
    @Override
    public Integer cost() {
        return 2;
    }

    @Override
    public String cardName() {
        return "Zookeeper Allie";
    }

    @Override
    public String cardDescription() {
        return String.format("This card enters with +1/+1 for every %sanimal%s on the battlefield.", ChatColor.BOLD, ChatColor.RESET);
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public Integer strength() {
        return 0;
    }

    @Override
    public Integer maxHealth() {
        return 0;
    }

    @Override
    public EntityType minionType() {
        return EntityType.CAT;
    }

    @Override
    public Class<? extends Minion> minionClass() {
        return ZookeeperAllie.class;
    }
}
