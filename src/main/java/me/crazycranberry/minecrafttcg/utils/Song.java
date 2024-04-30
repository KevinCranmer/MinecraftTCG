package me.crazycranberry.minecrafttcg.utils;

import me.crazycranberry.minecrafttcg.model.Note;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.List;

import static me.crazycranberry.minecrafttcg.MinecraftTCG.getPlugin;
import static org.bukkit.Sound.BLOCK_NOTE_BLOCK_HARP;

public class Song {
    int tickProgress = 0;
    int noteProgress = 0;
    int taskId = 0;
    private final List<Note> notes;
    private final Sound sound;
    private final Location location;

    public Song(List<Note> notes, Sound sound, Location location) {
        this.notes = notes;
        this.sound = sound;
        this.location = location;
    }

    public void play() {
        this.tickProgress = 0;
        this.noteProgress = 0;
        this.taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            if (this.noteProgress >= this.notes.size()) {
                Bukkit.getScheduler().cancelTask(this.taskId);
                return;
            }
            this.notes.stream()
                .filter(n -> n.theTicToPlayTheNoteOn() == this.tickProgress)
                .forEach(n -> {
                    this.location.getWorld().playSound(this.location, this.sound, 1, n.pitch());
                    noteProgress++;
                });
            tickProgress++;
        }, 0 /*<-- the initial delay */, 1 /*<-- the interval */).getTaskId();
    }
}
