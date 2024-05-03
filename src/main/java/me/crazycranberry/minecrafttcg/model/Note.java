package me.crazycranberry.minecrafttcg.model;

public record Note(Float pitch, Integer theTicToPlayTheNoteOn) {
    public Float pitch() {
        return pitch;
    }

    public Integer theTicToPlayTheNoteOn() {
        return theTicToPlayTheNoteOn;
    }

    public static class Octave1 {
        public static final Float Fsharp = 0.5f;
        public static final Float G = 0.529732f;
        public static final Float Gsharp = 0.561231f;
        public static final Float A = 0.594604f;
        public static final Float Asharp = 0.629961f;
        public static final Float B = 0.667420f;
        public static final Float C = 0.707107f;
        public static final Float Csharp = 0.749154f;
        public static final Float D = 0.793701f;
        public static final Float Dsharp = 0.840896f;
        public static final Float E = 0.890899f;
        public static final Float F = 0.943874f;
    }

    public static class Octave2 {
        public static final Float Fsharp = 1.0f;
        public static final Float G = 1.059463f;
        public static final Float Gsharp = 1.122462f;
        public static final Float A = 1.189207f;
        public static final Float Asharp = 1.259921f;
        public static final Float B = 1.334840f;
        public static final Float C = 1.414214f;
        public static final Float Csharp = 1.498307f;
        public static final Float D = 1.587401f;
        public static final Float Dsharp = 1.681793f;
        public static final Float E = 1.781797f;
        public static final Float F = 1.887749f;
        public static final Float Fsharp2 = 2.0f;
    }
}
