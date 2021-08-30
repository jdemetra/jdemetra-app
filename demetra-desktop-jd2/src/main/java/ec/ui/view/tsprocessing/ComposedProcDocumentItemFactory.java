/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.ui.view.tsprocessing;

import com.google.common.base.Preconditions;
import demetra.ui.components.JExceptionPanel;
import demetra.desktop.design.SwingComponent;
import ec.tstoolkit.algorithm.IProcDocument;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.InformationExtractor;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

/**
 * An implementation of {@link IProcDocumentItemFactory} that combines an
 * {@link InformationExtractor} an an {@link ItemUI}.
 *
 * @param <D> the type of the source of information
 * @param <I> the type of the information
 */
public abstract class ComposedProcDocumentItemFactory<D extends IProcDocument, I> implements ProcDocumentItemFactory {

    protected final Class<D> documentType;
    protected final Id itemId;
    protected final InformationExtractor<? super D, ?> informationExtractor;
    protected final ItemUI itemUI;
    protected boolean async;

    /**
     * The only constructor of this class. Note that all its parameters must be
     * non-null and are checked to find out NPE as early as possible.
     *
     * @param documentType
     * @param itemId
     * @param informationExtractor
     * @param itemUI
     */
    public ComposedProcDocumentItemFactory(@NonNull Class<D> documentType, @NonNull Id itemId, @NonNull InformationExtractor<? super D, I> informationExtractor, @NonNull ItemUI<? extends IProcDocumentView<D>, I> itemUI) {
        this.documentType = Objects.requireNonNull(documentType, "documentType");
        this.itemId = Objects.requireNonNull(itemId, "itemId");
        this.informationExtractor = Objects.requireNonNull(informationExtractor, "informationExtractor");
        this.itemUI = Objects.requireNonNull(itemUI, "itemUI");
        this.async = false;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    @Override
    public Class<D> getDocumentType() {
        return documentType;
    }

    @Override
    public Id getItemId() {
        return itemId;
    }

    @Override
    public JComponent getView(IProcDocumentView<? extends IProcDocument> host, IProcDocument document) {
        Preconditions.checkArgument(getDocumentType().isInstance(document), "Invalid document type");
        D source = getDocumentType().cast(document);
        Object info = informationExtractor.retrieve(source);
        if (info == null) {
            return host.getToolkit().getMessageViewer("No information for this item");
        }
        return async ? new AsyncView(host, source) : itemUI.getView(host, info);
    }

    @NonNull
    public InformationExtractor<? super D, ?> getInformationExtractor() {
        return informationExtractor;
    }

    @NonNull
    public ItemUI getItemUI() {
        return itemUI;
    }

    @SwingComponent
    private final class AsyncView extends JComponent {

        public AsyncView(final IProcDocumentView<? extends IProcDocument> host, final D source) {
            setLayout(new BorderLayout());
            add(newLoadingComponent(), BorderLayout.CENTER);

            new SwingWorker<Object, Void>() {
                @Override
                protected Object doInBackground() throws Exception {
                    return informationExtractor.retrieve(source);
                }

                @Override
                protected void done() {
                    try {
                        switchToComponent(itemUI.getView(host, get()));
                    } catch (InterruptedException | ExecutionException ex) {
                        Thread.currentThread().interrupt();
                        switchToComponent(JExceptionPanel.create(ex));
                    }
                }
            }.execute();
        }

        private JComponent newLoadingComponent() {
            JLabel result = new JLabel();
            result.setHorizontalAlignment(SwingConstants.CENTER);
            result.setFont(result.getFont().deriveFont(result.getFont().getSize2D() * 2));
            result.setText("<html><center>Loading");
            return result;
        }

        private void switchToComponent(Component c) {
            removeAll();
            if (c != null) {
                add(c, BorderLayout.CENTER);
                validate();
                invalidate();
                repaint();
                c.setSize(getSize());
            }
        }
    }
}
