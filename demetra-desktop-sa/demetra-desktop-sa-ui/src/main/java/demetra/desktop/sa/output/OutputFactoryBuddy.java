/*
 * Copyright 2016 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package demetra.desktop.sa.output;

import demetra.desktop.NamedService;
import demetra.desktop.Persistable;
import demetra.sa.SaOutputFactory;
import demetra.sa.SaProcessingFactory;
import java.awt.Image;
import java.util.List;
import org.openide.nodes.Sheet;

/**
 * Makes the link between actual output factories and the gui, through specialized nodes
 * @author Jean Palate
 */
public interface OutputFactoryBuddy extends NamedService, Persistable {

    @Override
    String getName();
    
    AbstractOutputNode createNode();
    
    AbstractOutputNode createNodeFor(Object properties);

    @Override
    default String getDisplayName() {
        return getName();
    }

    @Override
    default Image getIcon(int type, boolean opened) {
        return null;
    }

    @Override
    default Sheet createSheet() {
        return new Sheet();
    }
}
