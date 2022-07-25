/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.disaggregation;

import com.google.common.collect.ObjectArrays;
import java.awt.Dimension;
import java.util.Arrays;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 *
 * @author Jean
 */
final class FixedRegTsProcessingViewer extends DefaultProcessingViewer<MultiTsDocument> {

    // FACTORY METHODS >
    public static FixedRegTsProcessingViewer create(MultiTsDocument doc) {
        FixedRegTsProcessingViewer viewer = new FixedRegTsProcessingViewer(DefaultProcessingViewer.Type.APPLY);
        if (doc != null) {
            viewer.setDocument(doc);
        }
        return viewer;
    }
    // visual components
    private final JTsList yList, xList;
    private final JLabel specLabel;
    private boolean quietRefresh;

    public FixedRegTsProcessingViewer(DefaultProcessingViewer.Type type) {
        super(type);
        yList = new JTsList();
        yList.setVisible(true);
        yList.setShowHeader(false);
        yList.setTsUpdateMode(TsUpdateMode.Single);
        yList.setInformation(new InfoType[]{InfoType.Name});
        xList = new JTsList();
        yList.setVisible(true);
        xList.setShowHeader(false);
        xList.setInformation(new InfoType[]{InfoType.Name});
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

        // FIXME: there is a deadlock with Ts.Master#freeze() & TsFactory#update(TsInformation);
        // these two lines force loading to prevent deadlock but might freeze EDT
        xList.setFreezeOnImport(true);
        yList.setFreezeOnImport(true);

        xList.addPropertyChangeListener(JTsList.TS_COLLECTION_PROPERTY, evt -> {
            if (!quietRefresh) {
                updateDocument();
            }
        });
        yList.addPropertyChangeListener(JTsList.TS_COLLECTION_PROPERTY, evt -> {
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
        yList.dispose();
        xList.dispose();
    }
}
