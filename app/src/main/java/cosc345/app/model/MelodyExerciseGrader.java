package cosc345.app.model;

public class MelodyExerciseGrader extends Grader {
    public MelodyExerciseGrader(Difficulty difficulty) {
        super();

        int melodyRange;
        int melodyLength;
        int maxStep;
        double reverseProbability;

        if (difficulty == Difficulty.EASY) {
            melodyRange = 3;
            melodyLength = 4;
            reverseProbability = 0;
            maxStep = 3;
        } else if (difficulty == Difficulty.MEDIUM) {
            melodyRange = Utilities.random.nextInt(3) + 3;
            reverseProbability = 0.2;
            melodyLength = 8;
            maxStep = 3;
        } else {
            melodyRange = Utilities.random.nextInt(3) + 5;
            melodyLength = 8;
            reverseProbability = 0.3;
            maxStep = 3;
        }

        Note startingNote = Note.getRandom(Note.C4_INDEX - 12, 4.0, Note.NoteLength.CROTCHET);
        Scale scale = new Scale(startingNote, Scale.ScaleType.MAJOR, Note.NoteLength.MINIM);
        Melody melody = new Melody(scale, melodyLength, maxStep, melodyRange, reverseProbability);

        super.notes = melody.notes;
        this.playable = melody;
    }
}
