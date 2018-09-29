package cosc345.app.model;

public class ScaleExerciseGrader extends Grader {
    public Scale scale;

    static final Scale.ScaleType[] EASY_SCALES = {
            Scale.ScaleType.MAJOR,
            Scale.ScaleType.MAJOR_PENTATONIC
    };

    static final Scale.ScaleType[] MEDIUM_SCALES = {
            Scale.ScaleType.MAJOR,
            Scale.ScaleType.MAJOR_PENTATONIC,
            Scale.ScaleType.NATURAL_MINOR,
            Scale.ScaleType.MINOR_PENTATONIC
    };

    static final Scale.ScaleType[] HARD_SCALES = Scale.ScaleType.values();

    public ScaleExerciseGrader(Difficulty difficulty) {
        super();

        Note rootNote = Note.getRandom(Note.C4_INDEX - 12, 4.0, Note.NoteLength.CROTCHET);
        Scale.ScaleType[] possibleScaleTypes = {};

        switch (difficulty) {
            case EASY:
                possibleScaleTypes = EASY_SCALES;
                break;
            case MEDIUM:
                possibleScaleTypes = MEDIUM_SCALES;
                break;
            case HARD:
                possibleScaleTypes = HARD_SCALES;
                break;
        }

        int i = Utilities.random.nextInt(possibleScaleTypes.length);

        scale = new Scale(rootNote, possibleScaleTypes[i]);

        super.notes = scale.notes;
    }
}
