package ec.ui;

import ec.tss.Ts;
import ec.tss.TsEvent;
import ec.tss.TsFactory;
import ec.tss.TsInformationType;
import ec.tss.datatransfer.TssTransferSupport;
import ec.ui.interfaces.IColorSchemeAble;
import ec.ui.interfaces.ITsView;
import ec.util.chart.ColorScheme;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

/**
 *
 * @author Demortier Jeremy
 */
public abstract class ATsView extends ATsControl implements ITsView, IColorSchemeAble {

    // PROPERTIES
    protected Ts m_ts;
    // OTHER
    protected final TsFactoryObserver tsFactoryObserver;

    public ATsView() {
        this.m_ts = null;
        this.tsFactoryObserver = new TsFactoryObserver();

        addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String p = evt.getPropertyName();
                if (p.equals(TS_PROPERTY)) {
                    onTsChange();
                }
            }
        });

        TsFactory.instance.addObserver(tsFactoryObserver);
    }

    // EVENT HANDLERS > 
    abstract protected void onTsChange();
    // < EVENT HANDLERS 

    // GETTERS/SETTERS >
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
    // < GETTERS/SETTERS 

    @Override
    public void dispose() {
        TsFactory.instance.deleteObserver(tsFactoryObserver);
        super.dispose();
    }

    protected class TsFactoryObserver implements Observer {

        final AtomicBoolean dirty = new AtomicBoolean(false);

        @Override
        public void update(Observable o, Object arg) {
            if (arg instanceof TsEvent) {
                TsEvent event = (TsEvent) arg;
                if (event.isSeries() && event.ts.equals(m_ts)) {
                    dirty.set(true);
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            if (dirty.getAndSet(false)) {
                                firePropertyChange(TS_PROPERTY, null, m_ts);
                            }
                        }
                    });
                }
            }
        }
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
