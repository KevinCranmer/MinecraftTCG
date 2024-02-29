package me.crazycranberry.minecrafttcg.carddefinitions.minions.fluffynecromancer;

import me.crazycranberry.minecrafttcg.carddefinitions.CardEnum;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.Minion;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.MinionInfo;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.Llama;

public class FluffyNecromancer extends Minion {
    public FluffyNecromancer(MinionInfo minionInfo) {
        super(CardEnum.FLUFFY_NECROMANCER, minionInfo);
    }

    @Override
    public void onEnter() {
        ((Llama) this.minionInfo().entity()).setBaby();
        ((Breedable) this.minionInfo().entity()).setAgeLock(true);
    }
}
