package cosc345.app;

import org.junit.Test;

import cosc345.app.model.VoiceRecognitionManager;

import static org.junit.Assert.assertFalse;

/**
 * Tests the InteralExerciseCreator class.
 *
 * @author Rory Jackson
 */
public class VoiceRecognitionManagerUnitTest {
    @Test
    public void testVRM(){
        VoiceRecognitionManager tVRM = VoiceRecognitionManager.getInstance();
        boolean isNull = false;
        if(tVRM.getInstance() == null){
            isNull = true;
        }
        assertFalse(isNull);
    }
}
