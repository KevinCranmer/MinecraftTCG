package me.crazycranberry.minecrafttcg.carddefinitions.spells;

import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import net.minecraft.world.level.block.TntBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.newAnimationStarted;
import static me.crazycranberry.minecrafttcg.carddefinitions.AnimatedCardHelper.oneAnimationFinished;

public class TNT implements SpellCardDefinition {
    private static final int TARGET_DAMAGE = 6;
    private static final int NEARBY_DAMAGE = 3;

    @Override
    public Integer cost() {
        return 5;
    }

    @Override
    public String cardName() {
        return "TNT";
    }

    @Override
    public String cardDescription() {
        return "Deal 6 damage at targeted spot. Also deals 3 damage to adjacent minions.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.RARE;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        newAnimationStarted(stadium, caster, 1);
        TNTPrimed tnt = caster.getWorld().spawn(stadium.locOfSpot(targets.get(0)), TNTPrimed.class);
        Bukkit.getPluginManager().registerEvents(new TNTListener(stadium, caster, targets.get(0), tnt), getPlugin());
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, false, true, true);
    }

    private static class TNTListener implements Listener {
        private final Stadium stadium;
        private final Player caster;
        private final Spot spot;
        private final TNTPrimed tnt;

        public TNTListener(Stadium stadium, Player caster, Spot spot, TNTPrimed tnt) {
            this.stadium = stadium;
            this.caster = caster;
            this.spot = spot;
            this.tnt = tnt;
        }

        @EventHandler
        private void onEssplood(EntityExplodeEvent event) {
            if (event.getEntity().equals(tnt)) {
                // Gather minions
                Minion mainTarget = stadium.minionFromSpot(spot);
                List<Minion> nearbyMinions = new ArrayList<>();
                stadium.adjacentSpots(spot).stream()
                        .map(stadium::minionFromSpot)
                        .filter(Objects::nonNull)
                        .forEach(nearbyMinions::add);
                Optional.ofNullable(stadium.getAllyMinionInFront(spot)).ifPresent(nearbyMinions::add);
                Optional.ofNullable(stadium.getAllyMinionBehind(spot)).ifPresent(nearbyMinions::add);

                // Damage minions
                mainTarget.onDamageReceived(caster, TARGET_DAMAGE, mainTarget.isProtected());
                nearbyMinions.forEach(m -> m.onDamageReceived(caster, NEARBY_DAMAGE, m.isProtected()));

                // Stop the animation
                oneAnimationFinished(stadium, caster);

                //Deregister this listener
                HandlerList.unregisterAll(this);
            }
        }
    }
}
