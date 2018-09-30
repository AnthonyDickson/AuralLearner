package cosc345.app;

import org.junit.Test;

import cosc345.app.model.Note;
import cosc345.app.model.Scale;

import static org.junit.Assert.assertEquals;

/**
 * Tests the Scale class.
 */
public class ScaleUnitTest {
    @Test
    public void testMajorScale() {
        final Note[] expected = new Note[]{
                new Note("C4"),
                new Note("D4"),
                new Note("E4"),
                new Note("F4"),
                new Note("G4"),
                new Note("A4"),
                new Note("B4"),
                new Note("C5")
        };

        final Scale scale = new Scale(expected[0], Scale.ScaleType.MAJOR);

        for (int i = 0; i < expected.length; i++) {
            assertEquals(scale.notes.get(i), expected[i]);
        }
    }

    @Test
    public void testNaturalMinorScale() {
        final Note[] expected = new Note[]{
                new Note("C4"),
                new Note("D4"),
                new Note("Eb4"),
                new Note("F4"),
                new Note("G4"),
                new Note("Ab4"),
                new Note("Bb4"),
                new Note("C5")
        };

        final Scale scale = new Scale(expected[0], Scale.ScaleType.NATURAL_MINOR);

        for (int i = 0; i < expected.length; i++) {
            assertEquals(
                    scale.notes.get(i).getName(true),
                    expected[i].getName(true));
        }
    }
}

