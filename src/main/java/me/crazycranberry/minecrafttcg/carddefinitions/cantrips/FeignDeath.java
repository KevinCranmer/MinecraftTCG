package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import me.crazycranberry.minecrafttcg.events.DuelCloseEvent;
import me.crazycranberry.minecrafttcg.events.MinionDiedEvent;
import me.crazycranberry.minecrafttcg.events.TurnEndEvent;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;
import static me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionCardDefinition.summonMinion;

public class FeignDeath implements CantripCardDefinition, Listener {
    private static final int numParticlesPerSpawning = 2;
    private static final int tickIntervalForParticles = 4;
    private static final Map<Minion, Integer> minionsFeigningDeath = new HashMap<>();

    @Override
    public Integer cost() {
        return 2;
    }

    @Override
    public String cardName() {
        return "Feign Death";
    }

    @Override
    public String cardDescription() {
        return "Until end of turn, when targeted minion dies, resummon it.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.UNCOMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        Minion targetedMinion = stadium.minionFromSpot(targets.get(0));
        if (targetedMinion == null) {
            return;
        }
        if (minionsFeigningDeath.isEmpty()) {
            Bukkit.getPluginManager().registerEvents(this, getPlugin());
        }
        minionsFeigningDeath.put(targetedMinion, particleTask(targetedMinion));
    }

    private Integer particleTask(Minion minion) {
        return Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            minion.minionInfo().entity().getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, minion.minionInfo().entity().getEyeLocation(), numParticlesPerSpawning, 0.5, 0.25, 0.5, 0.5);
        }, 0 /*<-- the initial delay */, tickIntervalForParticles /*<-- the interval */).getTaskId();
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, false, false, false);
    }

    @Override
    public Boolean canCastDuringCombat() {
        return true;
    }

    @EventHandler
    private void onTurnEnd(TurnEndEvent event) {
        removeAllFromList(event.getStadium());
    }

    @EventHandler
    private void onDuelFinished(DuelCloseEvent event) {
        removeAllFromList(event.stadium());
    }

    @EventHandler
    private void onMinionDeath(MinionDiedEvent event) {
        if (!minionsFeigningDeath.containsKey(event.minion())) {
            return;
        }
        effectEnded(event.minion());
        MinionInfo info = event.minion().minionInfo();
        info.entity().getWorld().playSound(info.entity(), Sound.ITEM_TOTEM_USE, 1, 1);
        info.entity().getWorld().spawnParticle(Particle.GUST, info.entity().getLocation(), 3, 0.3, 0, 0.3);
        newAnimationStarted(event.minion(), 1);
        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            summonMinion(info.spot(), info.stadium(), info.master(), event.minion().getClass(), event.minion().cardDef());
            oneAnimationFinished(info.stadium(), info.master());
        }, 6);
    }

    private void removeAllFromList(Stadium stadium) {
        List<Minion> list = minionsFeigningDeath.keySet().stream().filter(m -> m.minionInfo().stadium().equals(stadium)).toList();
        list.forEach(this::effectEnded);
    }

    private void effectEnded(Minion minion) {
        Integer taskId = minionsFeigningDeath.get(minion);
        if (taskId == null) {
            return;
        }
        Bukkit.getScheduler().cancelTask(taskId);
        minionsFeigningDeath.remove(minion);
        if (minionsFeigningDeath.isEmpty()) {
            HandlerList.unregisterAll(this);
        }
    }
}
