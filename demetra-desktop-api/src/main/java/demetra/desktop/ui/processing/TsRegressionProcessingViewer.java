/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import demetra.desktop.components.JTsTable;
import demetra.desktop.components.JTsTable.Column;
import demetra.desktop.components.parts.HasTsCollection.TsUpdateMode;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.processing.ProcSpecification;
import demetra.timeseries.MultiTsDocument;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 *
 * @author Jean Palate
 * @param <S>
 * @param <D>
 */
public class TsRegressionProcessingViewer<S extends ProcSpecification, D extends MultiTsDocument<S, ?>> extends DefaultProcessingViewer<S, D> {

    // FACTORY METHODS >
    public static <S extends ProcSpecification, D extends MultiTsDocument<S, ?>> TsRegressionProcessingViewer<S, D> create(D doc, DocumentUIServices<S, D> uifac, boolean singleX) {
        TsRegressionProcessingViewer viewer = new TsRegressionProcessingViewer(uifac, Type.APPLY, singleX);
        if (doc != null) {
            viewer.setDocument(doc);
        }
        return viewer;
    }
    // visual components
    private final JTsTable yList, xList;
    private final JLabel specLabel;

    public TsRegressionProcessingViewer(DocumentUIServices<S, D> ui, Type type, boolean singleX) {
        super(ui, type);
        yList = new JTsTable();
        yList.setVisible(true);
        yList.setShowHeader(false);
        yList.setTsUpdateMode(TsUpdateMode.Single);
        yList.setColumns(Collections.singletonList(Column.NAME));
        xList = new JTsTable();
        yList.setVisible(true);
        xList.setShowHeader(false);
        if (singleX) {
            xList.setTsUpdateMode(TsUpdateMode.Single);
        }
        xList.setColumns(Collections.singletonList(Column.NAME));
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
    }

    
    public void initialize() {
        updateList();
        xList.addPropertyChangeListener(JTsTable.TS_COLLECTION_PROPERTY, evt -> {
            updateInput();
            onDocumentChanged();
        });
        yList.addPropertyChangeListener(JTsTable.TS_COLLECTION_PROPERTY, evt -> {
            updateInput();
            onDocumentChanged();
        });
    }

    @Override
    public void refreshHeader() {
        try {
            refreshSpec();
        } catch (Exception err) {
        }
    }

    public void refreshSpec() {
        MultiTsDocument doc = getDocument();
        ProcSpecification spec = doc.getSpecification();
        specLabel.setText("Spec: " + (spec != null ? spec.toString() : ""));
    }

    private void updateList() {
        List<Ts> s = getDocument().getInput();
        if (s.isEmpty()) {
            yList.setTsCollection(TsCollection.EMPTY);
            xList.setTsCollection(TsCollection.EMPTY);
        } else {
            yList.setTsCollection(TsCollection.of(s.get(0)));
            xList.setTsCollection(TsCollection.of(s.subList(1, s.size())));
        }
    }

    private void updateInput() {
        List<Ts> y = yList.getTsCollection().getItems();
        if (y.isEmpty()) {
            getDocument().set(Collections.emptyList());
        }
        List<Ts> x = xList.getTsCollection().getItems();
        if (x.isEmpty()) {
            getDocument().set(y);
        } else {
            List<Ts> all = new ArrayList<>();
            all.addAll(y);
            all.addAll(x);
            getDocument().set(all);
        }
    }
}
