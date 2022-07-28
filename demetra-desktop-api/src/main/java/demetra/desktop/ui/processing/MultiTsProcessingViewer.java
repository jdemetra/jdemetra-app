/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import demetra.desktop.components.JTsTable;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.processing.ProcSpecification;
import demetra.timeseries.MultiTsDocument;
import demetra.timeseries.TsDocument;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 *
 * @author Jean Palate
 * @param <S>
 * @param <D>
 */
public class MultiTsProcessingViewer<S extends ProcSpecification, D extends MultiTsDocument<S, ?>> extends DefaultProcessingViewer<S, D> {

     // FACTORY METHODS >
    public static <S extends ProcSpecification, D extends MultiTsDocument<S, ?>> MultiTsProcessingViewer create(D doc, DocumentUIServices<S, D> uifac) {
        MultiTsProcessingViewer viewer = new MultiTsProcessingViewer(uifac, Type.APPLY);
        if (doc != null) {
            viewer.setDocument(doc);
        }
        return viewer;
    }

    // CONSTANTS
    private static final Font DROP_DATA_FONT = new JLabel().getFont().deriveFont(Font.ITALIC);
    // visual components
    private final JTsTable tsList;
    private final JLabel specLabel;
    private boolean quietRefresh;
    
    public MultiTsProcessingViewer(DocumentUIServices ui, Type type) {
        super(ui, type);
        this.tsList = new JTsTable();
        tsList.setVisible(true);
        this.specLabel = new JLabel("Spec: ");
        specLabel.setVisible(true);
        
        toolBar.add(Box.createHorizontalStrut(3), 0);
        toolBar.add(tsList, 2);
        toolBar.add(new JToolBar.Separator(), 3);
        toolBar.add(specLabel, 4);
        
        tsList.addPropertyChangeListener(JTsTable.TS_COLLECTION_PROPERTY, evt -> {
            if (!quietRefresh) {
                getDocument().set(tsList.getTsCollection().getItems());
            }
        });
    }
    
    @Override
    public void refreshHeader() {
        try {
            MultiTsDocument doc = getDocument();
            ProcSpecification spec = doc.getSpecification();
            specLabel.setText("Spec: " + (spec != null ? spec.toString() : ""));
            quietRefresh = true;
            tsList.getTsCollection().replaceAll(doc.getInput());
        } catch (Exception err) {
        } finally {
            quietRefresh = false;
        }
    }
}
