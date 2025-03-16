package me.crazycranberry.minecrafttcg.carddefinitions.minions.cowardlybob;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.model.Column;
import me.crazycranberry.minecrafttcg.model.Wall;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

public class CowardlyBob extends Minion {
    public static Material MATERIAL = Material.CHISELED_SANDSTONE;

    public CowardlyBob(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public String signDescription() {
        return "When damaged,\nbuilds walls in\neach lane.";
    }

    @Override
    public void onDamageReceived(LivingEntity source, Integer damageReceived, Boolean wasProtected) {
        super.onDamageReceived(source, damageReceived, wasProtected);
        for (Column column : Column.values()) {
            this.minionInfo().stadium().setWall(column, new Wall(this.minionInfo().stadium(), column, MATERIAL, 2));
        }
    }
}
