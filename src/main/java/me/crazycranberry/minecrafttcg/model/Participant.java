package me.crazycranberry.minecrafttcg.model;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;

public class Participant {
    private final Player player;
    private final ParticipantStartingWorldConfig startingWorldConfig;


    public Participant(Player p) {
        this.player = p;
        this.startingWorldConfig = new ParticipantStartingWorldConfig(p.getLocation(), p.getGameMode(), p.getHealth(), p.getHealthScale(), p.getAbsorptionAmount(), p.getRemainingAir(), p.getFoodLevel(), p.getSaturation(), p.getExhaustion(), p.getActivePotionEffects(), p.getCompassTarget(), p.getBedSpawnLocation(), p.getAllowFlight(), p.isInvulnerable(), p.getNoDamageTicks(), p.getLevel(), p.getExp(), p.getFireTicks(), p.getInventory());
    }

    public Player getPlayer() {
        return player;
    }

    public ParticipantStartingWorldConfig getStartingWorldConfig() {
        return startingWorldConfig;
    }

    public static class ParticipantStartingWorldConfig {
        private final Location location;
        private final GameMode gameMode;
        private final Double health;
        private final Double healthScale;
        private final Double absorptionAmount;
        private final int remainingAir;
        private final int foodLevel;
        private final float saturation;
        private final float exhaustion;
        private final Collection<PotionEffect> activePotionEffects;
        private final Location compassTarget;
        private final Location bedSpawnLocation;
        private final boolean allowFlight;
        private final boolean invulnerable;
        private final int noDamageTicks;
        private final int level;
        private final float exp;
        private final int fireTicks;
        private final PlayerInventory inventory;

        public ParticipantStartingWorldConfig(Location location, GameMode gameMode, Double health, Double healthScale, Double absorptionAmount, int remainingAir, int foodLevel, float saturation, float exhaustion, Collection<PotionEffect> activePotionEffects, Location compassTarget, Location bedSpawnLocation, boolean allowFlight, boolean invulnerable, int noDamageTicks, int level, float exp, int fireTicks, PlayerInventory inventory) {
            this.location = location;
            this.gameMode = gameMode;
            this.health = health;
            this.healthScale = healthScale;
            this.absorptionAmount = absorptionAmount;
            this.remainingAir = remainingAir;
            this.foodLevel = foodLevel;
            this.saturation = saturation;
            this.exhaustion = exhaustion;
            this.activePotionEffects = activePotionEffects;
            this.compassTarget = compassTarget;
            this.bedSpawnLocation = bedSpawnLocation;
            this.allowFlight = allowFlight;
            this.invulnerable = invulnerable;
            this.noDamageTicks = noDamageTicks;
            this.level = level;
            this.exp = exp;
            this.fireTicks = fireTicks;
            this.inventory = inventory;
        }

        public Location getLocation() {
            return location;
        }

        public GameMode getGameMode() {
            return gameMode;
        }

        public Double getHealth() {
            return health;
        }

        public Double getHealthScale() {
            return healthScale;
        }

        public Double getAbsorptionAmount() {
            return absorptionAmount;
        }

        public int getRemainingAir() {
            return remainingAir;
        }

        public int getFoodLevel() {
            return foodLevel;
        }

        public float getSaturation() {
            return saturation;
        }

        public float getExhaustion() {
            return exhaustion;
        }

        public Collection<PotionEffect> getActivePotionEffects() {
            return activePotionEffects;
        }

        public Location getCompassTarget() {
            return compassTarget;
        }

        public Location getBedSpawnLocation() {
            return bedSpawnLocation;
        }

        public boolean isAllowFlight() {
            return allowFlight;
        }

        public boolean isInvulnerable() {
            return invulnerable;
        }

        public int getNoDamageTicks() {
            return noDamageTicks;
        }

        public int getLevel() {
            return level;
        }

        public float getExp() {
            return exp;
        }

        public int getFireTicks() {
            return fireTicks;
        }

        public PlayerInventory getInventory() {
            return inventory;
        }
    }
}
