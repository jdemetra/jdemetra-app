/*
 * Copyright 2018 National Bank of Belgium
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
package internal.ui.components;

import ec.nbdemetra.ui.awt.ActionMaps;
import ec.util.chart.swing.Charts;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ActionMap;

/**
 *
 * @author Philippe Charles
 */
public final class OpenOnDoubleClick extends MouseAdapter {

    private final ActionMap am;

    public OpenOnDoubleClick(ActionMap am) {
        this.am = am;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!Charts.isPopup(e) && Charts.isDoubleClick(e)) {
            ActionMaps.performAction(am, HasTsCollectionCommands.OPEN_ACTION, e);
        }
    }
}
