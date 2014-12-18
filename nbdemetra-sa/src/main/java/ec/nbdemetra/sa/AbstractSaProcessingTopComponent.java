/*
 * Copyright 2013 National Bank of Belgium
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
package ec.nbdemetra.sa;

import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.ui.WorkspaceTopComponent;
import ec.tss.sa.SaProcessing;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;

/**
 *
 * @author Jean Palate
 */
public abstract class AbstractSaProcessingTopComponent extends WorkspaceTopComponent<MultiProcessingDocument> implements MultiViewElement, MultiViewDescription {

    protected MultiProcessingController controller;

    public AbstractSaProcessingTopComponent() {
        this(null, new MultiProcessingController());
    }

    AbstractSaProcessingTopComponent(WorkspaceItem<MultiProcessingDocument> document, MultiProcessingController controller) {
        super(document);
        String txt = document == null ? "" : document.getDisplayName();
        setName(txt);
        setToolTipText(txt + " view");
        this.controller = controller;
        controller.addPropertyChangeListener(MultiProcessingController.SAPROCESSING_STATE_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // forward event
                firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
                onSaProcessingStateChange();
            }
        });
    }

    public MultiProcessingController getController() {
        return controller;
    }

    public SaProcessing getCurrentProcessing() {
        return getDocument().getElement().getCurrent();
    }

    public SaProcessing getInitialProcessing() {
        return getDocument().getElement().getInitial();
    }

    public MultiProcessingController.SaProcessingState getState() {
        return controller != null ? controller.getState() : MultiProcessingController.SaProcessingState.DONE;
    }

    protected void onSaProcessingStateChange() {
        this.getToolbarRepresentation().updateUI();
        this.getVisualRepresentation().updateUI();
    }

    //<editor-fold defaultstate="collapsed" desc="MultiViewElement">
    @Override
    public void componentOpened() {
        super.componentOpened();
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
        controller = null;
        // TODO add custom code on component closing
    }

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    @Override
    public void componentActivated() {
        super.componentActivated();
    }

    @Override
    public void componentDeactivated() {
        super.componentDeactivated();
    }

    @Override
    public void componentHidden() {
        super.componentHidden();
    }

    @Override
    public void componentShowing() {
        super.componentShowing();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="MultiViewDescription">
    @Override
    public MultiViewElement createElement() {
        return this;
    }

    @Override
    public String preferredID() {
        return super.preferredID();
    }
    //</editor-fold>    

    @Override
    protected String getContextPath() {
        return MultiProcessingManager.CONTEXTPATH;
    }

}
