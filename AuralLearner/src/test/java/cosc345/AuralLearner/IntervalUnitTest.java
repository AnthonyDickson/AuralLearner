package cosc345.AuralLearner;

import org.junit.Test;

import cosc345.AuralLearner.model.Interval;
import cosc345.AuralLearner.model.Intervals;
import cosc345.AuralLearner.model.Note;

import static org.junit.Assert.assertEquals;

/**
 * Tests the interval class.
 */
public class IntervalUnitTest {
    @Test
    public void createFromInterval() {
        Interval p1 = new Interval(new Note("C4"), Intervals.P1);
        assertEquals(Intervals.P1, p1.interval);
        assertEquals("Perfect unison (C4, C4)", p1.toString());
        assertEquals("Perfect unison", p1.name.toString());
        assertEquals("Perfect unison", p1.name.getFullName());
        assertEquals("P1", p1.name.getShortName());
        assertEquals("C4", p1.root.getName());
        assertEquals("C4", p1.other.getName());
        assertEquals(0, p1.size);

        Interval a4 = new Interval(new Note("C4"), Intervals.A4);
        assertEquals(Intervals.A4, a4.interval);
        assertEquals("Augmented fourth", a4.name.toString());
        assertEquals("F#4", a4.other.getName());
        assertEquals(6, a4.size);
    }

    @Test
    public void createFromNotes() {
        Interval interval = new Interval(new Note("C4"), new Note("G4"));
        assertEquals(Intervals.P5, interval.interval);
        assertEquals("Perfect fifth", interval.name.toString());
        assertEquals(7, interval.size);
    }

    @Test
    public void createInversions() {
        Interval inverted = new Interval(new Note("C4"), Intervals.P5, true);
        assertEquals(Intervals.P4, inverted.interval);
        assertEquals("Perfect fourth (G4, C4)", inverted.toString());
        assertEquals(5, inverted.size);
    }

    @Test
    public void outOfRangeIntervals() {
        Interval interval = new Interval(new Note("C4"), new Note("G5"));
        assertEquals(Intervals.P5, interval.interval);
        assertEquals("Perfect fifth", interval.name.toString());

        Interval interval2 = new Interval(new Note("G5"), new Note("C4"));
        assertEquals(Intervals.P5, interval2.interval);
        assertEquals("Perfect fifth", interval2.name.toString());
    }
}
