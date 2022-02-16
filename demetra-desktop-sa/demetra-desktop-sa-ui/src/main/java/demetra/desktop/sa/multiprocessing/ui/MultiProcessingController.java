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
package demetra.desktop.sa.multiprocessing.ui;

import demetra.desktop.design.SwingProperty;
import demetra.desktop.beans.PropertyChangeSource;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Philippe Charles
 */
public final class MultiProcessingController implements PropertyChangeSource.WithWeakListeners {

    public enum SaProcessingState {

        READY, STARTED, PENDING, DONE, CANCELLED;

        public boolean isFinished() {
            return this == DONE || this == CANCELLED;
        }
    }

    @SwingProperty
    public static final String SA_PROCESSING_STATE_PROPERTY = "saProcessingState";

    @lombok.experimental.Delegate(types = PropertyChangeSource.class)
    private final PropertyChangeSupport broadcaster = new PropertyChangeSupport(this);

    private SaProcessingState saProcessingState;

    public MultiProcessingController() {
        this.saProcessingState = SaProcessingState.READY;
    }

    public SaProcessingState getSaProcessingState() {
        return saProcessingState;
    }

    public void setSaProcessingState(SaProcessingState state) {
        this.saProcessingState = state;
        broadcaster.firePropertyChange(SA_PROCESSING_STATE_PROPERTY, null, this.saProcessingState); // force refreshing in all cases
    }

    public void dispose() {

        PropertyChangeListener[] ls = broadcaster.getPropertyChangeListeners();
        if (ls != null) {
            for (PropertyChangeListener l : ls) {
                broadcaster.removePropertyChangeListener(l);
            }
        }
    }
}
