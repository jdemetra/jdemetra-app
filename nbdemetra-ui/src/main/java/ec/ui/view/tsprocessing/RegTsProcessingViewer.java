/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import com.google.common.collect.ObjectArrays;
import demetra.ui.components.HasTsCollection.TsUpdateMode;
import demetra.ui.components.JTsTable;
import ec.tss.Ts;
import ec.tss.documents.MultiTsDocument;
import ec.tstoolkit.algorithm.IProcSpecification;
import demetra.ui.components.JTsTable.Column;
import java.awt.Dimension;
import java.util.Arrays;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 *
 * @author Jean
 */
public class RegTsProcessingViewer extends DefaultProcessingViewer<MultiTsDocument> {

    // FACTORY METHODS >
    public static RegTsProcessingViewer create(MultiTsDocument doc) {
        RegTsProcessingViewer viewer = new RegTsProcessingViewer(Type.APPLY);
        if (doc != null) {
            viewer.setDocument(doc);
        }
        return viewer;
    }
    // visual components
    private final JTsTable yList, xList;
    private final JLabel specLabel;
    private boolean quietRefresh;

    public RegTsProcessingViewer(Type type) {
        super(type);
        yList = new JTsTable();
        yList.setVisible(true);
        yList.setShowHeader(false);
        yList.setTsUpdateMode(TsUpdateMode.Single);
        yList.setColumns(Arrays.asList(Column.NAME));
        xList = new JTsTable();
        yList.setVisible(true);
        xList.setShowHeader(false);
        xList.setColumns(Arrays.asList(Column.NAME));
        this.specLabel = new JLabel("Spec: ");
        specLabel.setVisible(true);
        xList.setPreferredSize(new Dimension(50, 60));
        yList.setPreferredSize(new Dimension(50, 60));

        toolBar.add(Box.createHorizontalStrut(3), 0);
        toolBar.add(new JLabel("Y: "), 1);
        toolBar.add(new JToolBar.Separator(), 2);
        toolBar.add(yList, 3);
        toolBar.add(new JToolBar.Separator(), 4);
        toolBar.add(new JLabel("X: "), 5);
        toolBar.add(new JToolBar.Separator(), 6);
        toolBar.add(xList, 7);
        toolBar.add(new JToolBar.Separator(), 8);
        toolBar.add(specLabel, 9);
        toolBar.add(new JToolBar.Separator(), 10);
        toolBar.add(Box.createHorizontalStrut(100), 11);

        toolBar.setVisible(true);

        xList.addPropertyChangeListener(JTsTable.TS_COLLECTION_PROPERTY, evt -> {
            if (!quietRefresh) {
                updateDocument();
            }
        });
        yList.addPropertyChangeListener(JTsTable.TS_COLLECTION_PROPERTY, evt-> {
            if (!quietRefresh) {
                updateDocument();
            }
        });
    }

    private void updateDocument() {
        try {

            quietRefresh = true;
            Ts[] y = yList.getTsCollection().toArray();
            if (y == null || y.length == 0) {
                getDocument().setInput(null);
            }
            Ts[] x = xList.getTsCollection().toArray();
            if (x == null || x.length == 0) {
                getDocument().setInput(y);
            } else {
                getDocument().setInput(ObjectArrays.concat(y[0], x));
            }
            refreshAll();
        } catch (Exception err) {
        } finally {
            quietRefresh = false;
        }
    }

    private void updateList() {
        Ts[] s = getDocument().getTs();
        if (s == null || s.length == 0) {
            yList.getTsCollection().clear();
            xList.getTsCollection().clear();
        } else {
            yList.getTsCollection().replace(s[0]);
            xList.getTsCollection().replace(Arrays.copyOfRange(s, 1, s.length));
        }
    }

    public void refreshSpec() {
        MultiTsDocument doc = getDocument();
        IProcSpecification spec = doc.getSpecification();
        specLabel.setText("Spec: " + (spec != null ? spec.toString() : ""));
    }

    @Override
    public void refreshHeader() {
        try {
            if (quietRefresh) {
                return;
            }
            refreshSpec();
            updateList();
        } catch (Exception err) {
        }
    }

    @Override
    public void refreshView() {
        super.refreshView();
        refreshSpec();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
