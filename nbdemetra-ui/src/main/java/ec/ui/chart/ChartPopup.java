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
package ec.ui.chart;

import ec.tstoolkit.timeseries.simplets.TsData;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.List;
import javax.swing.JDialog;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

/**
 * Utility Popup dialog displaying graphical results of Revision History
 * @author Mats Maggi
 */
public class ChartPopup extends JDialog {

    private RevisionChartPanel panel;

    public ChartPopup(Frame owner, boolean modal) {
        super(owner, modal);
        setLayout(new BorderLayout());

        setType(Type.UTILITY);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                dispose();
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {
            }
        });

        panel = new RevisionChartPanel(createChart());
        
        add(panel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(350, 200));
        setSize(new Dimension(350, 200));
    }

    /**
     * Sets the location of the popup.
     * If the given location places the popup outside of the screen, the location
     * is adapted to be placed at the maximum visible location.
     * @param p Point on the screen where the upper left corner of the popup
     * must be placed.
     */
    @Override
    public void setLocation(Point p) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = p.x;
        int y = p.y;

        if (x + getWidth() > screenSize.width) {
            x = screenSize.width - getWidth();
        }

        if (y + getHeight() > screenSize.height) {
            y = screenSize.height - getHeight();
        }

        super.setLocation(x, y);
    }

    /**
     * Sets the title of the graph
     * @param title Title of the graph
     */
    public void setChartTitle(String title) {
        panel.setChartTitle(title);
    }

    /**
     * Sets the data to display
     * @param reference Reference serie
     * @param series List of revised series
     */
    public void setTsData(TsData reference, List<TsData> series) {
        panel.setTsData(reference, series);
    }

    private JFreeChart createChart() {
        XYPlot plot = new XYPlot();
        JFreeChart result = new JFreeChart("", TsCharts.CHART_TITLE_FONT, plot, false);
        return result;
    }
}
