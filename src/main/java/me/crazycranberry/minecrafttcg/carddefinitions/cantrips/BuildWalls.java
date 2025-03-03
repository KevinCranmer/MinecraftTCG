package me.crazycranberry.minecrafttcg.carddefinitions.cantrips;

import me.crazycranberry.minecrafttcg.carddefinitions.Card;
import me.crazycranberry.minecrafttcg.carddefinitions.CardRarity;
import me.crazycranberry.minecrafttcg.carddefinitions.MultiTargetCard;
import me.crazycranberry.minecrafttcg.carddefinitions.TargetRules;
import me.crazycranberry.minecrafttcg.model.Spot;
import me.crazycranberry.minecrafttcg.model.Stadium;
import me.crazycranberry.minecrafttcg.model.Wall;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class BuildWalls implements CantripCardDefinition, MultiTargetCard {
    public static Material MATERIAL = Material.BRICKS;

    @Override
    public Integer cost() {
        return 1;
    }

    @Override
    public String cardName() {
        return "Build Walls";
    }

    @Override
    public String cardDescription() {
        return "Build a wall on up to 2 lanes. These walls last 1 turn.";
    }

    @Override
    public CardRarity rarity() {
        return CardRarity.COMMON;
    }

    @Override
    public void onCast(Stadium stadium, Player caster, List<Spot> targets) {
        if (targets.get(0).column().equals(targets.get(1).column())) {
            stadium.setWall(targets.get(0).column(), new Wall(stadium, targets.get(0).column(), MATERIAL, 1));
        } else {
            stadium.setWall(targets.get(0).column(), new Wall(stadium, targets.get(0).column(), MATERIAL, 1));
            stadium.setWall(targets.get(1).column(), new Wall(stadium, targets.get(1).column(), MATERIAL, 1));
        }
    }

    @Override
    public List<TargetRules> targetRulesForExtraTargets() {
        return List.of(new TargetRules(true, true, false, true, true));
    }

    @Override
    public boolean isValidAdditionalTarget(Player p, Stadium stadium, Card card, List<Spot> targets, Spot newTarget) {
        return true;
    }

    @Override
    public TargetRules targetRules() {
        return new TargetRules(true, true, false, true, true);
    }

    @Override
    public Boolean canCastDuringCombat() {
        return false;
    }
}
