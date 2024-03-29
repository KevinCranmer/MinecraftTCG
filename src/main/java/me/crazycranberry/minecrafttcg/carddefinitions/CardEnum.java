package me.crazycranberry.minecrafttcg.carddefinitions;

import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.Adrenaline;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.BaronsGrind;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.FireBlast;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.GhettoWarArmy;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.Heal;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.LightningStrike;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.MysticSurge;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.Protect;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.RadicalShift;
import me.crazycranberry.minecrafttcg.carddefinitions.cantrips.Switch;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.abee.ABeeDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.abeekeeper.ABeeKeeperDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.aggressivebandit.AggressiveBanditDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.apprenticearcher.ApprenticeArcherDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.backmasseuse.BackMasseuseDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.bacon.BaconDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.bigtim.BigTimDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.billy.BillyDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.borsharak.BorsharakDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.brunswick.BrunswickDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.bulldozer.BullDozerDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.couchpotato.CouchPotatoDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.dingyskeleton.DingySkeletonDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.emovitro.EmoVitroDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.fluffynecromancer.FluffyNecromancerDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.glassjaw.GlassJawDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.happynarwhale.HappyNarwhaleDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.healwitch.HealWitchDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.heavyslammer.HeavySlammerDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.hungryzombie.HungryZombieDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.kevinthesmith.KevinTheSmithDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.lavaimp.LavaImpDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.liljim.LilJimDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.lurkingthief.LurkingThiefDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.mikethestoryteller.MikeTheStoryTellerDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.mom.MomDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.ninjamac.NinjaMacDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.recklessstinker.RecklessStinkerDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.scaredduelist.ScaredDuelistDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.yellowpanther.YellowPantherDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.packleader.PackLeaderDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.sewerzombie.SewerZombieDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.skiturtle.SkiTurtleDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.supportivezombie.SupportiveZombieDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.theduke.TheDukeDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.theknapper.TheKnapperDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.unstablepyro.UnstablePyroDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.yousefssoulmender.YousefsSoulMenderDef;
import me.crazycranberry.minecrafttcg.carddefinitions.minions.zookeeperallie.ZookeeperAllieDef;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.DigDeeper;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.DoubleKill;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.FlingSmallPoops;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.FreshWaterBottle;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.KeepItSchwifty;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.PlantCrops;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.SinfulSeduction;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.TheVoid;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.ToxicSpikes;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.Unalive;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.ZooExpedition;
import me.crazycranberry.minecrafttcg.carddefinitions.spells.awakenthealpha.AwakenTheAlpha;

public enum CardEnum {
    // Cantrips
    ADRENALINE(new Adrenaline()),
    BARONS_GRIND(new BaronsGrind()),
    FIRE_BLAST(new FireBlast()),
    GHETTO_WAR_ARMY(new GhettoWarArmy()),
    HEAL(new Heal()),
    LIGHTNING_STRIKE(new LightningStrike()),
    MYSTIC_SURGE(new MysticSurge()),
    PROTECT(new Protect()),
    RADICAL_SHIFT(new RadicalShift()),
    SWITCH(new Switch()),

    // Minions
    A_BEE(new ABeeDef()),
    A_BEE_KEEPER(new ABeeKeeperDef()),
    AGGRESSIVE_BANDIT(new AggressiveBanditDef()),
    APPRENTICE_ARCHER(new ApprenticeArcherDef()),
    BACK_MASSEUSE(new BackMasseuseDef()),
    BACON(new BaconDef()),
    BIG_TIM(new BigTimDef()),
    BILLY(new BillyDef()),
    BORSHARAK(new BorsharakDef()),
    BRUNSWICK(new BrunswickDef()),
    BULLDOZER(new BullDozerDef()),
    COUCH_POTATO(new CouchPotatoDef()),
    DINGY_SKELETON(new DingySkeletonDef()),
    EMO_VITRO(new EmoVitroDef()),
    FLUFFY_NECROMANCER(new FluffyNecromancerDef()),
    GLASS_JAW(new GlassJawDef()),
    HAPPY_NARWHALE(new HappyNarwhaleDef()),
    HEAL_WITCH(new HealWitchDef()),
    HEAVY_SLAMMER(new HeavySlammerDef()),
    HUNGRY_ZOMBIE(new HungryZombieDef()),
    KEVIN_THE_SMITH(new KevinTheSmithDef()),
    LAVA_IMP(new LavaImpDef()),
    LIL_JIM(new LilJimDef()),
    LURKING_THIEF(new LurkingThiefDef()),
    MIKE_THE_STORY_TELLER(new MikeTheStoryTellerDef()),
    MOM(new MomDef()),
    NINJA_MAC(new NinjaMacDef()),
    PACK_LEADER(new PackLeaderDef()),
    RECKLESS_STINKER(new RecklessStinkerDef()),
    SCARED_DUELIST(new ScaredDuelistDef()),
    SEWER_ZOMBIE(new SewerZombieDef()),
    SKI_TURTLE(new SkiTurtleDef()),
    SUPPORTIVE_ZOMBIE(new SupportiveZombieDef()),
    THE_DUKE(new TheDukeDef()),
    THE_KNAPPER(new TheKnapperDef()),
    UNSTABLE_PYRO(new UnstablePyroDef()),
    YELLOW_PANTHER(new YellowPantherDef()),
    YOUSEFS_SOUL_MENDER(new YousefsSoulMenderDef()),
    ZOOKEEPER_ALLIE(new ZookeeperAllieDef()),

    // Spells
    AWAKEN_THE_ALPHA(new AwakenTheAlpha()),
    DIG_DEEPER(new DigDeeper()),
    DOUBLE_KILL(new DoubleKill()),
    FLING_SMALL_POOPS(new FlingSmallPoops()),
    FRESH_WATER_BOTTLE(new FreshWaterBottle()),
    KEEP_IT_SCHWIFTY(new KeepItSchwifty()),
    PLANT_CROPS(new PlantCrops()),
    SINFUL_SEDUCTION(new SinfulSeduction()),
    THE_VOID(new TheVoid()),
    TOXIC_SPIKES(new ToxicSpikes()),
    UNALIVE(new Unalive()),
    ZOO_EXPEDITION(new ZooExpedition());

    final Card card;

    CardEnum(Card minionCard) {
        this.card = minionCard;
    }

    public Card card(){
        return card;
    }

    public static CardEnum fromString(String s) {
        for (CardEnum cardEnum : CardEnum.values()) {
            if (cardEnum.name().equals(s)) {
                return cardEnum;
            }
        }
        return null;
    }
}
