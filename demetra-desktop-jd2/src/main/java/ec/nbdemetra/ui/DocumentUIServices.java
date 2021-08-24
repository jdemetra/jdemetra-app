/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui;

import demetra.ui.design.GlobalService;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import demetra.ui.util.LazyGlobalService;
import ec.nbdemetra.ui.properties.l2fprod.PropertiesPanelFactory;
import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.ui.view.tsprocessing.IProcDocumentView;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.JComponent;

/**
 *
 * @author Jean Palate
 */
@GlobalService
public final class DocumentUIServices {

    @NonNull
    public static DocumentUIServices getDefault() {
        return LazyGlobalService.get(DocumentUIServices.class, DocumentUIServices::new);
    }

    public static final String SPEC_PROPERTY = "specification";

    public interface UIFactory<S extends IProcSpecification, D extends IProcDocument<S, ?, ?>> {

        IObjectDescriptor<S> getSpecificationDescriptor(D document);

        IProcDocumentView<D> getDocumentView(D document);

        JComponent getSpecView(IObjectDescriptor<S> desc);
    }

    static abstract public class AbstractUIFactory<S extends IProcSpecification, D extends IProcDocument<S, ?, ?>> implements UIFactory<S, D> {

        @Override
        public JComponent getSpecView(IObjectDescriptor<S> desc) {
            final PropertySheetPanel panel = PropertiesPanelFactory.INSTANCE.createPanel(desc);
            panel.addPropertySheetChangeListener(evt -> panel.firePropertyChange(SPEC_PROPERTY, 0, 1));
            return panel;
        }
    }

    protected final Map<Class, UIFactory> map;

    private DocumentUIServices() {
        this.map = new HashMap<>();
    }

    public <S extends IProcSpecification, D extends IProcDocument<S, ?, ?>> UIFactory<S, D> getFactory(Class<D> dclass) {
        return map.get(dclass);
    }

    public <S extends IProcSpecification, D extends IProcDocument<S, ?, ?>> void register(Class<D> dclass, UIFactory<S, D> factory) {
        map.put(dclass, factory);
    }

    public <D extends IProcDocument<?, ?, ?>> void unregister(Class<D> dclass) {
        map.remove(dclass);
    }
}
