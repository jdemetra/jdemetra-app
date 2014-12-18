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
package ec.nbdemetra.sa;

import ec.nbdemetra.ui.awt.ListenableBean;
import java.beans.PropertyChangeListener;
import org.openide.windows.TopComponent;

/**
 *
 * @author Philippe Charles
 */
public final class MultiProcessingController extends ListenableBean {

    public enum SaProcessingState {

        READY, STARTED, PENDING, DONE, CANCELLED;

        public boolean isFinished() {
            return this == DONE || this == CANCELLED ;
        }
    };

    public static final String SAPROCESSING_STATE_PROPERTY = "SaProcessingState";

    private SaProcessingState state;

    public MultiProcessingController() {
        this.state = state.READY;
    }

    public SaProcessingState getState() {
        return state;
    }

    public void setState(SaProcessingState state) {
        this.state = state;
        firePropertyChange(SAPROCESSING_STATE_PROPERTY, null, this.state); // force refreshing in all cases
    }
    
    public void dispose(){
        
        PropertyChangeListener[] ls = this.support.getPropertyChangeListeners();
        if (ls != null){
            for (int i=0; i<ls.length; ++i){
                this.support.removePropertyChangeListener(ls[i]);
            }
        }
    }
}
