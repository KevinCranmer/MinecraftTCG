package me.crazycranberry.minecrafttcg.carddefinitions.minions.captainpiggie;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import org.bukkit.entity.Player;

public class CaptainPiggie extends Minion {
    public CaptainPiggie(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
    }

    @Override
    public String signDescription() {
        return "Heals master when\nhealed. +1/+1 when\nmaster heals";
    }

    @Override
    public void onHeal(Integer healFor) {
        super.onHeal(healFor);
        this.minionInfo().stadium().healPlayer(this.minionInfo().master(), healFor);
    }

    @Override
    public void onPlayerHealed(Player playerHealed, int healedFor) {
        super.onPlayerHealed(playerHealed, healedFor);
        if (playerHealed.equals(this.minionInfo().master())) {
            this.addPermanentStrength(1);
            this.setMaxHealth(this.maxHealth() + 1);
            this.setHealthNoHealTrigger(this.health() + 1);
        }
    }
}
