/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import demetra.processing.ProcDocument;
import demetra.util.Id;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

/**
 * @author Jean Palate
 * @param <D>
 */
public abstract class ProcDocumentViewFactory<D extends ProcDocument> implements IProcDocumentViewFactory<D> {

    public class View implements IProcDocumentView<D> {

        private final D document;

        public View(D document) {
            this.document = document;
        }

        @Override
        public JComponent getView(Id path) {
            if (document == null || document.getResult() == null) {
                return null;
            }
            IProcDocumentItemFactory itemFactory = itemFactories.get(path);
            return itemFactory != null ? itemFactory.getView(document) : null;
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
            // Necessary ?
//            itemFactories.values().stream()
//                    .filter(ProcDocumentItemFactory.class::isInstance)
//                    .forEach(o -> ((ProcDocumentItemFactory) o).getInformationExtractor().flush(document));
        }

        @Override
        public void dispose() {
            refresh();
        }

        @Override
        public D getDocument() {
            return document;
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
    private final LinkedHashMap<Id, IProcDocumentItemFactory> itemFactories = new LinkedHashMap();

    /**
     * Call this method to register {@link IProcDocumentItemFactory} from
     * lookup. Note that it must be called in leaf classes.
     *
     * @param documentType
     */
    protected void registerFromLookup(Class<D> documentType) {
        for (IProcDocumentItemFactory o : IProcDocumentItemFactoryLoader.get()) {
            if (o.getDocumentType().isAssignableFrom(documentType)) {
                itemFactories.put(o.getItemId(), o);
            }
        }
    }

    public Icon getIcon(Id id) {
        IProcDocumentItemFactory o = itemFactories.get(id);
        return o instanceof ProcDocumentItemFactory ? o.getIcon() : null;
    }

    public abstract Id getPreferredView();

    public Action[] getActions(Id path) {
        IProcDocumentItemFactory o = itemFactories.get(path);
        return o instanceof ProcDocumentItemFactory ? o.getActions() : null;
    }

    @NonNull
    public List<Id> getItems() {
        return new ArrayList<>(itemFactories.keySet());
    }

    @Override
    public final IProcDocumentView<D> create(D document) {
        return new View(document);
    }
    
    <D,S>Function<D, S> doNothingExtractor(){return d->null;}


}
