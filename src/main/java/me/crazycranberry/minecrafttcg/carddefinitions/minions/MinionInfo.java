package me.crazycranberry.minecrafttcg.carddefinitions.minions;

import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MinionInfo {
    private final Stadium stadium;
    private Spot spot;
    private final LivingEntity entity;
    private final Player master;

    public MinionInfo(Stadium stadium, Spot spot, LivingEntity entity, Player master) {
        this.stadium = stadium;
        this.spot = spot;
        this.entity = entity;
        this.master = master;
    }

    public Stadium stadium() {
        return stadium;
    }

    public Spot spot() {
        return spot;
    }

    public LivingEntity entity() {
        return entity;
    }

    public Player master() {
        return master;
    }

    public void setSpot(Spot spot) {
        this.spot = spot;
    }
}
