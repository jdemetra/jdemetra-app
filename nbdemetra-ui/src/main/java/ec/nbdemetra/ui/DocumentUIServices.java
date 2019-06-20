/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui;

import ec.nbdemetra.core.GlobalService;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import ec.nbdemetra.ui.properties.l2fprod.PropertiesPanelFactory;
import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.descriptors.IObjectDescriptor;
import ec.ui.view.tsprocessing.IProcDocumentView;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.JComponent;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@GlobalService
@ServiceProvider(service = DocumentUIServices.class)
public class DocumentUIServices {

    @NonNull
    public static DocumentUIServices getDefault() {
        return Lookup.getDefault().lookup(DocumentUIServices.class);
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

    public DocumentUIServices() {
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
