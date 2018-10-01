package cosc345.AuralLearner;

import org.junit.Test;

import cosc345.AuralLearner.model.Note;

import static org.junit.Assert.assertEquals;

/**
 * Tests the Note class.
 *
 * @author Anthony Dickson
 */
public class NoteUnitTest {
    @Test
    public void noteFromFrequency() {
        Note a4 = new Note(440.0);
        assertEquals(4, a4.getOctave());
        assertEquals(0, a4.getCents());
        assertEquals("A4", a4.getName());
        assertEquals("A4", a4.toString());

        Note c4 = new Note(261.63);
        assertEquals(4, c4.getOctave());
        assertEquals(0, c4.getCents());
        assertEquals("C4", c4.getName());

        Note aSharp3 = new Note(227);
        assertEquals(3, aSharp3.getOctave());
        assertEquals(-46, aSharp3.getCents());
        assertEquals("A#3", aSharp3.getName(false));
        assertEquals("Bb3", aSharp3.getName(true));

        Note e5 = new Note(663.23);
        assertEquals(5, e5.getOctave());
        assertEquals(10, e5.getCents());
        assertEquals("E5", e5.getName(false));
        assertEquals("E5", e5.getName(true));

        Note d4 = new Note(295.41);
        assertEquals(10, d4.getCents());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTooLowFrequency() {
        new Note(0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTooHighFrequency() {
        new Note(9999);
    }

    @Test
    public void noteFromString() {
        Note a4 = new Note("A4");
        assertEquals(4, a4.getOctave());
        assertEquals(0, a4.getCents());
        assertEquals("A4", a4.getName());
        assertEquals("A4", a4.toString());
        assertEquals(440.0, a4.getFrequency(), 1e-9);

        Note fSharp5 = new Note("F#5");
        assertEquals(5, fSharp5.getOctave());
        assertEquals(0, fSharp5.getCents());
        assertEquals(739.99, fSharp5.getFrequency(), 1e-1);
        assertEquals("F#5", fSharp5.getName(false));
        assertEquals("Gb5", fSharp5.getName(true));

        Note gFlat5 = new Note("Gb5");
        assertEquals("F#5", gFlat5.getName(false));
        assertEquals("Gb5", gFlat5.getName(true));
        assertEquals(fSharp5.getName(), gFlat5.getName());
    }

    @Test
    public void noteSetDuration() {
        Note note = new Note("C4");
        assertEquals(Note.NoteLength.CROTCHET, note.getNoteLength());
        assertEquals(500, note.getDuration());

        note.setNoteLength(Note.NoteLength.MINIM);
        assertEquals(Note.NoteLength.MINIM, note.getNoteLength());
        assertEquals(1000, note.getDuration());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadStringInput() {
        new Note("A@OY*");
    }
}
