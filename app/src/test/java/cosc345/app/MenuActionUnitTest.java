package cosc345.app;

import org.junit.Test;

import cosc345.app.model.MenuAction;

import static org.junit.Assert.assertEquals;

/**
 * Tests the MenuAction class.
 *
 * @author Rory Jackson
 */
public class MenuActionUnitTest {
    @Test
    public void testMenuAction(){
        MenuAction testMenuAction = new MenuAction("test", () -> dummyMethod());
        assertEquals(testMenuAction.activation, "test");
    }

    public void dummyMethod(){
        System.out.println("I'm just here to be used by a unit test");
    }
}
