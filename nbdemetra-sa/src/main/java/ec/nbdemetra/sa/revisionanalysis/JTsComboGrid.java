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
package ec.nbdemetra.sa.revisionanalysis;

import demetra.ui.TsManager;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.MonikerUI;
import ec.tss.Ts;
import ec.tss.TsCollection;
import demetra.ui.components.JTsGrid;
import ec.util.list.swing.JLists;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * Component combining a standard <code>JTsGrid</code> and a
 * <code>JComboBox</code> allowing the selection of results from a specific
 * input time series
 *
 * @author Mats Maggi
 */
public class JTsComboGrid extends JComponent {

    private JComboBox series;
    private JTsGrid grid;
    private LinkedHashMap<Ts, TsCollection> collections;

    public JTsComboGrid(Map<Ts, TsCollection> list) {
        setLayout(new BorderLayout());

        collections = (LinkedHashMap<Ts, TsCollection>) list;

        if (collections != null && !collections.isEmpty()) {
            series = new JComboBox();
            series.setRenderer(JLists.cellRendererOf(JTsComboGrid::renderTsIdentifier));
            generateComboBox();
            grid = new JTsGrid();

            series.addItemListener(event -> {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    Object item = event.getItem();
                    if (item != null) {
                        if (item instanceof Ts) {
                            grid.setTsCollection(collections.get((Ts) item));
                        } else {
                            showAllTs();
                        }
                    }
                }
            });

            add(series, BorderLayout.NORTH);
            add(grid, BorderLayout.CENTER);

            showAllTs();
        }
    }

    private void showAllTs() {
        TsCollection coll = TsManager.getDefault().newTsCollection();
        for (Map.Entry<Ts, TsCollection> entry : collections.entrySet()) {
            TsCollection c = entry.getValue();
            for (int i = 0; i < c.getCount(); i++) {
                coll.quietAdd(c.get(i).rename(entry.getKey().getName() + " (" + c.get(i).getName() + ")"));
            }
        }
        grid.setTsCollection(coll);
    }

    /**
     * Sets the input data to be displayed in this view
     *
     * @param collections Map of results (key are each input Ts, values are
     * their corresponding TsCollection results)
     */
    public void setCollections(LinkedHashMap<Ts, TsCollection> collections) {
        this.collections = collections;
        generateComboBox();
    }

    private void generateComboBox() {
        series.removeAllItems();
        series.addItem("Show all series");
        for (Map.Entry<Ts, TsCollection> entry : collections.entrySet()) {
            series.addItem(entry.getKey());
        }
        series.setSelectedIndex(0);
    }

    private static void renderTsIdentifier(JLabel label, Object value) {
        if (value instanceof String) {
            label.setText(String.valueOf(value));
            label.setIcon(DemetraUiIcon.DOCUMENT_TASK_16);
        } else {
            Ts ts = (Ts) value;
            label.setText(ts.getName());
            label.setIcon(MonikerUI.getDefault().getIcon(ts.getMoniker()));
        }
    }
}
