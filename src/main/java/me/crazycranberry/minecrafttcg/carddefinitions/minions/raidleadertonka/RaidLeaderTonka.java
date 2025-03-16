package me.crazycranberry.minecrafttcg.carddefinitions.minions.raidleadertonka;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionWithStaticEffect;
import org.bukkit.ChatColor;

import java.util.List;

public class RaidLeaderTonka extends MinionWithStaticEffect {
    public RaidLeaderTonka(MinionCardDefinition cardDef, MinionInfo minionInfo) {
        super(cardDef, minionInfo, RaidLeaderTonka::getTargets, RaidLeaderTonka::effectForTargets, RaidLeaderTonka::removeEffect, true);
    }

    public static List<Minion> getTargets(Minion m) {
        Minion minionBehind = m.minionInfo().stadium().getAllyMinionBehind(m.minionInfo().spot());
        return minionBehind == null ? List.of() : List.of(minionBehind);
    }

    public static void effectForTargets(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, 1);
        target.setStaticMaxHealthBonus(source, 1);
        target.giveRally(source);
    }

    public static void removeEffect(Minion source, Minion target) {
        target.setStaticStrengthBonus(source, 0);
        target.setStaticMaxHealthBonus(source, 0);
        target.removeRally(source);
    }

    @Override
    public String signDescription() {
        return String.format("%sBlock 1%s.\nMinion behind gets\n+1/+1 and %sRally%s.", ChatColor.BOLD, ChatColor.RESET, ChatColor.BOLD, ChatColor.RESET);
    }
}
