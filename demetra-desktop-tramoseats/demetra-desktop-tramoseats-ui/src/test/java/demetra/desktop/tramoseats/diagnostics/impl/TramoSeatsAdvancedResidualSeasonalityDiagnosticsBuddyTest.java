/*
 * Copyright 2022 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.tramoseats.diagnostics.impl;

import demetra.desktop.Config;
import org.junit.jupiter.api.Test;

/**
 *
 * @author palatej
 */
public class TramoSeatsAdvancedResidualSeasonalityDiagnosticsBuddyTest {
    
    public TramoSeatsAdvancedResidualSeasonalityDiagnosticsBuddyTest() {
    }

    @Test
    public void testAll(){
        TramoSeatsAdvancedResidualSeasonalityDiagnosticsBuddy buddy=new TramoSeatsAdvancedResidualSeasonalityDiagnosticsBuddy();
        Config config = buddy.getConfig();
        buddy.setConfig(config);
        buddy.createFactory();
//        System.out.println(buddy.getDisplayName());
        buddy.reset();
    }
    
}
