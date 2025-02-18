package me.crazycranberry.minecrafttcg.carddefinitions.minions.raidleadertonka;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionWithStaticEffect;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.packleader.PackLeader;
import me.crazycranberry.minecrafttcg.model.Spot;
import org.bukkit.ChatColor;


import java.util.List;

import static me.crazycranberry.minecrafttcg.carddefinitions.CardEnum.RAID_LEADER_TONKA;

public class RaidLeaderTonka extends MinionWithStaticEffect {
    public RaidLeaderTonka(MinionInfo minionInfo) {
        super(RAID_LEADER_TONKA.card(), minionInfo, RaidLeaderTonka::getTargets, RaidLeaderTonka::effectForTargets, RaidLeaderTonka::removeEffect, true);
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
