package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import org.bukkit.entity.EntityType;

import java.util.List;

public class CardUtils {
    public static void swapTwoSpots(Stadium stadium, Spot spot1, Spot spot2) {
        Minion firstMinion = spot1.minionRef().apply(stadium);
        Minion secondMinion = spot2.minionRef().apply(stadium);
        spot1.minionSetRef().accept(stadium, secondMinion, false);
        spot2.minionSetRef().accept(stadium, firstMinion, false);
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
