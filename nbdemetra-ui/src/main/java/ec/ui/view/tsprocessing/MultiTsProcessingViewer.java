/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import demetra.bridge.TsConverter;
import demetra.timeseries.TsCollection;
import demetra.ui.components.JTsTable;
import ec.tss.documents.MultiTsDocument;
import ec.tstoolkit.algorithm.IProcSpecification;
import java.awt.Font;
import java.util.stream.Stream;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 *
 * @author Jean
 */
public class MultiTsProcessingViewer extends DefaultProcessingViewer<MultiTsDocument> {

    // FACTORY METHODS >
    // FACTORY METHODS >
    public static MultiTsProcessingViewer create(MultiTsDocument doc) {
        MultiTsProcessingViewer viewer = new MultiTsProcessingViewer(Type.APPLY);
        if (doc != null) {
            viewer.setDocument(doc);
        }
        return viewer;
    }
   // < FACTORY METHODS
    // CONSTANTS
    private static final Font DROP_DATA_FONT = new JLabel().getFont().deriveFont(Font.ITALIC);
    // visual components
    private final JTsTable tsList;
    private final JLabel specLabel;
    private boolean quietRefresh;
    
    public MultiTsProcessingViewer(Type type) {
        super(type);
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
                getDocument().setInput(tsList.getTsCollection().stream().map(TsConverter::fromTs).toArray());
            }
        });
    }
    
    @Override
    public void refreshHeader() {
        try {
            MultiTsDocument doc = getDocument();
            IProcSpecification spec = doc.getSpecification();
            specLabel.setText("Spec: " + (spec != null ? spec.toString() : ""));
            quietRefresh = true;
            tsList.setTsCollection(Stream.of(doc.getTs()).map(TsConverter::toTs).collect(TsCollection.toTsCollection()));
        } catch (Exception err) {
        } finally {
            quietRefresh = false;
        }
    }
}
