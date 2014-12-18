/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.utilities.DefaultInformationExtractor;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import org.openide.util.Lookup;

/**
 *
 * @author Jean Palate
 */
public abstract class ProcDocumentViewFactory<D extends IProcDocument> implements IProcDocumentViewFactory<D> {

    public class View implements IProcDocumentView<D> {

        private final D document_;

        public View(D document) {
            document_ = document;
        }

        @Override
        public ITsViewToolkit getToolkit() {
            return toolkit_;
        }

        @Deprecated
        public ItemUI getUI(Id id) {
            return ProcDocumentViewFactory.this.getUI(id);
        }

        @Deprecated
        @Nullable
        public InformationExtractor<? super D, ?> getExtractor(Id id) {
            return ProcDocumentViewFactory.this.getExtractor(id);
        }

        @Deprecated
        protected Object getInformation(Id id) {
            InformationExtractor extractor = getExtractor(id);
            return extractor != null ? extractor.retrieve(document_) : document_;
        }

        @Override
        public JComponent getView(Id path) {
            if (document_ == null || document_.getResults() == null) {
                return null;
            }
            ProcDocumentItemFactory itemFactory = itemFactories.get(path);
            return itemFactory != null ? itemFactory.getView(this, document_) : null;
        }

        @Override
        public List<Id> getItems() {
            return ProcDocumentViewFactory.this.getItems();
        }

        @Override
        public Icon getIcon(Id path) {
            return ProcDocumentViewFactory.this.getIcon(path);
        }

        @Override
        public void refresh() {
            for (ComposedProcDocumentItemFactory o : Iterables.filter(itemFactories.values(), ComposedProcDocumentItemFactory.class)) {
                o.getInformationExtractor().flush(document_);
            }
        }

        @Override
        public void dispose() {
            refresh();
        }

        @Override
        public D getDocument() {
            return document_;
        }

        @Override
        public Action[] getActions(Id path) {
            return ProcDocumentViewFactory.this.getActions(path);
        }

        @Override
        public Id getPreferredView() {
            return ProcDocumentViewFactory.this.getPreferredView();
        }
    }
    //
    private final LinkedHashMap<Id, ProcDocumentItemFactory> itemFactories = Maps.newLinkedHashMap();
    private ITsViewToolkit toolkit_ = TsViewToolkit.getInstance();

    /**
     * Call this method to register {@link IProcDocumentItemFactory} from
     * lookup. Note that it must be called in leaf classes.
     *
     * @param documentType
     */
    protected void registerFromLookup(Class<D> documentType) {
        for (ProcDocumentItemFactory o : Lookup.getDefault().lookupAll(ProcDocumentItemFactory.class)) {
            if (o.getDocumentType().isAssignableFrom(documentType)) {
                itemFactories.put(o.getItemId(), o);
            }
        }
    }

    public Icon getIcon(Id id) {
        ProcDocumentItemFactory o = itemFactories.get(id);
        return o instanceof ComposedProcDocumentItemFactory ? ((ComposedProcDocumentItemFactory) o).getIcon() : null;
    }

    public abstract Id getPreferredView();

    public Action[] getActions(Id path) {
        ProcDocumentItemFactory o = itemFactories.get(path);
        return o instanceof ComposedProcDocumentItemFactory ? ((ComposedProcDocumentItemFactory) o).getActions() : null;
    }

    /**
     * @deprecated use {@link ComposedProcDocumentItemFactory} instead
     */
    @Override
    @Deprecated
    public <I> void register(Id id, InformationExtractor<? super D, I> info, ItemUI<? extends IProcDocumentView<D>, I> ui) {
        itemFactories.put(id, new ComposedProcDocumentItemFactory(IProcDocument.class, id, info != null ? info : new DoNothingExtractor(), ui));
    }

    /**
     * @deprecated use {@link ComposedProcDocumentItemFactory} instead
     */
    @Override
    @Deprecated
    public void unregister(Id id) {
        itemFactories.remove(id);
    }

    /**
     * @deprecated use {@link ComposedProcDocumentItemFactory} instead
     */
    @Deprecated
    public void unregisterAll() {
        itemFactories.clear();
    }

    public void registerToolkit(@Nullable ITsViewToolkit toolkit) {
        toolkit_ = toolkit != null ? toolkit : TsViewToolkit.getInstance();
    }

    @Nonnull
    public ITsViewToolkit getToolkit() {
        return toolkit_;
    }

    @Nonnull
    public List<Id> getItems() {
        return Lists.newArrayList(itemFactories.keySet());
    }

    /**
     * @deprecated use {@link ComposedProcDocumentItemFactory} instead
     */
    @Deprecated
    @Nullable
    public ItemUI<? extends IProcDocumentView<D>, ?> getUI(Id id) {
        ProcDocumentItemFactory o = itemFactories.get(id);
        return o instanceof ComposedProcDocumentItemFactory ? ((ComposedProcDocumentItemFactory) o).getItemUI() : null;
    }

    /**
     * @deprecated use {@link ComposedProcDocumentItemFactory} instead
     */
    @Deprecated
    @Nullable
    public InformationExtractor<? super D, ?> getExtractor(Id id) {
        ProcDocumentItemFactory o = itemFactories.get(id);
        return o instanceof ComposedProcDocumentItemFactory ? ((ComposedProcDocumentItemFactory) o).getInformationExtractor() : null;
    }

    @Override
    public final IProcDocumentView<D> create(D document) {
        return new View(document);
    }

    // Let's avoid NPE
    public static class DoNothingExtractor<S> extends DefaultInformationExtractor<S, S> {
        
        @Override
        public S retrieve(S source) {
            return source;
        }
    }
    
}
