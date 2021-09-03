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
package demetra.desktop.components;

import demetra.desktop.beans.PropertyChangeBroadcaster;
import demetra.desktop.beans.PropertyChangeSource;
import demetra.desktop.components.parts.HasTsCollection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
public final class TsSelectionBridge {

    public static final String TS_SELECTION_PROPERTY = "selection";

    @lombok.NonNull
    private final PropertyChangeBroadcaster broadcaster;

    private final PropertyChangeBridge propertyChangeBridge = new PropertyChangeBridge();
    private final ListSelectionBridge listSelectionBridge = new ListSelectionBridge();

    public <C extends HasTsCollection & PropertyChangeSource> void register(C c) {
        c.addPropertyChangeListener(HasTsCollection.TS_SELECTION_MODEL_PROPERTY, propertyChangeBridge);
        c.getTsSelectionModel().addListSelectionListener(listSelectionBridge);
    }

    private final class PropertyChangeBridge implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            ((ListSelectionModel) evt.getOldValue()).removeListSelectionListener(listSelectionBridge);
            ((ListSelectionModel) evt.getNewValue()).addListSelectionListener(listSelectionBridge);
        }
    }

    private final class ListSelectionBridge implements ListSelectionListener {

        private boolean enabled = true;

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting() && enabled) {
                enabled = false;
                broadcaster.firePropertyChange(TS_SELECTION_PROPERTY, null, e);
                enabled = true;
            }
        }
    }
}
