package me.crazycranberry.minecrafttcg.carddefinitions;

public interface SpellOrCantripCardDefinition extends Card {
    public boolean targetsMinion();
    public boolean targetsPlayer();
    public boolean targetsEmptySpots();
}
