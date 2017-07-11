/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.desktop.impl;

import ec.util.desktop.Desktop;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class XdgDesktopTest {

    @Test
    public void testNoOp() throws IOException {
        Desktop desktop = new XdgDesktop(ZSystem.noOp(), XdgConfig.noOp());
        for (Desktop.KnownFolder o : Desktop.KnownFolder.values()) {
            assertThat(desktop.getKnownFolderPath(o)).isNull();
        }
    }
}
