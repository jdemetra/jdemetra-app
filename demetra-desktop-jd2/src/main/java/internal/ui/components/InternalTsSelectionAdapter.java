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

import demetra.ui.components.parts.HasTsCollection;
import ec.util.list.swing.JLists;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
public class InternalTsSelectionAdapter implements ListSelectionListener {

    @lombok.NonNull
    private final HasTsCollection outer;

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected int indexToModel(int index) {
        return index;
    }

    protected int indexToView(int index) {
        return index;
    }

    protected void selectionChanged(ListSelectionModel back) {
        ListSelectionModel front = outer.getTsSelectionModel();
        JLists.setSelectionIndexStream(front, JLists.getSelectionIndexStream(back).map(this::indexToModel));
    }

    public void changeSelection(ListSelectionModel back) {
        ListSelectionModel front = outer.getTsSelectionModel();
        JLists.setSelectionIndexStream(back, JLists.getSelectionIndexStream(front).map(this::indexToView));
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (enabled && !e.getValueIsAdjusting()) {
            enabled = false;
            selectionChanged((ListSelectionModel) e.getSource());
            enabled = true;
        }
    }
}
