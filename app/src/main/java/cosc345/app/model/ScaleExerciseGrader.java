package cosc345.app.model;

import android.util.Log;

public class ScaleExerciseGrader extends Grader {
    public Scale scale;

    public ScaleExerciseGrader() {
        super();

        Note rootNote = Note.getRandom(Note.C4_INDEX - 12, 4.0, Note.NoteLength.CROTCHET);
        Scale.ScaleType[] possibleScaleTypes = Scale.ScaleType.values();

        int i = Utilities.random.nextInt(possibleScaleTypes.length);

        scale = new Scale(rootNote, possibleScaleTypes[i], Note.NoteLength.MINIM);

        super.notes = scale.notes;
    }
}
