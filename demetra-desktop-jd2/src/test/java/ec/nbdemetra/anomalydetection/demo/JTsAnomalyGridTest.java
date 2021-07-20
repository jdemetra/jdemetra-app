/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.anomalydetection.demo;

import ec.nbdemetra.anomalydetection.ui.AnomalyDetectionSummary;
import ec.nbdemetra.anomalydetection.ui.JTsAnomalyGrid;
import ec.nbdemetra.anomalydetection.ui.OutliersTopComponent;
import ec.nbdemetra.ui.DemetraUiIcon;
import demetra.ui.util.NbComponents;
import ec.tstoolkit.modelling.arima.PreprocessingModel;
import demetra.ui.components.JTsGrid;
import ec.util.various.swing.BasicSwingLauncher;
import demetra.ui.components.TsSelectionBridge;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import org.openide.awt.DropDownButtonFactory;

/**
 *
 * @author maggima
 */
public final class JTsAnomalyGridTest extends JPanel {

    public static void main(String[] args) {

        new BasicSwingLauncher()
                .content(OutliersTopComponent.class)
                .launch();
    }

    static JButton createZoomButton(final JTsAnomalyGrid view) {
        final JPopupMenu addPopup = new JPopupMenu();
        int[] zoomValues = {200, 100, 75, 50, 25};
        for (int i = 0; i < zoomValues.length; ++i) {
            JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(Integer.toString(zoomValues[i]));
            menuItem.setName(Integer.toString(zoomValues[i]));
            menuItem.addActionListener(new AbstractAction(Integer.toString(zoomValues[i])) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    view.setZoomPercentage(Integer.parseInt(getValue(NAME).toString()));
                }
            });
            menuItem.setState(i == 1);
            addPopup.add(menuItem);
        }
        view.addPropertyChangeListener(JTsGrid.ZOOM_PROPERTY, evt -> {
            for (Component o : addPopup.getComponents()) {
                ((JCheckBoxMenuItem) o).setState(view.getZoomPercentage() == Integer.parseInt(o.getName()));
            }
        });

        JButton result = DropDownButtonFactory.createDropDownButton(DemetraUiIcon.MAGNIFYING_TOOL, addPopup);
        return result;
    }

    public void set(PreprocessingModel model) {
        summ.set(model);
    }
    private AnomalyDetectionSummary summ;

    public JTsAnomalyGridTest() {
        super();
        setLayout(new BorderLayout());

        JPanel p = new JPanel();
        JPanel comps = new JPanel();

        comps.setLayout(new BorderLayout());

        final JTsAnomalyGrid g = new JTsAnomalyGrid();

        p.add(createZoomButton(g), BorderLayout.NORTH);
        p.setLayout(new FlowLayout());

        comps.add(g, BorderLayout.CENTER);
        comps.add(p, BorderLayout.NORTH);

        summ = new AnomalyDetectionSummary();
        g.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals(TsSelectionBridge.TS_SELECTION_PROPERTY)) {
                set(g.getModelOfSelection());
            }
        });

        JSplitPane split = NbComponents.newJSplitPane(JSplitPane.HORIZONTAL_SPLIT, comps, summ);
        split.setDividerLocation(0.5);

        add(split, BorderLayout.CENTER);

    }
}