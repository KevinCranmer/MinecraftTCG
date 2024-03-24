package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import me.crazycranberry.minecrafttcg.model.TurnPhase;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static me.crazycranberry.minecrafttcg.managers.StadiumManager.PLAYER_PROXY_ENTITY_TYPE;
import static me.crazycranberry.minecrafttcg.managers.StadiumManager.stadium;

public class CardUtils {
    public static List<Minion> minionsFromSpots(List<Spot> spots, Stadium stadium) {
        return spots.stream()
            .map(s -> s.minionRef().apply(stadium))
            .filter(Objects::nonNull)
            .toList();
    }

    public static void handleOverkillDamage(Minion damageReceiver, int damageRemaining, LivingEntity damager, boolean wasCombatAttack) {
        if (damageRemaining <= 0) {
            return;
        }
        int damageToOriginal = Math.min(damageRemaining, damageReceiver.health());
        damageRemaining = damageRemaining - damageToOriginal;
        Stadium stadium = damageReceiver.minionInfo().stadium();
        //Deal normal damage
        damageReceiver.onDamageReceived(damager, damageToOriginal, damageReceiver.isProtected());
        Optional<Minion> maybeDamagerMinion = stadium.minionFromEntity(damager);
        maybeDamagerMinion.ifPresent(minion -> minion.onDamageDealt(damageReceiver.minionInfo().entity(), damageToOriginal, wasCombatAttack, damageReceiver.isProtected()));
        //Get what's behind to do damage to them too
        LivingEntity targetBehindEntity = stadium.getEntityBehind(damageReceiver.minionInfo().spot());
        if (targetBehindEntity == null) {
            return;
        }
        Optional<Minion> targetBehind = stadium.minionFromEntity(targetBehindEntity);
        if (targetBehind.isEmpty() && targetBehindEntity.getType().equals(PLAYER_PROXY_ENTITY_TYPE)) {
            //Damage to player (a base case)
            Optional<Player> targetPlayer = stadium.getPlayerFromChicken(targetBehindEntity);
            if (targetPlayer.isEmpty() || damageRemaining <= 0) {
                return;
            }
            if (stadium.phase().equals(TurnPhase.COMBAT_PHASE)) {
                stadium.pendingDamageForPlayer(targetPlayer.get(), damageRemaining);
            } else {
                targetPlayer.get().damage(damageRemaining);
            }
            if (maybeDamagerMinion.isPresent()) {
                maybeDamagerMinion.get().onDamageDealt(targetPlayer.get(), damageRemaining, wasCombatAttack, false);
            }
            targetBehindEntity.damage(0);
        } else if (targetBehind.isPresent() && !targetBehind.get().isProtected()) {
            //Damage to minion (might have excess damage once again)
            handleOverkillDamage(targetBehind.get(), damageRemaining, damager, wasCombatAttack);
        }
    }

    public static void dealDamage(LivingEntity target, LivingEntity source, Stadium stadium, int damage) {
        if (target instanceof Player) {
            target.damage(3);
        } else {
            stadium.minionFromEntity(target).ifPresent(m -> m.onDamageReceived(source, damage, m.isProtected()));
        }
    }

    public static void swapTwoSpots(Stadium stadium, Spot spot1, Spot spot2) {
        Minion firstMinion = spot1.minionRef().apply(stadium);
        Minion secondMinion = spot2.minionRef().apply(stadium);
        spot1.minionSetRef().accept(stadium, secondMinion);
        spot2.minionSetRef().accept(stadium, firstMinion);
        if (firstMinion != null) {
            firstMinion.minionInfo().entity().teleport(stadium.locOfSpot(spot2));
            firstMinion.minionInfo().setSpot(spot2);
            firstMinion.setupGoals();
        }
        if (secondMinion != null) {
            secondMinion.minionInfo().entity().teleport(stadium.locOfSpot(spot1));
            secondMinion.minionInfo().setSpot(spot1);
            secondMinion.setupGoals();
        }
    }

    public static final List<EntityType> ANIMAL_TYPES = List.of(
        EntityType.AXOLOTL,
        EntityType.BEE,
        EntityType.CAMEL,
        EntityType.CAT,
        EntityType.CHICKEN,
        EntityType.DONKEY,
        EntityType.FOX,
        EntityType.FROG,
        EntityType.GOAT,
        EntityType.HORSE,
        EntityType.LLAMA,
        EntityType.MUSHROOM_COW,
        EntityType.MULE,
        EntityType.OCELOT,
        EntityType.PANDA,
        EntityType.PIG,
        EntityType.RABBIT,
        EntityType.SHEEP,
        EntityType.TRADER_LLAMA,
        EntityType.TURTLE,
        EntityType.WOLF,
        EntityType.BEE,
        EntityType.DOLPHIN,
        EntityType.POLAR_BEAR
    );

    public static final List<Spot> FRONT_ROW_SPOTS = List.of(
        Spot.RED_1_FRONT,
        Spot.RED_2_FRONT,
        Spot.BLUE_1_FRONT,
        Spot.BLUE_2_FRONT,
        Spot.GREEN_1_FRONT,
        Spot.GREEN_2_FRONT
    );
}
