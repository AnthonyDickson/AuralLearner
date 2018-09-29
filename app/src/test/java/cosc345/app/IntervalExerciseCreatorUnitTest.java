package cosc345.app;

import org.junit.Test;

import cosc345.app.model.Note;
import cosc345.app.model.Difficulty;
import cosc345.app.model.IntervalExerciseCreator;
import cosc345.app.model.Interval;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Tests the InteralExerciseCreator class.
 *
 * @author Rory Jackson
 */
public class IntervalExerciseCreatorUnitTest {
    @Test
    public void testIntervalGeneration(){
        IntervalExerciseCreator testExercise = new IntervalExerciseCreator(Difficulty.EASY);
        Interval testInterval = testExercise.interval;
        ArrayList<Note> testNotes = testInterval.getNotes();
        boolean firstValid, secondValid;
        if(testNotes.get(0).getFrequency() > 0){
            firstValid = true;
        } else {
            firstValid = false;
        }
        if(testNotes.get(1).getFrequency() > 0){
            secondValid = true;
        } else {
            secondValid = false;
        }
        assertEquals(true, firstValid);
        assertEquals(true, secondValid);
    }

}
