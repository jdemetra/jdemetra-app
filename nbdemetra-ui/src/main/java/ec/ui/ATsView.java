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
package ec.ui;

import demetra.ui.TsManager;
import ec.tss.Ts;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.datatransfer.TssTransferSupport;
import ec.ui.interfaces.IColorSchemeAble;
import ec.ui.interfaces.ITsView;
import static ec.ui.interfaces.ITsView.TS_PROPERTY;
import ec.util.chart.ColorScheme;
import javax.annotation.Nullable;
import javax.swing.TransferHandler;

/**
 *
 * @author Demortier Jeremy
 */
public abstract class ATsView extends ATsControl implements ITsView, IColorSchemeAble {

    // PROPERTIES
    protected Ts m_ts;

    public ATsView() {
        this.m_ts = null;

        enableProperties();

        TsManager.getDefault().addUpdateListener(this::checkTsUpdate);
    }

    private void checkTsUpdate(TsMoniker moniker) {
        if (moniker.equals(m_ts.getMoniker())) {
            firePropertyChange(TS_PROPERTY, null, m_ts);
        }
    }

    private void enableProperties() {
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case TS_PROPERTY:
                    onTsChange();
                    break;
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    abstract protected void onTsChange();
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Override
    public Ts getTs() {
        return m_ts;
    }

    @Override
    public void setTs(Ts ts) {
        Ts old = this.m_ts;
        this.m_ts = ts;
        firePropertyChange(TS_PROPERTY, old, this.m_ts);
    }

    @Override
    public ColorScheme getColorScheme() {
        return themeSupport.getLocalColorScheme();
    }

    @Override
    public void setColorScheme(ColorScheme theme) {
        themeSupport.setLocalColorScheme(theme);
    }
    //</editor-fold>

    @Override
    public void dispose() {
        TsManager.getDefault().removeUpdateListener(this::checkTsUpdate);
        super.dispose();
    }

    @Nullable
    protected TsInformation getTsInformation() {
        return m_ts != null ? m_ts.toInfo(TsInformationType.Data) : null;
    }

    public class TsTransferHandler extends TransferHandler {

//        @Override
//        public int getSourceActions(JComponent c) {
//            return COPY;
//        }
//
//        @Override
//        protected Transferable createTransferable(JComponent c) {
//            return ATsView.this.transferableOnSelection();
//        }
        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return TssTransferSupport.getDefault().canImport(support.getDataFlavors());
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            Ts ts = TssTransferSupport.getDefault().toTs(support.getTransferable());
            if (ts != null) {
                ts.query(TsInformationType.All);
                ATsView.this.setTs(ts);
                return true;
            }
            return false;
        }
    }
}
