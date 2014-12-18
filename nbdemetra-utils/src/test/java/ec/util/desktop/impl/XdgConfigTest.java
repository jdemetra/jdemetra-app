/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.util.desktop.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Philippe Charles
 */
public class XdgConfigTest {

    @Test
    public void testParseConfig() throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        Map<String, String> env = new HashMap<>();
        env.put("HOME", tmpDir);

        try (InputStream fis = XdgConfigTest.class.getResourceAsStream("user-dirs.dirs")) {
            XdgConfig config = XdgConfig.parseConfig(fis, env);
            Assert.assertEquals(8, config.keySet().size());
            Assert.assertEquals(new File(tmpDir, "Documents"), new File(config.get(XdgDesktop.DOCUMENTS_DIR)));
        }
    }

}
