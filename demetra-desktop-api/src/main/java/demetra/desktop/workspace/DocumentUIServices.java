/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.workspace;

import demetra.desktop.descriptors.*;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.ui.properties.l2fprod.PropertiesPanelFactory;
import demetra.desktop.workspace.WorkspaceItem;
import demetra.processing.ProcDocument;
import demetra.processing.ProcSpecification;
import java.awt.Color;
import java.util.Optional;
import javax.swing.Icon;
import nbbrd.service.Mutability;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import org.openide.util.Lookup;

/**
 *
 * @author Jean Palate
 * @param <S>
 * @param <D>
 */
@ServiceDefinition(quantifier = Quantifier.MULTIPLE, mutability = Mutability.NONE, singleton = true)
public interface DocumentUIServices<S extends ProcSpecification, D extends ProcDocument<S, ?, ?>> {

    public final String SPEC_PROPERTY = "specification";

    Class<D> getDocumentType();

    Class<S> getSpecType();

    IObjectDescriptor<S> getSpecificationDescriptor(D document);

    IProcDocumentView<D> getDocumentView(D document);
    
    default PropertySheetPanel getSpecView(IObjectDescriptor<S> desc) {
        final PropertySheetPanel panel = PropertiesPanelFactory.INSTANCE.createPanel(desc);
        panel.addPropertySheetChangeListener(evt -> panel.firePropertyChange(SPEC_PROPERTY, 0, 1));
        return panel;
    }

    Color getColor();

    Icon getIcon();
    
    default Icon getItemIcon(WorkspaceItem<D> doc){
        return getIcon();
    }

    void showDocument(WorkspaceItem<D> doc);

    public static DocumentUIServices forSpec(Class sclass) {
        Optional<? extends DocumentUIServices> s = Lookup.getDefault().lookupAll(DocumentUIServices.class).stream()
                .filter(ui->ui.getSpecType().equals(sclass)).findFirst();
      
        return s.isPresent() ? s.get() : null;
    }

    public static DocumentUIServices forDocument(Class dclass) {
        Optional<? extends DocumentUIServices> s = Lookup.getDefault().lookupAll(DocumentUIServices.class).stream()
                .filter(ui->ui.getDocumentType().equals(dclass)).findFirst();
      
        return s.isPresent() ? s.get() : null;
    }
}
