/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view;

import ec.nbdemetra.ui.NbComponents;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.interfaces.IDisposable;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

/**
 *
 * @author pcuser
 */
public class SpectralView extends JComponent implements IDisposable {

    private final JSplitPane m_splitter;
    AutoRegressiveSpectrumView m_arView;
    PeriodogramView m_pView;

    public SpectralView() {
        m_splitter = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT);
        m_arView = new AutoRegressiveSpectrumView();
        m_pView = new PeriodogramView();
        build();
    }

    public void set(TsData series, boolean wn) {
        int freq = series.getFrequency().intValue();
        m_pView.setLimitVisible(wn);
        m_pView.setData("Periodogram", freq, series.getValues());
        m_arView.setData("Auto-regressive spectrum", freq, series.getValues());
    }
    
    public void setDifferencingOrder(int order){
        m_pView.setDifferencingOrder(order);
        m_arView.setDifferencingOrder(order);
    }

    public void setDifferencingLag(int lag){
        m_pView.setDifferencingLag(lag);
        m_arView.setDifferencingLag(lag);
    }

    public void setLogTransformation(boolean log){
        m_pView.setLogTransformation(log);
        m_arView.setLogTransformation(log);
    }
    private void build() {
        setLayout(new BorderLayout());
        add(m_splitter, BorderLayout.CENTER);

        m_splitter.setTopComponent(m_pView);
        m_splitter.setBottomComponent(m_arView);
        m_splitter.setDividerLocation(.5);
        m_splitter.setResizeWeight(.5);
        
        m_pView.setDifferencingOrder(0);
        m_arView.setDifferencingOrder(0);
    }

    @Override
    public void dispose() {
        m_pView.dispose();
        m_arView.dispose();
    }
}
