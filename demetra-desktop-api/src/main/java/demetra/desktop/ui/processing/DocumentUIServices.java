/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import demetra.desktop.descriptors.*;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import demetra.desktop.ui.properties.l2fprod.PropertiesPanelFactory;
import demetra.processing.ProcDocument;
import demetra.processing.ProcSpecification;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
@lombok.experimental.UtilityClass
public class DocumentUIServices {

    public final String SPEC_PROPERTY = "specification";

    public static interface UIFactory<S extends ProcSpecification, D extends ProcDocument<S, ?, ?>> {

        IObjectDescriptor<S> getSpecificationDescriptor(D document);

        IProcDocumentView<D> getDocumentView(D document);

        JComponent getSpecView(IObjectDescriptor<S> desc);
    }

    public static abstract class AbstractUIFactory<S extends ProcSpecification, D extends ProcDocument<S, ?, ?>> implements UIFactory<S, D> {

        @Override
        public JComponent getSpecView(IObjectDescriptor<S> desc) {
            final PropertySheetPanel panel = PropertiesPanelFactory.INSTANCE.createPanel(desc);
            panel.addPropertySheetChangeListener(evt -> panel.firePropertyChange(SPEC_PROPERTY, 0, 1));
            return panel;
        }
    }

}
