package cosc345.AuralLearner.model;

import android.util.Log;

import java.util.ArrayList;

public class PitchMatchingExerciseGrader extends Grader {
    public PitchMatchingExerciseGrader() {
        Note target = Note.getRandom(Note.C4_INDEX - 12, 4.0, Note.NoteLength.SEMIBREVE);
        this.playable = target;
        super.notes = new ArrayList<>();
        super.notes.add(target);
    }

    @Override
    protected double calculateScore() {
        Note detectedNote = detectedNotes.get(0);
        Note referenceNote = notes.get(0);

        double centDist = Note.centDistance(detectedNote, referenceNote);
        Log.d(LOG_TAG, String.format("Reference Pitch: %f Hz; Detected Pitch: %f Hz; Distance in Cents: %f",
                referenceNote.getFrequency(),
                detectedNote.getFrequency(),
                centDist));

        if (Math.abs(centDist) > 100) return 0.0;

        return 100.0 - Math.abs(centDist);
    }
}
