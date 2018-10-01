package cosc345.AuralLearner.model;

public class PitchMatchingExerciseGrader extends Grader {
    public PitchMatchingExerciseGrader() {
        Note target = Note.getRandom(Note.C4_INDEX - 12, 4.0, Note.NoteLength.SEMIBREVE);
        this.playable = target;
    }
}
