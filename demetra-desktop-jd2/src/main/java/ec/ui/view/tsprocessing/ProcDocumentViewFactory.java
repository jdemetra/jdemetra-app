/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.utilities.DefaultInformationExtractor;
import ec.tstoolkit.utilities.Id;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Jean Palate
 */
public abstract class ProcDocumentViewFactory<D extends IProcDocument> implements IProcDocumentViewFactory<D> {

    public class View implements IProcDocumentView<D> {

        private final D document_;

        public View(D document) {
            document_ = document;
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
            itemFactories.values().stream()
                    .filter(ComposedProcDocumentItemFactory.class::isInstance)
                    .forEach(o -> ((ComposedProcDocumentItemFactory) o).getInformationExtractor().flush(document_));
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
    private final LinkedHashMap<Id, ProcDocumentItemFactory> itemFactories = new LinkedHashMap();

    /**
     * Call this method to register {@link IProcDocumentItemFactory} from
     * lookup. Note that it must be called in leaf classes.
     *
     * @param documentType
     */
    protected void registerFromLookup(Class<D> documentType) {
        for (ProcDocumentItemFactory o : ProcDocumentItemFactoryLoader.get()) {
            if (o.getDocumentType().isAssignableFrom(documentType)) {
                itemFactories.put(o.getItemId(), o);
            }
        }
    }

    public Icon getIcon(Id id) {
        ProcDocumentItemFactory o = itemFactories.get(id);
        return o instanceof ComposedProcDocumentItemFactory ? o.getIcon() : null;
    }

    public abstract Id getPreferredView();

    public Action[] getActions(Id path) {
        ProcDocumentItemFactory o = itemFactories.get(path);
        return o instanceof ComposedProcDocumentItemFactory ? o.getActions() : null;
    }

    @NonNull
    public List<Id> getItems() {
        return new ArrayList<>(itemFactories.keySet());
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
