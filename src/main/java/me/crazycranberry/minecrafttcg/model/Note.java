package me.crazycranberry.minecrafttcg.model;

public record Note(Float pitch, Integer theTicToPlayTheNoteOn) {
    public Float pitch() {
        return pitch;
    }

    public Integer theTicToPlayTheNoteOn() {
        return theTicToPlayTheNoteOn;
    }
}
