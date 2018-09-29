package cosc345.app;

import org.junit.Test;

import cosc345.app.model.Note;
import cosc345.app.model.Grader;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

/**
 * Tests the Grader class.
 *
 * @author Rory Jackson
 */
public class GraderUnitTest {
    @Test
    public void testConstructor() {
        Grader empty = new Grader();
        assertEquals(0.0, empty.getScore(), 0.001);
        Note a4 = new Note("A4");
        Note a5 = new Note("A5");
        Note f5 = new Note("F5");
        Note fSharp5 = new Note("F#5");
        ArrayList<Note> noteArray = new ArrayList<Note>();
        noteArray.add(a4);
        noteArray.add(a5);
        noteArray.add(f5);
        noteArray.add(fSharp5);
        Grader testGrader = new Grader(noteArray);
        assertEquals(noteArray, testGrader.notes);
    }
}
