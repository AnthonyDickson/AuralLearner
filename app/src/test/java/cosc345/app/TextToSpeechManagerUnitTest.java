package cosc345.app;

import org.junit.Test;

import cosc345.app.model.TextToSpeechManager;

import static org.junit.Assert.assertFalse;

/**
 * Tests the TextToSpeechManager class.
 *
 * @author Rory Jackson
 */
public class TextToSpeechManagerUnitTest {
    @Test
    public void testTTS(){
        TextToSpeechManager tTSM = TextToSpeechManager.getInstance();
        boolean isNull = false;
        if(tTSM.getInstance() == null){
            isNull = true;
        }
        assertFalse(isNull);
    }
}
