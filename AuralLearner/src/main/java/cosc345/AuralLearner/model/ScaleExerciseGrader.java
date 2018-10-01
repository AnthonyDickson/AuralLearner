package cosc345.AuralLearner.model;

public class ScaleExerciseGrader extends Grader {
    public ScaleExerciseGrader() {
        super();

        Note rootNote = Note.getRandom(Note.C4_INDEX - 12, 4.0, Note.NoteLength.CROTCHET);
        Scale.ScaleType[] possibleScaleTypes = Scale.ScaleType.values();

        int i = Utilities.random.nextInt(possibleScaleTypes.length);

        Scale scale = new Scale(rootNote, possibleScaleTypes[i], Note.NoteLength.MINIM);

        super.notes = scale.notes;
        this.playable = scale;
    }
}
