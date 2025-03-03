package me.crazycranberry.minecrafttcg.managers.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvLoader {
    public static void loadBlocksCsv(World w, InputStream input, Integer startX, Integer startY, Integer startZ) {
        List<List<String>> blocks = loadCsvList(new InputStreamReader(input));
        for (List<String> blockRecord : blocks) {
            int xOffset = Integer.parseInt(blockRecord.get(0)), yOffset = Integer.parseInt(blockRecord.get(1)), zOffset = Integer.parseInt(blockRecord.get(2));
            Material blockType = Material.getMaterial(blockRecord.get(3));
            String blockDataSerialized = blockRecord.get(4);
            Block block = w.getBlockAt(startX + xOffset, startY + yOffset, startZ + zOffset);
            block.setType(blockType);
            block.setBlockData(Bukkit.createBlockData(blockDataSerialized));
        }
    }
    public static void loadMobsCsv(World w, InputStream input, Integer startX, Integer startY, Integer startZ) {
        List<List<String>> mobs = loadCsvList(new InputStreamReader(input));
        for (List<String> mobRecord : mobs) {
            LivingEntity e = (LivingEntity) w.spawnEntity(new Location(w, startX + Integer.parseInt(mobRecord.get(0)), startY + Integer.parseInt(mobRecord.get(1)), startZ + Integer.parseInt(mobRecord.get(2))), EntityType.valueOf(mobRecord.get(3)));
            e.setSilent(true);
            e.setRemoveWhenFarAway(false);
            if (e instanceof Piglin piglin) piglin.setImmuneToZombification(true);
            if (e instanceof Hoglin hoglin) hoglin.setImmuneToZombification(true);
        }
    }

    private static List<List<String>> loadCsvList(InputStreamReader structureFileReader) {
        List<List<String>> blocks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(structureFileReader)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\t");
                blocks.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return blocks;
    }
}
