package me.crazycranberry.minecrafttcg.carddefinitions.minions.captainpiggie;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import org.bukkit.entity.Player;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.CAPTAIN_PIGGIE;

public class CaptainPiggie extends Minion {
    public CaptainPiggie(MinionInfo minionInfo) {
        super(CAPTAIN_PIGGIE.card(), minionInfo);
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
