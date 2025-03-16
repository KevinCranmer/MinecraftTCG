package me.crazycranberry.minecrafttcg.carddefinitions.minions.skiturtle;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SkiTurtle extends Minion {
    public SkiTurtle(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo);
        this.minionInfo().entity().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 7200, 2));
    }

    @Override
    public void onCombatEnd() {
        super.onCombatEnd();
        Minion ally = this.minionInfo().stadium().getAllyMinionInFront(this.minionInfo().spot());
        if (ally != null && ally.health() < ally.maxHealth()) {
            ally.onHeal(ally.maxHealth() - ally.health());
        }
    }

    @Override
    public String signDescription() {
        return String.format("%sRally%s%nHeals ally%nin front of it", ChatColor.BOLD, ChatColor.RESET);
    }
}
