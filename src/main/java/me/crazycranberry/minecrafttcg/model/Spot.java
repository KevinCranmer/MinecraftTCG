package me.crazycranberry.minecrafttcg.model;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static me.crazycranberry.minecrafttcg.model.Stadium.BLUE_MATERIAL;
import static me.crazycranberry.minecrafttcg.model.Stadium.GREEN_MATERIAL;
import static me.crazycranberry.minecrafttcg.model.Stadium.RED_MATERIAL;

public enum Spot {
    RED_A(new Vector(20, 1, 1), RED_MATERIAL, false, true, Stadium::redAEntityStatic, Stadium::setRedAEntityStatic),
    RED_D(new Vector(17, 1, 1), RED_MATERIAL, false, true, Stadium::redDEntityStatic, Stadium::setRedDEntityStatic),
    RED_1(new Vector(9, 1, 1), RED_MATERIAL, true, true, Stadium::red1EntityStatic, Stadium::setRed1EntityStatic),
    RED_4(new Vector(6, 1 ,1), RED_MATERIAL, true, true, Stadium::red4EntityStatic, Stadium::setRed4EntityStatic),
    BLUE_B(new Vector(20, 1, 5), BLUE_MATERIAL, false, true, Stadium::blueBEntityStatic, Stadium::setBlueBEntityStatic),
    BLUE_E(new Vector(17, 1, 5), BLUE_MATERIAL, false, true, Stadium::blueEEntityStatic, Stadium::setBlueEEntityStatic),
    BLUE_2(new Vector(9, 1, 5), BLUE_MATERIAL, true, true, Stadium::blue2EntityStatic, Stadium::setBlue2EntityStatic),
    BLUE_5(new Vector(6, 1 ,5), BLUE_MATERIAL, true, true, Stadium::blue5EntityStatic, Stadium::setBlue5EntityStatic),
    GREEN_C(new Vector(20, 1, 9), GREEN_MATERIAL, false, true, Stadium::greenCEntityStatic, Stadium::setGreenCEntityStatic),
    GREEN_F(new Vector(17, 1, 9), GREEN_MATERIAL, false, true, Stadium::greenFEntityStatic, Stadium::setGreenFEntityStatic),
    GREEN_3(new Vector(9, 1, 9), GREEN_MATERIAL, true, true, Stadium::green3EntityStatic, Stadium::setGreen3EntityStatic),
    GREEN_6(new Vector(6, 1, 9), GREEN_MATERIAL, true, true, Stadium::green6EntityStatic, Stadium::setGreen6EntityStatic),
    PLAYER_1_OUTLOOK(new Vector(2, 8, 5), Material.BIRCH_PLANKS, true, false, Stadium::player1Static, null),
    PLAYER_2_OUTLOOK(new Vector(24, 8, 5), Material.COBBLESTONE, false, false, Stadium::player2Static, null),
    PLAYER_1_SMACK_ZONE(new Vector(2, 8, 5), Material.BIRCH_PLANKS, true, false, Stadium::player1Static, null),
    PLAYER_2_SMACK_ZONE(new Vector(24, 8, 5), Material.COBBLESTONE, false, false, Stadium::player2Static, null);

    private final Vector offset;
    private final Material material;
    private final Boolean isPlayer1Spot;
    private final Boolean isSummonableSpot;
    private final Function<Stadium, LivingEntity> entityRef;
    private final BiConsumer<Stadium, LivingEntity> entitySetRef;

    Spot(Vector offset, Material material, Boolean isPlayer1Spot, Boolean isSummonableSpot, Function<Stadium, LivingEntity> entityRef, BiConsumer<Stadium, LivingEntity> entitySetRef) {
        this.offset = offset;
        this.material = material;
        this.isPlayer1Spot = isPlayer1Spot;
        this.isSummonableSpot = isSummonableSpot;
        this.entityRef = entityRef;
        this.entitySetRef = entitySetRef;
    }

    public Vector offset() {
        return offset;
    }

    public Material material() {
        return material;
    }

    public Boolean isPlayer1Spot() {
        return isPlayer1Spot;
    }

    public Boolean isSummonableSpot() {
        return isSummonableSpot;
    }

    public Function<Stadium, LivingEntity> entityRef() {
        return entityRef;
    }

    public BiConsumer<Stadium, LivingEntity> entitySetRef() {
        return entitySetRef;
    }
}
