/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.desktop;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class MailtoBuilderTest {

    @Test
    public void testBuild() {
        MailtoBuilder b = new MailtoBuilder();
        assertEquals(
                b.clear().build().toString(),
                "mailto:?");
        assertEquals(
                b.clear().to("email@example.com").build().toString(),
                "mailto:?to=email%40example.com");
        assertEquals(
                b.clear().to("\"Tim Jones\" <tim@example.com>").build().toString(),
                "mailto:?to=%22Tim%20Jones%22%20%3Ctim%40example.com%3E");
    }
}
