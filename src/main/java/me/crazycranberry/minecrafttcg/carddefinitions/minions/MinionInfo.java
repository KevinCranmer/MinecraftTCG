package me.crazycranberry.minecrafttcg.carddefinitions.minions;

import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MinionInfo {
    private final Stadium stadium;
    private Spot spot;
    private LivingEntity entity;
    private Player master;

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

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public Player master() {
        return master;
    }

    public void setSpot(Spot spot) {
        this.spot = spot;
    }

    public void setMaster(Player p) {
        this.master = p;
    }
}
