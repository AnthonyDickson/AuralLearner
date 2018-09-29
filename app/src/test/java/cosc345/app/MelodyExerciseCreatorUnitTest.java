package cosc345.app;

import org.junit.Test;

import cosc345.app.model.Note;
import cosc345.app.model.Difficulty;
import cosc345.app.model.MelodyExerciseCreator;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the InteralExerciseCreator class.
 *
 * @author Rory Jackson
 */
public class MelodyExerciseCreatorUnitTest {
    @Test
    public void testMelodyGeneration(){
        MelodyExerciseCreator testMEC = new MelodyExerciseCreator(Difficulty.EASY);
        ArrayList<Note> testNotes = testMEC.pickMelody(Difficulty.EASY);
        for(int i=0; i<testNotes.size(); i++){
            assertTrue(testNotes.get(i).getFrequency() > 0);
        }
    }

    @Test
    public void testScaleGeneration(){
        MelodyExerciseCreator testMEC = new MelodyExerciseCreator(Difficulty.EASY);
        Note testRootA = new Note("A3");
        Note[] testScale = testMEC.scaleGenerator(testRootA);
        assertEquals(testScale[0].getName(), "A3");
        assertEquals(testScale[1].getName(), "B3");
        assertEquals(testScale[2].getName(), "C#4");
        assertEquals(testScale[3].getName(), "D4");
        assertEquals(testScale[4].getName(), "E4");
        assertEquals(testScale[5].getName(), "F#4");
        assertEquals(testScale[6].getName(), "G#4");
        assertEquals(testScale[7].getName(), "A4");
        Note testRootAb = new Note("Ab3");
        Note[] testScaleFlat = testMEC.scaleGenerator(testRootA);
        assertEquals(testScaleFlat[0].getName(), "Ab3");
        assertEquals(testScaleFlat[1].getName(), "Bb3");
        assertEquals(testScaleFlat[2].getName(), "C4");
        assertEquals(testScaleFlat[3].getName(), "Db4");
        assertEquals(testScaleFlat[4].getName(), "Eb4");
        assertEquals(testScaleFlat[5].getName(), "F4");
        assertEquals(testScaleFlat[6].getName(), "G4");
        assertEquals(testScaleFlat[7].getName(), "Ab4");
        
    }
}
