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
package ec.nbdemetra.spreadsheet;

import ec.util.various.swing.BasicFileViewer;
import ec.util.various.swing.BasicSwingLauncher;
import java.awt.Component;
import java.util.concurrent.Callable;
import java.util.logging.Level;

/**
 *
 * @author Philippe Charles
 */
final class SpreadSheetProfilerDemo implements Callable<Component> {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(new SpreadSheetProfilerDemo())
                .title("SpreadSheet Profiler")
                .logLevel(Level.FINE)
                .launch();
    }

    @Override
    public Component call() throws Exception {
        BasicFileViewer result = new BasicFileViewer();
        result.setFileHandler(new SpreadSheetBasicFileHandler());
        return result;
    }
}
